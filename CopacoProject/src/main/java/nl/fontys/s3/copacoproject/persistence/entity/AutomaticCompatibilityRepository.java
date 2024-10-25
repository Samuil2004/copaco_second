package nl.fontys.s3.copacoproject.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutomaticCompatibilityRepository extends JpaRepository<AutomaticCompatibilityEntity, Long> {
    List<AutomaticCompatibilityEntity> findByComponent1Id_IdOrComponent2Id_Id(Long component1Id, Long component2Id);

}
