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
@Table(name="Automatic_compatibility")
public class AutomaticCompatibility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @JoinColumn(name="component1_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private ComponentEntity component1Id;

    @JoinColumn(name="component2_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private ComponentEntity component2Id;

    @JoinColumn(name="rule_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private RuleEntity ruleId;
}
