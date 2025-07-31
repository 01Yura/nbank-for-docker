package api.requests.skeleton.interfaces;

//      Метод getAll используется для получения списка всех объектов определённого типа
//      (например, всех пользователей, всех аккаунтов и т.д.) с сервера через API.
public interface GetAllEndpointsInterface {
    Object getAll(Class<?> clazz);
}
