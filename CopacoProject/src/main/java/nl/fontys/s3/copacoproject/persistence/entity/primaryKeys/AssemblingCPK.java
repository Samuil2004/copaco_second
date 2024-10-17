package nl.fontys.s3.copacoproject.persistence.entity.primaryKeys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssemblingCPK implements Serializable {
    private Long customProductId;
    private Long componentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssemblingCPK that = (AssemblingCPK) o;
        return Objects.equals(customProductId, that.customProductId) &&
                Objects.equals(componentId, that.componentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customProductId, componentId);
    }
}
