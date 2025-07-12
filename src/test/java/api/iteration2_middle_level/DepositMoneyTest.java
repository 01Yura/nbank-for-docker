package api.iteration2_middle_level;

import io.restassured.common.mapper.TypeRef;
import middle.models.GetCustomerAccountsResponseModel;
import middle.models.TransactionType;
import middle.models.UserDepositMoneyRequestModel;
import middle.models.UserDepositMoneyResponseModel;
import middle.requests.DepositMoneyRequestSender;
import middle.requests.GetCustomerAccountsRequestSender;
import middle.specs.RequestSpecs;
import middle.specs.ResponseSpecs;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

public class DepositMoneyTest extends BaseTest {

    static Stream<Arguments> argsFor_userCanDepositMoney() {
        return Stream.of(
                //                Positive: Authorized user can deposit a valid amount of money to their account (1)
                Arguments.of(1.0F),
                //                Positive: Authorized user can deposit a valid amount of money to their account (4999)
                Arguments.of(4999.0F),
                //                Positive: Authorized user can deposit a valid amount of money to their account (5000)
                Arguments.of(5000.0F)
        );
    }


    static Stream<Arguments> argsFor_userCannotDepositMoney() {
        return Stream.of(
                //                Negative: Authorized user CANNOT deposit money to their account if amount of money is negative number
                Arguments.of(-0.01F, "Invalid account or amount"),
                //                Negative: Authorized user CANNOT deposit money to their account if amount of money is 0
                Arguments.of(0.0F, "Invalid account or amount"),
                //                Negative: Authorized user CANNOT deposit money to their account if amount of money is more than 5000 (5001)
                Arguments.of(5000.01F, "Deposit amount exceeds the 5000 limit")
        );
    }


    @ParameterizedTest
    @MethodSource("argsFor_userCanDepositMoney")
    void userCanDepositMoney(Float depositBalance) {
        //        Deposit money
        UserDepositMoneyRequestModel userDepositMoneyRequestModel = UserDepositMoneyRequestModel.builder()
                .id(firstUserAccount.getId())
                .balance(depositBalance)
                .build();

        UserDepositMoneyResponseModel userDepositMoneyResponseModel = new DepositMoneyRequestSender(
                RequestSpecs.userSpec(
                        firstUser.getUsername(),
                        firstUser.getPassword()),
                ResponseSpecs.responseReturns200Spec())
                .request(userDepositMoneyRequestModel)
                .extract().as(UserDepositMoneyResponseModel.class);

        //        Check whether the balance is equal as should
        List<GetCustomerAccountsResponseModel> accounts = new GetCustomerAccountsRequestSender(
                RequestSpecs.userSpec(
                        firstUser.getUsername(),
                        firstUser.getPassword()),
                ResponseSpecs.responseReturns200Spec())
                .request(null)
                .extract().as(new TypeRef<List<GetCustomerAccountsResponseModel>>() {
                });

        GetCustomerAccountsResponseModel account = accounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(userDepositMoneyResponseModel.getAccountNumber()))
                .findFirst().get();

        String transactionType = account.getTransactions().getFirst().getType();
        Long relatedAccountId = account.getTransactions().getFirst().getRelatedAccountId();


        softly.assertThat(userDepositMoneyResponseModel.getBalance()).isEqualTo(account.getBalance());
        softly.assertThat(transactionType).isEqualTo(TransactionType.DEPOSIT.toString());
        softly.assertThat(relatedAccountId).isEqualTo(firstUserAccount.getId());
    }


    @ParameterizedTest
    @MethodSource("argsFor_userCannotDepositMoney")
    void userCannotDepositMoney(Float depositBalance, String expectedError) {
        //        Deposit money
        UserDepositMoneyRequestModel userDepositMoneyRequestModel = UserDepositMoneyRequestModel.builder()
                .id(firstUserAccount.getId())
                .balance(depositBalance)
                .build();

        new DepositMoneyRequestSender(
                RequestSpecs.userSpec(
                        firstUser.getUsername(),
                        firstUser.getPassword()),
                ResponseSpecs.responseReturns400WithoutKeyValueSpec(expectedError))
                .request(userDepositMoneyRequestModel);


        //        Check whether the balance is equal as should
        List<GetCustomerAccountsResponseModel> accounts = new GetCustomerAccountsRequestSender(
                RequestSpecs.userSpec(
                        firstUser.getUsername(),
                        firstUser.getPassword()),
                ResponseSpecs.responseReturns200Spec())
                .request(null)
                .extract().as(new TypeRef<List<GetCustomerAccountsResponseModel>>() {
                });

        GetCustomerAccountsResponseModel account = accounts.stream()
                .filter(acc -> acc.getId().equals(firstUserAccount.getId()))
                .findFirst().get();

        softly.assertThat(account.getBalance()).isEqualTo(0.0F);
    }
}