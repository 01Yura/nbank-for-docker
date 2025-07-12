package api.iteration1_middle_level;

import middle.generators.RandomData;
import middle.models.CreateUserRequestModel;
import middle.models.CreateUserResponseModel;
import middle.models.UserRole;
import middle.requests.CreateUserRequestSender;
import middle.specs.RequestSpecs;
import middle.specs.ResponseSpecs;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class CreateUserTest extends BaseTest {

    static Stream<Arguments> argsFor_AdminCannotCreateUserWithInvalidCredentials() {
        return Stream.of(
//            Username field validation:
//            Negative: Authorized admin cannot create new user with blank username
                Arguments.of("", "TestUser2!", "USER", "username", "Username cannot be blank"),
//             Negative: Authorized admin cannot create a new user with username consists of 2 characters
                Arguments.of("Te", "TestUser3!", "USER", "username", "Username must be between 3 and 15 characters"),
//             Negative: Authorized admin cannot create a new user with username consists of 16 characters
                Arguments.of("TestUserUserUser", "TestUser4!", "USER", "username", "Username must be between 3 and 15 characters"),
//             Negative: Authorized admin cannot create new user with username has invalid characters (@#$%^&*)
                Arguments.of("TestUser5#", "TestUser5!", "USER", "username", "Username must contain only letters, digits, dashes, underscores, and dots"),

//             Role field validation:
//             Negative: Authorized admin cannot create new user with a role is not from the list
                Arguments.of("TestUser6", "TestUser16", "SUPERADMIN", "role", "Role must be either 'ADMIN' or 'USER'"),

//             Password field validation:
//             Negative: Authorized admin cannot create new user with valid username and invalid password (less then 8 symbols)
                Arguments.of("TestUser7", "Seven7!", "USER", "password", "Password must contain at least one digit, " +
                        "one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"),
//             Negative: Authorized admin cannot create new user with valid username and invalid password (without special characters)
                Arguments.of("TestUser8", "NoSpecial1", "USER", "password", "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"),
//             Negative: Authorized admin cannot create new user with valid username and invalid password (without an uppercase letter)
                Arguments.of("TestUser9", "nouppercase1!", "USER", "password", "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"),
//             Negative: Authorized admin cannot create new user with valid username and invalid password (without a lowercase letter)
                Arguments.of("TestUser10", "NOLOWERCASE1!", "USER", "password", "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"),
//             Negative: Authorized admin cannot create new user with valid username and invalid password (without digits)
                Arguments.of("TestUser11", "NoNumber!", "USER", "password", "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"),
//             Negative: Authorized admin cannot create new user with valid username and invalid password (with spaces)
                Arguments.of("TestUser12", "With spaces1!", "USER", "password", "Password must contain at least one digit, one lower case, one upper case, one special character, no spaces, and be at least 8 characters long"),
//             Negative: Authorized admin cannot create new user with username with blank password
                Arguments.of("TestUser13", "", "USER", "password", "Password cannot be blank")
        );
    }


    @Test
    void adminCanCreateUserWithValidCredentials() {
        CreateUserRequestModel createUserRequestModel = CreateUserRequestModel.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        CreateUserResponseModel responseBody = new CreateUserRequestSender(RequestSpecs.adminSpec(), ResponseSpecs.responseReturns201Spec())
                .request(createUserRequestModel)
                .extract().as(CreateUserResponseModel.class);

        softly.assertThat(responseBody.getUsername()).isEqualTo(createUserRequestModel.getUsername());
        softly.assertThat(responseBody.getPassword()).isNotEqualTo(createUserRequestModel.getPassword());
        softly.assertThat(responseBody.getRole()).isEqualTo(createUserRequestModel.getRole());
    }


    @Test
    void adminCannotCreateUserWhoIsAlreadyExist() {
//        Create user
        String username = RandomData.getUsername();
        String password = RandomData.getPassword();

        CreateUserRequestModel createUserRequestModel = CreateUserRequestModel.builder()
                .username(username)
                .password(password)
                .role(UserRole.USER.toString())
                .build();

        given()
                .spec(RequestSpecs.adminSpec())
                .body(createUserRequestModel)
                .when()
                .post("api/v1/admin/users");

//        Create the same user
        given()
                .spec(RequestSpecs.adminSpec())
                .body(createUserRequestModel)
                .when()
                .post("api/v1/admin/users")
                .then()
                .statusCode(400)
                .body(Matchers.containsString(String.format("Error: Username '%s' already exists.", username)));
    }


    @ParameterizedTest
    @MethodSource("argsFor_AdminCannotCreateUserWithInvalidCredentials")
    void adminCannotCreateUserWithInvalidCredentials(String username, String password, String role, String errorKey,
                                                     String errorValue) {

        CreateUserRequestModel createUserRequestModel = CreateUserRequestModel.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();

        new CreateUserRequestSender(RequestSpecs.adminSpec(), ResponseSpecs.responseReturns400Spec(errorKey, errorValue))
                .request(createUserRequestModel);
    }
}
