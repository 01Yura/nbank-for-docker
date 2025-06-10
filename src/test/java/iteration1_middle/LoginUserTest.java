package iteration1_middle;

import generators.RandomData;
import iteration1.BaseTest;
import models.CreateUserRequestModel;
import models.LoginUserRequestModel;
import models.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.CreateUserRequestSender;
import requests.LoginUserRequestSender;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class LoginUserTest extends BaseTest {

    @Test
    void adminCanLoginWithValidCredentialsAndGetAuthToken() {

        LoginUserRequestModel loginUserRequestModel = LoginUserRequestModel.builder()
                .username("admin")
                .password("admin")
                .build();

        new LoginUserRequestSender(RequestSpecs.unauthSpec(), ResponseSpecs.responseReturns200Spec())
                .request(loginUserRequestModel)
                .header("Authorization", Matchers.notNullValue());
    }


    @Test
    void userCanLoginWithValidCredentialsAndGetAuthToken() {
//        Create user
        CreateUserRequestModel createUserRequestModel = CreateUserRequestModel.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new CreateUserRequestSender(RequestSpecs.adminSpec(), ResponseSpecs.responseReturns201Spec())
                .request(createUserRequestModel);

//        User can log in using valid credentials and get auth token
        LoginUserRequestModel loginUserRequestModel = LoginUserRequestModel.builder()
                .username(createUserRequestModel.getUsername())
                .password(createUserRequestModel.getPassword())
                .build();

        new LoginUserRequestSender(RequestSpecs.unauthSpec(), ResponseSpecs.responseReturns200Spec())
                .request(loginUserRequestModel)
                .header("Authorization", Matchers.notNullValue());
    }
}
