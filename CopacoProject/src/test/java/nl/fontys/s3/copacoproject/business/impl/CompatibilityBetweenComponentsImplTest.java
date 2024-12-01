package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.Exceptions.CompatibilityError;
import nl.fontys.s3.copacoproject.business.Exceptions.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityResponse;
import nl.fontys.s3.copacoproject.business.dto.GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest;
import nl.fontys.s3.copacoproject.domain.AutomaticCompatibility;
import nl.fontys.s3.copacoproject.domain.ComponentType;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentSpecificationListRepository;
import nl.fontys.s3.copacoproject.persistence.ComponentTypeRepository;
import nl.fontys.s3.copacoproject.persistence.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompatibilityBetweenComponentsImplTest {
  @InjectMocks
    CompatibilityBetweenComponentsImpl compatibilityBetweenComponents;

  @Mock
    AutomaticCompatibilityRepository automaticCompatibilityRepository;

    @Mock
    ComponentRepository componentRepository;

    @Mock
    ComponentTypeRepository componentTypeRepository;

    @Mock
    ComponentSpecificationListRepository componentSpecificationListRepository;

    ComponentEntity givenComponent1;
    ComponentEntity givenComponent2;
    ComponentEntity givenComponent3;


    ComponentEntity resultComponent3;



    GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest requestOneComponentOneSearched;
    GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest requestTwoComponentOneSearched;

    GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest requestWithSameComponentAndComponentTypeIds;

    List<AutomaticCompatibilityEntity> automaticCompatibilityListBetweenComponent2AndComponentType4;
    List<AutomaticCompatibilityEntity> automaticCompatibilityListBetweenComponent3AndComponentType4;

    AutomaticCompatibilityEntity automaticCompatibility1;
    AutomaticCompatibilityEntity automaticCompatibility2;
    AutomaticCompatibilityEntity automaticCompatibility3;

    ComponentTypeEntity componentTypeEntity1;
    ComponentTypeEntity componentTypeEntity2;
    ComponentTypeEntity componentTypeEntity3;

    RuleEntity ruleEntity1;
    SpecficationTypeList_ComponentTypeEntity specficationTypeListEntity1;
    SpecficationTypeList_ComponentTypeEntity specficationTypeListEntity2;

    SpecificationTypeEntity specificationTypeEntity1;
    SpecificationTypeEntity specificationTypeEntity2;

    RuleEntity ruleEntity2;
    SpecficationTypeList_ComponentTypeEntity specficationTypeListEntity3;
    SpecficationTypeList_ComponentTypeEntity specficationTypeListEntity4;

    RuleEntity ruleEntity3;
    SpecficationTypeList_ComponentTypeEntity specficationTypeListEntity5;

    SpecificationTypeEntity specificationTypeEntity3;
    SpecificationTypeEntity specificationTypeEntity4;
    SpecificationType specificationType1;
    SpecificationType specificationType2;

    Component_SpecificationList specificationForFirstComponent1;
    Component_SpecificationList specificationForFirstComponent2;
    Component_SpecificationList specificationForFirstComponent3;
    Component_SpecificationList specificationForFirstComponent4;
    Component_SpecificationList specificationForFirstComponent5;
    Component_SpecificationList specificationForFirstComponent6;
    Component_SpecificationList specificationForSecondComponent1;
    Component_SpecificationList specificationForSecondComponent2;
    Component_SpecificationList specificationForThirdResultComponent1;
    Component_SpecificationList specificationForThirdComponent1;

    List<Component_SpecificationList> allSpecificationsForComponentForFirstSpecification1;
    List<Component_SpecificationList> allSpecificationsForComponentForSecondSpecification1;
    List<Component_SpecificationList> allSpecificationsForComponentForThirdSpecification1;

    List<Component_SpecificationList> allSpecificationsForComponentForSecondComponent;
    List<Component_SpecificationList> allSpecificationsForComponentForThirdResultComponent;

    List<GetAutomaticCompatibilityResponse> expectedResponse;
    List<GetAutomaticCompatibilityResponse> expectedResponseWithOnlyOneGivenComponentIdAndNoRules;
    List<GetAutomaticCompatibilityResponse> expectedResponseWithMoreThanOnceComponentsInRequest;


    Map<SpecificationType, List<String>> componentSpecificationsForSecondComponent;
    Map<SpecificationType, List<String>> componentSpecificationsForThirdComponent;

    GetAutomaticCompatibilityResponse expectedResponse1;
    GetAutomaticCompatibilityResponse expectedResponse2;
    GetAutomaticCompatibilityResponse expectedResponse3;
    GetAutomaticCompatibilityResponse expectedResponse4;

    BrandEntity brandEntity1;
    BrandEntity brandEntity2;
    BrandEntity brandEntity3;

    @BeforeEach
    void setUp() {
        componentTypeEntity1 = ComponentTypeEntity.builder().id(2L).build();
        componentTypeEntity2 = ComponentTypeEntity.builder().id(4L).build();
        componentTypeEntity3 = ComponentTypeEntity.builder().id(3L).build();

        brandEntity1 = BrandEntity.builder().id(1L).name("MSI").build();
        brandEntity2 = BrandEntity.builder().id(2L).name("FURY").build();
        brandEntity3 = BrandEntity.builder().id(3L).name("DELL").build();

        givenComponent1 = ComponentEntity.builder()
                .componentId(1L)
                .componentName("MSI motherboard")
                .componentType(componentTypeEntity1)
                .brand(brandEntity1)
                .componentPrice(100.0)
                .build();

        givenComponent2 = ComponentEntity.builder()
                .componentId(2L)
                .componentName("FURY ram 1")
                .componentType(componentTypeEntity2)
                .brand(brandEntity2)
                .componentPrice(101.0)
                .build();

        givenComponent3 = ComponentEntity.builder()
                .componentId(2L)
                .componentName("DELL ssd 1")
                .componentType(componentTypeEntity3)
                .brand(brandEntity3)
                .componentPrice(101.0)
                .build();

        resultComponent3 = ComponentEntity.builder()
                .componentId(3L)
                .componentName("FURY ram 2")
                .componentType(componentTypeEntity2)
                .brand(brandEntity2)
                .componentPrice(102.0)
                .build();

//        componentEntity4 = ComponentEntity.builder()
//                .componentId(4L)
//                .componentName("FURY ram 2")
//                .componentType(componentTypeEntity2)
//                .brand(brandEntity2)
//                .componentPrice(102.0)
//                .build();
//

        requestOneComponentOneSearched = GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest.builder()
            .firstComponentId(2L)
            .searchedComponentTypeId(4L)
            .build();

        requestTwoComponentOneSearched = GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest.builder()
                .firstComponentId(2L)
                .secondComponentId(3L)
                .searchedComponentTypeId(4L)
                .build();

        requestWithSameComponentAndComponentTypeIds = GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest.builder()
                .firstComponentId(2L)
                .searchedComponentTypeId(2L)
                .build();


        specificationTypeEntity1 = SpecificationTypeEntity.builder()
                .id(96L)
                .specificationTypeName("Clock Speed")
                .unit("MHz")
                .build();

        specificationTypeEntity2 = SpecificationTypeEntity.builder()
                .id(96L)
                .specificationTypeName("Clock Speed")
                .unit("MHz")
                .build();

        specificationTypeEntity3 = SpecificationTypeEntity.builder()
                .id(97L)
                .specificationTypeName("Double Data Rate")
                .unit(null)
                .build();

        specificationTypeEntity4 = SpecificationTypeEntity.builder()
                .id(97L)
                .specificationTypeName("Double Data Rate")
                .unit(null)
                .build();
        specficationTypeListEntity1 = SpecficationTypeList_ComponentTypeEntity.builder()
                .id(1L)
                .componentType(componentTypeEntity1)
                .specificationType(specificationTypeEntity1)
                .build();

        specficationTypeListEntity2 = SpecficationTypeList_ComponentTypeEntity.builder()
                .id(2L)
                .componentType(componentTypeEntity2)
                .specificationType(specificationTypeEntity2)
                .build();

        specficationTypeListEntity3 = SpecficationTypeList_ComponentTypeEntity.builder()
                .id(191L)
                .componentType(componentTypeEntity1)
                .specificationType(specificationTypeEntity3)
                .build();

        specficationTypeListEntity4 = SpecficationTypeList_ComponentTypeEntity.builder()
                .id(191L)
                .componentType(componentTypeEntity1)
                .specificationType(specificationTypeEntity4)
                .build();

//        specficationTypeListEntity5 = SpecficationTypeList_ComponentTypeEntity.builder()
//                .id(191L)
//                .componentType(componentTypeEntity2)
//                .specificationType(specificationTypeEntity3)
//                .build();

        specficationTypeListEntity5 = SpecficationTypeList_ComponentTypeEntity.builder()
                .id(191L)
                .componentType(componentTypeEntity3)
                .specificationType(specificationTypeEntity4)
                .build();





        ruleEntity1 = RuleEntity.builder()
                .id(1L)
                .specificationToConsider1Id(specficationTypeListEntity1)
                .specificationToConsider2Id(specficationTypeListEntity2)
                .build();

        ruleEntity2 = RuleEntity.builder()
                .id(1L)
                .specificationToConsider1Id(specficationTypeListEntity3)
                .specificationToConsider2Id(specficationTypeListEntity4)
                .build();

        ruleEntity3 = RuleEntity.builder()
                .id(1L)
                .specificationToConsider1Id(specficationTypeListEntity3)
                .specificationToConsider2Id(specficationTypeListEntity5)
                .build();

        automaticCompatibility1 = AutomaticCompatibilityEntity.builder()
                .id(1L)
                .component1Id(componentTypeEntity1)
                .component2Id(componentTypeEntity2)
                .ruleId(ruleEntity1)
                .build();

        automaticCompatibility2 = AutomaticCompatibilityEntity.builder()
                .id(2L)
                .component1Id(componentTypeEntity1)
                .component2Id(componentTypeEntity2)
                .ruleId(ruleEntity2)
                .build();


        automaticCompatibilityListBetweenComponent2AndComponentType4 = List.of(automaticCompatibility1,automaticCompatibility2);

        automaticCompatibility3 = AutomaticCompatibilityEntity.builder()
                .id(1L)
                .component1Id(componentTypeEntity2)
                .component2Id(componentTypeEntity3)
                .ruleId(ruleEntity3)
                .build();

        automaticCompatibilityListBetweenComponent3AndComponentType4 = List.of(automaticCompatibility3);
        specificationForFirstComponent1 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(givenComponent1)
                .specificationType(specificationTypeEntity1)
                .value("6000")
                .build();
        specificationForFirstComponent2 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(givenComponent1)
                .specificationType(specificationTypeEntity1)
                .value("5800")
                .build();
        specificationForFirstComponent3 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(givenComponent1)
                .specificationType(specificationTypeEntity1)
                .value("6400")
                .build();
        specificationForFirstComponent4 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(givenComponent1)
                .specificationType(specificationTypeEntity1)
                .value("6600")
                .build();
        specificationForFirstComponent5 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(givenComponent1)
                .specificationType(specificationTypeEntity1)
                .value("7000")
                .build();
        specificationForFirstComponent6 = Component_SpecificationList.builder()
                .id(2L)
                .componentId(givenComponent1)
                .specificationType(specificationTypeEntity3)
                .value("DDR5")
                .build();

        specificationForSecondComponent1 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(givenComponent2)
                .specificationType(specificationTypeEntity2)
                .value("6000")
                .build();

        specificationForSecondComponent2 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(givenComponent2)
                .specificationType(specificationTypeEntity4)
                .value("DDR5")
                .build();

        specificationForThirdResultComponent1 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(resultComponent3)
                .specificationType(specificationTypeEntity4)
                .value("DDR5")
                .build();

        specificationForThirdComponent1 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(givenComponent3)
                .specificationType(specificationTypeEntity4)
                .value("DDR5")
                .build();


        allSpecificationsForComponentForFirstSpecification1 = List.of(specificationForFirstComponent1,specificationForFirstComponent2,specificationForFirstComponent3,specificationForFirstComponent4,specificationForFirstComponent5);
        allSpecificationsForComponentForSecondSpecification1 = List.of(specificationForFirstComponent6);
        allSpecificationsForComponentForSecondComponent = List.of(specificationForSecondComponent1,specificationForSecondComponent2);
        allSpecificationsForComponentForThirdResultComponent = List.of(specificationForThirdResultComponent1);
        allSpecificationsForComponentForThirdSpecification1 = List.of(specificationForThirdComponent1);

        specificationType1 = SpecificationType.builder()
                .specificationTypeId(96L)
                .specificationTypeName("Clock Speed")
                .build();

        specificationType2 = SpecificationType.builder()
                .specificationTypeId(97L)
                .specificationTypeName("Double Data Rate")
                .build();

        componentSpecificationsForSecondComponent = Map.ofEntries(Map.entry(specificationType1, List.of("6000")), Map.entry(specificationType2, List.of("DDR5")));
        componentSpecificationsForThirdComponent = Map.ofEntries(Map.entry(specificationType2, List.of("DDR5")));
        expectedResponse1 = GetAutomaticCompatibilityResponse.builder()
                .componentId(givenComponent2.getComponentId())
                .componentName(givenComponent2.getComponentName())
                .componentTypeId(givenComponent2.getComponentType().getId())
                .componentTypeName(givenComponent2.getComponentType().getComponentTypeName())
                .componentImageUrl(givenComponent2.getComponentImageUrl())
                .brand(givenComponent2.getBrand().getName())
                .price(givenComponent2.getComponentPrice())
                .componentSpecifications(componentSpecificationsForSecondComponent)
                .build();

        expectedResponse2 = GetAutomaticCompatibilityResponse.builder()
                .componentId(resultComponent3.getComponentId())
                .componentName(resultComponent3.getComponentName())
                .componentTypeId(resultComponent3.getComponentType().getId())
                .componentTypeName(resultComponent3.getComponentType().getComponentTypeName())
                .componentImageUrl(resultComponent3.getComponentImageUrl())
                .brand(resultComponent3.getBrand().getName())
                .price(resultComponent3.getComponentPrice())
                .componentSpecifications(componentSpecificationsForThirdComponent)
                .build();

        expectedResponse3 = GetAutomaticCompatibilityResponse.builder()
                .componentId(givenComponent2.getComponentId())
                .componentName(givenComponent2.getComponentName())
                .componentTypeId(givenComponent2.getComponentType().getId())
                .componentTypeName(givenComponent2.getComponentType().getComponentTypeName())
                .componentImageUrl(givenComponent2.getComponentImageUrl())
                .brand(givenComponent2.getBrand().getName())
                .price(givenComponent2.getComponentPrice())
                .componentSpecifications(componentSpecificationsForSecondComponent)
                .build();

        expectedResponse4 = GetAutomaticCompatibilityResponse.builder()
                .componentId(resultComponent3.getComponentId())
                .componentName(resultComponent3.getComponentName())
                .componentTypeId(resultComponent3.getComponentType().getId())
                .componentTypeName(resultComponent3.getComponentType().getComponentTypeName())
                .componentImageUrl(resultComponent3.getComponentImageUrl())
                .brand(resultComponent3.getBrand().getName())
                .price(resultComponent3.getComponentPrice())
                .componentSpecifications(componentSpecificationsForThirdComponent)
                .build();

        expectedResponseWithMoreThanOnceComponentsInRequest = List.of(expectedResponse4);
        expectedResponse = List.of(expectedResponse2,expectedResponse1);
        expectedResponseWithOnlyOneGivenComponentIdAndNoRules = List.of(expectedResponse3);
    }

    @Test
    void givenOneComponentAndASearchedComponentType_whenGettingCompatibility_returnsCompatibleComponentFromSearchedComponentType()
    {
        when(componentRepository.existsById(2L)).thenReturn(true);
        when(componentTypeRepository.existsById(4L)).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(2L)).thenReturn(2L);
        when(automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(2L, requestOneComponentOneSearched.getSearchedComponentTypeId())).thenReturn(automaticCompatibilityListBetweenComponent2AndComponentType4);

        when(componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(2L,specificationTypeEntity1)).thenReturn(allSpecificationsForComponentForFirstSpecification1);
//        when(componentRepository.findComponentsByTypeAndSpecification(4L,96L,anyList())).thenReturn(List.of(givenComponent2,resultComponent3));
        when(componentRepository.findComponentsByTypeAndSpecification(eq(4L), eq(96L), anyList()))
                .thenReturn(List.of(givenComponent2, resultComponent3));

        when(componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(2L,specificationTypeEntity3)).thenReturn(allSpecificationsForComponentForSecondSpecification1);
//        when(componentRepository.findComponentsByTypeAndSpecification(4L,97L,anyList())).thenReturn(List.of(resultComponent3));
        when(componentRepository.findComponentsByTypeAndSpecification(eq(4L), eq(97L), anyList()))
                .thenReturn(List.of(givenComponent2, resultComponent3));
        //when(automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(2L,4L)).thenReturn(automaticCompatibilityListBetweenComponent2AndComponentType4)
        when(componentSpecificationListRepository.findByComponentId(givenComponent2)).thenReturn(allSpecificationsForComponentForSecondComponent);
        when(componentSpecificationListRepository.findByComponentId(resultComponent3)).thenReturn(allSpecificationsForComponentForThirdResultComponent);
        //when()
        when(componentSpecificationListRepository.findByComponentId(2L)).thenReturn(allSpecificationsForComponentForSecondComponent);
        when(componentSpecificationListRepository.findByComponentId(3L)).thenReturn(allSpecificationsForComponentForThirdResultComponent);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestOneComponentOneSearched);
        assertEquals(Set.copyOf(expectedResponse), Set.copyOf(actualResponse));
    }

    @Test
    void givenANonExistingComponentId_whenGettingCompatibility_throwsAObjectNotFoundException()
    {
        when(componentRepository.existsById(2L)).thenReturn(false);

        ObjectNotFound exception = assertThrows(ObjectNotFound.class, () -> {
            compatibilityBetweenComponents.automaticCompatibility(requestOneComponentOneSearched);
        });
        assertEquals("Components not found: [2]", exception.getReason());
    }

    @Test
    void givenANonExistingComponentTypeId_whenGettingCompatibility_throwsAObjectNotFoundException()
    {
        when(componentRepository.existsById(2L)).thenReturn(true);
        when(componentTypeRepository.existsById(4L)).thenReturn(false);

        ObjectNotFound exception = assertThrows(ObjectNotFound.class, () -> {
            compatibilityBetweenComponents.automaticCompatibility(requestOneComponentOneSearched);
        });
        assertEquals("Component type not found", exception.getReason());
    }

    @Test
    void givenASearchedComponentTypeWhenComponentFromItIsAdded_whenGettingCompatibility_throwsAObjectNotFoundException()
    {
        when(componentRepository.existsById(2L)).thenReturn(true);
        when(componentTypeRepository.existsById(2L)).thenReturn(true);
        when(componentRepository.findComponentTypeIdByComponentId(2L)).thenReturn(2L);

        CompatibilityError exception = assertThrows(CompatibilityError.class, () -> {
            compatibilityBetweenComponents.automaticCompatibility(requestWithSameComponentAndComponentTypeIds);
        });
        assertEquals("Once a component is selected, other components from the same category can not be searched.", exception.getReason());
    }

    @Test
    void givenOneComponentAndASearchedComponentType_whenThereIsNoCompatibilityBetweenThem_returnsCompatibleComponentFromSearchedComponentType()
    {
        when(componentRepository.existsById(2L)).thenReturn(true);
        when(componentTypeRepository.existsById(4L)).thenReturn(true);
        when(componentRepository.findComponentTypeIdByComponentId(2L)).thenReturn(2L);
        when(automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(2L, requestOneComponentOneSearched.getSearchedComponentTypeId())).thenReturn(Collections.emptyList());
        when(componentRepository.findByComponentType_Id(4L)).thenReturn(List.of(givenComponent2));
        when(componentSpecificationListRepository.findByComponentId(2L)).thenReturn(allSpecificationsForComponentForSecondComponent);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestOneComponentOneSearched);
        assertEquals(expectedResponseWithOnlyOneGivenComponentIdAndNoRules.get(0).getComponentName(), actualResponse.get(0).getComponentName());
        assertEquals(expectedResponseWithOnlyOneGivenComponentIdAndNoRules.get(0).getComponentTypeName(), actualResponse.get(0).getComponentTypeName());
        assertEquals(expectedResponseWithOnlyOneGivenComponentIdAndNoRules.get(0).getComponentImageUrl(), actualResponse.get(0).getComponentImageUrl());
        assertEquals(expectedResponseWithOnlyOneGivenComponentIdAndNoRules.get(0).getBrand(), actualResponse.get(0).getBrand());
        assertEquals(expectedResponseWithOnlyOneGivenComponentIdAndNoRules.get(0).getPrice(), actualResponse.get(0).getPrice());
        assertEquals(expectedResponseWithOnlyOneGivenComponentIdAndNoRules.get(0).getComponentSpecifications(), actualResponse.get(0).getComponentSpecifications());
    }

    @Test
    void givenOneComponentAndASearchedComponentType_whenThereIsNoComponentsInTheSearchedComponentType_throwsAnCompatibilityError()
    {
        when(componentRepository.existsById(2L)).thenReturn(true);
        when(componentTypeRepository.existsById(4L)).thenReturn(true);
        when(componentRepository.findComponentTypeIdByComponentId(2L)).thenReturn(2L);
        when(automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(2L, requestOneComponentOneSearched.getSearchedComponentTypeId())).thenReturn(Collections.emptyList());
        when(componentRepository.findByComponentType_Id(4L)).thenReturn(List.of());

        CompatibilityError exception = assertThrows(CompatibilityError.class, () -> {
            compatibilityBetweenComponents.automaticCompatibility(requestOneComponentOneSearched);
        });
        assertEquals("COMPONENTS_FROM_CATEGORY_NOT_FOUND", exception.getReason());
  }

    @Test
    void givenOneComponentAndASearchedComponentType_whenThereAreNoCompatibleComponentsFromTheSearchedComponentType_returnsCompatibleComponentFromSearchedComponentType() {
        when(componentRepository.existsById(2L)).thenReturn(true);
        when(componentTypeRepository.existsById(4L)).thenReturn(true);
        when(componentRepository.findComponentTypeIdByComponentId(2L)).thenReturn(2L);
        when(automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(2L, requestOneComponentOneSearched.getSearchedComponentTypeId())).thenReturn(automaticCompatibilityListBetweenComponent2AndComponentType4);
        when(componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(2L, specificationTypeEntity1)).thenReturn(allSpecificationsForComponentForFirstSpecification1);
        when(componentRepository.findComponentsByTypeAndSpecification(eq(4L), eq(96L), anyList()))
                .thenReturn(List.of(givenComponent2, resultComponent3));
        when(componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(2L, specificationTypeEntity3)).thenReturn(Collections.emptyList());

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestOneComponentOneSearched);
        assertEquals(actualResponse, List.of());
    }

    @Test
    void givenTwoComponentsAndASearchedComponentType_whenGettingCompatibilityAndThereIsNotRuleBetweenTheSecondComponentAndThSearchedComponentType_returnsCompatibleComponentFromSearchedComponentType()
    {
        when(componentRepository.existsById(2L)).thenReturn(true);
        when(componentRepository.existsById(3L)).thenReturn(true);
        when(componentTypeRepository.existsById(4L)).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(2L)).thenReturn(2L);
        when(automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(2L, requestOneComponentOneSearched.getSearchedComponentTypeId())).thenReturn(automaticCompatibilityListBetweenComponent2AndComponentType4);
        when(componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(2L,specificationTypeEntity1)).thenReturn(allSpecificationsForComponentForFirstSpecification1);
        when(componentRepository.findComponentsByTypeAndSpecification(eq(4L), eq(96L), anyList()))
                .thenReturn(List.of(givenComponent2, resultComponent3));
        when(componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(2L,specificationTypeEntity3)).thenReturn(allSpecificationsForComponentForSecondSpecification1);
        when(componentRepository.findComponentsByTypeAndSpecification(eq(4L), eq(97L), anyList()))
                .thenReturn(List.of(givenComponent2, resultComponent3));
         when(componentSpecificationListRepository.findByComponentId(givenComponent2)).thenReturn(allSpecificationsForComponentForSecondComponent);
         when(componentSpecificationListRepository.findByComponentId(resultComponent3)).thenReturn(allSpecificationsForComponentForThirdResultComponent);
         when(componentSpecificationListRepository.findByComponentId(2L)).thenReturn(allSpecificationsForComponentForSecondComponent);
         when(componentSpecificationListRepository.findByComponentId(3L)).thenReturn(allSpecificationsForComponentForThirdResultComponent);


        when(componentRepository.findComponentTypeIdByComponentId(3L)).thenReturn(3L);
        when(automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(3L, requestOneComponentOneSearched.getSearchedComponentTypeId())).thenReturn(Collections.emptyList());


        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestTwoComponentOneSearched);
        assertEquals(expectedResponse.get(0).getComponentName(), actualResponse.get(0).getComponentName());
        assertEquals(expectedResponse.get(0).getComponentTypeName(), actualResponse.get(0).getComponentTypeName());
        assertEquals(expectedResponse.get(0).getComponentImageUrl(), actualResponse.get(0).getComponentImageUrl());
        assertEquals(expectedResponse.get(0).getBrand(), actualResponse.get(0).getBrand());
        assertEquals(expectedResponse.get(0).getPrice(), actualResponse.get(0).getPrice());
        assertEquals(expectedResponse.get(0).getComponentSpecifications(), actualResponse.get(0).getComponentSpecifications());

        assertEquals(expectedResponse.get(1).getComponentId(), actualResponse.get(1).getComponentId());
        assertEquals(expectedResponse.get(1).getComponentName(), actualResponse.get(1).getComponentName());
        assertEquals(expectedResponse.get(1).getComponentTypeName(), actualResponse.get(1).getComponentTypeName());
        assertEquals(expectedResponse.get(1).getComponentImageUrl(), actualResponse.get(1).getComponentImageUrl());
        assertEquals(expectedResponse.get(1).getBrand(), actualResponse.get(1).getBrand());
        assertEquals(expectedResponse.get(1).getPrice(), actualResponse.get(1).getPrice());
        assertEquals(expectedResponse.get(1).getComponentSpecifications(), actualResponse.get(1).getComponentSpecifications());
    }

    @Test
    void givenTwoComponentsAndASearchedComponentType_whenGettingCompatibility_returnsCompatibleComponentFromSearchedComponentType()
    {
        when(componentRepository.existsById(2L)).thenReturn(true);
        when(componentRepository.existsById(3L)).thenReturn(true);
        when(componentTypeRepository.existsById(4L)).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(2L)).thenReturn(2L);
        when(automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(2L, requestOneComponentOneSearched.getSearchedComponentTypeId())).thenReturn(automaticCompatibilityListBetweenComponent2AndComponentType4);
        when(componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(2L,specificationTypeEntity1)).thenReturn(allSpecificationsForComponentForFirstSpecification1);
        when(componentRepository.findComponentsByTypeAndSpecification(eq(4L), eq(96L), anyList()))
                .thenReturn(List.of(givenComponent2, resultComponent3));
        when(componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(2L,specificationTypeEntity3)).thenReturn(allSpecificationsForComponentForSecondSpecification1);
        when(componentRepository.findComponentsByTypeAndSpecification(eq(4L), eq(97L), anyList()))
                .thenReturn(List.of(givenComponent2, resultComponent3));

        when(componentSpecificationListRepository.findByComponentId(givenComponent2)).thenReturn(allSpecificationsForComponentForSecondComponent);
        when(componentSpecificationListRepository.findByComponentId(resultComponent3)).thenReturn(allSpecificationsForComponentForThirdResultComponent);
        when(componentSpecificationListRepository.findByComponentId(2L)).thenReturn(allSpecificationsForComponentForSecondComponent);
        when(componentSpecificationListRepository.findByComponentId(3L)).thenReturn(allSpecificationsForComponentForThirdResultComponent);


        when(componentRepository.findComponentTypeIdByComponentId(3L)).thenReturn(3L);
        when(automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(3L, requestOneComponentOneSearched.getSearchedComponentTypeId())).thenReturn(automaticCompatibilityListBetweenComponent3AndComponentType4);
        when(componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(3L,specificationTypeEntity3)).thenReturn(allSpecificationsForComponentForThirdSpecification1);
        when(componentRepository.findComponentsByTypeAndSpecification(eq(4L), eq(96L), anyList()))
                .thenReturn(List.of(resultComponent3));

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestTwoComponentOneSearched);

        //assertEquals(Set.copyOf(expectedResponseWithMoreThanOnceComponentsInRequest), Set.copyOf(actualResponse));

