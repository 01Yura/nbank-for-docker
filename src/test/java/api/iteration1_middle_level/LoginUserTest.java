package api.iteration1_middle_level;

import api.middle.generators.RandomData;
import api.middle.models.CreateUserRequestModel;
import api.middle.models.LoginUserRequestModel;
import api.middle.models.UserRole;
import api.middle.requests.CreateUserRequestSender;
import api.middle.requests.LoginUserRequestSender;
import api.middle.specs.RequestSpecs;
import api.middle.specs.ResponseSpecs;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

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
