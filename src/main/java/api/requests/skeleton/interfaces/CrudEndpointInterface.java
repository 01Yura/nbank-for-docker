package api.requests.skeleton.interfaces;

import api.models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);
    Object delete (Long id);
    Object get();
    Object put(BaseModel model);

}
