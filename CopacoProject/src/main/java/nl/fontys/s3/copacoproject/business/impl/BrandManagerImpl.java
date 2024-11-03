package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.converters.BrandConverter;
import nl.fontys.s3.copacoproject.domain.Brand;
import nl.fontys.s3.copacoproject.persistence.BrandRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrandManagerImpl implements BrandManager {
    private final BrandRepository brandRepository;
    @Override
    public Brand getBrandById(long id) {
        return BrandConverter.convertFromEntityToBase(brandRepository.findBrandById(id));
    }
}
