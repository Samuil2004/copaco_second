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
@Table(name="Rule_entity")
public class RuleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @JoinColumn(name="specification1_id")
    @ManyToOne
    private SpecficationTypeList_ComponentType specificationToConsider1Id;

    @JoinColumn(name="specification2_id")
    @ManyToOne
    private SpecficationTypeList_ComponentType specificationToConsider2Id;
}
