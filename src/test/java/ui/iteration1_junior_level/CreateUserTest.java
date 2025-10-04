package ui.iteration1_junior_level;

import com.codeborne.selenide.*;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import api.senior.generators.RandomModelGenerator;
import api.senior.models.CreateUserRequestModel;
import api.senior.models.CreateUserResponseModel;
import api.senior.models.comparison.ModelAssertions;
import api.senior.specs.RequestSpecs;

import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CreateUserTest {
    @BeforeAll
    static void setupSelenoid() {
//        Configuration.remote = "http://192.168.0.127:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.0.127";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";


        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true,
                        "enableLog", true,
                        "enableVideo", true)
        );
    }

    @Test
    void adminCanCreateUserWithValidCredentials() {
        //        Login as admin
        CreateUserRequestModel admin = CreateUserRequestModel.builder()
                .username("admin")
                .password("admin")
                .build();

        Selenide.open("/login");

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(admin.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.getPassword());
        $("button").click();

        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);

//        Create user
        CreateUserRequestModel user =
                RandomModelGenerator.generateRandomModel(CreateUserRequestModel.class);

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(user.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(user.getPassword());
        $(Selectors.byText("Add User")).click();

//        Check that alert equals expected alert
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).isEqualTo("✅ User created successfully!");
        alert.accept();

//        Check that user is shown on UI
        ElementsCollection allUsers = $(Selectors.byText("All Users")).parent().findAll("li");
        allUsers.findBy(Condition.partialText(user.getUsername())).shouldBe(Condition.visible);

//        Check that user was created on API
        List<CreateUserResponseModel> users = given()
                .spec(RequestSpecs.adminSpec())
                .when()
                .get("http://192.168.0.127api/v1/admin/users")
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<List<CreateUserResponseModel>>() {
                });

        CreateUserResponseModel createdUser =
                users.stream().filter(u -> u.getUsername().equals(user.getUsername())).findFirst().get();

        ModelAssertions.assertThatModels(user, createdUser).match();
    }

    @Test
    void adminCannotCreateUserWithInvalidCredentials() {
//        Login as admin
        CreateUserRequestModel admin = CreateUserRequestModel.builder()
                .username("admin")
                .password("admin")
                .build();

        Selenide.open("/login");

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(admin.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(admin.getPassword());
        $("button").click();

        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);

//        Create user
        CreateUserRequestModel user =
                RandomModelGenerator.generateRandomModel(CreateUserRequestModel.class);

        user.setUsername("a"); // меняем username на invalid

        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(user.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(user.getPassword());
        $(Selectors.byText("Add User")).click();

//        Check that alert equals expected alert
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).contains("Failed to create user");
        alert.accept();

//        Check that the user is not shown on UI
        ElementsCollection allUsers = $(Selectors.byText("All Users")).parent().findAll("li");
        allUsers.findBy(Condition.exactText(user.getUsername() + "\nUSER")).shouldNotBe(Condition.exist);

//        Check that the user was not created on API
        List<CreateUserResponseModel> users = given()
                .spec(RequestSpecs.adminSpec())
                .when()
                .get("http://192.168.0.127/api/v1/admin/users")
                .then()
                .statusCode(200)
                .extract().as(new TypeRef<List<CreateUserResponseModel>>() {
                });

        long numberOfUsersWithSameUsernameAsCreatedUser =
                users.stream().filter(u -> u.getUsername().equals(user.getUsername())).count();

        assertThat(numberOfUsersWithSameUsernameAsCreatedUser).isZero();
    }
}
