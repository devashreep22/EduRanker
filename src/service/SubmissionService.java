package service;

<<<<<<< HEAD
import com.google.gson.Gson;
=======
>>>>>>> 170c2e6 (Add Modern Dashboard with Supabase Backend Integration)
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
<<<<<<< HEAD
import model.Submission;
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
import java.util.ArrayList;
import java.util.List;

public class SubmissionService {
    private static final Gson GSON = new Gson();

    public static List<Submission> fetchSubmissions(String userId) {
        try {
            String query = Config.SUPABASE_URL + "/rest/v1/submissions?select=*";
            // Temporarily remove user_id filtering since column doesn't exist
            // if (userId != null && !userId.isBlank()) {
            //     query += "&user_id=eq." + URLEncoder.encode(userId, StandardCharsets.UTF_8);
            // }
            URL url = new URL(query);
            HttpURLConnection conn = createConnection(url, "GET");

            int status = conn.getResponseCode();
            String response = readResponse(conn, status);
            if (response == null || response.isBlank()) {
                return new ArrayList<>();
            }

            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            List<Submission> items = new ArrayList<>();
            for (JsonElement element : array) {
                JsonObject object = element.getAsJsonObject();
                Submission submission = new Submission();
                submission.title = readString(object, "title", "Untitled");
                submission.type = readString(object, "type", "Unknown");
                submission.file_url = readString(object, "file_url", "");
                submission.status = readString(object, "status", "pending");
                submission.user_id = readString(object, "user_id", ""); // Add this line
                items.add(submission);
            }
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static boolean uploadSubmission(Submission submission, String userId, File file) {
        try {
            // Temporarily don't set user_id since column doesn't exist
            // if (userId != null && !userId.isBlank()) {
            //     submission.user_id = userId;
            // }
            // Skip file upload for now since bucket doesn't exist
            // if (file != null && file.exists()) {
            //     String fileUrl = uploadFileToStorage(file, userId);
            //     if (fileUrl == null) {
            //         return false;
            //     }
            //     submission.file_url = fileUrl;
            // }

            // Create a copy without user_id for the payload
            JsonObject jsonObj = new JsonObject();
            jsonObj.addProperty("title", submission.title);
            jsonObj.addProperty("type", submission.type);
            jsonObj.addProperty("file_url", submission.file_url);
            jsonObj.addProperty("status", submission.status);
            // jsonObj.addProperty("user_id", submission.user_id); // Skip for now

            URL url = new URL(Config.SUPABASE_URL + "/rest/v1/submissions");
            HttpURLConnection conn = createConnection(url, "POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Prefer", "return=representation");

            String payload = jsonObj.toString();
            try (OutputStream outputStream = conn.getOutputStream()) {
                outputStream.write(payload.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }

            int status = conn.getResponseCode();
            if (status >= 200 && status < 300) {
                return true;
            }
            String error = readResponse(conn, status);
            System.err.println("Submission upload failed: " + status + " " + error);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String uploadFileToStorage(File file, String userId) {
        try {
            String remotePath = buildObjectPath(userId == null ? "anonymous" : userId, file.getName());
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String buildObjectPath(String folder, String fileName) {
        String encodedFolder = URLEncoder.encode(folder == null ? "" : folder, StandardCharsets.UTF_8).replace("+", "%20");
        String encodedName = URLEncoder.encode(fileName == null ? "file" : fileName, StandardCharsets.UTF_8).replace("+", "%20");
        return encodedFolder + "/" + encodedName;
    }

    private static HttpURLConnection createConnection(URL url, String method) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setRequestProperty("apikey", Config.API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + Config.API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        return conn;
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
=======
import util.ApiClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SubmissionService - Handles all submission and achievement related API calls
 * Fetches data from the 'submissions' and 'achievements' tables
 */
public class SubmissionService {

    /**
     * Get all submissions for a user
     * @param prn Student PRN
     * @return List of submissions
     */
    public static List<Map<String, String>> getSubmissions(String prn) {
        List<Map<String, String>> submissions = new ArrayList<>();

        String endpoint = "/rest/v1/submissions?select=*&student_prn=eq." + encode(prn) + "&order=created_at.desc";
        String response = ApiClient.get(endpoint);

        if (response == null || response.equals("[]")) {
            return submissions;
        }

        try {
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                Map<String, String> submission = new HashMap<>();
                
                submission.put("id", readString(obj, "id", ""));
                submission.put("title", readString(obj, "title", ""));
                submission.put("type", readString(obj, "type", ""));
                submission.put("status", readString(obj, "status", ""));
                submission.put("date", readString(obj, "created_at", ""));
                submission.put("description", readString(obj, "description", ""));
                
                submissions.add(submission);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return submissions;
    }

    /**
     * Get submission statistics for a user
     * @param prn Student PRN
     * @return Map with counts of different statuses
     */
    public static Map<String, Integer> getSubmissionStats(String prn) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", 0);
        stats.put("approved", 0);
        stats.put("pending", 0);
        stats.put("rejected", 0);

        List<Map<String, String>> submissions = getSubmissions(prn);
        stats.put("total", submissions.size());

        for (Map<String, String> submission : submissions) {
            String status = submission.get("status").toLowerCase();
            switch (status) {
                case "approved":
                    stats.put("approved", stats.get("approved") + 1);
                    break;
                case "pending":
                    stats.put("pending", stats.get("pending") + 1);
                    break;
                case "rejected":
                    stats.put("rejected", stats.get("rejected") + 1);
                    break;
            }
        }

        return stats;
    }

    /**
     * Get achievements for a user
     * @param prn Student PRN
     * @return List of achievements
     */
    public static List<Map<String, String>> getAchievements(String prn) {
        List<Map<String, String>> achievements = new ArrayList<>();

        String endpoint = "/rest/v1/achievements?select=*&student_prn=eq." + encode(prn);
        String response = ApiClient.get(endpoint);

        if (response == null || response.equals("[]")) {
            return achievements;
        }

        try {
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                Map<String, String> achievement = new HashMap<>();
                
                achievement.put("id", readString(obj, "id", ""));
                achievement.put("title", readString(obj, "title", ""));
                achievement.put("category", readString(obj, "category", ""));
                achievement.put("date", readString(obj, "achieved_date", ""));
                achievement.put("certificate_url", readString(obj, "certificate_url", ""));
                
                achievements.add(achievement);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return achievements;
    }

    /**
     * Get achievement summary counts
     * @param prn Student PRN
     * @return Map with project, certificate, and workshop counts
     */
    public static Map<String, Integer> getAchievementCounts(String prn) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("projects", 0);
        counts.put("certificates", 0);
        counts.put("workshops", 0);

        List<Map<String, String>> achievements = getAchievements(prn);
        
        for (Map<String, String> achievement : achievements) {
            String category = achievement.get("category").toLowerCase();
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
            }
        }

        return counts;
    }

    /**
     * Create a new submission
     * @param prn Student PRN
     * @param title Submission title
     * @param type Submission type
     * @param description Description
     * @return Response from API
     */
    public static String createSubmission(String prn, String title, String type, String description) {
        String json = String.format(
                "{\"student_prn\":\"%s\",\"title\":\"%s\",\"type\":\"%s\",\"status\":\"pending\",\"description\":\"%s\"}",
                prn, escapeJson(title), escapeJson(type), escapeJson(description)
        );
        return ApiClient.post("/rest/v1/submissions", json);
    }

    /**
     * Update submission status
     * @param submissionId Submission ID
     * @param status New status
     * @return Response from API
     */
    public static String updateSubmissionStatus(String submissionId, String status) {
        String json = String.format("{\"status\":\"%s\"}", status);
        String endpoint = "/rest/v1/submissions?id=eq." + encode(submissionId);
        return ApiClient.patch(endpoint, json);
>>>>>>> 170c2e6 (Add Modern Dashboard with Supabase Backend Integration)
    }

    private static String readString(JsonObject object, String key, String fallback) {
        JsonElement element = object.get(key);
        if (element == null || element.isJsonNull()) {
            return fallback;
        }
        String value = element.getAsString();
        return value == null || value.isBlank() ? fallback : value;
    }
<<<<<<< HEAD
=======

    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private static String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
>>>>>>> 170c2e6 (Add Modern Dashboard with Supabase Backend Integration)
}
