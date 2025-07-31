package api.specs;

import api.configs.Config;
import api.configs.CustomLoggingFilter;
import api.models.LoginUserRequestModel;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.Endpoint;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
🔹 RequestSpecification (RequestSpec)
Это шаблон для всех запросов: хедеры, content type, базовый URL, фильтры, авторизация и т.п.
→ Это хедер + content type + фильтры → всё собрано в одном месте.
→ Позволяет легко использовать например adminSpec() везде, где нужен доступ с правами админа.
*/
public class RequestSpecs {
    private static Map<String, String> authHeaders = new HashMap<>();

    static {
        authHeaders.put("admin", "Basic YWRtaW46YWRtaW4=");
    }

    private RequestSpecs() {
    }


    private static RequestSpecBuilder defaultRequestSpecBuilder() {
        return new RequestSpecBuilder()
                .setBaseUri(Config.getProperty("apiBaseurl") + Config.getProperty("apiVersion"))
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


    public static String getUserAuthHeader(String username, String password) {
        String userAuthToken;

        if (!authHeaders.containsKey(username)) {
            userAuthToken = new CrudRequester(RequestSpecs.unauthSpec(),
                    ResponseSpecs.responseReturns200Spec(),
                    Endpoint.AUTH_LOGIN)
                    .post(LoginUserRequestModel.builder().username(username).password(password).build())
                    .extract()
                    .header("Authorization");

            authHeaders.put(username, userAuthToken);
        } else {
            userAuthToken = authHeaders.get(username);
        }

        return userAuthToken;
    }


    public static RequestSpecification userSpec(String username, String password) {
        return defaultRequestSpecBuilder()
                .addHeader("Authorization", getUserAuthHeader(username, password))
                .build();
    }
}




