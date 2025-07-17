package ui.iteration2_junior_level;

import api.senior.models.*;
import api.senior.requests.skeleton.requesters.CrudRequester;
import api.senior.requests.skeleton.requesters.Endpoint;
import api.senior.requests.skeleton.requesters.ValidatedCrudRequester;
import api.senior.requests.steps.AdminSteps;
import api.senior.specs.RequestSpecs;
import api.senior.specs.ResponseSpecs;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferMoneyTest extends SetupTest {
    @Test
    void userCanTransferMoney() {
//        Setup env:
//        create userSender, login to dashboard, create a senderAccount, deposit money
        CreateUserRequestModel userSender = AdminSteps.createUser();

        String userSenderAuthToken = new CrudRequester(RequestSpecs.unauthSpec(),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.AUTH_LOGIN)
                .post(LoginUserRequestModel.builder().username(userSender.getUsername()).password(userSender.getPassword()).build())
                .extract()
                .header("Authorization");

        Selenide.open("/");

        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userSenderAuthToken);

        Selenide.open("/dashboard");

//        create account
        CreateAccountResponseModel newSenderAccount =
                new ValidatedCrudRequester<CreateAccountResponseModel>(RequestSpecs.userSpec(userSender.getUsername(),
                        userSender.getPassword()),
                        ResponseSpecs.responseReturns201Spec(),
                        Endpoint.ACCOUNTS)
                        .post(null);

//        deposit money
        Float deposit = 100.0F;
        UserDepositMoneyRequestModel userDepositMoneyRequestModel = UserDepositMoneyRequestModel.builder()
                .id(newSenderAccount.getId())
                .balance(deposit)
                .build();

        UserDepositMoneyResponseModel userDepositMoneyResponseModel = new ValidatedCrudRequester<UserDepositMoneyResponseModel>(
                RequestSpecs.userSpec(
                        userSender.getUsername(),
                        userSender.getPassword()),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.ACCOUNTS_DEPOSIT)
                .post(userDepositMoneyRequestModel);


//        create userReceiver and create a receiverAccount
        CreateUserRequestModel userReceiver = AdminSteps.createUser();

        CreateAccountResponseModel newReceiverAccount =
                new ValidatedCrudRequester<CreateAccountResponseModel>(RequestSpecs.userSpec(userReceiver.getUsername(),
                        userReceiver.getPassword()),
                        ResponseSpecs.responseReturns201Spec(),
                        Endpoint.ACCOUNTS)
                        .post(null);

//        Start UI test
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();


        $(Selectors.byXpath("//option[text()='-- Choose an account --']/ancestor::select")).selectOptionContainingText(newSenderAccount.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter recipient name"));
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).sendKeys(newReceiverAccount.getAccountNumber());


        Float transferAmountOfMoney = 10.0F;
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(transferAmountOfMoney.toString());


        $(Selectors.byId("confirmCheck")).click();


        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();


//        check alert
        Alert alert = switchTo().alert();
        String actualAlertMessage = alert.getText();
        String expectedAlertMessage = String.format("✅ Successfully transferred $%s to account %s!", transferAmountOfMoney, newReceiverAccount.getAccountNumber());
        alert.accept();

        assertThat(actualAlertMessage).isEqualTo(expectedAlertMessage);

//        logout
        $(Selectors.byText("\uD83D\uDEAA Logout")).click();

//        check receiverUser
        String userReceiverAuthToken = new CrudRequester(RequestSpecs.unauthSpec(),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.AUTH_LOGIN)
                .post(LoginUserRequestModel.builder().username(userReceiver.getUsername()).password(userReceiver.getPassword()).build())
                .extract()
                .header("Authorization");

        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userReceiverAuthToken);

        Selenide.open("/dashboard");


        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).click();


        $(Selectors.byXpath("//option[text()='-- Choose an account --']/ancestor::select")).click();
        String expectedText = String.format("%s (Balance: $%s0)", newReceiverAccount.getAccountNumber(),
                transferAmountOfMoney);
        $$(Selectors.byXpath("//select/option")).findBy(Condition.text(expectedText)).shouldBe(Condition.visible);

