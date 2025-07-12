package api.iteration1_senior_level;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import senior.models.CreateUserRequestModel;
import senior.models.LoginUserRequestModel;
import senior.requests.skeleton.requesters.CrudRequester;
import senior.requests.skeleton.requesters.Endpoint;
import senior.requests.steps.AdminSteps;
import senior.specs.RequestSpecs;
import senior.specs.ResponseSpecs;

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
