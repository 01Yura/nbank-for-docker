package ui.iteration1_senior_level;

import api.senior.generators.RandomModelGenerator;
import api.senior.models.CreateUserRequestModel;
import api.senior.models.CreateUserResponseModel;
import api.senior.models.comparison.ModelAssertions;
import api.senior.requests.steps.AdminSteps;
import common.annotations.AdminSession;
import org.junit.jupiter.api.Test;
import ui.senior.pages.AdminPanel;
import ui.senior.pages.BankAlert;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CreateUserTest extends BaseUiTest {

    @Test
    @AdminSession
        // С помощью JUnit Extension шаги по логину как админ выполняются под капотом, а не в тесте
    void adminCanCreateUserWithValidCredentials() throws InterruptedException {
//        Create a user and check alert message and that user is shown on UI
        CreateUserRequestModel user =
                RandomModelGenerator.generateRandomModel(CreateUserRequestModel.class);

        assertTrue(new AdminPanel().open().createUser(user.getUsername(), user.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY.getMessage())
                .getAllUsers()
                .stream().anyMatch(userBage -> userBage.getUsername().equals(user.getUsername())));

//        Check that a user was created on API
        CreateUserResponseModel createdUser =
                AdminSteps.getAllUsers().stream().filter(u -> u.getUsername().equals(user.getUsername())).findFirst().get();

        ModelAssertions.assertThatModels(user, createdUser).match();
    }

    @Test
    @AdminSession
    void adminCannotCreateUserWithInvalidCredentials() throws InterruptedException {
//        Create user
        CreateUserRequestModel user =
                RandomModelGenerator.generateRandomModel(CreateUserRequestModel.class);

        user.setUsername("a"); // меняем username на invalid

        assertTrue(new AdminPanel().open().createUser(user.getUsername(), user.getPassword())
                .checkAlertMessageAndAccept(BankAlert.FAILD_TO_CREATE_USER.getMessage())
                .getAllUsers().stream().noneMatch(userBage -> userBage.getUsername().equals(user.getUsername())));

//        Check that the user was not created on API
        long numberOfUsersWithSameUsernameAsCreatedUser =
                AdminSteps.getAllUsers().stream()
                        .filter(u -> u.getUsername().equals(user.getUsername())).count();

        assertThat(numberOfUsersWithSameUsernameAsCreatedUser).isZero();
    }
}
