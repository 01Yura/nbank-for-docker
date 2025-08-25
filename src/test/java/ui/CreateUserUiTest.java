package ui;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequestModel;
import api.models.CreateUserResponseModel;
import api.models.comparison.ModelAssertions;
import api.requests.steps.AdminSteps;
import common.annotations.AdminSession;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import ui.elements.UserBage;
import ui.pages.AdminPanel;
import ui.pages.BankAlert;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CreateUserUiTest extends BaseUiTest {

    @Test
    @AdminSession
        // С помощью JUnit Extension шаги по логину как админ выполняются под капотом, а не в тесте
    void adminCanCreateUserWithValidCredentials() throws InterruptedException {
//        Create a user and check alert message and that user is shown on UI
        CreateUserRequestModel user =
                RandomModelGenerator.generateRandomModel(CreateUserRequestModel.class);

        UserBage newUserBage = new AdminPanel().open().createUser(user.getUsername(), user.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY.getMessage())
                .findUserByUsername(user.getUsername());

        assertThat(newUserBage).as("UserBage should exist on DashBoard after user creation.")
                .isNotNull();

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
