package ui.iteration1_middle_level;

import api.senior.models.CreateAccountResponseModel;
import api.senior.models.CreateUserRequestModel;
import api.senior.requests.steps.AdminSteps;
import api.senior.requests.steps.UserSteps;
import org.junit.jupiter.api.Test;
import ui.middle.pages.BankAlert;
import ui.middle.pages.UserDashboard;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest extends BaseUiTest {
    @Test
    void userCanCreateAccount() {
//        STEPS TO SET UP ENVIRONMENT: (делаются на уровне API)
//        1. Admin login + create user + User login
        CreateUserRequestModel user = AdminSteps.createUser();
        authAsUser(user);

//        4. User creates account
        new UserDashboard().open().createNewAccount();
        List<CreateAccountResponseModel> allUsersAccounts = new UserSteps(user.getUsername(), user.getPassword())
                .getAllAccounts();

        CreateAccountResponseModel createdAccount = allUsersAccounts.getFirst();

        new UserDashboard().checkAlertMessageAndAccept(BankAlert.NEW_ACCOUNT_CREATED.getMessage() + createdAccount.getAccountNumber());

        assertThat(allUsersAccounts).hasSize(1);
        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getBalance()).isZero();
    }
}
