package nl.fontys.s3.copacoproject.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AutomaticCompatibilityRepository extends JpaRepository<AutomaticCompatibilityEntity, Long> {
    List<AutomaticCompatibilityEntity> findByComponent1Id_IdOrComponent2Id_Id(Long component1Id, Long component2Id);

    //Old
//    @Query("SELECT c FROM AutomaticCompatibilityEntity c " +
//            "WHERE (c.component1Id = :component1 AND c.component2Id = :component2) " +
//            "OR (c.component1Id = :component2 AND c.component2Id = :component1)")
//    List<AutomaticCompatibilityEntity> findCompatibilityRecordsBetweenTwoComponentTypes(
//            @Param("component1") ComponentTypeEntity component1,
//            @Param("component2") ComponentTypeEntity component2);

    //New

    @Query("SELECT c FROM AutomaticCompatibilityEntity c " +
            "WHERE (c.component1Id.id = :component1Id AND c.component2Id.id = :component2Id) " +
            "   OR (c.component1Id.id = :component2Id AND c.component2Id.id = :component1Id)")
    List<AutomaticCompatibilityEntity> findCompatibilityRecordsBetweenTwoComponentTypeIds(
            @Param("component1Id") Long component1Id,
            @Param("component2Id") Long component2Id);


}
