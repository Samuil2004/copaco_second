package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.customProductDto.CreateCustomProductRequest;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.CreateCustomProductResponse;

public interface CustomProductManager {
    CreateCustomProductResponse CreateCustomProduct(CreateCustomProductRequest request, long authenticatedUserId);
}
