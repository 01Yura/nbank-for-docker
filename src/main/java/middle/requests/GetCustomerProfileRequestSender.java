package middle.requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import middle.models.BaseModel;

import static io.restassured.RestAssured.given;

public class GetCustomerProfileRequestSender extends BaseRequestSender {
    public GetCustomerProfileRequestSender(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }


    @Override
    public ValidatableResponse request(BaseModel model) {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/v1/customer/profile")
                .then()
                .spec(responseSpecification);
    }
}
