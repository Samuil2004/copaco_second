package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.BrandManager;
import nl.fontys.s3.copacoproject.business.converters.BrandConverter;
import nl.fontys.s3.copacoproject.domain.Brand;
import nl.fontys.s3.copacoproject.persistence.BrandRepository;
import nl.fontys.s3.copacoproject.persistence.entity.BrandEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandManagerImpl implements BrandManager {
    private final BrandRepository brandRepository;
    @Override
    public Brand getBrandById(long id) {
        return BrandConverter.convertFromEntityToBase(brandRepository.findBrandById(id));
    }

    @Override
    public List<Brand> getAllBrands() {
        List<BrandEntity> brandEntities = brandRepository.findAll();
        List<Brand> brands = new ArrayList<>();
        for(BrandEntity brandEntity : brandEntities) {
            brands.add(BrandConverter.convertFromEntityToBase(brandEntity));
        }
        return brands;
    }


}
