package common.storage;

import api.models.CreateUserRequestModel;
import api.requests.steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SessionStorage {
//    Для внедрения многопоточности нужно обернуть SessionStorage в ThreadLocal
//    ThreadLocal<T> — это механизм, который позволяет хранить переменную отдельно для каждого потока.
//    У каждого потока будет своя собственная копия SessionStorage. Каждый поток обращаясь к INSTANCE.get() получает свою копию
//    Там под капотом Map<Thread, SessionStorage>
//    Это используется, например, чтобы каждый поток-тест хранил свои шаги (userStepsMap) изолированно и не мешал другим.
    private static final ThreadLocal<SessionStorage> INSTANCE = ThreadLocal.withInitial(SessionStorage::new);

    private final LinkedHashMap<CreateUserRequestModel, UserSteps> userStepsMap = new LinkedHashMap<>();

    private SessionStorage(){};

    public static void addUser(List<CreateUserRequestModel> users){
        for (CreateUserRequestModel user : users) {
            INSTANCE.get().userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
        }
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

    public static CreateUserRequestModel getFirstUser(){
        return getUser(1);
    }

    public static UserSteps  getSteps(int number){
        List<UserSteps> steps = new ArrayList<>(INSTANCE.get().userStepsMap.values());
        return steps.get(number-1);
    }

    public static UserSteps getFirstStep(){
        return getSteps(1);
    }

    public static void clearStorage(){
        INSTANCE.get().userStepsMap.clear();
    }

}
