package senior.requests.steps;

import senior.generators.RandomModelGenerator;
import senior.models.CreateUserRequestModel;
import senior.models.CreateUserResponseModel;
import senior.requests.skeleton.requesters.Endpoint;
import senior.requests.skeleton.requesters.ValidatedCrudRequester;
import senior.specs.RequestSpecs;
import senior.specs.ResponseSpecs;

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
}
