package senior.requests.skeleton.requesters;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import senior.models.BaseModel;
import senior.requests.skeleton.interfaces.CrudEndpointInterface;
/*

ValidatedCrudRequester<T>
Это высокоуровневая обёртка над CrudRequester, которая:
- использует CrudRequester внутри (this.crudRequester = new CrudRequester(...));
- делает вызов и сразу маппит ответ в Java-модель через .extract().as(...);
- знает, какую модель ожидать, благодаря endpoint.getResponseModel().

Используется, когда:
- ты точно знаешь, какую модель ожидаешь в ответе;
- хочешь получить готовый Java-объект, с которым можно работать (например, UserModel, AccountResponseModel);
- это упрощает код, т.к. можно сразу писать response.getBalance() и т. д.
*/


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
