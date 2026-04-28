package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Submission;
import model.TeacherSubmissionReviewRecord;
import model.User;
import util.ApiClient;
import util.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SubmissionService {
    private static final String[] STUDENT_FILTER_FIELDS = {"student_id", "student_prn"};

    public static List<Submission> fetchSubmissions(User user) {
        if (user == null) {
            return new ArrayList<>();
        }

        for (String field : STUDENT_FILTER_FIELDS) {
            for (String identifier : candidateStudentIdentifiers(user)) {
                String endpoint = "/rest/v1/submissions?select=*&" + field + "=eq." + encode(identifier);
                String response = ApiClient.get(endpoint);
                if (response != null) {
                    return parseSubmissions(response);
                }
            }
        }

        return filterSubmissionsForUser(fetchAllSubmissions(), user);
    }

    public static List<Submission> fetchSubmissions(String identifier) {
        User user = new User();
        user.prn = identifier;
        user.id = identifier;
        return fetchSubmissions(user);
    }

    public static boolean uploadSubmission(Submission submission, User user, File file) {
        if (submission == null || user == null) {
            return false;
        }

        try {
            if (file != null && file.exists()) {
                String fileUrl = uploadFileToStorage(file, user.prn == null || user.prn.isBlank() ? user.id : user.prn);
                if (fileUrl == null) {
                    return false;
                }
                submission.file_url = fileUrl;
            }

            for (String field : STUDENT_FILTER_FIELDS) {
                for (String identifier : candidateStudentIdentifiers(user)) {
                    JsonObject payload = buildSubmissionPayload(submission, field, identifier);
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Prefer", "return=representation");
                    String response = ApiClient.send("POST", "/rest/v1/submissions", payload.toString(), headers);
                    if (isWriteSuccessful(response)) {
                        return true;
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return false;
    }

    public static boolean uploadSubmission(Submission submission, String userId, File file) {
        User user = new User();
        user.id = userId;
        user.prn = userId;
        return uploadSubmission(submission, user, file);
    }

    public static List<Map<String, String>> getSubmissions(String prn) {
        User user = new User();
        user.prn = prn;
        user.id = prn;
        List<Map<String, String>> submissions = new ArrayList<>();
        for (Submission submission : fetchSubmissions(user)) {
            submissions.add(toSubmissionMap(submission));
        }
        return submissions;
    }

    public static Map<String, Integer> getSubmissionStats(User user) {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("total", 0);
        stats.put("approved", 0);
        stats.put("pending", 0);
        stats.put("rejected", 0);

        for (Submission submission : fetchSubmissions(user)) {
            stats.put("total", stats.get("total") + 1);
            if (submission.isApproved()) {
                stats.put("approved", stats.get("approved") + 1);
            } else if (submission.isRejected()) {
                stats.put("rejected", stats.get("rejected") + 1);
            } else {
                stats.put("pending", stats.get("pending") + 1);
            }
        }

        return stats;
    }

    public static Map<String, Integer> getSubmissionStats(String prn) {
        User user = new User();
        user.prn = prn;
        user.id = prn;
        return getSubmissionStats(user);
    }

    public static List<Map<String, String>> getAchievements(User user) {
        if (user == null || user.prn == null || user.prn.isBlank()) {
            return deriveAchievementsFromApprovedSubmissions(new ArrayList<>());
        }

        return deriveAchievementsFromApprovedSubmissions(fetchSubmissions(user));
    }

    public static List<Map<String, String>> getAchievements(String prn) {
        User user = new User();
        user.prn = prn;
        user.id = prn;
        return getAchievements(user);
    }

    public static Map<String, Integer> getAchievementCounts(User user) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("projects", 0);
        counts.put("certificates", 0);
        counts.put("workshops", 0);

        for (Map<String, String> achievement : getAchievements(user)) {
            String category = achievement.getOrDefault("category", "").toLowerCase();
            switch (category) {
                case "project":
                    counts.put("projects", counts.get("projects") + 1);
                    break;
                case "certificate":
                    counts.put("certificates", counts.get("certificates") + 1);
                    break;
                case "workshop":
                    counts.put("workshops", counts.get("workshops") + 1);
                    break;
                default:
                    break;
            }
        }

        return counts;
    }

    public static Map<String, Integer> getAchievementCounts(String prn) {
        User user = new User();
        user.prn = prn;
        user.id = prn;
        return getAchievementCounts(user);
    }

    public static String createSubmission(String prn, String title, String type, String description) {
        Submission submission = new Submission(title, type, "", "pending");
        submission.description = description;
        User user = new User();
        user.prn = prn;
        user.id = prn;
        return uploadSubmission(submission, user, null) ? "created" : null;
    }

    public static String updateSubmissionStatus(String submissionId, String status) {
        JsonObject payload = new JsonObject();
        payload.addProperty("status", status);
        payload.addProperty("updated_at", LocalDate.now().toString());

        Map<String, String> headers = new HashMap<>();
        headers.put("Prefer", "return=representation");
        return ApiClient.send("PATCH",
                "/rest/v1/submissions?id=eq." + encode(submissionId),
                payload.toString(),
                headers);
    }

    public static boolean syncAchievementForApprovedSubmission(TeacherSubmissionReviewRecord review) {
        if (review == null || review.studentPrn == null || review.studentPrn.isBlank()) {
            return false;
        }

        return true;
    }

    public static boolean removeAchievementForSubmission(TeacherSubmissionReviewRecord review) {
        return true;
    }

    public static boolean refreshStudentMetrics(String studentPrn, int totalStudents) {
        if (studentPrn == null || studentPrn.isBlank()) {
            return false;
        }

        User user = new User();
        user.prn = studentPrn;
        user.id = studentPrn;
        Map<String, Integer> counts = getAchievementCounts(user);
        int approvedProjects = counts.getOrDefault("projects", 0);
        int approvedCertificates = counts.getOrDefault("certificates", 0);
        int approvedWorkshops = counts.getOrDefault("workshops", 0);
        int score = approvedProjects * 18 + approvedCertificates * 12 + approvedWorkshops * 6;

        JsonObject payload = new JsonObject();
        payload.addProperty("current_rank", Math.max(1, totalStudents - Math.max(1, score / 4)));
        payload.addProperty("total_students", Math.max(totalStudents, 1));
        payload.addProperty("percentile", Math.min(99, 40 + score));
        payload.addProperty("academics_progress", Math.min(100, 45 + approvedCertificates * 8));
        payload.addProperty("coding_progress", Math.min(100, 40 + approvedProjects * 10));
        payload.addProperty("clubs_progress", Math.min(100, 30 + approvedWorkshops * 12));

        Map<String, String> headers = new HashMap<>();
        headers.put("Prefer", "return=representation");
        String response = ApiClient.send("PATCH",
                "/rest/v1/users?prn=eq." + encode(studentPrn),
                payload.toString(),
                headers);
        return isWriteSuccessful(response);
    }

    public static List<Submission> fetchAllSubmissions() {
        String response = ApiClient.get("/rest/v1/submissions?select=*");
        return response == null ? new ArrayList<>() : parseSubmissions(response);
    }

    public static List<TeacherSubmissionReviewRecord> buildReviewRecords(List<Submission> submissions, Map<String, User> usersByIdentifier) {
        List<TeacherSubmissionReviewRecord> records = new ArrayList<>();
        for (Submission submission : submissions) {
            TeacherSubmissionReviewRecord record = new TeacherSubmissionReviewRecord();
            record.submissionId = submission.id;
            record.title = submission.title;
            record.type = submission.type;
            record.description = submission.description;
            record.status = submission.status;
            record.fileUrl = submission.file_url;
            record.createdAt = submission.createdAt;

            User matchedUser = usersByIdentifier.get(submission.studentPrn);
            if (matchedUser != null) {
                record.studentPrn = matchedUser.prn != null ? matchedUser.prn : submission.studentPrn;
                record.studentName = matchedUser.name != null ? matchedUser.name : submission.studentName;
                record.className = matchedUser.className;
            } else {
                record.studentPrn = submission.studentPrn;
                record.studentName = submission.studentName == null || submission.studentName.isBlank() ? "Unknown Student" : submission.studentName;
            }
            records.add(record);
        }
        return records;
    }

    private static JsonObject buildSubmissionPayload(Submission submission, String studentField, String identifier) {
        JsonObject payload = new JsonObject();
        payload.addProperty(studentField, identifier);
        payload.addProperty("title", submission.title);
        payload.addProperty("type", submission.type == null ? "project" : submission.type.toLowerCase());
        payload.addProperty("description", submission.description == null ? "" : submission.description);
        payload.addProperty("file_url", submission.file_url == null ? "" : submission.file_url);
        payload.addProperty("status", submission.status == null || submission.status.isBlank() ? "pending" : submission.status.toLowerCase());
        return payload;
    }

    private static List<String> candidateStudentIdentifiers(User user) {
        List<String> identifiers = new ArrayList<>();
        if (user.prn != null && !user.prn.isBlank()) {
            identifiers.add(user.prn);
        }
        if (user.id != null && !user.id.isBlank() && !identifiers.contains(user.id)) {
            identifiers.add(user.id);
        }
        return identifiers;
    }

    private static List<Submission> filterSubmissionsForUser(List<Submission> submissions, User user) {
        List<Submission> filtered = new ArrayList<>();
        for (Submission submission : submissions) {
            if (belongsToUser(submission, user)) {
                filtered.add(submission);
            }
        }
        return filtered;
    }

    private static boolean belongsToUser(Submission submission, User user) {
        for (String identifier : candidateStudentIdentifiers(user)) {
            if (identifier.equalsIgnoreCase(submission.studentPrn == null ? "" : submission.studentPrn)) {
                return true;
            }
        }
        return false;
    }

    private static List<Submission> parseSubmissions(String response) {
        List<Submission> submissions = new ArrayList<>();
        if (response == null || response.isBlank() || "[]".equals(response)) {
            return submissions;
        }

        try {
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject object = element.getAsJsonObject();
                Submission submission = new Submission();
                submission.id = readString(object, "id", "");
                submission.studentPrn = readFirstString(object, "student_id", "student_prn", "student_id");
                submission.studentName = readString(object, "student_name", "");
                submission.title = readString(object, "title", "Untitled");
                submission.type = readString(object, "type", "project");
                submission.description = readString(object, "description", "");
                submission.file_url = readString(object, "file_url", "");
                submission.status = readString(object, "status", "pending");
                submission.createdAt = readFirstString(object, "created_at", "submitted_on", "updated_at");
                submission.updatedAt = readFirstString(object, "updated_at", "submitted_on", "created_at");
                submission.user_id = readString(object, "user_id", "");
                submissions.add(submission);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return submissions;
    }

    private static List<Map<String, String>> deriveAchievementsFromApprovedSubmissions(List<Submission> submissions) {
        List<Map<String, String>> achievements = new ArrayList<>();
        for (Submission submission : submissions) {
            if (!submission.isApproved()) {
                continue;
            }
            Map<String, String> row = new LinkedHashMap<>();
            row.put("id", submission.id);
            row.put("title", submission.title);
            row.put("category", submission.type);
            row.put("date", submission.createdAt);
            row.put("certificate_url", submission.file_url);
            achievements.add(row);
        }
        return achievements;
    }

    private static Map<String, String> toSubmissionMap(Submission submission) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("id", submission.id);
        row.put("title", submission.title);
        row.put("type", submission.type);
        row.put("status", submission.status);
        row.put("date", submission.createdAt);
        row.put("description", submission.description);
        row.put("file_url", submission.file_url);
        return row;
    }

    private static String uploadFileToStorage(File file, String folderName) {
        try {
            String remotePath = buildObjectPath(folderName == null ? "anonymous" : folderName, file.getName());
            URL url = new URL(Config.SUPABASE_URL + "/storage/v1/object/" + Config.STORAGE_BUCKET + "/" + remotePath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestProperty("apikey", Config.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + Config.API_KEY);
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            conn.setRequestProperty("Content-Type", contentType);
            conn.setFixedLengthStreamingMode((int) file.length());

            try (OutputStream outputStream = conn.getOutputStream()) {
                Files.copy(file.toPath(), outputStream);
                outputStream.flush();
            }

            int status = conn.getResponseCode();
            if (status >= 200 && status < 300) {
                return Config.STORAGE_PUBLIC_URL + remotePath;
            }
            String error = readResponse(conn, status);
            System.err.println("Storage upload failed: " + status + " " + error);
            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private static String buildObjectPath(String folder, String fileName) {
        String encodedFolder = URLEncoder.encode(folder == null ? "" : folder, StandardCharsets.UTF_8).replace("+", "%20");
        String encodedName = URLEncoder.encode(fileName == null ? "file" : fileName, StandardCharsets.UTF_8).replace("+", "%20");
        return encodedFolder + "/" + encodedName;
    }

    private static String readResponse(HttpURLConnection conn, int statusCode) {
        try (InputStream stream = statusCode >= 200 && statusCode < 300 ? conn.getInputStream() : conn.getErrorStream();
             BufferedReader reader = stream == null ? null : new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            if (reader == null) {
                return null;
            }
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private static boolean isWriteSuccessful(String response) {
        if (response == null) {
            return false;
        }
        try {
            JsonElement element = JsonParser.parseString(response);
            if (element.isJsonArray()) {
                return true;
            }
            if (element.isJsonObject()) {
                return !element.getAsJsonObject().has("code");
            }
        } catch (Exception ignored) {
            return false;
        }
        return false;
    }

    private static String readString(JsonObject object, String key, String fallback) {
        JsonElement element = object.get(key);
        if (element == null || element.isJsonNull()) {
            return fallback;
        }
        String value = element.getAsString();
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String readFirstString(JsonObject object, String firstKey, String secondKey, String thirdKey) {
        String first = readString(object, firstKey, "");
        if (!first.isBlank()) {
            return first;
        }
        String second = readString(object, secondKey, "");
        if (!second.isBlank()) {
            return second;
        }
        return readString(object, thirdKey, "");
    }

    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
