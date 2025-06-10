package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;

import static io.restassured.RestAssured.given;

public class CreateAccountRequestSender extends BaseRequestSender {
    public CreateAccountRequestSender(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }


    @Override
    public ValidatableResponse request(BaseModel model) {
        return given()
                .spec(requestSpecification)
                .when()
                .post("/api/v1/accounts")
                .then()
                .spec(responseSpecification);
    }
}
