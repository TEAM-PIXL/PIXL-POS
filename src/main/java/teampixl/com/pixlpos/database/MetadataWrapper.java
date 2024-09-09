package teampixl.com.pixlpos.database;

import java.util.Map;
import java.util.Objects;



/*==============================================================================================================================
 MetadataWrapper is a wrapper class that encapsulates a map of metadata. It is used to store metadata for a record in the
 database. The class is immutable and unmodifiable, and it implements the Comparable interface to allow for sorting based on
 a unique identifier, such as the id field. The class also overrides the equals, hashCode, and toString methods.
 ============================================================================================================================*/



public record MetadataWrapper(Map<String, Object> metadata) implements Comparable<MetadataWrapper> {

    public MetadataWrapper {
        metadata = Map.copyOf(metadata);
    }

    @Override
    public int compareTo(MetadataWrapper other) {
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

