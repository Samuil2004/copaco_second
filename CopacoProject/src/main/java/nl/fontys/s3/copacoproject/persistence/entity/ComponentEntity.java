package nl.fontys.s3.copacoproject.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

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

    @Column(name="imageURL")
    @NotNull
    @Length(max = 256)
    private String componentImageUrl;

    @JoinColumn(name="brand_id")
    @ManyToOne
    private BrandEntity brand;

    @Column(name="price")
    private Double componentPrice;
}
