package nl.fontys.s3.copacoproject.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Manual_compatibility")
public class ManualCompatibilityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @JoinColumn(name="specification_type1_id")
    @ManyToOne
    @NotNull
    private SpecficationTypeList_ComponentType specificationType1Id;

    @JoinColumn(name="specification_type2_id")
    @ManyToOne
    @NotNull
    private SpecficationTypeList_ComponentType specificationType2Id;

    @JoinColumn(name="specification_value1_id")
    @ManyToOne
    @NotNull
    private Component_SpecificationList specificationValue1_id;

    @JoinColumn(name="specification_value2_id")
    @ManyToOne
    @NotNull
    private Component_SpecificationList specificationValue2_id;
}

