package nl.fontys.s3.copacoproject.business.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.CustomProductManager;
import nl.fontys.s3.copacoproject.business.Exceptions.ActionDeniedException;
import nl.fontys.s3.copacoproject.business.Exceptions.InvalidInputException;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.Exceptions.UnauthorizedException;
import nl.fontys.s3.copacoproject.business.converters.ComponentConverter;
import nl.fontys.s3.copacoproject.business.converters.StatusConverter;
import nl.fontys.s3.copacoproject.business.dto.component.ComponentInConfigurationResponse;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.*;
import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.domain.enums.Status;
import nl.fontys.s3.copacoproject.persistence.*;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public List<CustomProductResponse> getCustomProductsOfUserByState(long userId, long authenticatedUser, int currentPage, int itemsPerPage, int statusId) {
        validateGetRequest(userId, authenticatedUser, statusId);
        List<CustomProductResponse> customProducts = new ArrayList<>();
        Status status = Status.fromValue(statusId);
        StatusEntity statusEntity = StatusEntity.builder()
                .id(status.getValue())
                .name(status.name())
                .build();
        UserEntity userEntity = userRepository.findUserEntityById(userId);

        Pageable pageable = PageRequest.of(currentPage-1, itemsPerPage, Sort.by("id").descending());
        Page<CustomProductEntity> productEntities = customProductRepository.findCustomProductEntitiesByStatusAndUserId(statusEntity, userEntity, pageable);
        for(CustomProductEntity productEntity : productEntities) {
            List<Component> components = getComponentsOfCustomProductEntity(productEntity);
            List<ComponentInConfigurationResponse> componentsResponse = new ArrayList<>();
            for(Component component : components) {
                componentsResponse.add(ComponentConverter.convertFromBaseToResponse(component));
            }
            customProducts.add(CustomProductResponse.builder()
                    .customProductId(productEntity.getId())
                    .componentsIncluded(componentsResponse)
                    .statusId(status.getValue())
                    .userId(userId)
                    .templateId(productEntity.getTemplate().getId())
                    .build());
        }
        return customProducts;
    }

    @Override
    public int getNumberOfCustomProductsOfUserByStatus(long userId, long authenticatedUser, int statusId) {
        validateGetRequest(userId, authenticatedUser, statusId);
        StatusEntity statusEntity = statusRepository.findById(statusId);
        UserEntity userEntity = userRepository.findUserEntityById(userId);

        return customProductRepository.countCustomProductEntitiesByStatusAndUserId(statusEntity, userEntity);
    }

    @Transactional
    @Override
    public void deleteCustomProduct(long productId, long authenticatedUserId) {
        CustomProductEntity productEntity = customProductRepository.findById(productId);
        if(productEntity == null){
            throw new ObjectNotFound("Product not found");
        }
        validate(productId, authenticatedUserId, productEntity);
        assemblingRepository.deleteAssemblingEntitiesByCustomProductId(productEntity);
        customProductRepository.deleteById(productId);
    }

    @Transactional
    @Override
    public void updateCustomProduct(long productId, UpdateCustomTemplateRequest request, long authenticatedUserId) {
        CustomProductEntity productEntity = customProductRepository.findById(productId);
        if(productEntity == null) {
            throw new ObjectNotFound("Custom product not found");
        }
        validate(productId, authenticatedUserId, productEntity);

        List<Component> currentComponents = getComponentsOfCustomProductEntity(productEntity);
        updateComponents(productEntity, currentComponents, request.getComponentsIncluded());
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

    private void validateGetRequest( long userId,long authenticatedUserId, int statusId) {

        if(userId != authenticatedUserId) {
            throw new UnauthorizedException("You are not authorized to perform this operation");
        }
        if(!userRepository.existsById(userId)) {
            throw new ObjectNotFound("User not found");
        }
        if(!statusRepository.existsById(statusId)){
            throw new InvalidInputException("Invalid status");
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

    private void validate(long productId, long authenticatedUserId, CustomProductEntity productEntity) {
        if(authenticatedUserId != productEntity.getUserId().getId()) {
            throw new UnauthorizedException("You are not authorized to perform this operation");
        }
        if(!customProductRepository.existsById(productId)) {
            throw new ObjectNotFound("Custom product not found");
        }
        if(Objects.equals(productEntity.getStatus().getName(), Status.FINISHED.name())){
            throw new ActionDeniedException("You cannot delete a finished product");
        }
    }

    private void updateComponents(CustomProductEntity customProductEntity ,List<Component> currentComponents, List<ComponentInCustomProductInput> newComponentIdsList){
        List<Long> newComponentIds = newComponentIdsList.stream()
                .map(ComponentInCustomProductInput::getComponentId)
                .toList();

        List<AssemblingEntity> itemsToDelete = currentComponents.stream()
                .filter(existing -> !newComponentIds.contains(existing.getComponentId()))
                .map(existing -> AssemblingEntity.builder()
                        .customProductId(customProductEntity)
                        .componentId(ComponentConverter.convertFromBaseToEntity(existing))
                        .build())
                .toList();

        assemblingRepository.deleteAll(itemsToDelete);

        for(Long componentId : newComponentIds){
            if(componentRepository.existsById(componentId)) {
                ComponentEntity component = componentRepository.findComponentEntityByComponentId(componentId);
                if(!assemblingRepository.existsAssemblingEntityByComponentIdAndCustomProductId(component, customProductEntity)) {
                    assemblingRepository.save(AssemblingEntity.builder()
                                    .componentId(component)
                                    .customProductId(customProductEntity).build());
                }
            }
            else{
                throw new ObjectNotFound("Component not found");
            }
        }
    }
}
