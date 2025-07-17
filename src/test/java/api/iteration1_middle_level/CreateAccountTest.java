package api.iteration1_middle_level;

import io.restassured.common.mapper.TypeRef;
import api.middle.generators.RandomData;
import api.middle.models.CreateAccountResponseModel;
import api.middle.models.CreateUserRequestModel;
import api.middle.models.GetCustomerAccountsResponseModel;
import api.middle.models.UserRole;
import api.middle.requests.CreateAccountRequestSender;
import api.middle.requests.CreateUserRequestSender;
import api.middle.specs.RequestSpecs;
import api.middle.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

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

        Long accountId = createAccountResponseModel.getId();
        String accountNumber = createAccountResponseModel.getAccountNumber();

        softly.assertThat(createAccountResponseModel.getId()).isPositive();
        softly.assertThat(createAccountResponseModel.getAccountNumber()).isNotEmpty();
        softly.assertThat(createAccountResponseModel.getBalance()).isZero();
        softly.assertThat(createAccountResponseModel.getTransactions().size()).isZero();

//      Get customer accounts
        List<GetCustomerAccountsResponseModel> accounts = given()
                .spec(RequestSpecs.userSpec(createUserRequestModel.getUsername(), createUserRequestModel.getPassword()))
                .when()
                .get("/api/v1/customer/accounts")
                .then()
                .spec(ResponseSpecs.responseReturns200Spec())
                .extract().as(new TypeRef<List<GetCustomerAccountsResponseModel>>() {
                });

        GetCustomerAccountsResponseModel account = accounts.getFirst();

        softly.assertThat(createAccountResponseModel.getId()).isEqualTo(account.getId());
        softly.assertThat(createAccountResponseModel.getAccountNumber()).isEqualTo(account.getAccountNumber());
        softly.assertThat(account.getBalance()).isZero();
        softly.assertThat(account.getTransactions().size()).isZero();
    }
}
