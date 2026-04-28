package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.OperationResult;
import model.Submission;
import model.TeacherAssignmentRecord;
import model.TeacherClassRecord;
import model.TeacherDashboardData;
import model.TeacherNoticeRecord;
import model.TeacherStudentRecord;
import model.TeacherSubmissionReviewRecord;
import model.User;
import util.ApiClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TeacherDashboardService {

    public static TeacherDashboardData loadDashboard(String preferredTeacherPrn) {
        TeacherDashboardData data = new TeacherDashboardData();

        JsonObject teacher = fetchTeacher(preferredTeacherPrn);
        if (teacher == null) {
            data.sourceMessage = "Teacher profile not found in Supabase";
            return data;
        }

        data.teacherPrn = readString(teacher, "prn", "");
        data.teacherName = readString(teacher, "name", "Teacher");
        data.department = readString(teacher, "department", "Department");
        data.profileLoaded = true;
        data.sourceMessage = "Connected to Supabase";

        data.classRecords = fetchClasses(data.teacherPrn);
        data.studentRecords = fetchStudents(data.classRecords);
        data.assignmentRecords = fetchAssignments(data.teacherPrn);
        data.noticeRecords = fetchNotices(data.teacherPrn);
        data.reviewRecords = fetchReviewQueue(data.studentRecords);
        data.anySupabaseDataLoaded = true;
        return data;
    }

    public static OperationResult saveAttendance(String teacherPrn, String className, String subject, List<TeacherStudentRecord> students) {
        if (teacherPrn == null || teacherPrn.isBlank()) {
            return OperationResult.failure("No teacher profile found in Supabase");
        }

        JsonArray payload = new JsonArray();
        for (TeacherStudentRecord student : students) {
            JsonObject row = new JsonObject();
            row.addProperty("teacher_prn", teacherPrn);
            row.addProperty("class_name", className);
            row.addProperty("subject", subject);
            row.addProperty("student_id", student.id);
            row.addProperty("student_name", student.name);
            row.addProperty("present", student.present);
            row.addProperty("saved_on", LocalDate.now().toString());
            payload.add(row);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Prefer", "return=representation");
        String response = ApiClient.send("POST", "/rest/v1/attendance_records", payload.toString(), headers);
        return isWriteSuccessful(response)
                ? OperationResult.success("Attendance saved")
                : OperationResult.failure("Could not save attendance. Check the attendance_records table.");
    }

    public static OperationResult saveMarks(String teacherPrn, String className, String subject, List<TeacherStudentRecord> students) {
        if (teacherPrn == null || teacherPrn.isBlank()) {
            return OperationResult.failure("No teacher profile found in Supabase");
        }

        JsonArray payload = new JsonArray();
        for (TeacherStudentRecord student : students) {
            JsonObject row = new JsonObject();
            row.addProperty("teacher_prn", teacherPrn);
            row.addProperty("class_name", className);
            row.addProperty("subject", subject);
            row.addProperty("student_id", student.id);
            row.addProperty("student_name", student.name);
            row.addProperty("assignment_marks", student.assignmentMarks);
            row.addProperty("exam_marks", student.examMarks);
            row.addProperty("saved_on", LocalDate.now().toString());
            payload.add(row);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Prefer", "return=representation");
        String response = ApiClient.send("POST", "/rest/v1/marks_records", payload.toString(), headers);
        return isWriteSuccessful(response)
                ? OperationResult.success("Marks saved")
                : OperationResult.failure("Could not save marks. Check the marks_records table.");
    }

    public static OperationResult postAssignment(String teacherPrn, String title, String description) {
        if (teacherPrn == null || teacherPrn.isBlank()) {
            return OperationResult.failure("No teacher profile found in Supabase");
        }

        JsonObject row = new JsonObject();
        row.addProperty("teacher_prn", teacherPrn);
        row.addProperty("title", title);
        row.addProperty("description", description);
        row.addProperty("due_date", LocalDate.now().plusDays(7).toString());

        Map<String, String> headers = new HashMap<>();
        headers.put("Prefer", "return=representation");
        String response = ApiClient.send("POST", "/rest/v1/assignments", row.toString(), headers);
        return isWriteSuccessful(response)
                ? OperationResult.success("Assignment posted")
                : OperationResult.failure("Could not post assignment. Check the assignments table.");
    }

    public static OperationResult postNotice(String teacherPrn, String text) {
        if (teacherPrn == null || teacherPrn.isBlank()) {
            return OperationResult.failure("No teacher profile found in Supabase");
        }

        JsonObject row = new JsonObject();
        row.addProperty("teacher_prn", teacherPrn);
        row.addProperty("notice_text", text);
        row.addProperty("posted_on", LocalDate.now().toString());

        Map<String, String> headers = new HashMap<>();
        headers.put("Prefer", "return=representation");
        String response = ApiClient.send("POST", "/rest/v1/notices", row.toString(), headers);
        return isWriteSuccessful(response)
                ? OperationResult.success("Notice posted")
                : OperationResult.failure("Could not post notice. Check the notices table.");
    }

    public static OperationResult updatePassword(String teacherPrn, String newPassword, String confirmPassword) {
        if (teacherPrn == null || teacherPrn.isBlank()) {
            return OperationResult.failure("No teacher profile found in Supabase");
        }
        if (newPassword == null || newPassword.isBlank()) {
            return OperationResult.failure("Enter a new password");
        }
        if (!newPassword.equals(confirmPassword)) {
            return OperationResult.failure("New password and confirm password do not match");
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("password", newPassword);

        Map<String, String> headers = new HashMap<>();
        headers.put("Prefer", "return=representation");
        String response = ApiClient.send("PATCH",
                "/rest/v1/users?prn=eq." + encode(teacherPrn),
                payload.toString(),
                headers);
        return isWriteSuccessful(response)
                ? OperationResult.success("Password updated")
                : OperationResult.failure("Could not update password in Supabase");
    }

    public static OperationResult reviewSubmission(TeacherSubmissionReviewRecord review, boolean approve, int totalStudents) {
        if (review == null || review.submissionId == null || review.submissionId.isBlank()) {
            return OperationResult.failure("Select a submission first");
        }

        String response = SubmissionService.updateSubmissionStatus(review.submissionId, approve ? "approved" : "rejected");
        if (!isWriteSuccessful(response)) {
            return OperationResult.failure("Could not update submission status in Supabase");
        }

        if (approve) {
            SubmissionService.syncAchievementForApprovedSubmission(review);
        } else {
            SubmissionService.removeAchievementForSubmission(review);
        }
        SubmissionService.refreshStudentMetrics(review.studentPrn, totalStudents);

        return OperationResult.success(approve
                ? "Submission approved and student dashboard updated"
                : "Submission rejected and student dashboard updated");
    }

    private static JsonObject fetchTeacher(String preferredTeacherPrn) {
        String endpoint;
        if (preferredTeacherPrn != null && !preferredTeacherPrn.isBlank()) {
            endpoint = "/rest/v1/users?select=prn,name,department,role&prn=eq." + encode(preferredTeacherPrn) + "&limit=1";
        } else {
            endpoint = "/rest/v1/users?select=prn,name,department,role&role=eq.teacher&limit=1";
        }

        JsonArray array = parseArray(ApiClient.get(endpoint));
        if (array.isEmpty()) {
            return null;
        }
        JsonObject object = array.get(0).getAsJsonObject();
        String role = readString(object, "role", "");
        return role.equalsIgnoreCase("teacher") ? object : null;
    }

    private static List<TeacherClassRecord> fetchClasses(String teacherPrn) {
        List<TeacherClassRecord> records = new ArrayList<>();
        String endpoint = teacherPrn == null || teacherPrn.isBlank()
                ? "/rest/v1/teacher_classes?select=class_name,subject,semester"
                : "/rest/v1/teacher_classes?select=class_name,subject,semester&teacher_prn=eq." + encode(teacherPrn);

        JsonArray array = parseArray(ApiClient.get(endpoint));
        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();
            TeacherClassRecord record = new TeacherClassRecord();
            record.className = readString(object, "class_name", "");
            record.subject = readString(object, "subject", "");
            record.semester = readString(object, "semester", "");
            if (!record.className.isBlank()) {
                records.add(record);
            }
        }
        return records;
    }

    private static List<TeacherStudentRecord> fetchStudents(List<TeacherClassRecord> classes) {
        List<TeacherStudentRecord> records = new ArrayList<>();
        JsonArray array = parseArray(ApiClient.get("/rest/v1/users?select=prn,id,name,class_name,attendance_percentage,assignment_marks,exam_marks,role&role=eq.student"));
        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();
            TeacherStudentRecord record = new TeacherStudentRecord();
            record.id = readString(object, "prn", readString(object, "id", ""));
            record.name = readString(object, "name", "");
            record.className = readString(object, "class_name", "");
            record.attendancePercentage = readInt(object, "attendance_percentage", 0);
            record.assignmentMarks = readInt(object, "assignment_marks", 0);
            record.examMarks = readInt(object, "exam_marks", 0);
            record.present = true;

            if (!classes.isEmpty() && !belongsToClasses(record.className, classes)) {
                continue;
            }
            records.add(record);
        }
        return records;
    }

    private static List<TeacherAssignmentRecord> fetchAssignments(String teacherPrn) {
        List<TeacherAssignmentRecord> records = new ArrayList<>();
        String endpoint = teacherPrn == null || teacherPrn.isBlank()
                ? "/rest/v1/assignments?select=title,description,due_date&order=due_date.desc"
                : "/rest/v1/assignments?select=title,description,due_date&teacher_prn=eq." + encode(teacherPrn) + "&order=due_date.desc";

        JsonArray array = parseArray(ApiClient.get(endpoint));
        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();
            TeacherAssignmentRecord record = new TeacherAssignmentRecord();
            record.title = readString(object, "title", "");
            record.description = readString(object, "description", "");
            record.dueDate = readString(object, "due_date", "");
            if (!record.title.isBlank()) {
                records.add(record);
            }
        }
        return records;
    }

    private static List<TeacherNoticeRecord> fetchNotices(String teacherPrn) {
        List<TeacherNoticeRecord> records = new ArrayList<>();
        String endpoint = teacherPrn == null || teacherPrn.isBlank()
                ? "/rest/v1/notices?select=posted_on,notice_text&order=posted_on.desc"
                : "/rest/v1/notices?select=posted_on,notice_text&teacher_prn=eq." + encode(teacherPrn) + "&order=posted_on.desc";

        JsonArray array = parseArray(ApiClient.get(endpoint));
        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();
            TeacherNoticeRecord record = new TeacherNoticeRecord();
            record.date = readString(object, "posted_on", "");
            record.text = readString(object, "notice_text", "");
            if (!record.text.isBlank()) {
                records.add(record);
            }
        }
        return records;
    }

    private static List<TeacherSubmissionReviewRecord> fetchReviewQueue(List<TeacherStudentRecord> students) {
        Map<String, User> usersByIdentifier = buildUserLookup(students);
        List<Submission> submissions = SubmissionService.fetchAllSubmissions();
        List<TeacherSubmissionReviewRecord> reviews = SubmissionService.buildReviewRecords(submissions, usersByIdentifier);
        List<TeacherSubmissionReviewRecord> filtered = new ArrayList<>();
        for (TeacherSubmissionReviewRecord review : reviews) {
            if (review.type == null) {
                continue;
            }
            String type = review.type.toLowerCase();
            if (!type.equals("project") && !type.equals("certificate") && !type.equals("workshop")) {
                continue;
            }
            filtered.add(review);
        }
        return filtered;
    }

    private static Map<String, User> buildUserLookup(List<TeacherStudentRecord> students) {
        Map<String, User> lookup = new LinkedHashMap<>();
        for (TeacherStudentRecord student : students) {
            User user = new User();
            user.prn = student.id;
            user.id = student.id;
            user.name = student.name;
            user.className = student.className;
            lookup.put(student.id, user);
        }

        JsonArray users = parseArray(ApiClient.get("/rest/v1/users?select=id,prn,name,class_name,role&role=eq.student"));
        for (JsonElement element : users) {
            JsonObject object = element.getAsJsonObject();
            User user = new User();
            user.id = readString(object, "id", "");
            user.prn = readString(object, "prn", "");
            user.name = readString(object, "name", "");
            user.className = readString(object, "class_name", "");
            if (!user.id.isBlank()) {
                lookup.put(user.id, user);
            }
            if (!user.prn.isBlank()) {
                lookup.put(user.prn, user);
            }
        }
        return lookup;
    }

    private static boolean belongsToClasses(String className, List<TeacherClassRecord> classes) {
        if (className == null || className.isBlank()) {
            return false;
        }
        for (TeacherClassRecord record : classes) {
            if (className.equalsIgnoreCase(record.className)) {
                return true;
            }
        }
        return false;
    }

    private static JsonArray parseArray(String response) {
        if (response == null || response.isBlank()) {
            return new JsonArray();
        }
        try {
            JsonElement element = JsonParser.parseString(response);
            return element.isJsonArray() ? element.getAsJsonArray() : new JsonArray();
        } catch (Exception ignored) {
            return new JsonArray();
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

    private static int readInt(JsonObject object, String key, int fallback) {
        JsonElement element = object.get(key);
        if (element == null || element.isJsonNull()) {
            return fallback;
        }
        try {
            return element.getAsInt();
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
