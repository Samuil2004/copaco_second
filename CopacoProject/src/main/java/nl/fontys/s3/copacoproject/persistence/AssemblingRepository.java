package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.AssemblingEntity;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import nl.fontys.s3.copacoproject.persistence.entity.CustomProductEntity;
import nl.fontys.s3.copacoproject.persistence.entity.primaryKeys.AssemblingCPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssemblingRepository extends JpaRepository<AssemblingEntity, AssemblingCPK> {
    List<AssemblingEntity> findAssemblingEntitiesByCustomProductId(CustomProductEntity customProductEntity);

    @Query("SELECT a FROM AssemblingEntity a WHERE a.customProductId.id = :customProductId")
    List<AssemblingEntity> findAssemblingEntitiesByCustomProductId(@Param("customProductId") Long customProductId);

    void deleteAssemblingEntitiesByCustomProductId(CustomProductEntity customProductEntity);
    boolean existsAssemblingEntityByComponentIdAndCustomProductId(ComponentEntity componentEntity, CustomProductEntity customProductEntity);
}
