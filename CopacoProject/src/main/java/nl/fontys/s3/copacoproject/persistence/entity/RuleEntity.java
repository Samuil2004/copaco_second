package nl.fontys.s3.copacoproject.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Rule_entity")
public class RuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @JoinColumn(name="specification1_id", referencedColumnName = "id")
    @ManyToOne
    private SpecficationTypeList_ComponentTypeEntity specificationToConsider1Id;

    @JoinColumn(name="specification2_id",referencedColumnName = "id")
    @ManyToOne
    private SpecficationTypeList_ComponentTypeEntity specificationToConsider2Id;

    @Column(name = "valueOfFirstSpecification", nullable = true)
    private String valueOfFirstSpecification;

    @Column(name = "valueOfSecondSpecification", nullable = true)
    private String valueOfSecondSpecification;

    @Column(name = "configuration_type", nullable = true)
    private String configurationType;

}
