package api.iteration1_senior_level;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import api.senior.models.CreateUserRequestModel;
import api.senior.models.LoginUserRequestModel;
import api.senior.requests.skeleton.requesters.CrudRequester;
import api.senior.requests.skeleton.requesters.Endpoint;
import api.senior.requests.steps.AdminSteps;
import api.senior.specs.RequestSpecs;
import api.senior.specs.ResponseSpecs;

public class LoginUserTest extends BaseTest {

    @Test
    void adminCanLoginWithValidCredentialsAndGetAuthToken() {

        LoginUserRequestModel loginUserRequestModel = LoginUserRequestModel.builder()
                .username("admin")
                .password("admin")
                .build();


        new CrudRequester(RequestSpecs.unauthSpec(),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.AUTH_LOGIN)
                .post(loginUserRequestModel)
                .header("Authorization", Matchers.notNullValue());
    }


    @Test
    void userCanLoginWithValidCredentialsAndGetAuthToken() {
//        Create user
        CreateUserRequestModel createUserRequestModel = AdminSteps.createUser();

//        User can log in using valid credentials and get an auth token
        LoginUserRequestModel loginUserRequestModel = LoginUserRequestModel.builder()
                .username(createUserRequestModel.getUsername())
                .password(createUserRequestModel.getPassword())
                .build();

        new CrudRequester(RequestSpecs.unauthSpec(),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.AUTH_LOGIN)
                .post(loginUserRequestModel)
                .header("Authorization", Matchers.notNullValue());
    }
}
