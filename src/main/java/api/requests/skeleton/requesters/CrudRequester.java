package api.requests.skeleton.requesters;

import api.models.BaseModel;
import api.requests.skeleton.interfaces.CrudEndpointInterface;
import api.requests.skeleton.interfaces.GetAllEndpointsInterface;
import common.helper.StepLogger;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

/*
Класс CrudRequester — это универсальный класс для выполнения CRUD-запросов (GET, PUT, POST, DELETE и т.д.) на определенный эндпоинт,
с заранее настроенными RequestSpecification и ResponseSpecification.
CrudRequester — это фасад над REST Assured, который:
 - знает, куда обращаться (Endpoint endpoint)
 - знает, с какими настройками (RequestSpecification, ResponseSpecification)
 - знает, как выполнять стандартные операции (get(), put() и т.д.)
Возвращает ValidatableResponse из RestAssured. Это объект из REST Assured, который позволяет:
 - работать с “сырым” ответом или нестандартно извлекать данные из ответа (например токен из хедера)
 - делать ассерты над телом/статусом/заголовками ответа
 - проверять JSON, XML, текст и т.д.
* */
public class CrudRequester extends HttpRequest implements CrudEndpointInterface, GetAllEndpointsInterface {
    public CrudRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification, Endpoint endpoint) {
        super(requestSpecification, responseSpecification, endpoint);
    }


    @Override
    @Step("GET запрос на {endpoint}")
    public ValidatableResponse get() {
        return given()
                .spec(requestSpecification)
                .when()
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }


    @Override
    @Step("PUT запрос на {endpoint} c телом {model}")
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
        return StepLogger.log("POST запрос на" + endpoint.getUrl(), () -> {
            var request = given()
                    .spec(requestSpecification);

            if (model != null) request.body(model);

            return request
                    .when()
                    .post(endpoint.getUrl())
                    .then()
                    .spec(responseSpecification);
        });
    }


    @Override
    @Step("DELETE запрос на {endpoint} c id {id}")
    public ValidatableResponse delete(Long id) {
        return given()
                .spec(requestSpecification)
                .when()
                .delete(endpoint.getUrl() + "/" + id)
                .then()
                .spec(responseSpecification);
    }

    @Override
    @Step("GET запрос на {endpoint}")
    public ValidatableResponse getAll(Class<?> clazz) {
        return given()
                .spec(requestSpecification)
                .when()
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpecification);
    }
}
