import util.ApiClient;

public class TestApi {
    public static void main(String[] args) {

        String response = ApiClient.get("/rest/v1/users");

        System.out.println(response);
    }
}