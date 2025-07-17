package ui.iteration1_junior_level;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import api.senior.models.CreateAccountResponseModel;
import api.senior.models.CreateUserRequestModel;
import api.senior.models.LoginUserRequestModel;
import api.senior.requests.skeleton.requesters.CrudRequester;
import api.senior.requests.skeleton.requesters.Endpoint;
import api.senior.requests.steps.AdminSteps;
import api.senior.specs.RequestSpecs;
import api.senior.specs.ResponseSpecs;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateAccountTest {
    @BeforeAll
    static void setupSelenoid() {
//        Configuration.remote = "http://192.168.0.127:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.0.22:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";


        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true,
                        "enableLog", true,
                        "enableVideo", true)
        );
    }

    @Test
    void userCanCreateAccount() {
//        STEPS TO SET UP ENVIRONMENT: (делаются на уровне API)
//        1. Admin login
//        2. Admin create user
//        3. User login

        CreateUserRequestModel user = AdminSteps.createUser();

        String userAuthToken = new CrudRequester(RequestSpecs.unauthSpec(),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.AUTH_LOGIN)
                .post(LoginUserRequestModel.builder().username(user.getUsername()).password(user.getPassword()).build())
                .extract()
                .header("Authorization");

        Selenide.open("/");

        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthToken);

        Selenide.open("/dashboard");

//        TEST STEPS:
//        4. User creates account
        $(Selectors.byText("➕ Create New Account")).click();

//        Account has been created on UI
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains("✅ New Account Created! Account Number:");
        String alertText = alert.getText();

        Pattern pattern = Pattern.compile("Account Number: (\\w+)");
        Matcher matcher = pattern.matcher(alertText);
        matcher.find();
        String accountNumber = matcher.group(1);

//        Account has been created on API
        List<CreateAccountResponseModel> existingUserAccounts = given()
                .spec(RequestSpecs.userSpec(user.getUsername(), user.getPassword()))
                .when()
                .get("http://localhost:4111/api/v1/customer/accounts")
                .then()
                .assertThat()
                .extract().as(new TypeRef<List<CreateAccountResponseModel>>() {
                });

        assertThat(existingUserAccounts).hasSize(1);

        CreateAccountResponseModel createdAccount = existingUserAccounts.getFirst();

        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getBalance()).isZero();
    }
}
