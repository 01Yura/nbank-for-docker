package iteration1_senior_level;

import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Test;
import senior.generators.RandomModelGenerator;
import senior.models.CreateAccountResponseModel;
import senior.models.CreateUserRequestModel;
import senior.models.GetCustomerAccountsResponseModel;
import senior.requests.skeleton.requesters.CrudRequester;
import senior.requests.skeleton.requesters.Endpoint;
import senior.requests.skeleton.requesters.ValidatedCrudRequester;
import senior.specs.RequestSpecs;
import senior.specs.ResponseSpecs;

import java.util.List;

public class CreateAccountTest extends BaseTest {

    @Test
    void userCanCreateAccount() {
//       Create user

        CreateUserRequestModel createUserRequestModel = RandomModelGenerator.generateRandomModel(CreateUserRequestModel.class);

        new CrudRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.responseReturns201Spec(),
                Endpoint.ADMIN_USERS)
                .post(createUserRequestModel);

//      Create an account
        CreateAccountResponseModel createAccountResponseModel =
                new ValidatedCrudRequester<CreateAccountResponseModel>(RequestSpecs.userSpec(createUserRequestModel.getUsername(),
                        createUserRequestModel.getPassword()),
                        ResponseSpecs.responseReturns201Spec(),
                        Endpoint.ACCOUNTS)
                        .post(null);


        softly.assertThat(createAccountResponseModel.getId()).isPositive();
        softly.assertThat(createAccountResponseModel.getAccountNumber()).isNotEmpty();
        softly.assertThat(createAccountResponseModel.getBalance()).isZero();
        softly.assertThat(createAccountResponseModel.getTransactions().size()).isZero();

//      Get customer accounts
        List<GetCustomerAccountsResponseModel> accounts = new CrudRequester(RequestSpecs.userSpec(createUserRequestModel.getUsername(), createUserRequestModel.getPassword()),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.CUSTOMER_ACCOUNTS)
                .get()
                .extract()
                .as(new TypeRef<List<GetCustomerAccountsResponseModel>>() {
                });

        GetCustomerAccountsResponseModel account = accounts.getFirst();

        softly.assertThat(createAccountResponseModel.getId()).isEqualTo(account.getId());
        softly.assertThat(createAccountResponseModel.getAccountNumber()).isEqualTo(account.getAccountNumber());
        softly.assertThat(account.getBalance()).isZero();
        softly.assertThat(account.getTransactions().size()).isZero();
    }
}
