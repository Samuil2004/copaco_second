package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.customProductDto.CreateCustomProductRequest;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.CreateCustomProductResponse;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.CustomProductResponse;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.GetCustomProductsByUserAndStatusRequest;

import java.util.List;

public interface CustomProductManager {
    CreateCustomProductResponse createCustomProduct(CreateCustomProductRequest request, long authenticatedUserId);
    List<CustomProductResponse> getCustomProductsOfUserByState(long userId, long authenticatedUser, GetCustomProductsByUserAndStatusRequest request);
    void deleteCustomProduct(long productId, long authenticatedUserId);
}
