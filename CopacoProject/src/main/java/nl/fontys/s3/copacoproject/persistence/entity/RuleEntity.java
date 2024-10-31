package nl.fontys.s3.copacoproject.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JoinFormula;

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

//
//    @Column(name = "component1_id")
//    private long component1Id;
//
//    @Column(name = "component2_id")
//    private long component2Id;

    @JoinColumn(name="specification1_id", referencedColumnName = "id")
    @ManyToOne
    private SpecficationTypeList_ComponentTypeEntity specificationToConsider1Id;

    @JoinColumn(name="specification2_id",referencedColumnName = "id")
    @ManyToOne
    private SpecficationTypeList_ComponentTypeEntity specificationToConsider2Id;

}
