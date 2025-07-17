package ui.iteration1_middle_level;

import api.senior.models.CreateUserRequestModel;
import api.senior.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import ui.middle.pages.AdminPanel;
import ui.middle.pages.LoginPage;
import ui.middle.pages.UserDashboard;

public class LoginUserTest extends BaseUiTest {

    @Test
    void adminCanLoginWithValidCredentials() {
        CreateUserRequestModel admin = CreateUserRequestModel.getAdmin();

        new LoginPage().open().login(admin.getUsername(), admin.getPassword())
                .getPage(AdminPanel.class).getAdminPanelText()
                .shouldBe(Condition.visible);
    }

    @Test
    void userCanLoginWithValidCredentials() {
        CreateUserRequestModel user = AdminSteps.createUser();

        new LoginPage().open().login(user.getUsername(), user.getPassword())
                .getPage(UserDashboard.class).getWelcomeText()
                .shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, " + "noname!"));
    }
}