package nl.fontys.s3.copacoproject.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.fontys.s3.copacoproject.persistence.entity.primaryKeys.AssemblingCPK;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Assembling")
@IdClass(AssemblingCPK.class)
public class AssemblingEntity {

    @Id
    @JoinColumn(name="custom_product_id", referencedColumnName = "id")
    @ManyToOne
    private CustomProductEntity customProductId;

    @Id
    @JoinColumn(name="component_id", referencedColumnName = "id")
    @ManyToOne
    private ComponentEntity componentId;
}
