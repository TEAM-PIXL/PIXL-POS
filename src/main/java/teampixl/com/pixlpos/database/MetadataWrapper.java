package teampixl.com.pixlpos.database;

import java.util.Map;
import java.util.Objects;

public record MetadataWrapper(Map<String, Object> metadata) implements Comparable<MetadataWrapper> {

    public MetadataWrapper {
        // Ensure the map is immutable and unmodifiable
        metadata = Map.copyOf(metadata);
    }

    @Override
    public int compareTo(MetadataWrapper other) {
        // Compare based on a unique identifier, such as the id field
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
        MetadataWrapper that = (MetadataWrapper) o;
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