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
@Table(name="Custom_product")
public class CustomProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @JoinColumn(name="user_id", referencedColumnName = "id")
    @ManyToOne
    private UserEntity userId;

    @JoinColumn(name="template_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private TemplateEntity template;

    @JoinColumn(name="status_id", referencedColumnName = "id")
    @ManyToOne
    @NotNull
    private StatusEntity status;
}
