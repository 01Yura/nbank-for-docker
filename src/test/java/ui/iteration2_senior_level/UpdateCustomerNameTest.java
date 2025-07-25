package ui.iteration2_senior_level;

import api.senior.models.GetCustomerProfileResponseModel;
import com.codeborne.selenide.Condition;
import common.annotations.Browsers;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Test;
import ui.middle.pages.BankAlert;
import ui.middle.pages.EditProfilePage;
import ui.middle.pages.UserDashboard;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateCustomerNameTest extends BaseUiTest {
    @Test
    @Browsers({"chrome"})
    @UserSession
    void userCanChangeTheirNameUsingValidName() throws InterruptedException {
//        STEPS OF TEST:
//        check that the initial name is "noname" on UI
        String initialName = "noname";
        new UserDashboard().open().getUserNameInfoIcon().shouldHave(Condition.text(initialName));

//        check that the initial name is null on API
        GetCustomerProfileResponseModel initialCustomerProfile = SessionStorage.getFirstStep().getUserInfo();
        assertThat(initialCustomerProfile.getName()).isNull();

//        change name
        String newUserName = "New Name";
        new EditProfilePage().open().changeName(newUserName).checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage());

//        check that the name has been changed on UI
        new EditProfilePage().open().getUserNameInfoIcon().shouldHave(Condition.text(newUserName));

//        check that the name has been changed on API
        GetCustomerProfileResponseModel finalCustomerProfile = SessionStorage.getFirstStep().getUserInfo();
        assertThat(finalCustomerProfile.getName()).isEqualTo(newUserName);
    }


    @Test
    @Browsers({"chrome"})
    @UserSession
    void userCannotChangeTheirNameUsingInvalidName() throws InterruptedException {
//        STEPS OF TEST:
//        check that the initial name is "noname" on UI
        String initialName = "noname";
        new UserDashboard().open().getUserNameInfoIcon().shouldHave(Condition.text(initialName));

//        check that the initial name is null on API
        GetCustomerProfileResponseModel initialCustomerProfile = SessionStorage.getFirstStep().getUserInfo();
        assertThat(initialCustomerProfile.getName()).isNull();

//        change the name
        String newUserIncorrectName = "IncorrectName";
        new EditProfilePage().open().changeName(newUserIncorrectName).checkAlertMessageAndAccept(
                BankAlert.NAME_MUST_CONTAIN_TWO_WORDS.getMessage(),
                BankAlert.PLEASE_ENTER_A_VALID_NAME.getMessage());

//        check that the name has not been changed on UI
        new EditProfilePage().open().getUserNameInfoIcon().shouldHave(Condition.text(initialName));

//        check that the name has not been changed on API
        GetCustomerProfileResponseModel finalCustomerProfile = SessionStorage.getFirstStep().getUserInfo();
        assertThat(finalCustomerProfile.getName()).isNull();
    }
}
