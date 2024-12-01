package nl.fontys.s3.copacoproject.business.impl;

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

import java.util.List;
import java.util.Map;

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

    ComponentEntity componentEntity1;
    ComponentEntity componentEntity2;
    ComponentEntity componentEntity3;

    GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest requestOneComponentOneSearched;
    List<AutomaticCompatibilityEntity> automaticCompatibilityListBetweenComponent2AndComponentType4;
    AutomaticCompatibilityEntity automaticCompatibility1;
    AutomaticCompatibilityEntity automaticCompatibility2;

    ComponentTypeEntity componentTypeEntity1;
    ComponentTypeEntity componentTypeEntity2;
    RuleEntity ruleEntity1;
    SpecficationTypeList_ComponentTypeEntity specficationTypeListEntity1;
    SpecficationTypeList_ComponentTypeEntity specficationTypeListEntity2;

    SpecificationTypeEntity specificationTypeEntity1;
    SpecificationTypeEntity specificationTypeEntity2;

    RuleEntity ruleEntity2;
    SpecficationTypeList_ComponentTypeEntity specficationTypeListEntity3;
    SpecficationTypeList_ComponentTypeEntity specficationTypeListEntity4;

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
    Component_SpecificationList specificationForThirdComponent1;

    List<Component_SpecificationList> allSpecificationsForComponentForFirstSpecification1;
    List<Component_SpecificationList> allSpecificationsForComponentForSecondSpecification1;

    List<Component_SpecificationList> allSpecificationsForComponentForSecondComponent;
    List<Component_SpecificationList> allSpecificationsForComponentForThirdComponent;

    List<GetAutomaticCompatibilityResponse> expectedResponse;
    Map<SpecificationType, List<String>> componentSpecificationsForSecondComponent;
    Map<SpecificationType, List<String>> componentSpecificationsForThirdComponent;

    GetAutomaticCompatibilityResponse expectedResponse1;
    GetAutomaticCompatibilityResponse expectedResponse2;
    BrandEntity brandEntity1;
    BrandEntity brandEntity2;

    @BeforeEach
    void setUp() {
        componentTypeEntity1 = ComponentTypeEntity.builder().id(2L).build();
        componentTypeEntity2 = ComponentTypeEntity.builder().id(4L).build();

        brandEntity1 = BrandEntity.builder().id(1L).name("MSI").build();
        brandEntity2 = BrandEntity.builder().id(2L).name("FURY").build();

        componentEntity1 = ComponentEntity.builder()
                .componentId(1L)
                .componentName("MSI motherboard")
                .componentType(componentTypeEntity1)
                .brand(brandEntity1)
                .componentPrice(100.0)
                .build();

        componentEntity2 = ComponentEntity.builder()
                .componentId(2L)
                .componentName("FURY ram 1")
                .componentType(componentTypeEntity2)
                .brand(brandEntity2)
                .componentPrice(101.0)
                .build();

        componentEntity3 = ComponentEntity.builder()
                .componentId(3L)
                .componentName("FURY ram 2")
                .componentType(componentTypeEntity2)
                .brand(brandEntity2)
                .componentPrice(102.0)
                .build();

        requestOneComponentOneSearched = GetCompatibilityBetweenSelectedItemsAndSearchedComponentTypeRequest.builder()
            .firstComponentId(2L)
            .searchedComponentTypeId(4L)
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

        specificationForFirstComponent1 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(componentEntity1)
                .specificationType(specificationTypeEntity1)
                .value("6000")
                .build();
        specificationForFirstComponent2 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(componentEntity1)
                .specificationType(specificationTypeEntity1)
                .value("5800")
                .build();
        specificationForFirstComponent3 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(componentEntity1)
                .specificationType(specificationTypeEntity1)
                .value("6400")
                .build();
        specificationForFirstComponent4 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(componentEntity1)
                .specificationType(specificationTypeEntity1)
                .value("6600")
                .build();
        specificationForFirstComponent5 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(componentEntity1)
                .specificationType(specificationTypeEntity1)
                .value("7000")
                .build();
        specificationForFirstComponent6 = Component_SpecificationList.builder()
                .id(2L)
                .componentId(componentEntity1)
                .specificationType(specificationTypeEntity3)
                .value("DDR5")
                .build();

        specificationForSecondComponent1 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(componentEntity2)
                .specificationType(specificationTypeEntity2)
                .value("6000")
                .build();

        specificationForSecondComponent2 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(componentEntity2)
                .specificationType(specificationTypeEntity4)
                .value("DDR5")
                .build();

        specificationForThirdComponent1 = Component_SpecificationList.builder()
                .id(1L)
                .componentId(componentEntity3)
                .specificationType(specificationTypeEntity4)
                .value("DDR5")
                .build();

        allSpecificationsForComponentForFirstSpecification1 = List.of(specificationForFirstComponent1,specificationForFirstComponent2,specificationForFirstComponent3,specificationForFirstComponent4,specificationForFirstComponent5);
        allSpecificationsForComponentForSecondSpecification1 = List.of(specificationForFirstComponent6);
        allSpecificationsForComponentForSecondComponent = List.of(specificationForSecondComponent1,specificationForSecondComponent2);
        allSpecificationsForComponentForThirdComponent = List.of(specificationForThirdComponent1);

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
                .componentId(componentEntity2.getComponentId())
                .componentName(componentEntity2.getComponentName())
                .componentTypeId(componentEntity2.getComponentType().getId())
                .componentTypeName(componentEntity2.getComponentType().getComponentTypeName())
                .componentImageUrl(componentEntity2.getComponentImageUrl())
                .brand(componentEntity2.getBrand().getName())
                .price(componentEntity2.getComponentPrice())
                .componentSpecifications(componentSpecificationsForSecondComponent)
                .build();

        expectedResponse2 = GetAutomaticCompatibilityResponse.builder()
                .componentId(componentEntity3.getComponentId())
                .componentName(componentEntity3.getComponentName())
                .componentTypeId(componentEntity3.getComponentType().getId())
                .componentTypeName(componentEntity3.getComponentType().getComponentTypeName())
                .componentImageUrl(componentEntity3.getComponentImageUrl())
                .brand(componentEntity3.getBrand().getName())
                .price(componentEntity3.getComponentPrice())
                .componentSpecifications(componentSpecificationsForThirdComponent)
                .build();
        expectedResponse = List.of(expectedResponse2,expectedResponse1);
    }

    @Test
    void givenOneComponentAndASearchedComponentType_whenGettingCompatibility_returnsCompatibleComponentFromSearchedComponentType()
    {
        when(componentRepository.existsById(2L)).thenReturn(true);
        when(componentTypeRepository.existsById(4L)).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(2L)).thenReturn(2L);
        when(automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(2L, requestOneComponentOneSearched.getSearchedComponentTypeId())).thenReturn(automaticCompatibilityListBetweenComponent2AndComponentType4);

        when(componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(2L,specificationTypeEntity1)).thenReturn(allSpecificationsForComponentForFirstSpecification1);
