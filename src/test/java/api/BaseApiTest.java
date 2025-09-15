package api;

import common.extensions.UsersWithAccountsExtension;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

// закомментил так так вылезала ошибка - NullPointer Cannot invoke "java.lang.Long.longValue()" because the return value of "java.util.Map.get(Object)" is null
//@ExtendWith(TimingExtension.class)
@ExtendWith(UsersWithAccountsExtension.class)
public class BaseApiTest {
    //    переменная amountOfAllUsers просто для наглядности, чтобы увидеть в конце прогона
//    сколько юзеров было создано и удалено соответственно
    private static int amountOfAllUsers;
    protected SoftAssertions softly;

    @BeforeEach
    public void initSoftAssertions() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void verifySoftAssertions() {
        this.softly.assertAll();
    }
}