package teampixl.com.pixlpos.database;

import java.util.Map;
import java.util.Objects;

public class MetadataWrapper<T> implements Comparable<MetadataWrapper<T>> {

    private final Map<String, Object> metadata;

    public MetadataWrapper(Map<String, Object> metadata) {
        // Ensure the map is immutable and unmodifiable
        this.metadata = Map.copyOf(metadata);
    }

    public Object getMetadataField(String key) {
        return metadata.get(key);
    }

    public String getStringField(String key) {
        return (String) metadata.get(key);
    }

    public Double getDoubleField(String key) {
        return (Double) metadata.get(key);
    }

    public Integer getIntField(String key) {
        return (Integer) metadata.get(key);
    }

    public T getField(String key, Class<T> clazz) {
        return clazz.cast(metadata.get(key));
    }

    @Override
    public int compareTo(MetadataWrapper<T> other) {
        String thisId = (String) this.metadata.get("id");
        String otherId = (String) other.metadata.get("id");

        if (thisId == null || otherId == null) {
            throw new IllegalStateException("Metadata id cannot be null");
        }

        return thisId.compareTo(otherId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetadataWrapper<?> that = (MetadataWrapper<?>) o;
        return Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadata);
    }

    @Override
    public String toString() {
        return metadata.toString();
    }
}
