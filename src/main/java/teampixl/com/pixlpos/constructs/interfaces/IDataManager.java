package teampixl.com.pixlpos.constructs.interfaces;

import teampixl.com.pixlpos.database.MetadataWrapper;

import java.util.Map;

public interface IDataManager {
    Map<String, Object> getData();
    MetadataWrapper getMetadata();
    void updateMetadata(String key, Object value);
    void setDataValue(String key, Object value);
}
