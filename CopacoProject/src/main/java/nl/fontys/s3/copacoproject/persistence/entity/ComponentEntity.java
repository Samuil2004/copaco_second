package nl.fontys.s3.copacoproject.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Component")
public class ComponentEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long componentId;

    @Column(name="name")
    @NotNull
    @Length(max = 50)
    private String componentName;

    @JoinColumn(name="component_type_id")
//    @ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private ComponentTypeEntity componentType;

    @Column(name="image_url")
    @NotNull
    @Length(max = 256)
    private String componentImageUrl;

    @Column(name="price")
    private Double componentPrice;
}
