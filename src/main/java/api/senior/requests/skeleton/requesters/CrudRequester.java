package api.senior.requests.skeleton.requesters;

import api.senior.requests.skeleton.interfaces.GetAllEndpointsInterface;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.senior.models.BaseModel;
import api.senior.requests.skeleton.interfaces.CrudEndpointInterface;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface, GetAllEndpointsInterface {
    public CrudRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
        super(requestSpecification, responseSpecification, endpoint);
    }


    @Override
    public ValidatableResponse get() {
        return given()
                .spec(requestSpecification)
                .when()
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }


    @Override
    public ValidatableResponse put(BaseModel model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .when()
                .put(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }


    @Override
    public ValidatableResponse post(BaseModel model) {
        var request = given()
                .spec(requestSpecification);

        if (model != null) request.body(model);

        return request
                .when()
                .post(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }


    @Override
    public ValidatableResponse delete(Long id) {
        return given()
                .spec(requestSpecification)
                .when()
                .delete(endpoint.getUrl() + "/" + id)
                .then()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse getAll(Class<?> clazz) {
        return given()
                .spec(requestSpecification)
                .when()
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }
}
