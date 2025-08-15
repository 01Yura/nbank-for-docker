package api;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequestModel;
import api.models.CreateUserResponseModel;
import api.models.comparison.ModelAssertions;
import api.requests.skeleton.requesters.CrudRequester;
import api.requests.skeleton.requesters.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class CreateUserApiTest extends BaseApiTest {

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
//             Negative: Authorized admin cannot create a new user with a role is not from the list
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
        CreateUserRequestModel createUserRequestModel = RandomModelGenerator.generateRandomModel(CreateUserRequestModel.class);

        CreateUserResponseModel createUserResponseModel = new ValidatedCrudRequester<CreateUserResponseModel>(RequestSpecs.adminSpec(), ResponseSpecs.responseReturns201Spec(), Endpoint.ADMIN_USERS)
                .post(createUserRequestModel);

        ModelAssertions.assertThatModels(createUserRequestModel, createUserResponseModel).match();

//        softly.assertThat(createUserResponseModel.getUsername()).isEqualTo(createUserRequestModel.getUsername());
//        softly.assertThat(createUserResponseModel.getPassword()).isNotEqualTo(createUserRequestModel.getPassword());
//        softly.assertThat(createUserResponseModel.getRole()).isEqualTo(createUserRequestModel.getRole());
    }


    @Test
    void adminCannotCreateUserWhoIsAlreadyExist() {
//        Create user
        CreateUserRequestModel createUserRequestModel = AdminSteps.createUser();

//        Create the same user
        String expextedError = "Error: Username '" + createUserRequestModel.getUsername() + "' already exists.";

        new CrudRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.responseReturns400WithoutKeyValueSpec(expextedError),
                Endpoint.ADMIN_USERS)
                .post(createUserRequestModel);
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

        new CrudRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.responseReturns400Spec(errorKey, errorValue),
                Endpoint.ADMIN_USERS)
                .post(createUserRequestModel);
    }
}