//        Check both accounts on API level

//        take current info about the accountSender after transfer
        GetCustomerAccountsResponseModel accountSenderAfterTransfer =
                new CrudRequester(RequestSpecs.userSpec(userSender.getUsername(), userSender.getPassword()),
                        ResponseSpecs.responseReturns200Spec(),
                        Endpoint.CUSTOMER_ACCOUNTS)
                        .get()
                        .extract().as(new TypeRef<List<GetCustomerAccountsResponseModel>>() {
                        }).getFirst();

//        take current info about the accountReceiver after transfer
        GetCustomerAccountsResponseModel accountReceiverAfterTransfer =
                new CrudRequester(RequestSpecs.userSpec(userReceiver.getUsername(), userReceiver.getPassword()),
                        ResponseSpecs.responseReturns200Spec(),
                        Endpoint.CUSTOMER_ACCOUNTS)
                        .get()
                        .extract().as(new TypeRef<List<GetCustomerAccountsResponseModel>>() {
                        }).getFirst();

//      Проверяем, что сумма правильно списалась, и затем то, что списалось, то и пришло на другой счет и
//      суммировалось правильно (округляя до сотых, так как это FLOAT, будь он не ладен)
        assertThat(accountSenderAfterTransfer.getBalance()).isEqualTo(Math.round((deposit - transferAmountOfMoney) * 100F) / 100F);
        assertThat(accountReceiverAfterTransfer.getBalance()).isEqualTo(Math.round((newReceiverAccount.getBalance() + transferAmountOfMoney) * 100F) / 100F);

//      Проверяем что у отправителя статус транзакции TRANSFER_OUT, а у получателя TRANSFER_IN
        assertThat(accountSenderAfterTransfer.getTransactions().stream()
                .filter(transaction -> transaction.getRelatedAccountId().equals(newReceiverAccount.getId())).findFirst().get().getType())
                .isEqualTo(TransactionType.TRANSFER_OUT.toString());
        assertThat(accountReceiverAfterTransfer.getTransactions().stream()
                .filter(transaction -> transaction.getRelatedAccountId().equals(newSenderAccount.getId())).findFirst().get().getType())
                .isEqualTo(TransactionType.TRANSFER_IN.toString());

//        Проверяем, что время транзакции совпадает у отправителя и получателя
        assertThat(accountSenderAfterTransfer.getTransactions().stream()
                .filter(transaction -> transaction.getRelatedAccountId().equals(accountReceiverAfterTransfer.getId())).findFirst().get().getTimestamp())
                .isEqualTo(accountReceiverAfterTransfer.getTransactions().stream()
                        .filter(transaction -> transaction.getRelatedAccountId().equals(accountSenderAfterTransfer.getId())).findFirst().get().getTimestamp());
    }

    @Test
    void userCannotTransferMoney() {
//        Setup env:
//        create userSender, login to dashboard, create a senderAccount, deposit money
        CreateUserRequestModel userSender = AdminSteps.createUser();

        String userSenderAuthToken = new CrudRequester(RequestSpecs.unauthSpec(),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.AUTH_LOGIN)
                .post(LoginUserRequestModel.builder().username(userSender.getUsername()).password(userSender.getPassword()).build())
                .extract()
                .header("Authorization");

        Selenide.open("/");

        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userSenderAuthToken);

        Selenide.open("/dashboard");

//        create account
        CreateAccountResponseModel newSenderAccount =
                new ValidatedCrudRequester<CreateAccountResponseModel>(RequestSpecs.userSpec(userSender.getUsername(),
                        userSender.getPassword()),
                        ResponseSpecs.responseReturns201Spec(),
                        Endpoint.ACCOUNTS)
                        .post(null);

