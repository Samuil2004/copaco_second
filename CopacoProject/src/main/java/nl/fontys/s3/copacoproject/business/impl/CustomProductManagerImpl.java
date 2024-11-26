package nl.fontys.s3.copacoproject.business.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.CustomProductManager;
import nl.fontys.s3.copacoproject.business.Exceptions.ActionDeniedException;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.Exceptions.UnauthorizedException;
import nl.fontys.s3.copacoproject.business.converters.ComponentConverter;
import nl.fontys.s3.copacoproject.business.converters.StatusConverter;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.*;
import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.domain.enums.Status;
import nl.fontys.s3.copacoproject.persistence.*;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class CustomProductManagerImpl implements CustomProductManager {

    private final CustomProductRepository customProductRepository;
    private final AssemblingRepository assemblingRepository;
    private final ComponentRepository componentRepository;
    private final UserRepository userRepository;
    private final TemplateRepository templateRepository;
    private final StatusRepository statusRepository;
    private final ComponentSpecificationListRepository componentSpecificationListRepository;


    @Override
    @Transactional
    public CreateCustomProductResponse createCustomProduct(CreateCustomProductRequest request, long authenticatedUserId) {
        if(request.getUserId() != authenticatedUserId) {
            throw new UnauthorizedException("You are not authorized to perform this operation");
        }

        validateCreateRequest(request);
        UserEntity userEntity = userRepository.findUserEntityById(request.getUserId());
        TemplateEntity templateEntity = templateRepository.findTemplateEntityById(request.getTemplateId());
        Status status = Status.fromValue(request.getStatusId());

        CustomProductEntity customProductEntity = CustomProductEntity.builder()
                .userId(userEntity)
                .template(templateEntity)
                .status(StatusConverter.convertFromBaseToEntity(status))
                .build();
        CustomProductEntity productEntity = customProductRepository.save(customProductEntity);
        assemble(request.getComponentsIncluded(), productEntity);
        return CreateCustomProductResponse.builder().createdProductId(productEntity.getId()).build();
    }

    @Override
    public List<CustomProductResponse> getCustomProductsOfUserByState(long userId, long authenticatedUser, GetCustomProductsByUserAndStatusRequest request) {
        validateGetRequest(userId, authenticatedUser, request);
        List<CustomProductResponse> customProducts = new ArrayList<>();
        Status status = Status.fromValue(request.getStatusId());
        StatusEntity statusEntity = StatusEntity.builder()
                .id(status.getValue())
                .name(status.name())
                .build();
        UserEntity userEntity = userRepository.findUserEntityById(userId);
        List<CustomProductEntity> productEntities = customProductRepository.findCustomProductEntitiesByStatusAndUserId(statusEntity, userEntity);
        for(CustomProductEntity productEntity : productEntities) {
            List<Component> components = getComponentsOfCustomProductEntity(productEntity);
            customProducts.add(CustomProductResponse.builder()
                    .customProductId(productEntity.getId())
                    .componentsIncluded(components)
                    .statusId(status.getValue())
                    .userId(userId)
                    .templateId(productEntity.getTemplate().getId())
                    .build());
        }
        return customProducts;
    }

    @Transactional
    @Override
    public void deleteCustomProduct(long productId, long authenticatedUserId) {
        CustomProductEntity productEntity = customProductRepository.findById(productId);
        if(authenticatedUserId != productEntity.getUserId().getId()) {
            throw new UnauthorizedException("You are not authorized to perform this operation");
        }
        if(!customProductRepository.existsById(productId)) {
            throw new ObjectNotFound("Custom product not found");
        }
        if(Objects.equals(productEntity.getStatus().getName(), Status.FINISHED.name())){
            throw new ActionDeniedException("You cannot delete a finished product");
        }
        assemblingRepository.deleteAssemblingEntitiesByCustomProductId(productEntity);
        customProductRepository.deleteById(productId);
    }

    private void validateCreateRequest(CreateCustomProductRequest request){

        if(!userRepository.existsById(request.getUserId())) {
            throw new ObjectNotFound("User not found");
        }
        if(!templateRepository.existsById(request.getTemplateId())) {
            throw new ObjectNotFound("Template not found");
        }
        if(!statusRepository.existsById(request.getStatusId())){
            throw new ObjectNotFound("Status not found");
        }
    }

    private void assemble(List<ComponentInCustomProductInput> components, CustomProductEntity customProductEntity) {
        for (ComponentInCustomProductInput input : components) {
            if(componentRepository.existsById(input.getComponentId())) {
                ComponentEntity component = componentRepository.findComponentEntityByComponentId(input.getComponentId());
                AssemblingEntity assembling = AssemblingEntity.builder()
                        .componentId(component)
                        .customProductId(customProductEntity)
                        .build();
                assemblingRepository.save(assembling);
            }
            else{
                throw new ObjectNotFound("There is no component with id " + input.getComponentId());
            }
        }
    }

    private void validateGetRequest( long userId,long authenticatedUserId, GetCustomProductsByUserAndStatusRequest request) {

        if(userId != authenticatedUserId) {
            throw new UnauthorizedException("You are not authorized to perform this operation");
        }
        if(!userRepository.existsById(userId)) {
            throw new ObjectNotFound("User not found");
        }
        if(!statusRepository.existsById(request.getStatusId())){
            throw new ObjectNotFound("Invalid status");
        }
    }

    private List<Component> getComponentsOfCustomProductEntity (CustomProductEntity entity){
        List<Component> components = new ArrayList<>();
        List<AssemblingEntity> componentEntities = assemblingRepository.findAssemblingEntitiesByCustomProductId(entity);
        for(AssemblingEntity assembling : componentEntities){
            ComponentEntity componentEntity = assembling.getComponentId();
            List<Component_SpecificationList> allSpecificationsForComponent = componentSpecificationListRepository.findByComponentId(componentEntity);
            Map<SpecificationTypeEntity, List<String>> dictionaryWithTheSpecificationAndAllValuesForComponent = new HashMap<>();

            for(Component_SpecificationList specificationList : allSpecificationsForComponent){
                SpecificationTypeEntity specType = specificationList.getSpecificationType();
                String value = specificationList.getValue();

                List<String> valuesList = dictionaryWithTheSpecificationAndAllValuesForComponent.get(specType);

                if (valuesList == null) {
                    valuesList = new ArrayList<>();
                    valuesList.add(value);
                    dictionaryWithTheSpecificationAndAllValuesForComponent.put(specType, valuesList);
                } else {
                    valuesList.add(value);
                }
            }
            Component componentBase = ComponentConverter.convertFromEntityToBase(componentEntity,dictionaryWithTheSpecificationAndAllValuesForComponent);
            components.add(componentBase);
        }

        return components;
    }
}
