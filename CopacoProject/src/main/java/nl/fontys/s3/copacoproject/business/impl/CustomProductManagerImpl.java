package nl.fontys.s3.copacoproject.business.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.CustomProductManager;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.Exceptions.UnauthorizedException;
import nl.fontys.s3.copacoproject.business.converters.StatusConverter;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.ComponentInCustomProductInput;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.CreateCustomProductRequest;
import nl.fontys.s3.copacoproject.business.dto.customProductDto.CreateCustomProductResponse;
import nl.fontys.s3.copacoproject.domain.enums.Status;
import nl.fontys.s3.copacoproject.persistence.*;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomProductManagerImpl implements CustomProductManager {

    private final CustomProductRepository customProductRepository;
    private final AssemblingRepository assemblingRepository;
    private final ComponentRepository componentRepository;
    private final UserRepository userRepository;
    private final TemplateRepository templateRepository;
    private final StatusRepository statusRepository;


    @Override
    @Transactional
    public CreateCustomProductResponse CreateCustomProduct(CreateCustomProductRequest request, long authenticatedUserId) {
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
}
