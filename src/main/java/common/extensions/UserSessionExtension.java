package common.extensions;

import api.models.CreateUserRequestModel;
import api.requests.steps.AdminSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

import java.util.ArrayList;
import java.util.List;

public class UserSessionExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
//        1. Проверяем, что у теста есть аннотация UserSession
        UserSession annotation = context.getRequiredTestMethod().getAnnotation(UserSession.class);
        if (annotation != null) {    // если есть, то добавляем в localStorage token админа
            int userCount = annotation.value();

            SessionStorage.clearStorage();
            List<CreateUserRequestModel> users = new ArrayList<>();

            for (int i = 0; i < userCount; i++) {
                CreateUserRequestModel user = AdminSteps.createUser();
                users.add(user);
            }

            SessionStorage.addUser(users);

            int authAsUser = annotation.auth();
            BasePage.authAsUser(SessionStorage.getUser(authAsUser));
        }
    }
}
