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
@Table(name="compatibility_between_component_types")

public class CompatibilityBetweenComponentTypesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="idOfComponentType1")
    private Long idOfComponentType1;

    @Column(name="idOfComponentType2")
    private Long idOfComponentType2;

    @ManyToOne
    @JoinColumn(name = "typeOfCompatibilityId", referencedColumnName = "id")
    private CompatibilityTypeEntity compatibilityType;
}
