package common.extensions;

import api.senior.models.CreateUserRequestModel;
import common.annotations.AdminSession;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.senior.pages.BasePage;

// имплементируем BeforeEachCallback чтобы перед каждым методом мы анализировали наличие AdminSession аннтотации
public class AdminSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
//        1. Проверяем есть ли у теста аннотация AdminSession (для этого нужно навесить на класс аннотацию
//        ExtendWith()
        AdminSession annotation = context.getRequiredTestMethod().getAnnotation(AdminSession.class);
        if (annotation != null){    // если есть, то добавляем в localStorage token админа
            CreateUserRequestModel admin = CreateUserRequestModel.getAdmin();
            BasePage.authAsUser(admin);
        }
    }
}
