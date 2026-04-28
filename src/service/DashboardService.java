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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardService {

    public static DashboardData loadDashboard(User user) {
        DashboardData data = new DashboardData();
        if (user == null) {
            return data;
        }

        data.studentName = fallback(user.name, data.studentName);
        data.className = fallback(user.className, data.className);

        JsonObject profile = fetchUserProfile(user.prn);
        if (profile != null) {
            data.studentName = readString(profile, "name", data.studentName);
            data.className = readString(profile, "class_name", data.className);
            data.headline = readString(profile, "headline", "Track your verified progress");
            data.primaryGuideTitle = readString(profile, "primary_guide_title", "Approved projects");
            data.primaryGuideText = readString(profile, "primary_guide_text", "Projects and certificates move here after teacher approval.");
            data.secondaryGuideTitle = readString(profile, "secondary_guide_title", "Next step");
            data.secondaryGuideText = readString(profile, "secondary_guide_text", "Upload your latest work and wait for faculty review.");
            data.totalStudents = readInt(profile, "total_students", data.totalStudents);
        }

        Map<String, Integer> submissionStats = SubmissionService.getSubmissionStats(user);
        Map<String, Integer> achievementCounts = SubmissionService.getAchievementCounts(user);
        int approvedProjects = achievementCounts.getOrDefault("projects", 0);
        int approvedCertificates = achievementCounts.getOrDefault("certificates", 0);
        int approvedWorkshops = achievementCounts.getOrDefault("workshops", 0);
        int totalApproved = approvedProjects + approvedCertificates + approvedWorkshops;

        int derivedRank = Math.max(1, data.totalStudents - Math.max(1, totalApproved * 3));
        data.rank = profile == null ? derivedRank : readInt(profile, "current_rank", derivedRank);
        data.percentile = profile == null
                ? Math.min(99, 45 + totalApproved * 5)
                : readInt(profile, "percentile", Math.min(99, 45 + totalApproved * 5));

        data.academicsProgress = profile == null
                ? Math.min(100, 40 + approvedCertificates * 10)
                : readInt(profile, "academics_progress", Math.min(100, 40 + approvedCertificates * 10));
        data.codingProgress = profile == null
                ? Math.min(100, 35 + approvedProjects * 12)
                : readInt(profile, "coding_progress", Math.min(100, 35 + approvedProjects * 12));
        data.clubsProgress = profile == null
                ? Math.min(100, 25 + approvedWorkshops * 14)
                : readInt(profile, "clubs_progress", Math.min(100, 25 + approvedWorkshops * 14));

        List<Integer> derivedMonthlyProgress = buildMonthlyProgress(user);
        data.monthlyProgress = profile == null
                ? derivedMonthlyProgress
                : readProgress(profile.get("monthly_progress"), derivedMonthlyProgress);

        if (totalApproved == 0 && submissionStats.getOrDefault("pending", 0) > 0) {
            data.primaryGuideText = "You already have pending uploads. Once a teacher approves them, your rank and progress will move up automatically.";
        } else if (totalApproved > 0) {
            data.primaryGuideText = "Approved portfolio items: " + totalApproved + " across projects, certificates, and workshops.";
            data.secondaryGuideText = "Current rank improves as more verified work is approved.";
        }

        if (submissionStats.getOrDefault("rejected", 0) > 0) {
            data.secondaryGuideTitle = "Review feedback";
            data.secondaryGuideText = "You have " + submissionStats.get("rejected") + " rejected submission(s). Update and re-upload improved work.";
        }

        return data;
    }

    private static JsonObject fetchUserProfile(String prn) {
        if (prn == null || prn.isBlank()) {
            return null;
        }
        String endpoint = "/rest/v1/users?select=*&prn=eq." + encode(prn) + "&limit=1";
        String response = ApiClient.get(endpoint);
        if (response == null || response.isBlank() || "[]".equals(response)) {
            return null;
        }
        try {
            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            return array.isEmpty() ? null : array.get(0).getAsJsonObject();
        } catch (Exception ignored) {
            return null;
        }
    }

    private static List<Integer> buildMonthlyProgress(User user) {
        List<Integer> monthly = new ArrayList<>();
        Map<String, Integer> perMonth = new java.util.LinkedHashMap<>();
        LocalDate now = LocalDate.now();
        for (int index = 5; index >= 0; index--) {
            LocalDate month = now.minusMonths(index);
            perMonth.put(month.getYear() + "-" + month.getMonthValue(), 0);
        }

        for (Map<String, String> achievement : SubmissionService.getAchievements(user)) {
            String rawDate = achievement.getOrDefault("date", "");
            if (rawDate == null || rawDate.isBlank()) {
                continue;
            }
            String monthKey = rawDate.length() >= 7 ? rawDate.substring(0, 7) : rawDate;
            if (perMonth.containsKey(monthKey)) {
                perMonth.put(monthKey, perMonth.get(monthKey) + 1);
            }
        }

        for (Integer value : perMonth.values()) {
            monthly.add(value * 10 + 10);
        }
        return monthly;
    }

    private static String fallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
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
        try {
            if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement item : array) {
                    values.add(item.getAsInt());
                }
            } else {
                String[] parts = element.getAsString().split(",");
                for (String part : parts) {
                    values.add(Integer.parseInt(part.trim()));
                }
            }
        } catch (Exception ignored) {
            return fallback;
        }

        return values.isEmpty() ? fallback : values;
    }
}
