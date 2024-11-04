package nl.fontys.s3.copacoproject.persistence.entity.primaryKeys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.TemplateEntity;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ComponentTypeList_Template_CPK implements Serializable {
    private TemplateEntity template;
    private ComponentTypeEntity componentType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentTypeList_Template_CPK that = (ComponentTypeList_Template_CPK) o;
        return Objects.equals(template, that.template) &&
                Objects.equals(componentType, that.componentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, componentType);
    }
}