//
//
//
        assertEquals(expectedResponseWithMoreThanOnceComponentsInRequest.get(0).getComponentName(), actualResponse.get(0).getComponentName());
        assertEquals(expectedResponseWithMoreThanOnceComponentsInRequest.get(0).getComponentTypeName(), actualResponse.get(0).getComponentTypeName());
        assertEquals(expectedResponseWithMoreThanOnceComponentsInRequest.get(0).getComponentImageUrl(), actualResponse.get(0).getComponentImageUrl());
        assertEquals(expectedResponseWithMoreThanOnceComponentsInRequest.get(0).getBrand(), actualResponse.get(0).getBrand());
        assertEquals(expectedResponseWithMoreThanOnceComponentsInRequest.get(0).getPrice(), actualResponse.get(0).getPrice());
        assertEquals(expectedResponseWithMoreThanOnceComponentsInRequest.get(0).getComponentSpecifications(), actualResponse.get(0).getComponentSpecifications());

//        assertEquals(expectedResponse.get(1).getComponentId(), actualResponse.get(1).getComponentId());
//        assertEquals(expectedResponse.get(1).getComponentName(), actualResponse.get(1).getComponentName());
//        assertEquals(expectedResponse.get(1).getComponentTypeName(), actualResponse.get(1).getComponentTypeName());
//        assertEquals(expectedResponse.get(1).getComponentImageUrl(), actualResponse.get(1).getComponentImageUrl());
//        assertEquals(expectedResponse.get(1).getBrand(), actualResponse.get(1).getBrand());
//        assertEquals(expectedResponse.get(1).getPrice(), actualResponse.get(1).getPrice());
//        assertEquals(expectedResponse.get(1).getComponentSpecifications(), actualResponse.get(1).getComponentSpecifications());
    }


}