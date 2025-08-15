package ui;

import api.models.GetCustomerAccountsResponseModel;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountUiTest extends BaseUiTest {
    @Test
    @UserSession
    void userCanCreateAccount() {
//        User creates account
        new UserDashboard().open().createNewAccount();
        List<GetCustomerAccountsResponseModel> allUsersAccounts = SessionStorage.getFirstStep()
                .getAllAccounts();

        GetCustomerAccountsResponseModel createdAccount = allUsersAccounts.getFirst();

        new UserDashboard().checkAlertMessageAndAccept(BankAlert.NEW_ACCOUNT_CREATED.getMessage() + createdAccount.getAccountNumber());

        assertThat(allUsersAccounts).hasSize(1);
        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getBalance()).isZero();
    }
}
