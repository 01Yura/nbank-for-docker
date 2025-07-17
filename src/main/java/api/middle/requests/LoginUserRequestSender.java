package api.middle.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.middle.models.LoginUserRequestModel;

import static io.restassured.RestAssured.given;

public class LoginUserRequestSender extends BaseRequestSender<LoginUserRequestModel> {
    public LoginUserRequestSender(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    public ValidatableResponse request (LoginUserRequestModel model){
        return given()
                .spec(requestSpecification)
                .body(model)
                .post("/api/v1/auth/login")
                .then()
                .spec(responseSpecification);
    }
}
