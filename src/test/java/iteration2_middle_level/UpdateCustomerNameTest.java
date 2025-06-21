package iteration2_middle_level;

import middle.models.GetCustomerProfileResponseModel;
import middle.models.UpdateCustomerNameRequestModel;
import middle.models.UpdateCustomerNameResponseModel;
import middle.requests.GetCustomerProfileRequestSender;
import middle.requests.UpdateCustomerNameRequestSender;
import middle.specs.RequestSpecs;
import middle.specs.ResponseSpecs;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class UpdateCustomerNameTest extends BaseTest {

    static Stream<Arguments> argsFor_userCanChangeTheirNameUsingValidName() {
        return Stream.of(
//                Positive: Authorized user can change their name using a valid value
                Arguments.of("New Name"),
                Arguments.of("NEW NAME"),
                Arguments.of("N N"),
                Arguments.of("n n"),
                Arguments.of("N name"),
                Arguments.of("New n")
        );
    }


    static Stream<Arguments> argsFor_userCannotChangeTheirNameUsingInvalidName() {
        return Stream.of(
//                Negative: Authorized user CANNOT change their name using invalid value (one word)
                Arguments.of("Newname", "Name must contain two words with letters only"),
//                Negative: Authorized user CANNOT change their name using invalid value (+ one digit)
                Arguments.of("New name1", "Name must contain two words with letters only"),
//                Negative: Authorized user CANNOT change their name using invalid value (+ one special symbol)
                Arguments.of("New name!", "Name must contain two words with letters only"),
//                Negative: Authorized user CANNOT change their name using invalid value (empty string)
                Arguments.of("", "Name must contain two words with letters only")
        );
    }


    @MethodSource("argsFor_userCanChangeTheirNameUsingValidName")
    @ParameterizedTest
    void userCanChangeTheirNameUsingValidName(String username) {
//        Check that after @BeforeEach user creation their name is null
        GetCustomerProfileResponseModel responseModel =
                new GetCustomerProfileRequestSender(RequestSpecs.userSpec(firstUser.getUsername(),
                        firstUser.getPassword()),
                        ResponseSpecs.responseReturns200Spec())
                        .request(null)
                        .extract().as(GetCustomerProfileResponseModel.class);

        softly.assertThat(responseModel.getName()).isNull();

//        Change customer name
        UpdateCustomerNameRequestModel requestModel = UpdateCustomerNameRequestModel.builder()
                .name(username)
                .build();

        UpdateCustomerNameResponseModel updateCustomerNameResponseModel =
                new UpdateCustomerNameRequestSender(RequestSpecs.userSpec(firstUser.getUsername(),
                        firstUser.getPassword()), ResponseSpecs.responseReturns200Spec())
                        .request(requestModel)
                        .extract().as(UpdateCustomerNameResponseModel.class);

        AssertionsForClassTypes.assertThat(requestModel.getName()).isEqualTo(updateCustomerNameResponseModel.getCustomer().getName());
    }


    @ParameterizedTest
    @MethodSource("argsFor_userCannotChangeTheirNameUsingInvalidName")
    void userCannotChangeTheirNameUsingInvalidName(String newName, String errorValue) {
        //        Check that after @BeforeEach user creation their name is null
        GetCustomerProfileResponseModel responseModel =
                new GetCustomerProfileRequestSender(RequestSpecs.userSpec(firstUser.getUsername(),
                        firstUser.getPassword()),
                        ResponseSpecs.responseReturns200Spec())
                        .request(null)
                        .extract().as(GetCustomerProfileResponseModel.class);

        softly.assertThat(responseModel.getName()).isNull();

//        Change customer name
        UpdateCustomerNameRequestModel requestModel = UpdateCustomerNameRequestModel.builder()
                .name(newName)
                .build();


        new UpdateCustomerNameRequestSender(RequestSpecs.userSpec(firstUser.getUsername(),
                firstUser.getPassword()), ResponseSpecs.responseReturns400WithoutKeyValueSpec(errorValue))
                .request(requestModel);

//       Check again that after changing the name, the name is still null
        GetCustomerProfileResponseModel responseModel2 =
                new GetCustomerProfileRequestSender(RequestSpecs.userSpec(firstUser.getUsername(),
                        firstUser.getPassword()),
                        ResponseSpecs.responseReturns200Spec())
                        .request(null)
                        .extract().as(GetCustomerProfileResponseModel.class);

        softly.assertThat(responseModel2.getName()).isNull();
    }
}