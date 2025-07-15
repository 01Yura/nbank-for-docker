package ui.iteration2_junior_level;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import senior.models.*;
import senior.requests.skeleton.requesters.CrudRequester;
import senior.requests.skeleton.requesters.Endpoint;
import senior.requests.skeleton.requesters.ValidatedCrudRequester;
import senior.requests.steps.AdminSteps;
import senior.specs.RequestSpecs;
import senior.specs.ResponseSpecs;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;


public class DepositMoneyTest extends SetupTest {

    @Test
    void userCanDepositMoney() {
//        Setup env:
//        create user, login to dashboard and create an account
        CreateUserRequestModel user = AdminSteps.createUser();

        String userAuthToken = new CrudRequester(RequestSpecs.unauthSpec(),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.AUTH_LOGIN)
                .post(LoginUserRequestModel.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");

        Selenide.open("/");

        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthToken);

        Selenide.open("/dashboard");

        CreateAccountResponseModel newAccount =
                new ValidatedCrudRequester<CreateAccountResponseModel>(RequestSpecs.userSpec(user.getUsername(),
                        user.getPassword()),
                        ResponseSpecs.responseReturns201Spec(),
                        Endpoint.ACCOUNTS)
                        .post(null);

//        Test steps:
//        find and click depositMoneyButton
        By depositMoneyButtonLocator = Selectors.byText("\uD83D\uDCB0 Deposit Money");
        SelenideElement depositMoneyButton = $(depositMoneyButtonLocator);
        depositMoneyButton.click();

//        find dropdown menu and choose created account
        By accountDropdownSelector = Selectors.byXpath("//option[text()='-- Choose an account --']/ancestor::select");
        SelenideElement accountDropdown = $(accountDropdownSelector);
        accountDropdown.selectOptionContainingText(newAccount.getAccountNumber());

//        find field and fill it with amount of money for deposit
        By amountFieldLocator = Selectors.byAttribute("placeholder", "Enter amount");
        SelenideElement amountField = $(amountFieldLocator);
        Float amount = 10.0F;
        amountField.sendKeys(amount.toString());

//        click depositButton
        By depositButtonLocator = Selectors.byText("\uD83D\uDCB5 Deposit");
        SelenideElement depositButton = $(depositButtonLocator);
        depositButton.click();

//        check alert and it's text
        Alert alert = switchTo().alert();
        String actualAlertMessage = alert.getText();
        String expectedAlertMessage = String.format("✅ Successfully deposited $%s to account %s!", amount, newAccount.getAccountNumber());
        alert.accept();

        assertThat(actualAlertMessage).isEqualTo(expectedAlertMessage);

//        Check whether the balance is equal as should (API)
//        request the list of user accounts
        List<GetCustomerAccountsResponseModel> accounts = new CrudRequester(
                RequestSpecs.userSpec(
                        user.getUsername(),
                        user.getPassword()),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.CUSTOMER_ACCOUNTS)
                .get()
                .extract().as(new TypeRef<List<GetCustomerAccountsResponseModel>>() {
                });

//        retrieve created account from user's account list
        GetCustomerAccountsResponseModel account = accounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(newAccount.getAccountNumber()))
                .findFirst().orElseThrow(() -> new RuntimeException("Аккаунт с номером " + newAccount.getAccountNumber() + " не найден"));

        Float balance = account.getBalance();
        String transactionType = account.getTransactions().getFirst().getType();
        Long relatedAccountId = account.getTransactions().getFirst().getRelatedAccountId();
        Float amountOfDeposit = account.getTransactions().getFirst().getAmount().floatValue();

//        check that balance is expected (expected balance = initial balance + deposited money)
        assertThat(balance).isEqualTo(newAccount.getBalance() + amountOfDeposit);
//        check that the amount specified in the transaction is equal to the amount of money which has been deposited
        assertThat(amount).isEqualTo(amountOfDeposit);
//        check that a transaction type is equal to a DEPOSIT type
        assertThat(transactionType).isEqualTo(TransactionType.DEPOSIT.toString());
//        check that a sender account id specified in transaction info is equal to the account id which sent money
        assertThat(relatedAccountId).isEqualTo(account.getId());
    }

    @Test
    void userCannotDepositMoney() {
//        Setup env:
//        create user, login to dashboard and create an account
        CreateUserRequestModel user = AdminSteps.createUser();

        String userAuthToken = new CrudRequester(RequestSpecs.unauthSpec(),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.AUTH_LOGIN)
                .post(LoginUserRequestModel.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");

        Selenide.open("/");

        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthToken);

        Selenide.open("/dashboard");

        CreateAccountResponseModel newAccount =
                new ValidatedCrudRequester<CreateAccountResponseModel>(RequestSpecs.userSpec(user.getUsername(),
                        user.getPassword()),
                        ResponseSpecs.responseReturns201Spec(),
                        Endpoint.ACCOUNTS)
                        .post(null);

//        Test steps:
//        find and click depositMoneyButton
        By depositMoneyButtonLocator = Selectors.byText("\uD83D\uDCB0 Deposit Money");
        SelenideElement depositMoneyButton = $(depositMoneyButtonLocator);
        depositMoneyButton.click();

//        find dropdown menu and choose created account
        By accountDropdownSelector = Selectors.byXpath("//option[text()='-- Choose an account --']/ancestor::select");
        SelenideElement accountDropdown = $(accountDropdownSelector);
        accountDropdown.selectOptionContainingText(newAccount.getAccountNumber());

//        find field and fill it with amount of money for deposit
        By amountFieldLocator = Selectors.byAttribute("placeholder", "Enter amount");
        SelenideElement amountField = $(amountFieldLocator);
        Float amount = 0F;
        amountField.sendKeys(amount.toString());

//        click depositButton
        By depositButtonLocator = Selectors.byText("\uD83D\uDCB5 Deposit");
        SelenideElement depositButton = $(depositButtonLocator);
        depositButton.click();

//        check alert and it's text
        Alert alert = switchTo().alert();
        String actualAlertMessage = alert.getText();
        String expectedAlertMessage = "❌ Please enter a valid amount.";
        alert.accept();

        assertThat(actualAlertMessage).isEqualTo(expectedAlertMessage);

//        Check whether the balance is equal as should (API)
//        request the list of user accounts
        List<GetCustomerAccountsResponseModel> accounts = new CrudRequester(
                RequestSpecs.userSpec(
                        user.getUsername(),
                        user.getPassword()),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.CUSTOMER_ACCOUNTS)
                .get()
                .extract().as(new TypeRef<List<GetCustomerAccountsResponseModel>>() {
                });

//        retrieve created account from user's account list
        GetCustomerAccountsResponseModel account = accounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(newAccount.getAccountNumber()))
                .findFirst().orElseThrow(() -> new RuntimeException("Аккаунт с номером " + newAccount.getAccountNumber() + " не найден"));

        Float balance = account.getBalance();

//        check that balance is expected (expected balance = 0)
        assertThat(balance).isZero();
//        check that the initial balance is equal to the final balance (the initial balance wasn't changed)
        assertThat(newAccount.getBalance()).isEqualTo(balance);
    }
}
