package ui;

import api.models.CreateUserRequestModel;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import common.annotations.Browsers;
import org.junit.jupiter.api.Test;
import ui.pages.AdminPanel;
import ui.pages.LoginPage;
import ui.pages.UserDashboard;

public class LoginUserUiTest extends BaseUiTest {

    @Test
    @Browsers({"chrome"})
    void adminCanLoginWithValidCredentials() {
        CreateUserRequestModel admin = CreateUserRequestModel.getAdmin();

        new LoginPage().open().wait(2).login(admin.getUsername(), admin.getPassword())
                .getPage(AdminPanel.class).getAdminPanelText()
                .shouldBe(Condition.visible);
    }

    @Test
    void userCanLoginWithValidCredentials() {
        CreateUserRequestModel user = AdminSteps.createUser();

        new LoginPage().open().wait(2).login(user.getUsername(), user.getPassword())
                .getPage(UserDashboard.class).getWelcomeText()
                .shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, " + "noname!"));

    }
}