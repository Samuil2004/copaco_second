package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.domain.Brand;

import java.util.List;

public interface BrandManager {
    Brand getBrandById(long id);
    List<Brand> getAllBrands();
}
