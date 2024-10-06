package teampixl.com.pixlpos.models.tools;

import java.util.HashMap;
import java.util.Map;

public abstract class DataManager {

    protected MetadataWrapper metadata;
    protected Map<String, Object> data;

    public DataManager(MetadataWrapper metadata) {
        this.metadata = metadata;
        this.data = new HashMap<>();
    }

    public Object getMetadataValue(String key) {
        return metadata.metadata().get(key);
    }

    public void updateMetadata(String key, Object value) {
        Map<String, Object> modifiableMetadata = new HashMap<>(metadata.metadata());
        if (value != null) {
            modifiableMetadata.put(key, value);
        } else {
            modifiableMetadata.remove(key);
        }
        this.metadata = new MetadataWrapper(modifiableMetadata);
    }

    public Object getDataValue(String key) {
        return data.get(key);
    }

    public void setDataValue(String key, Object value) {
        data.put(key, value);
    }

    public MetadataWrapper getMetadata() {
        return metadata;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Map<String, Object> getMetadataMap() {
        return metadata.metadata();
    }

    @Override
    public String toString() {
        return String.format("%s{Metadata: %s, Data: %s}",
                this.getClass().getSimpleName(),
                metadata != null ? metadata.metadata() : "null",
                data != null ? new HashMap<>(data) : "null");
    }
}