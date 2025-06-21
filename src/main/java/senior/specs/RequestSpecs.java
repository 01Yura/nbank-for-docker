package senior.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import senior.configs.Config;
import senior.configs.CustomLoggingFilter;
import senior.models.LoginUserRequestModel;
import senior.requests.skeleton.requesters.CrudRequester;
import senior.requests.skeleton.requesters.Endpoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestSpecs {
    private static Map<String, String> authHeaders = new HashMap<>();

    static {
        authHeaders.put("admin", "Basic YWRtaW46YWRtaW4=");
    }

    private RequestSpecs() {
    }


    private static RequestSpecBuilder defaultRequestSpecBuilder() {
        return new RequestSpecBuilder()
                .setBaseUri(Config.getProperty("url") + Config.getProperty("apiVersion"))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new CustomLoggingFilter()));
    }


    public static RequestSpecification unauthSpec() {
        return defaultRequestSpecBuilder().build();
    }


    public static RequestSpecification adminSpec() {
        return defaultRequestSpecBuilder()
                .addHeader("Authorization", authHeaders.get("admin"))
                .build();
    }


    public static RequestSpecification userSpec(String username, String password) {

        String userAuthToken;

        if (!authHeaders.containsKey(username)) {
            LoginUserRequestModel loginUserRequestModel = LoginUserRequestModel.builder()
                    .username(username)
                    .password(password)
                    .build();

            userAuthToken = new CrudRequester(RequestSpecs.unauthSpec(),
                    ResponseSpecs.responseReturns200Spec(),
                    Endpoint.AUTH_LOGIN)
                    .post(loginUserRequestModel)
                    .extract()
                    .header("Authorization");

            authHeaders.put(username, userAuthToken);
        } else {
            userAuthToken = authHeaders.get(username);
        }

        return defaultRequestSpecBuilder()
                .addHeader("Authorization", userAuthToken)
                .build();
    }
}




