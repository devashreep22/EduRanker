package service;

import util.ApiClient;
import util.Config;

/**
 * Backend Connection Test
 * Verifies Supabase connection and API endpoints
 */
public class BackendConnectionTest {

    public static void main(String[] args) {
        System.out.println("🔗 Testing Supabase Backend Connection...\n");
        
        testConnection();
        testUserEndpoint();
        testSubmissionsEndpoint();
    }

    private static void testConnection() {
        System.out.println("1️⃣  Testing Basic Connection:");
        System.out.println("   Supabase URL: " + Config.SUPABASE_URL);
        System.out.println("   API Key: " + Config.API_KEY.substring(0, 20) + "...");
        System.out.println("   ✅ Configuration loaded\n");
    }

    private static void testUserEndpoint() {
        System.out.println("2️⃣  Testing Users Endpoint:");
        String endpoint = "/rest/v1/users?select=prn,name,role&limit=5";
        System.out.println("   GET " + endpoint);
        
        String response = ApiClient.get(endpoint);
        
        if (response != null && !response.isEmpty()) {
            System.out.println("   ✅ Success! Response: " + response.substring(0, Math.min(100, response.length())) + "...\n");
        } else {
            System.out.println("   ❌ Failed - No response\n");
        }
    }

    private static void testSubmissionsEndpoint() {
        System.out.println("3️⃣  Testing Submissions Endpoint:");
        String endpoint = "/rest/v1/submissions?select=*&limit=5";
        System.out.println("   GET " + endpoint);
        
        String response = ApiClient.get(endpoint);
        
        if (response != null && !response.isEmpty()) {
            System.out.println("   ✅ Success! Response: " + response.substring(0, Math.min(100, response.length())) + "...\n");
        } else {
            System.out.println("   ❌ Failed - No response\n");
        }
    }

    public static void testUserLogin(String prn, String password) {
        System.out.println("4️⃣  Testing User Login:");
        System.out.println("   PRN: " + prn);
        
        model.User user = AuthService.login(prn, password);
        
        if (user != null) {
            System.out.println("   ✅ Login Success!");
            System.out.println("      Name: " + user.name);
            System.out.println("      Role: " + user.role);
            System.out.println("      Class: " + user.className + "\n");
        } else {
            System.out.println("   ❌ Login Failed\n");
        }
    }
}