//        when(componentRepository.findComponentsByTypeAndSpecification(4L,96L,anyList())).thenReturn(List.of(componentEntity2,componentEntity3));
        when(componentRepository.findComponentsByTypeAndSpecification(eq(4L), eq(96L), anyList()))
                .thenReturn(List.of(componentEntity2, componentEntity3));

        when(componentSpecificationListRepository.findByComponentIdAndSpecificationTypeId(2L,specificationTypeEntity3)).thenReturn(allSpecificationsForComponentForSecondSpecification1);
//        when(componentRepository.findComponentsByTypeAndSpecification(4L,97L,anyList())).thenReturn(List.of(componentEntity3));
        when(componentRepository.findComponentsByTypeAndSpecification(eq(4L), eq(97L), anyList()))
                .thenReturn(List.of(componentEntity2, componentEntity3));
        //when(automaticCompatibilityRepository.findCompatibilityRecordsBetweenTwoComponentTypeIds(2L,4L)).thenReturn(automaticCompatibilityListBetweenComponent2AndComponentType4)
        when(componentSpecificationListRepository.findByComponentId(componentEntity2)).thenReturn(allSpecificationsForComponentForSecondComponent);
        when(componentSpecificationListRepository.findByComponentId(componentEntity3)).thenReturn(allSpecificationsForComponentForThirdComponent);
        //when()
        when(componentSpecificationListRepository.findByComponentId(2L)).thenReturn(allSpecificationsForComponentForSecondComponent);
        when(componentSpecificationListRepository.findByComponentId(3L)).thenReturn(allSpecificationsForComponentForThirdComponent);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestOneComponentOneSearched);
        assertEquals(expectedResponse.get(0).getComponentId(), actualResponse.get(0).getComponentId());
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
}