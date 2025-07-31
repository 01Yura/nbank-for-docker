package api.requests.steps;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequestModel;
import api.models.CreateUserResponseModel;
import api.requests.skeleton.requesters.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;
/*
Классы UserSteps и AdminSteps — это шаги (steps), которые являются высокоуровневыми обёртками над вызовами API.
Они служат для того, чтобы тесты выглядели максимально читаемо и не содержали низкоуровневой логики запросов (given().when().then()).
Зачем нужны Steps-классы?
Скрыть детали запросов (какие эндпоинты, какие RequestSpec/ResponseSpec используются).
Объединить несколько API-вызовов в одну бизнес-операцию (например, создать пользователя и получить его профиль).
Сделать код тестов "чистым" и читаемым — тесты не должны знать, как именно устроен REST-запрос.
*/
public class AdminSteps {

    public static CreateUserRequestModel createUser() {
        CreateUserRequestModel createUserRequestModel = RandomModelGenerator.generateRandomModel(CreateUserRequestModel.class);

        CreateUserResponseModel createUserResponseModel =
                new ValidatedCrudRequester<CreateUserResponseModel>(RequestSpecs.adminSpec(),
                        ResponseSpecs.responseReturns201Spec(),
                        Endpoint.ADMIN_USERS)
                        .post(createUserRequestModel);

        return createUserRequestModel;
    }

    public static List<CreateUserResponseModel> getAllUsers(){
        return new ValidatedCrudRequester<CreateUserResponseModel>(
                RequestSpecs.adminSpec(),
                ResponseSpecs.responseReturns200Spec(),
                Endpoint.ADMIN_USERS).
                getAll(CreateUserResponseModel[].class);
    }
}
