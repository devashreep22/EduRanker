package service;

import com.google.gson.*;
import model.User;
import util.ApiClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AuthService {

    public static User login(String prn, String password) {

        String endpoint = "/rest/v1/users?select=*&prn=eq." + encode(prn) + "&password=eq." + encode(password);

        String response = ApiClient.get(endpoint);

        if (response == null || response.equals("[]")) {
            return null;
        }

        JsonArray array = JsonParser.parseString(response).getAsJsonArray();
        JsonObject obj = array.get(0).getAsJsonObject();

        User user = new User();
        user.prn = obj.get("prn").getAsString();
        user.role = obj.get("role").getAsString();
        user.name = obj.get("name").getAsString();
        if (obj.has("class_name") && !obj.get("class_name").isJsonNull()) {
            user.className = obj.get("class_name").getAsString();
        }

        return user;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
