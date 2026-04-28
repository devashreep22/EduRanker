package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Enhanced API Client for Supabase REST API
 * Supports GET, POST, PATCH, DELETE operations
 */
public class ApiClient {

    public static String get(String endpoint) {
        try {
            URL url = new URL(Config.SUPABASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("apikey", Config.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + Config.API_KEY);
            conn.setRequestProperty("Accept", "application/json");

            return handleResponse(conn);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String post(String endpoint, String jsonBody) {
        try {
            URL url = new URL(Config.SUPABASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("apikey", Config.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + Config.API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            return handleResponse(conn);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String patch(String endpoint, String jsonBody) {
        try {
            URL url = new URL(Config.SUPABASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("PATCH");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("apikey", Config.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + Config.API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            return handleResponse(conn);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String delete(String endpoint) {
        try {
            URL url = new URL(Config.SUPABASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("DELETE");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("apikey", Config.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + Config.API_KEY);
            conn.setRequestProperty("Accept", "application/json");

            return handleResponse(conn);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String handleResponse(HttpURLConnection conn) throws Exception {
        int statusCode = conn.getResponseCode();
        InputStream stream = statusCode >= 200 && statusCode < 300
                ? conn.getInputStream()
                : conn.getErrorStream();

        if (stream == null) {
            System.err.println("Error: HTTP " + statusCode);
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        
        if (statusCode >= 400) {
            System.err.println("API Error " + statusCode + ": " + response.toString());
            return null;
        }

        return response.toString();
    }
}

