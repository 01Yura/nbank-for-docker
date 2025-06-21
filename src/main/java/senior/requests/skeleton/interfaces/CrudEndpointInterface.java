package senior.requests.skeleton.interfaces;

import senior.models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);
    Object delete (Long id);
    Object get();
    Object put(BaseModel model);

}
