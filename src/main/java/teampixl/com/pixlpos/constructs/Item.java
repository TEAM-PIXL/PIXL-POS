package teampixl.com.pixlpos.constructs;

import java.util.Map;

public interface Item {
    Map<String, Object> getMetadata();
    Map<String, Object> getData();
    void setMetadataValue(String key, Object value);
    void setDataValue(String key, Object value);
}
