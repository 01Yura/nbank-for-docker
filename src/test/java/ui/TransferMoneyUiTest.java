package ui;

import api.models.CreateAccountResponseModel;
import api.models.GetCustomerAccountsResponseModel;
import api.models.TransactionType;
import common.annotations.UserSession;
import common.annotations.UsersWithAccounts;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.DepositPage;
import ui.pages.TransferPage;
import ui.pages.UserDashboard;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferMoneyUiTest extends BaseUiTest {

    @Test
    @UsersWithAccounts(users = 2)
    void userCanTransferMoney() {
//        create senderAccount
        CreateAccountResponseModel senderAccount =
                SessionStorage.getSteps(1).getAccount();

        Float deposit = 100.0F;
        SessionStorage.getSteps(1).depositMoney(senderAccount, 100.0F);

//        create recipientAccount
        CreateAccountResponseModel recipientAccount =
               SessionStorage.getSteps(2).getAccount();

//        Start UI test
        Float amountToTransfer = 10.0F;
        new UserDashboard().open().makeTransfer()
                .getPage(TransferPage.class)
                .sendTransfer(senderAccount.getAccountNumber(), recipientAccount.getAccountNumber(), amountToTransfer)
                .checkAlertMessageAndAccept(
                        BankAlert.SUCCESSFULLY_TRANSFERRED.getMessage(),
                        amountToTransfer,
                        recipientAccount.getAccountNumber())
                .getLogoutButton().click();

//        check balance userRecipient on UI
        authAsUser(SessionStorage.getUser(2));
        new DepositPage().open().checkAccountBalance(recipientAccount.getAccountNumber(), amountToTransfer);

//        Check info about both accounts on API level:

//        take current info about the senderAccount after transfer
        GetCustomerAccountsResponseModel accountSenderAfterTransfer =
                SessionStorage.getSteps(1).getAccountWIthSpecificNumber(senderAccount.getAccountNumber());

//        take current info about the recipientAccount after transfer
        GetCustomerAccountsResponseModel accountRecipientAfterTransfer =
                SessionStorage.getSteps(2).getAccountWIthSpecificNumber(recipientAccount.getAccountNumber());

//      Проверяем, что сумма правильно списалась, и затем то, что списалось, то и пришло на другой счет и
//      суммировалось правильно (округляя до сотых, так как это FLOAT, будь он не ладен)
        assertThat(accountSenderAfterTransfer.getBalance()).isEqualTo(Math.round((deposit - amountToTransfer) * 100F) / 100F);
        assertThat(accountRecipientAfterTransfer.getBalance()).isEqualTo(Math.round((recipientAccount.getBalance() + amountToTransfer) * 100F) / 100F);

//      Проверяем что у отправителя статус транзакции TRANSFER_OUT, а у получателя TRANSFER_IN
        assertThat(accountSenderAfterTransfer.getTransactions().stream()
                .filter(transaction -> transaction.getRelatedAccountId().equals(recipientAccount.getId())).findFirst().get().getType())
                .isEqualTo(TransactionType.TRANSFER_OUT.toString());
        assertThat(accountRecipientAfterTransfer.getTransactions().stream()
                .filter(transaction -> transaction.getRelatedAccountId().equals(senderAccount.getId())).findFirst().get().getType())
                .isEqualTo(TransactionType.TRANSFER_IN.toString());

//        Проверяем, что время транзакции совпадает у отправителя и получателя
        assertThat(accountSenderAfterTransfer.getTransactions().stream()
                .filter(transaction -> transaction.getRelatedAccountId().equals(accountRecipientAfterTransfer.getId())).findFirst().get().getTimestamp())
                .isEqualTo(accountRecipientAfterTransfer.getTransactions().stream()
                        .filter(transaction -> transaction.getRelatedAccountId().equals(accountSenderAfterTransfer.getId())).findFirst().get().getTimestamp());
    }

    @Test
    @UserSession(2)
    void userCannotTransferMoney() {
        //        create senderAccount
        CreateAccountResponseModel senderAccount =
                SessionStorage.getSteps(1).createAccount();

        Float deposit = 100.0F;
        SessionStorage.getSteps(1).depositMoney(senderAccount, 100.0F);

//        create userRecipient and create a recipientAccount
    CreateAccountResponseModel recipientAccount =
            SessionStorage.getSteps(2).createAccount();

//        Start UI test
        Float amountToTransfer = 99999.0F;
        new UserDashboard().open().makeTransfer()
                .getPage(TransferPage.class)
                .sendTransfer(senderAccount.getAccountNumber(), recipientAccount.getAccountNumber(), amountToTransfer)
                .checkAlertMessageAndAccept(
                        BankAlert.ERROR_EXCEED_10000.getMessage(),
                        BankAlert.ERROR_INSUFFICIENT_FUNDS.getMessage(),
                        BankAlert.NO_USER_FOUND_WITH_THIS_ACCOUNT.getMessage())
                .getLogoutButton().click();

//        check balance userRecipient on UI
        authAsUser(SessionStorage.getUser(2));
        new DepositPage().open().checkAccountBalance(recipientAccount.getAccountNumber(), recipientAccount.getBalance());

//        Check both accounts on API level:

//        take current info about the senderAccount after transfer
        GetCustomerAccountsResponseModel accountSenderAfterTransfer =
                SessionStorage.getSteps(1).getAccountWIthSpecificNumber(senderAccount.getAccountNumber());

//        take current info about the recipientAccount after transfer
        GetCustomerAccountsResponseModel accountRecipientAfterTransfer =
                SessionStorage.getSteps(2).getAccountWIthSpecificNumber(recipientAccount.getAccountNumber());


//        Check on API level that balance stayed the same on both accounts
        assertThat(deposit).isEqualTo(accountSenderAfterTransfer.getBalance());
        assertThat(recipientAccount.getBalance()).isEqualTo(accountRecipientAfterTransfer.getBalance());
    }
}
