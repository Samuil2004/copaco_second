package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.customProductDto.*;

import java.util.List;

public interface CustomProductManager {
    CreateCustomProductResponse createCustomProduct(CreateCustomProductRequest request, long authenticatedUserId);
    List<CustomProductResponse> getCustomProductsOfUserByState(long userId, long authenticatedUser, int currentPage, int itemsPerPage, int statusId);
    int getNumberOfCustomProductsOfUserByStatus(long userId, long authenticatedUser,int statusId);
    void deleteCustomProduct(long productId, long authenticatedUserId);
    void updateCustomProduct(long productId, UpdateCustomTemplateRequest request, long authenticatedUserId);
}
