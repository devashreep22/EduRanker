package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.OperationResult;
import model.TeacherAssignmentRecord;
import model.TeacherClassRecord;
import model.TeacherDashboardData;
import model.TeacherNoticeRecord;
import model.TeacherStudentRecord;
import util.ApiClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardService {

    public static TeacherDashboardData loadDashboard(String preferredTeacherPrn) {
        TeacherDashboardData data = new TeacherDashboardData();

        JsonObject teacher = fetchTeacher(preferredTeacherPrn);
        if (teacher != null) {
            data.teacherPrn = readString(teacher, "prn", "");
            data.teacherName = readString(teacher, "name", data.teacherName);
            data.department = readString(teacher, "department", "Computer Science");
            data.profileLoaded = true;
            data.anySupabaseDataLoaded = true;
            data.sourceMessage = "Connected to Supabase";
        }

        List<TeacherClassRecord> classes = fetchClasses(data.teacherPrn);
        if (!classes.isEmpty()) {
            data.classRecords = classes;
            data.anySupabaseDataLoaded = true;
        }

        List<TeacherStudentRecord> students = fetchStudents(data.classRecords);
        if (!students.isEmpty()) {
            data.studentRecords = students;
            data.anySupabaseDataLoaded = true;
        }

        List<TeacherAssignmentRecord> assignments = fetchAssignments(data.teacherPrn);
        if (!assignments.isEmpty()) {
            data.assignmentRecords = assignments;
            data.anySupabaseDataLoaded = true;
        }

        List<TeacherNoticeRecord> notices = fetchNotices(data.teacherPrn);
        if (!notices.isEmpty()) {
            data.noticeRecords = notices;
            data.anySupabaseDataLoaded = true;
        }

        if (!data.anySupabaseDataLoaded) {
            data.sourceMessage = "Supabase tables not found or empty, using sample data";
        }

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

        String response = ApiClient.post("/rest/v1/attendance_records", payload.toString());
        return isWriteSuccessful(response)
                ? OperationResult.success("Attendance saved to Supabase")
                : OperationResult.failure("Create table attendance_records to save attendance");
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

        String response = ApiClient.post("/rest/v1/marks_records", payload.toString());
        return isWriteSuccessful(response)
                ? OperationResult.success("Marks saved to Supabase")
                : OperationResult.failure("Create table marks_records to save marks");
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

        String response = ApiClient.post("/rest/v1/assignments", "[" + row + "]");
        return isWriteSuccessful(response)
                ? OperationResult.success("Assignment posted to Supabase")
                : OperationResult.failure("Create table assignments to post assignments");
    }

    public static OperationResult postNotice(String teacherPrn, String text) {
        if (teacherPrn == null || teacherPrn.isBlank()) {
            return OperationResult.failure("No teacher profile found in Supabase");
        }

        JsonObject row = new JsonObject();
        row.addProperty("teacher_prn", teacherPrn);
        row.addProperty("notice_text", text);
        row.addProperty("posted_on", LocalDate.now().toString());

        String response = ApiClient.post("/rest/v1/notices", "[" + row + "]");
        return isWriteSuccessful(response)
                ? OperationResult.success("Notice posted to Supabase")
                : OperationResult.failure("Create table notices to post notices");
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

        String endpoint = "/rest/v1/users?prn=eq." + encode(teacherPrn);
        String response = ApiClient.patch(endpoint, payload.toString());
        return isWriteSuccessful(response)
                ? OperationResult.success("Password updated in Supabase")
                : OperationResult.failure("Could not update password in users table");
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

        JsonObject teacher = array.get(0).getAsJsonObject();
        if (teacher.has("role") && !teacher.get("role").isJsonNull()) {
            String role = teacher.get("role").getAsString();
            if (!role.equalsIgnoreCase("teacher") && preferredTeacherPrn == null) {
                return null;
            }
        }
        return teacher;
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
        JsonArray array = parseArray(ApiClient.get("/rest/v1/students?select=*"));
        if (array.isEmpty()) {
            array = parseArray(ApiClient.get("/rest/v1/users?select=prn,name,class_name,attendance_percentage,assignment_marks,exam_marks,role&role=eq.student"));
        }

        for (JsonElement element : array) {
            JsonObject object = element.getAsJsonObject();
            TeacherStudentRecord record = new TeacherStudentRecord();
            record.id = readString(object, "prn", readString(object, "student_id", ""));
            record.name = readString(object, "name", "");
            record.className = readString(object, "class_name", readString(object, "class", ""));
            record.attendancePercentage = readInt(object, "attendance_percentage", 0);
            record.assignmentMarks = readInt(object, "assignment_marks", 0);
            record.examMarks = readInt(object, "exam_marks", 0);
            record.present = true;

            if (record.id.isBlank()) {
                continue;
            }
            if (!classes.isEmpty() && !belongsToClasses(record.className, classes)) {
                continue;
            }
            records.add(record);
        }
        return records;
    }

    private static boolean belongsToClasses(String className, List<TeacherClassRecord> classes) {
        for (TeacherClassRecord record : classes) {
            if (record.className != null && record.className.equalsIgnoreCase(className)) {
                return true;
            }
        }
        return false;
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
                JsonObject object = element.getAsJsonObject();
                return !object.has("code");
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
