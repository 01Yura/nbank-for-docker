package api.senior.requests.steps;

import api.senior.generators.RandomModelGenerator;
import api.senior.models.CreateUserRequestModel;
import api.senior.models.CreateUserResponseModel;
import api.senior.requests.skeleton.requesters.Endpoint;
import api.senior.requests.skeleton.requesters.ValidatedCrudRequester;
import api.senior.specs.RequestSpecs;
import api.senior.specs.ResponseSpecs;

import java.util.List;

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
