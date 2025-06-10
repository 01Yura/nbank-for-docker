package iteration1_middle;

import generators.RandomData;
import iteration1.BaseTest;
import models.CreateAccountResponseModel;
import models.CreateUserRequestModel;
import models.UserRole;
import org.junit.jupiter.api.Test;
import requests.CreateAccountRequestSender;
import requests.CreateUserRequestSender;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class CreateAccountTest extends BaseTest {

    @Test
    void userCanCreateAccount() {
//       Create user
        CreateUserRequestModel createUserRequestModel = CreateUserRequestModel.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new CreateUserRequestSender(RequestSpecs.adminSpec(), ResponseSpecs.responseReturns201Spec())
                .request(createUserRequestModel);

//      Create account
        CreateAccountResponseModel createAccountResponseModel =
                new CreateAccountRequestSender(RequestSpecs.userSpec(createUserRequestModel.getUsername(),
                        createUserRequestModel.getPassword()), ResponseSpecs.responseReturns201Spec())
                        .request(null)
                        .extract().as(CreateAccountResponseModel.class);

        softly.assertThat(createAccountResponseModel.getId()).isPositive();
        softly.assertThat(createAccountResponseModel.getAccountNumber()).isNotEmpty();
        softly.assertThat(createAccountResponseModel.getBalance()).isZero();
        softly.assertThat(createAccountResponseModel.getTransactions().size()).isZero();
    }
}
