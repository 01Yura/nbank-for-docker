package common.storage;

import api.models.CreateUserRequestModel;
import api.models.CreateAccountResponseModel;
import api.requests.steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Хранилище данных текущей сессии для тестов.
 *
 * Назначение:
 * - Централизованно хранит соответствие созданных пользователей их шагам взаимодействия (`UserSteps`).
 * - Хранит связанные с пользователями созданные аккаунты (`CreateAccountResponseModel`).
 * - Предоставляет удобные методы выборки «первого/по номеру» пользователя, шагов и аккаунтов.
 *
 * Потокобезопасность:
 * Используется `ThreadLocal<SessionStorage>`, чтобы у каждого потока тестов была своя независимая копия хранилища.
 * Это предотвращает пересечения данных между параллельно выполняющимися тестами.
 */
public class SessionStorage {
//    Для внедрения многопоточности нужно обернуть SessionStorage в ThreadLocal
//    ThreadLocal<T> — это механизм, который позволяет хранить переменную отдельно для каждого потока.
//    У каждого потока будет своя собственная копия SessionStorage. Каждый поток обращаясь к INSTANCE.get() получает свою копию
//    Там под капотом концептуально Map<Thread, SessionStorage> (в реальности реализация оптимизирована, но идея именно в привязке к потоку)
//    Это используется, например, чтобы каждый поток-тест хранил свои шаги (userStepsMap) изолированно и не мешал другим.
    private static final ThreadLocal<SessionStorage> INSTANCE = ThreadLocal.withInitial(SessionStorage::new);

    private final LinkedHashMap<CreateUserRequestModel, UserSteps> userStepsMap = new LinkedHashMap<>();
    private final LinkedHashMap<CreateUserRequestModel, List<CreateAccountResponseModel>> userAccountsMap = new LinkedHashMap<>();

    private SessionStorage(){};

    /**
     * Регистрирует пользователей в текущем потоковом хранилище и создаёт для каждого экземпляр {@link UserSteps}.
     * @param users список моделей пользователей для добавления
     */
    public static void addUser(List<CreateUserRequestModel> users){
        for (CreateUserRequestModel user : users) {
            INSTANCE.get().userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
        }
    }

    /**
     * Добавляет к уже зарегистрированным пользователям списки их аккаунтов.
     * Ключи карты должны соответствовать тем же {@link CreateUserRequestModel}, что были переданы в {@link #addUser(List)}.
     * @param userToAccounts карта пользователь → список аккаунтов
     */
    public static void addAccounts(Map<CreateUserRequestModel, List<CreateAccountResponseModel>> userToAccounts){
        INSTANCE.get().userAccountsMap.putAll(userToAccounts);
    }

    /**
     * Возвращает CreateUserRequestModel из списка пользователей по порядковому номеру (а не по индексу!)
     * @param number Отсчет начинается с 1, а не с 0
     * @return Объект CreateUserRequestModel
     */
    public static CreateUserRequestModel getUser(int number){
        List<CreateUserRequestModel> users = new ArrayList<>(INSTANCE.get().userStepsMap.keySet());
        return users.get(number-1);
    }

    /**
     * Возвращает модель первого добавленного пользователя.
     * @return первый {@link CreateUserRequestModel}
     */
    public static CreateUserRequestModel getFirstUser(){
        return getUser(1);
    }

    /**
     * Возвращает объект шагов для пользователя по порядковому номеру его добавления.
     * @param number порядковый номер пользователя (начиная с 1)
     * @return {@link UserSteps} выбранного пользователя
     */
    public static UserSteps  getSteps(int number){
        List<UserSteps> steps = new ArrayList<>(INSTANCE.get().userStepsMap.values());
        return steps.get(number-1);
    }

    /**
     * Возвращает шаги первого добавленного пользователя.
     * @return {@link UserSteps} первого пользователя
     */
    public static UserSteps getFirstStep(){
        return getSteps(1);
    }

    /**
     * Очищает хранилище текущего потока: удаляет всех пользователей и их аккаунты из копии `SessionStorage` данного потока.
     * Не влияет на хранилища других потоков.
     */
    public static void clearStorage(){
        INSTANCE.get().userStepsMap.clear();
        INSTANCE.get().userAccountsMap.clear();
    }

    /**
     * Возвращает список аккаунтов пользователя по его порядковому номеру в хранилище.
     * @param userNumber порядковый номер пользователя (начиная с 1)
     * @return список аккаунтов пользователя
     */
    public static List<CreateAccountResponseModel> getAccounts(int userNumber){
        CreateUserRequestModel user = getUser(userNumber);
        return INSTANCE.get().userAccountsMap.get(user);
    }

    /**
     * Возвращает аккаунт по номеру пользователя и порядковому номеру аккаунта.
     * @param userNumber порядковый номер пользователя (начиная с 1)
     * @param accountNumber порядковый номер аккаунта пользователя (начиная с 1)
     * @return модель аккаунта
     */
    public static CreateAccountResponseModel getAccount(int userNumber, int accountNumber){
        return getAccounts(userNumber).get(accountNumber-1);
    }

    /**
     * Возвращает первый аккаунт указанного пользователя.
     * @param userNumber порядковый номер пользователя (начиная с 1)
     * @return первый аккаунт пользователя
     */
    public static CreateAccountResponseModel getFirstAccount(int userNumber){
        return getAccount(userNumber, 1);
    }

}
