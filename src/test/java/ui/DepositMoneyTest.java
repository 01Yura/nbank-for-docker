package ui;

import api.models.CreateAccountResponseModel;
import api.models.GetCustomerAccountsResponseModel;
import api.models.TransactionType;
import common.annotations.Browsers;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.DepositPage;
import ui.pages.UserDashboard;

import java.util.List;


public class DepositMoneyTest extends BaseUiTest {

    @Test
    @Browsers({"chrome"})
    @UserSession
    void userCanDepositMoney() {
        CreateAccountResponseModel newAccount =
                SessionStorage.getFirstStep().createAccount();

//        STEPS OF TEST:
        Float moneyAmount = 10.0F;
        new UserDashboard().open().depositMoney()
                .getPage(DepositPage.class).depositMoney(newAccount.getAccountNumber(), moneyAmount)
                .checkAlertMessageAndAccept(BankAlert.SUCCESSFULLY_DEPOSITED.getMessage(), moneyAmount, newAccount.getAccountNumber());

//        Check whether the balance is equal as should (API)
//        request the list of user accounts
        List<GetCustomerAccountsResponseModel> accounts =
                SessionStorage.getFirstStep().getAllAccounts();

//        retrieve a created account from the user's account list
        GetCustomerAccountsResponseModel account = SessionStorage.getFirstStep().getAccountWIthSpecificNumber(newAccount.getAccountNumber());

        Float balance = account.getBalance();
        String transactionType = account.getTransactions().getFirst().getType();
        Long relatedAccountId = account.getTransactions().getFirst().getRelatedAccountId();
        Float amountOfDeposit = account.getTransactions().getFirst().getAmount().floatValue();

//        check that balance is expected (expected balance = initial balance + deposited money)
        softly.assertThat(balance).isEqualTo(newAccount.getBalance() + amountOfDeposit);
//        check that the amount specified in the transaction is equal to the amount of money which has been deposited
        softly.assertThat(moneyAmount).isEqualTo(amountOfDeposit);
//        check that a transaction type is equal to a DEPOSIT type
        softly.assertThat(transactionType).isEqualTo(TransactionType.DEPOSIT.toString());
//        check that a sender account id specified in transaction info is equal to the account id which sent money
        softly.assertThat(relatedAccountId).isEqualTo(account.getId());
    }

    @Test
    @UserSession
    void userCannotDepositMoney() {
        CreateAccountResponseModel newAccount =
                SessionStorage.getFirstStep().createAccount();

//        STEPS OF TEST:
        Float moneyAmount = 0.0F;
        new UserDashboard().open().depositMoney()
                .getPage(DepositPage.class).depositMoney(newAccount.getAccountNumber(), moneyAmount)
                .checkAlertMessageAndAccept(BankAlert.PLEASE_ENTER_A_VALID_AMOUNT.getMessage());

//        Check whether the balance is equal as should (API)
//        request the list of user accounts
        List<GetCustomerAccountsResponseModel> accounts =
                SessionStorage.getFirstStep().getAllAccounts();

//        retrieve a created account from the user's account list
        GetCustomerAccountsResponseModel account = SessionStorage.getFirstStep().getAccountWIthSpecificNumber(newAccount.getAccountNumber());

        Float balance = account.getBalance();

//        check that balance is expected (expected balance = 0)
        softly.assertThat(balance).isZero();
//        check that the initial balance is equal to the final balance (the initial balance wasn't changed)
        softly.assertThat(newAccount.getBalance()).isEqualTo(balance);
    }
}
