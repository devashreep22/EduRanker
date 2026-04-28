package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Submission;
import util.Config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SubmissionService {
    private static final Gson GSON = new Gson();

    public static List<Submission> fetchSubmissions() {
        try {
            URL url = new URL(Config.SUPABASE_URL + "/rest/v1/submissions?select=*");
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
                items.add(submission);
            }
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static boolean uploadSubmission(Submission submission) {
        try {
            URL url = new URL(Config.SUPABASE_URL + "/rest/v1/submissions");
            HttpURLConnection conn = createConnection(url, "POST");
            conn.setDoOutput(true);

            String payload = GSON.toJson(submission);
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
    }

    private static String readString(JsonObject object, String key, String fallback) {
        JsonElement element = object.get(key);
        if (element == null || element.isJsonNull()) {
            return fallback;
        }
        String value = element.getAsString();
        return value == null || value.isBlank() ? fallback : value;
    }
}
