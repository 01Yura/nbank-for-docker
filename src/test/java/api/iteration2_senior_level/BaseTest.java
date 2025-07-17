package api.iteration2_senior_level;

import io.restassured.common.mapper.TypeRef;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import api.senior.models.CreateUserResponseModel;
import api.senior.requests.skeleton.requesters.CrudRequester;
import api.senior.requests.skeleton.requesters.Endpoint;
import api.senior.specs.RequestSpecs;
import api.senior.specs.ResponseSpecs;

import java.util.List;

public class BaseTest {
//    переменная amountOfAllUsers просто для наглядности, чтобы увидеть в конце прогона
//    сколько юзеров было создано и удалено соответственно
    private static int amountOfAllUsers;
    protected SoftAssertions softly;

    @AfterAll
    public static void deleteAllUsers() {
        List<CreateUserResponseModel> users = new CrudRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.ADMIN_USERS)
                .get()
                .extract().as(new TypeRef<List<CreateUserResponseModel>>() {
                });

        amountOfAllUsers += users.size();

        for (CreateUserResponseModel user : users) {
            new CrudRequester(RequestSpecs.adminSpec(),
                    ResponseSpecs.responseReturns200Spec(),
                    Endpoint.ADMIN_USERS)
                    .delete(user.getId());
        }

        users = new CrudRequester(RequestSpecs.adminSpec(),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.ADMIN_USERS)
                .get()
                .extract().as(new TypeRef<List<CreateUserResponseModel>>() {
                });

        if (users.isEmpty()) {
            System.out.println("All users after this test have been deleted successfully");
            System.out.println("Altogether " + amountOfAllUsers + " users have been deleted during this test run");
        }
    }

    @BeforeEach
    public void initSoftAssertions() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void verifySoftAssertions() {
        this.softly.assertAll();
    }
}