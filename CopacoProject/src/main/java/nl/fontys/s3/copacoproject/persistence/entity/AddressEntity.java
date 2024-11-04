package nl.fontys.s3.copacoproject.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Address")
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name="country")
    @NotNull
    @Length(max=50)
    private String country;

    @Column(name="city")
    @NotNull
    @Length(max=50)
    private String city;

    @Column(name="street")
    @NotNull
    @Length(max=50)
    private String street;

    @Column(name="number")
    @NotNull
    @Min(1)
    private int number;

    @Column(name="postal_code")
    @NotNull
    @Length(max=10)
    private String postalCode;
}
