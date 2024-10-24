package nl.fontys.s3.copacoproject.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Component_type")
public class ComponentTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name="name")
    @NotNull
    private String componentTypeName;

    @Column(name="image_URL")
    @NotNull
    private String componentTypeImageUrl;

    @JoinColumn(name="category_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private CategoryEntity category;

    @OneToMany(mappedBy = "componentType")
    private List<SpecficationTypeList_ComponentType> specifications;
}
