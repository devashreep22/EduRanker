package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.DashboardData;
import model.User;
import util.ApiClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DashboardService {

    public static DashboardData loadDashboard(User user) {
        DashboardData data = new DashboardData();

        if (user == null) {
            return data;
        }

        if (user.name != null && !user.name.isBlank()) {
            data.studentName = user.name;
        }
        if (user.className != null && !user.className.isBlank()) {
            data.className = user.className;
        }

        String endpoint = "/rest/v1/users?select=*&prn=eq." + encode(user.prn);
        String response = ApiClient.get(endpoint);

        if (response == null || response.isBlank() || "[]".equals(response)) {
            return data;
        }

        JsonArray array = JsonParser.parseString(response).getAsJsonArray();
        if (array.isEmpty()) {
            return data;
        }

        JsonObject object = array.get(0).getAsJsonObject();
        data.studentName = readString(object, "name", data.studentName);
        data.className = readString(object, "class_name", data.className);
        data.headline = readString(object, "headline", data.headline);
        data.primaryGuideTitle = readString(object, "primary_guide_title", data.primaryGuideTitle);
        data.primaryGuideText = readString(object, "primary_guide_text", data.primaryGuideText);
        data.secondaryGuideTitle = readString(object, "secondary_guide_title", data.secondaryGuideTitle);
        data.secondaryGuideText = readString(object, "secondary_guide_text", data.secondaryGuideText);
        data.rank = readInt(object, "current_rank", data.rank);
        data.totalStudents = readInt(object, "total_students", data.totalStudents);
        data.percentile = readInt(object, "percentile", data.percentile);
        data.academicsProgress = readInt(object, "academics_progress", data.academicsProgress);
        data.codingProgress = readInt(object, "coding_progress", data.codingProgress);
        data.clubsProgress = readInt(object, "clubs_progress", data.clubsProgress);
        data.monthlyProgress = readProgress(object.get("monthly_progress"), data.monthlyProgress);

        return data;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
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

    private static List<Integer> readProgress(JsonElement element, List<Integer> fallback) {
        if (element == null || element.isJsonNull()) {
            return fallback;
        }

        List<Integer> values = new ArrayList<>();

        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement item : array) {
                try {
                    values.add(item.getAsInt());
                } catch (Exception ignored) {
                    return fallback;
                }
            }
        } else {
            String raw = element.getAsString();
            String[] parts = raw.split(",");
            for (String part : parts) {
                try {
                    values.add(Integer.parseInt(part.trim()));
                } catch (NumberFormatException ignored) {
                    return fallback;
                }
            }
        }

        return values.isEmpty() ? fallback : values;
    }
}
