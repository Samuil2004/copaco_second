package nl.fontys.s3.copacoproject.persistence;

import nl.fontys.s3.copacoproject.persistence.entity.AssemblingEntity;
import nl.fontys.s3.copacoproject.persistence.entity.primaryKeys.AssemblingCPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssemblingRepository extends JpaRepository<AssemblingEntity, AssemblingCPK> {
}
