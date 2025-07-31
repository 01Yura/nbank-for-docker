package api.requests.skeleton.requesters;

import api.models.BaseModel;
import api.requests.skeleton.interfaces.CrudEndpointInterface;
import api.requests.skeleton.interfaces.GetAllEndpointsInterface;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.Arrays;
import java.util.List;

public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface, GetAllEndpointsInterface {
    private CrudRequester crudRequester;


    public ValidatedCrudRequester(RequestSpecification requestSpecification,
                                  ResponseSpecification responseSpecification, Endpoint endpoint) {
        super(requestSpecification, responseSpecification, endpoint);
        this.crudRequester = new CrudRequester(requestSpecification, responseSpecification, endpoint);
    }


    @Override
    public T get() {
        return (T) crudRequester
                .get()
                .extract().as(endpoint.getResponseModel());
    }


    @Override
    public T put(BaseModel model) {
        return (T) crudRequester
                .put(model)
                .extract().as(endpoint.getResponseModel());
    }


    @Override
    public T post(BaseModel model) {
        return (T) crudRequester
                .post(model)
                .extract().as(endpoint.getResponseModel());
    }


    @Override
    public T delete(Long id) {
        return null;
    }

    @Override
    public List<T> getAll(Class<?> clazz) {
        T[] array = (T[]) crudRequester.getAll(clazz).extract().as(clazz);
        return Arrays.asList(array);
    }
}
