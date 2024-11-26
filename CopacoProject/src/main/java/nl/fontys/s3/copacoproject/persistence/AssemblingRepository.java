package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.AssemblingEntity;
import nl.fontys.s3.copacoproject.persistence.entity.CustomProductEntity;
import nl.fontys.s3.copacoproject.persistence.entity.primaryKeys.AssemblingCPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssemblingRepository extends JpaRepository<AssemblingEntity, AssemblingCPK> {
    List<AssemblingEntity> findAssemblingEntitiesByCustomProductId(CustomProductEntity customProductEntity);
    void deleteAssemblingEntitiesByCustomProductId(CustomProductEntity customProductEntity);
}
