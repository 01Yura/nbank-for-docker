package middle.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import middle.models.LoginUserRequestModel;
import middle.requests.LoginUserRequestSender;

import java.util.List;

public class RequestSpecs {
    private RequestSpecs() {
    }


    private static RequestSpecBuilder defaultRequestSpecBuilder() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost:4111")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()));
    }


    public static RequestSpecification unauthSpec() {
        return defaultRequestSpecBuilder().build();
    }


    public static RequestSpecification adminSpec() {
        return defaultRequestSpecBuilder()
                .addHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
                .build();
    }


    public static RequestSpecification userSpec(String username, String password) {
        LoginUserRequestModel loginUserRequestModel = LoginUserRequestModel.builder()
                .username(username)
                .password(password)
                .build();

        String userAuthToken = new LoginUserRequestSender(RequestSpecs.unauthSpec(), ResponseSpecs.responseReturns200Spec())
                .request(loginUserRequestModel)
                .extract()
                .header("Authorization");

        return defaultRequestSpecBuilder()
                .addHeader("Authorization", userAuthToken)
                .build();
    }
}




