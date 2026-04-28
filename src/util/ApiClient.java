package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Enhanced API Client for Supabase REST API
 * Supports GET, POST, PATCH, DELETE operations
 */
public class ApiClient {

    public static String get(String endpoint) {
        return send("GET", endpoint, null, null);
    }

    public static String post(String endpoint, String body) {
        return send("POST", endpoint, body, null);
    }

    public static String patch(String endpoint, String body) {
        return send("PATCH", endpoint, body, null);
    }

    public static String send(String method, String endpoint, String body, Map<String, String> extraHeaders) {
        try {
            URL url = new URL(Config.SUPABASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod(method);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("apikey", Config.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + Config.API_KEY);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");

            if (extraHeaders != null) {
                for (Map.Entry<String, String> entry : extraHeaders.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (body != null) {
                conn.setDoOutput(true);
                byte[] payload = body.getBytes(StandardCharsets.UTF_8);
                try (OutputStream outputStream = conn.getOutputStream()) {
                    outputStream.write(payload);
                }
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

