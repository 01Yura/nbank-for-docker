package ui.iteration1_senior_level;

import api.senior.models.GetCustomerAccountsResponseModel;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.senior.pages.BankAlert;
import ui.senior.pages.UserDashboard;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest extends BaseUiTest {
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
