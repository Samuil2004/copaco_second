package nl.fontys.s3.copacoproject.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.CompatibilityManager;
import nl.fontys.s3.copacoproject.business.converters.CompatibilityTypeConverter;
import nl.fontys.s3.copacoproject.domain.CompatibilityType;
import nl.fontys.s3.copacoproject.persistence.entity.CompatibilityTypeEntity;
import nl.fontys.s3.copacoproject.persistence.entity.CompatibilityTypeEntityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor

public class CompatibilityManagerImpl implements CompatibilityManager {
    private final CompatibilityTypeEntityRepository compatibilityTypeEntityRepository;
    @Override
    public List<CompatibilityType> allCompatibilityTypes() {
        List<CompatibilityTypeEntity> allCompatibilityTypesEntity =  compatibilityTypeEntityRepository.findAll();
        return allCompatibilityTypesEntity.stream().map(CompatibilityTypeConverter::convertFromEntityToBase).toList();
    }
}
