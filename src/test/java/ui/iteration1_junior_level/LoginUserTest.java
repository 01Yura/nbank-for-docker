package ui.iteration1_junior_level;

import com.codeborne.selenide.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import senior.models.CreateUserRequestModel;
import senior.requests.steps.AdminSteps;

import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class LoginUserTest {
    @BeforeAll
    static void setupSelenoid() {
//        Configuration.remote = "http://192.168.0.118:32362/wd/hub";                  // c Moon
//        Configuration.remote = "http://192.168.0.127:4444/wd/hub";                     // вариант без Moon, обычныйSelenoid
        Configuration.baseUrl = "http://192.168.0.127";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";


        Configuration.browserCapabilities.setCapability("selenoid:options",             // вариант без Moon
                Map.of("enableVNC", true,
                        "enableLog", true,
                        "enableVideo", true)
        );

//        Configuration.browserCapabilities.setCapability("browserVersion", "17.0");    // вариант c Moon
//        Configuration.browserCapabilities.setCapability("selenoid:options",
//                Map.of("enableVNC", true, "enableLog", true)
//        );

    }

    @Test
    void adminCanLoginWithValidCredentials() {
        CreateUserRequestModel admin = CreateUserRequestModel.builder()
                .username("admin")
                .password("admin")
                .build();


        Selenide.open("/login");

        $x("//h1[text()='Login']");

//        тут я сначала сохраняю селектор, затем сам элемент, затем уже применяю действие к этому элементу
        By usernameFieldLocator = Selectors.byAttribute("placeholder", "Username");
        SelenideElement usernameField = $(usernameFieldLocator);
        usernameField.sendKeys(admin.getUsername());

//        здесь все пишу в одной строке, присем можно писать Selectors.byAttribute() или byAttribute())
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.getPassword());

        $("button").click();

        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);
    }

    @Test
    void userCanLoginWithValidCredentials() {
//        create user
        CreateUserRequestModel user = AdminSteps.createUser();

//        login as a user
        Selenide.open("/login");

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(user.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(user.getPassword());
        $("button").click();

        $(Selectors.byClassName("welcome-text")).shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, " +
                "noname!"));

//
    }
}