package common.storage;

import api.senior.models.CreateUserRequestModel;
import api.senior.requests.steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SessionStorage {
    private static final SessionStorage INSTANCE = new SessionStorage();

    private final LinkedHashMap<CreateUserRequestModel, UserSteps> userStepsMap = new LinkedHashMap<>();

    private SessionStorage(){};

    public static void addUser(List<CreateUserRequestModel> users){
        for (CreateUserRequestModel user : users) {
            INSTANCE.userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
        }
    }

    /**
     * Возвращает CreateUserRequestModel из списка пользователей по порядковому номеру (а не по индексу!)
     * @param number Отсчет начинается с 1, а не с 0
     * @return Объект CreateUserRequestModel
     */
    public static CreateUserRequestModel getUser(int number){
        List<CreateUserRequestModel> users = new ArrayList<>(INSTANCE.userStepsMap.keySet());
        return users.get(number-1);
    }

    public static CreateUserRequestModel getFirstUser(){
        return getUser(1);
    }

    public static UserSteps  getSteps(int number){
        List<UserSteps> steps = new ArrayList<>(INSTANCE.userStepsMap.values());
        return steps.get(number-1);
    }

    public static UserSteps getFirstStep(){
        return getSteps(1);
    }

    public static void clearStorage(){
        INSTANCE.userStepsMap.clear();
    }

}
