package api.senior.requests.skeleton.interfaces;

import api.senior.models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);
    Object delete (Long id);
    Object get();
    Object put(BaseModel model);

}