//        deposit money
        Float deposit = 100.0F;
        UserDepositMoneyRequestModel userDepositMoneyRequestModel = UserDepositMoneyRequestModel.builder()
                .id(newSenderAccount.getId())
                .balance(deposit)
                .build();

        UserDepositMoneyResponseModel userDepositMoneyResponseModel = new ValidatedCrudRequester<UserDepositMoneyResponseModel>(
                RequestSpecs.userSpec(
                        userSender.getUsername(),
                        userSender.getPassword()),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.ACCOUNTS_DEPOSIT)
                .post(userDepositMoneyRequestModel);


//        create userReceiver and create a receiverAccount
        CreateUserRequestModel userReceiver = AdminSteps.createUser();

        CreateAccountResponseModel newReceiverAccount =
                new ValidatedCrudRequester<CreateAccountResponseModel>(RequestSpecs.userSpec(userReceiver.getUsername(),
                        userReceiver.getPassword()),
                        ResponseSpecs.responseReturns201Spec(),
                        Endpoint.ACCOUNTS)
                        .post(null);

//        Start UI test
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();


        $(Selectors.byXpath("//option[text()='-- Choose an account --']/ancestor::select")).selectOptionContainingText(newSenderAccount.getAccountNumber());
        $(Selectors.byAttribute("placeholder", "Enter recipient name"));
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).sendKeys(newReceiverAccount.getAccountNumber());


        Float transferAmountOfMoney = 99999.0F;
        $(Selectors.byAttribute("placeholder", "Enter amount")).sendKeys(transferAmountOfMoney.toString());


        $(Selectors.byId("confirmCheck")).click();


        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();


//        check alert
        Alert alert = switchTo().alert();
        String actualAlertMessage = alert.getText();
        String expectedAlertMessage = "Error";
        alert.accept();

        assertThat(actualAlertMessage).contains(expectedAlertMessage);

//        logout
        $(Selectors.byText("\uD83D\uDEAA Logout")).click();

//        check receiverUser
        String userReceiverAuthToken = new CrudRequester(RequestSpecs.unauthSpec(),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.AUTH_LOGIN)
                .post(LoginUserRequestModel.builder().username(userReceiver.getUsername()).password(userReceiver.getPassword()).build())
                .extract()
                .header("Authorization");

        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userReceiverAuthToken);

        Selenide.open("/dashboard");


        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).click();


        $(Selectors.byXpath("//option[text()='-- Choose an account --']/ancestor::select")).click();
        String expectedText = String.format("%s (Balance: $0.00)", newReceiverAccount.getAccountNumber());
        $$(Selectors.byXpath("//select/option")).findBy(Condition.text(expectedText)).shouldBe(Condition.visible);

//        Check both accounts on API level

//        take current info about the accountSender after transfer
        GetCustomerAccountsResponseModel accountSenderAfterTransfer =
                new CrudRequester(RequestSpecs.userSpec(userSender.getUsername(), userSender.getPassword()),
                        ResponseSpecs.responseReturns200Spec(),
                        Endpoint.CUSTOMER_ACCOUNTS)
                        .get()
                        .extract().as(new TypeRef<List<GetCustomerAccountsResponseModel>>() {
                        }).getFirst();

//        take current info about the accountReceiver after transfer
        GetCustomerAccountsResponseModel accountReceiverAfterTransfer =
                new CrudRequester(RequestSpecs.userSpec(userReceiver.getUsername(), userReceiver.getPassword()),
                        ResponseSpecs.responseReturns200Spec(),
                        Endpoint.CUSTOMER_ACCOUNTS)
                        .get()
                        .extract().as(new TypeRef<List<GetCustomerAccountsResponseModel>>() {
                        }).getFirst();

//        Check on API level that balance stayed the same on both accounts
        assertThat(deposit).isEqualTo(accountSenderAfterTransfer.getBalance());
        assertThat(newReceiverAccount.getBalance()).isEqualTo(accountReceiverAfterTransfer.getBalance());
    }
}
