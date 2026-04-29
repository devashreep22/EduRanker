import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import util.ApiClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Script to add multiple students to Supabase database
 * Run this to populate your database with test students
 */
public class AddStudentsToSupabase {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("EduRanker - Bulk Student Import Tool");
        System.out.println("========================================\n");
        
        // Add students with PRN 101 to 115
        addStudentsToDB(101, 115);
        
        System.out.println("\n========================================");
        System.out.println("Student import completed!");
        System.out.println("========================================");
    }
    
    public static void addStudentsToDB(int startPrn, int endPrn) {
        String[] classes = {"Computer Engineering", "Mechanical Engineering", "Civil Engineering", "Electronics Engineering", "Electrical Engineering"};
        String[] departments = {"Computer Science", "Mechanical", "Civil", "Electronics", "Electrical"};
        String[] departments_alt = {"CSE", "ME", "CE", "EE", "ECE"};
        
        int addedCount = 0;
        int failedCount = 0;
        
        for (int prn = startPrn; prn <= endPrn; prn++) {
            String prnStr = String.valueOf(prn);
            String name = "Student_" + prn;
            String password = "password" + prn;
            String email = "student" + prn + "@example.com";
            String className = classes[(prn - startPrn) % classes.length];
            String department = departments_alt[(prn - startPrn) % departments_alt.length];
            
            System.out.print("Adding student PRN " + prnStr + " (" + name + ")... ");
            
            // Check if student already exists
            if (studentExists(prnStr)) {
                System.out.println("ALREADY EXISTS (skipped)");
                continue;
            }
            
            // Try to add student
            if (addStudent(prnStr, password, name, email, className, department)) {
                System.out.println("✓ SUCCESS");
                addedCount++;
                
                // Add sample submissions for this student
                addSampleSubmissions(prnStr, name);
                
            } else {
                System.out.println("✗ FAILED");
                failedCount++;
            }
        }
        
        System.out.println("\n" + addedCount + " students added successfully");
        if (failedCount > 0) {
            System.out.println(failedCount + " students failed to add");
        }
    }
    
    private static boolean studentExists(String prn) {
        String endpoint = "/rest/v1/users?select=prn&prn=eq." + encode(prn) + "&limit=1";
        String response = ApiClient.get(endpoint);
        if (response == null) {
            return false;
        }
        try {
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            return array.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    private static boolean addStudent(String prn, String password, String name, String email, String className, String department) {
        try {
            JsonObject payload = new JsonObject();
                payload.addProperty("prn", prn);
                payload.addProperty("password", password);
                payload.addProperty("name", name);
                payload.addProperty("role", "student");
            // Some Supabase schemas may not include class_name/department columns.
            // Send only minimal fields to ensure compatibility.

            Map<String, String> headers = new HashMap<>();
            headers.put("Prefer", "return=representation");

            String response = ApiClient.send("POST", "/rest/v1/users", payload.toString(), headers);
            if (response != null && response.startsWith("API Error")) {
                System.err.println(response);
            }
            return isSuccessful(response);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }
    
    private static void addStudentWithPassword(String prn, String name, String password) {
        try {
            JsonObject payload = new JsonObject();
            payload.addProperty("prn", prn);
            payload.addProperty("name", name);
            payload.addProperty("password", password);

            Map<String, String> headers = new HashMap<>();
            headers.put("Prefer", "return=representation");

            ApiClient.send("POST", "/rest/v1/users", payload.toString(), headers);
        } catch (Exception e) {
            System.err.println("Failed to add student: " + prn);
        }
    }
    
    private static void addSampleSubmissions(String studentPrn, String studentName) {
        // Add 3 sample submissions per student
        String[][] submissions = {
            {"Machine Learning Project", "project", "Built an ML model for classification tasks", "approved"},
            {"Python Certification", "certificate", "Completed Python Advanced Programming course", "approved"},
            {"Web Development Workshop", "workshop", "Attended full-stack web development workshop", "pending"}
        };
        
        for (String[] sub : submissions) {
            addSubmission(studentPrn, studentName, sub[0], sub[1], sub[2], sub[3]);
        }
    }
    
    private static void addSubmission(String studentPrn, String studentName, String title, String type, String description, String status) {
        try {
            JsonObject payload = new JsonObject();
            payload.addProperty("student_prn", studentPrn);
            payload.addProperty("student_name", studentName);
            payload.addProperty("title", title);
            payload.addProperty("type", type);
            payload.addProperty("description", description);
            payload.addProperty("status", status);
            payload.addProperty("file_url", "");
            
            Map<String, String> headers = new HashMap<>();
            headers.put("Prefer", "return=representation");
            
            ApiClient.send("POST", "/rest/v1/submissions", payload.toString(), headers);
            
        } catch (Exception e) {
            // Silent fail for submissions
        }
    }
    
    private static boolean isSuccessful(String response) {
        if (response == null) {
            return false;
        }
        try {
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            return array.size() > 0;
        } catch (Exception e) {
            try {
                JsonObject obj = JsonParser.parseString(response).getAsJsonObject();
                return obj.has("prn");
            } catch (Exception ex) {
                return false;
            }
        }
    }
    
    private static String encode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }
}
