package senior.requests.skeleton.requesters;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import senior.models.BaseModel;
import senior.requests.skeleton.interfaces.CrudEndpointInterface;

public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface {
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
}
