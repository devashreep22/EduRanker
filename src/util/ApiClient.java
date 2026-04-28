package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

            int statusCode = conn.getResponseCode();
            InputStream stream = statusCode >= 200 && statusCode < 300
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            if (stream == null) {
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
