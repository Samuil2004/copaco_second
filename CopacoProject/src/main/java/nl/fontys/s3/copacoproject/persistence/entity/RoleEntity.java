package nl.fontys.s3.copacoproject.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="role")
public class RoleEntity {
    @Id
    @Column(name = "id")
    private long id;

    @Column(name="name")
    @NotNull
    private String roleName;
}
