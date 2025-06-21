package middle.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import middle.models.CreateUserRequestModel;

import static io.restassured.RestAssured.given;

public class CreateUserRequestSender extends BaseRequestSender<CreateUserRequestModel> {
    public CreateUserRequestSender(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }


    public ValidatableResponse request(CreateUserRequestModel model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .when()
                .post("/api/v1/admin/users")
                .then()
                .spec(responseSpecification);
    }
}
