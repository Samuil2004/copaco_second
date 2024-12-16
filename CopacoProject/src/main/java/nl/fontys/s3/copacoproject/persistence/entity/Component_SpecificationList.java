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
@Table(name="Component_Specification")
public class Component_SpecificationList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @JoinColumn(name="component_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ComponentEntity componentId;

    @JoinColumn(name="specification_type_id", referencedColumnName = "id")
    @ManyToOne()
    private SpecificationTypeEntity specificationType;

    @Column(name="value")
    @NotNull
    private String value;
}
