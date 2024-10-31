package nl.fontys.s3.copacoproject.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="SpecficationType_ComponentType")
public class SpecficationTypeList_ComponentTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "component_type_id", referencedColumnName = "id")
    @ManyToOne
    private ComponentTypeEntity componentType;

    @JoinColumn(name = "specification_type_id",referencedColumnName = "id")
    @ManyToOne
    private SpecificationTypeEntity specificationType;
}
