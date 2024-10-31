package nl.fontys.s3.copacoproject.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fontys.s3.copacoproject.domain.ComponentType;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Automatic_compatibility")
public class AutomaticCompatibilityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @JoinColumn(name="component1_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private ComponentTypeEntity component1Id;

    @JoinColumn(name="component2_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private ComponentTypeEntity component2Id;

    @JoinColumn(name="rule_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private RuleEntity ruleId;
}
