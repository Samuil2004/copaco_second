package nl.fontys.s3.copacoproject.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fontys.s3.copacoproject.persistence.entity.primaryKeys.ComponentTypeList_Template_CPK;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Component_template")
@IdClass(ComponentTypeList_Template_CPK.class)
public class ComponentTypeList_Template {

    @Id
    @JoinColumn(name="template_id")
    @ManyToOne
    private TemplateEntity template;

    @Id
    @JoinColumn(name="component_type")
    @ManyToOne
    private ComponentTypeEntity componentType;

    @Column(name="order_of_importance")
    @NotNull
    @Min(1)
    private int orderOfImportance;
}
