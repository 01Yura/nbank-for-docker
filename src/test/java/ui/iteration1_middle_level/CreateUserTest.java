package ui.iteration1_middle_level;

import api.senior.generators.RandomModelGenerator;
import api.senior.models.CreateUserRequestModel;
import api.senior.models.CreateUserResponseModel;
import api.senior.models.comparison.ModelAssertions;
import api.senior.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import ui.middle.pages.AdminPanel;
import ui.middle.pages.BankAlert;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CreateUserTest extends BaseUiTest {
    @Test
    void adminCanCreateUserWithValidCredentials() {
        //        Login as admin
        CreateUserRequestModel admin = CreateUserRequestModel.getAdmin();
        authAsUser(admin);

//        Create a user and check alert message and that user is shown on UI
        CreateUserRequestModel user =
                RandomModelGenerator.generateRandomModel(CreateUserRequestModel.class);

        new AdminPanel().open().createUser(user.getUsername(), user.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY.getMessage())
                .getAllUsers()
                .findBy(Condition.partialText(user.getUsername())).shouldBe(Condition.visible);

//        Check that a user was created on API
        CreateUserResponseModel createdUser =
                AdminSteps.getAllUsers().stream().filter(u -> u.getUsername().equals(user.getUsername())).findFirst().get();

        ModelAssertions.assertThatModels(user, createdUser).match();
    }

    @Test
    void adminCannotCreateUserWithInvalidCredentials() {
        //        Login as admin
        CreateUserRequestModel admin = CreateUserRequestModel.getAdmin();
        authAsUser(admin);

//        Create user
        CreateUserRequestModel user =
                RandomModelGenerator.generateRandomModel(CreateUserRequestModel.class);

        user.setUsername("a"); // меняем username на invalid

        new AdminPanel().open().createUser(user.getUsername(), user.getPassword())
                .checkAlertMessageAndAccept(BankAlert.FAILD_TO_CREATE_USER.getMessage())
                .getAllUsers().findBy(Condition.exactText(user.getUsername() + "\nUSER")).shouldNotBe(Condition.exist);

//        Check that the user was not created on API
        long numberOfUsersWithSameUsernameAsCreatedUser =
                AdminSteps.getAllUsers().stream()
                        .filter(u -> u.getUsername().equals(user.getUsername())).count();

        assertThat(numberOfUsersWithSameUsernameAsCreatedUser).isZero();
    }
}
