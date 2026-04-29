package util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enhanced API Client for Supabase REST API
 * Supports GET, POST, PATCH, DELETE operations
 */
public class ApiClient {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();

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
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(Config.SUPABASE_URL + endpoint))
                    .header("apikey", Config.API_KEY)
                    .header("Authorization", "Bearer " + Config.API_KEY)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json");

            if (extraHeaders != null) {
                for (Map.Entry<String, String> entry : extraHeaders.entrySet()) {
                    builder.header(entry.getKey(), entry.getValue());
                }
            }

            HttpRequest request = builder.method(
                    method,
                    body == null
                            ? HttpRequest.BodyPublishers.noBody()
                            : HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)
            ).build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return handleResponse(response.statusCode(), response.body());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String delete(String endpoint) {
        return send("DELETE", endpoint, null, null);
    }

    public static String uploadFile(String endpoint, File file, Map<String, String> headers) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(Config.SUPABASE_URL + endpoint).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=---Boundary");

            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(("---Boundary\r\nContent-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n\r\n").getBytes());
                Files.copy(file.toPath(), outputStream);
                outputStream.write("\r\n---Boundary--\r\n".getBytes());
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    return reader.lines().collect(Collectors.joining());
                }
            } else {
                System.err.println("File upload failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error during file upload: " + e.getMessage());
        }
        return null;
    }

    private static String handleResponse(int statusCode, String body) {
        if (body == null) {
            System.err.println("Error: HTTP " + statusCode);
            return null;
        }
        
        if (statusCode >= 400) {
            System.err.println("API Error " + statusCode + ": " + body);
            return null;
        }

        return body;
    }
}

