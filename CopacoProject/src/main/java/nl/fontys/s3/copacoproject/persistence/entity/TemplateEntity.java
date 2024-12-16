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
@Table(name = "template")  // Ensure the table name matches the database
public class TemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @NotNull
    private CategoryEntity category;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "configuration_type")
    @NotNull
    private String configurationType;

    @Column(name = "image_url")
    private String imageURL;
}
