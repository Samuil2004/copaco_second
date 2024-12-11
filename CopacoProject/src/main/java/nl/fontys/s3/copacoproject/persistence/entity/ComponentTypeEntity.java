package nl.fontys.s3.copacoproject.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
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
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private CategoryEntity category;

    @OneToMany(mappedBy = "componentType", fetch = FetchType.LAZY)
    private List<SpecficationTypeList_ComponentTypeEntity> specifications;
}
