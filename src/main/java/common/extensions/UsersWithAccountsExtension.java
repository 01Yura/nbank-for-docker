package common.extensions;

import api.models.CreateUserRequestModel;
import api.models.CreateAccountResponseModel;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import common.annotations.UsersWithAccounts;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JUnit 5 Extension, который перед каждым тестом, помеченным аннотацией
 * {@link common.annotations.UsersWithAccounts},
 *
 * 1) создаёт указанное количество пользователей через API админа
 * 2) для каждого пользователя создаёт указанное количество аккаунтов через API пользователя
 * 3) складывает созданные сущности в {@link common.storage.SessionStorage}
 * 4) при необходимости логинит пользователя в UI (кладёт токен в localStorage)
 */
public class UsersWithAccountsExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        // 1. Считываем аннотацию @UsersWithAccounts с тестового метода.
        // Если аннотации нет — ничего не делаем и выходим.
        UsersWithAccounts annotation = context.getRequiredTestMethod().getAnnotation(UsersWithAccounts.class);
        if (annotation == null) {
            return;
        }

        // 2. Достаём настройки из аннотации:
        //    - userCount: сколько пользователей создать
        //    - accountsPerUser: сколько аккаунтов создать на каждого пользователя
        int userCount = annotation.users();
        int accountsPerUser = annotation.accountsPerUser();

        // 3. На всякий случай очищаем SessionStorage перед подготовкой данных,
        //    чтобы данные из предыдущего теста не протекали в текущий.
        SessionStorage.clearStorage();

        // 4. Сначала создаём всех пользователей и запоминаем список,
        //    а шаги положим в SessionStorage единообразно через addUser.
        List<CreateUserRequestModel> createdUsers = new ArrayList<>();
        for (int i = 0; i < userCount; i++) {
            createdUsers.add(AdminSteps.createUser());
        }

        // 5. Складываем пользователей в SessionStorage — внутри создаются UserSteps
        //    и закрепляются за каждым пользователем.
        SessionStorage.addUser(createdUsers);

        // 6. Теперь создаём аккаунты, используя именно те UserSteps,
        //    которые лежат в SessionStorage. Это важно, потому что у UserSteps
        //    есть внутреннее поле "account"; метод depositMoney(depositPerCycle, depositThreshold)
        //    использует именно его. Создавая аккаунт через эти же steps, мы гарантируем,
        //    что поле будет заполнено, и тесты не упадут на NPE.
        Map<CreateUserRequestModel, List<CreateAccountResponseModel>> userToAccounts = new HashMap<>();
        for (int i = 0; i < userCount; i++) {
            CreateUserRequestModel user = SessionStorage.getUser(i + 1);
            UserSteps steps = SessionStorage.getSteps(i + 1);
            List<CreateAccountResponseModel> accounts = new ArrayList<>();
            for (int j = 0; j < accountsPerUser; j++) {
                accounts.add(steps.createAccount());
            }
            userToAccounts.put(user, accounts);
        }

        // 7. Сохраняем информацию об аккаунтах в SessionStorage
        SessionStorage.addAccounts(userToAccounts);

        // 8. Если в аннотации не отключена UI-авторизация,
        //    авторизуемся под пользователем с порядковым номером auth
        //    (это откроет браузер и положит токен в localStorage).
        int authAsUser = annotation.auth();
        if (annotation.uiAuth()) {
            BasePage.authAsUser(SessionStorage.getUser(authAsUser));
        }
    }
}


