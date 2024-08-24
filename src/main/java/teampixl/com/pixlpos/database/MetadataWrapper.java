package teampixl.com.pixlpos.database;

import java.util.Map;
import java.util.Objects;

public record MetadataWrapper(Map<String, Object> metadata) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetadataWrapper that = (MetadataWrapper) o;
        return Objects.equals(metadata, that.metadata);
    }

    @Override
    public String toString() {
        return metadata.toString();
    }
}