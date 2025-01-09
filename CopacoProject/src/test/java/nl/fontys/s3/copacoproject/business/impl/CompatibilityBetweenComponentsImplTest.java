package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.SpecificationIdsForComponentPurpose;
import nl.fontys.s3.copacoproject.business.exception.CompatibilityError;
import nl.fontys.s3.copacoproject.business.exception.ObjectNotFound;
import nl.fontys.s3.copacoproject.business.dto.GetAutomaticCompatibilityResponse;
import nl.fontys.s3.copacoproject.business.dto.ConfiguratorRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompatibilityBetweenComponentsImplTest {
  @InjectMocks
    CompatibilityBetweenComponentsImpl compatibilityBetweenComponents;

  @Mock
  CompatibilityRepository compatibilityRepository;

    @Mock
    ComponentRepository componentRepository;

    @Mock
    ComponentTypeRepository componentTypeRepository;

    @Mock
    ComponentSpecificationListRepository componentSpecificationListRepository;

    @Mock
    SpecificationIdsForComponentPurpose specificationIdsForComponentPurpose;

    //Components
    ComponentEntity ram1;
    ComponentEntity ram2;
    ComponentEntity psu1;
    ComponentEntity psu2;
    ComponentEntity ssd1;
    ComponentEntity graphicsCard1;
    ComponentEntity processor1;
    ComponentEntity motherboard1;
    ComponentEntity hardDrive;
    ComponentEntity cooling;
    ComponentEntity processorCooling;
    ComponentEntity computerCase;
    ComponentEntity powerSupply;

    //List of Components
    List<ComponentEntity> listOfPsu1;
    List<ComponentEntity> listOfPsu2;
    List<ComponentEntity> listOf10PSUs;
    List<ComponentEntity> listOf1Ram;
    List<ComponentEntity> listOf10SSD;
    List<ComponentEntity> listOf1SSD;
    List<ComponentEntity> listOf1GraphicsCard;
    List<ComponentEntity> listOf10GraphicsCards;
    List<ComponentEntity> listOf1Processor;
    List<ComponentEntity> listOf1Motherboard;
    List<ComponentEntity> listOf1HardDrive;
    List<ComponentEntity> listOf1Cooling;
    List<ComponentEntity> listOf1ProcessorCooling;
    List<ComponentEntity> listOf1ComputerCase;
    List<ComponentEntity> listOf1PowerSupply;






    //Requests
    ConfiguratorRequest processorProvidedRamSearchedPC;
    ConfiguratorRequest processorProvidedRamSearchedWorkstation;
    ConfiguratorRequest processorProvidedRamSearchedServer;
    ConfiguratorRequest processorProvidedRamSearchedLaptop;

    ConfiguratorRequest requestWithProcessorProvidedSSDSearchedPC;
    ConfiguratorRequest requestWithProcessorProvidedSSDSearchedWorkstation;
    ConfiguratorRequest requestWithProcessorProvidedSSDSearchedServer;
    ConfiguratorRequest requestWithProcessorProvidedSSDSearchedLaptop;

    ConfiguratorRequest requestWithProcessorProvidedHardDriveSearchedPC;
    ConfiguratorRequest requestWithProcessorProvidedHardDriveSearchedWorkstation;
    ConfiguratorRequest requestWithProcessorProvidedHardDriveSearchedServer;

    ConfiguratorRequest requestWithProcessorProvidedCoolingSearchedPC;
    ConfiguratorRequest requestWithProcessorProvidedCoolingSearchedLaptop;
    ConfiguratorRequest requestWithProcessorProvidedCoolingSearchedServer;

    ConfiguratorRequest requestWithProcessorProvidedProcessorCoolingSearchedPC;
    ConfiguratorRequest requestWithProcessorProvidedProcessorCoolingSearchedLaptop;
    ConfiguratorRequest requestWithProcessorProvidedProcessorCoolingSearchedServer;

    ConfiguratorRequest requestWithProcessorProvidedComputerCaseSearchedPC;

    ConfiguratorRequest requestWithProcessorProvidedPowerSupplySearchedPC;
    ConfiguratorRequest requestWithProcessorProvidedPowerSupplySearchedWorkstation;
    ConfiguratorRequest requestWithProcessorProvidedPowerSupplySearchedServer;

    ConfiguratorRequest requestWithProcessorProvidedGraphicsCardSearchedPC;

    ConfiguratorRequest requestWithGraphicsCardProvidedProcessorSearchedPC;
    ConfiguratorRequest requestWithGraphicsCardProvidedProcessorSearchedServer;
    ConfiguratorRequest requestWithGraphicsCardProvidedProcessorSearchedWorkstation;

    ConfiguratorRequest requestWithGraphicsCardProvidedMotherboardSearchedPC;
    ConfiguratorRequest requestWithGraphicsCardProvidedMotherboardSearchedServer;
    ConfiguratorRequest requestWithGraphicsCardProvidedMotherboardSearchedWorkstation;


    ConfiguratorRequest requestWithProvidedComponentIdTheSameAsSearchedOne;
    ConfiguratorRequest requestWithMaxProvidedComponentsAndLastSearchedPSU;
    ConfiguratorRequest requestWithMaxProvidedComponentsAndLastSearchedPSU2;

    ConfiguratorRequest requestProcessorAndGraphicsCardProvidedRamSearchedPC;
    ConfiguratorRequest requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC;



    ComponentTypeEntity componentTypeProcessor;
    ComponentTypeEntity componentTypePSU;
    ComponentTypeEntity componentTypeSSD;
    ComponentTypeEntity componentTypeGraphicsCard;
    ComponentTypeEntity componentTypeMotherboard;



    //Distinct specification Ids from rules
    List<Long> allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam;

    //Types of Configuration
    String typeOfConfigurationPC;
    String typeOfConfigurationServer;
    String typeOfConfigurationWorkstation;
    String typeOfConfigurationLaptop;

    //Specifications for components
    //Processor
    String processorSpecification1;
    String processorSpecification2;
    String processorSpecification3;
    String processorSpecification4;
    List<String> specification1and2ForProcessor;
    List<String> specification1and2ForProcessor2;


    //Object responses
    List<Object[]> objectResponseProcessorRam;
    List<Object[]> objectResponseProcessorRam2;
    List<Object[]> objectResponseProcessorRam3;

    //Pages
    Page<ComponentEntity> oneRam;
    Page<ComponentEntity> twoRams;
    Page<ComponentEntity> tenRams;


    //Pageables
    Pageable pageableFirstPage;
    Pageable pageableCheckNextPage;


    //Responses
    GetAutomaticCompatibilityResponse ram1Response;
    GetAutomaticCompatibilityResponse ram2Response;
    GetAutomaticCompatibilityResponse psu1Response;
    GetAutomaticCompatibilityResponse psu1ResponseWithNextPage;
    GetAutomaticCompatibilityResponse ssd1ResponseWithNextPage;
    GetAutomaticCompatibilityResponse ssd1ResponseWithoutNextPage;
    GetAutomaticCompatibilityResponse graphicsCard1ResponseWithNextPage;
    GetAutomaticCompatibilityResponse graphicsCard1ResponseWithoutNextPage;
    GetAutomaticCompatibilityResponse processor1ResponseWithoutNextPage;
    GetAutomaticCompatibilityResponse motherboard1ResponseWithoutNextPage;
    GetAutomaticCompatibilityResponse ram1WithNextPageResponse;
    GetAutomaticCompatibilityResponse ram2WithNextPageResponse;
    GetAutomaticCompatibilityResponse hardDriveResponse;
    GetAutomaticCompatibilityResponse coolingResponse;
    GetAutomaticCompatibilityResponse processorCoolingResponse;
    GetAutomaticCompatibilityResponse computerCaseResponse;
    GetAutomaticCompatibilityResponse powerSupplyResponse;


    List<GetAutomaticCompatibilityResponse> expectedResponseSearchedRamsForProcessor;
    List<GetAutomaticCompatibilityResponse> expectedResponseHandlePowerSupply;
    List<GetAutomaticCompatibilityResponse> expectedResponseHandlePowerSupplyWithNextPage;
    List<GetAutomaticCompatibilityResponse> expectedResponseHandlePowerSupplyWithoutNextPage;
    List<GetAutomaticCompatibilityResponse> expectedResponse1RamNoNextPage;
    List<GetAutomaticCompatibilityResponse> expectedResponseSSDWithNextPage;
    List<GetAutomaticCompatibilityResponse> expectedResponseSSDWithoutNextPage;
    List<GetAutomaticCompatibilityResponse> expectedResponse1GraphicCardWithoutNextPage;
    List<GetAutomaticCompatibilityResponse> expectedResponse1ProcessorNoNextPage;
    List<GetAutomaticCompatibilityResponse> expectedResponse1MotherboardNoNextPage;
    List<GetAutomaticCompatibilityResponse> expectedResponseSearchedRamsForProcessorWithoutNextPage;
    List<GetAutomaticCompatibilityResponse> expectedResponse1HardDriveNoNextPage;
    List<GetAutomaticCompatibilityResponse> expectedResponse1CoolingNoNextPage;
    List<GetAutomaticCompatibilityResponse> expectedResponse1ProcessorCoolingNoNextPage;
    List<GetAutomaticCompatibilityResponse> expectedResponse1ComputerCaseNoNextPage;
    List<GetAutomaticCompatibilityResponse> expectedResponse1PowerSupplyNoNextPage;

    Map<Long,List<String>> ProcessorPC;
    Map.Entry<Long, List<String>> firstEntryProcessorPC;


    Map<Long, List<String>> ProcessorServer;
    Map.Entry<Long, List<String>> firstEntryProcessorServer;

    Map<Long, List<String>> ProcessorWorkstation;
    Map.Entry<Long, List<String>> firstEntryProcessorWorkstation;

    Map<Long,List<String>> RamPC;
    Map.Entry<Long, List<String>> firstEntryRamPC;

    Map<Long, List<String>> RamServer;
    Map.Entry<Long, List<String>> firstEntryRamServer;

    Map<Long, List<String>> RamWorkstation;
    Map.Entry<Long, List<String>> firstEntryRamWorkstation;

    Map<Long,List<String>> SSDPC;
    Map.Entry<Long, List<String>> firstEntrySSDPC;

    Map<Long,List<String>> SSDServer;
    Map.Entry<Long, List<String>> firstEntrySSDServer;

    Map<Long, List<String>> StorageServer;
    Map.Entry<Long, List<String>> firstEntryStorageServer;

    Map<Long, List<String>> StoragePC;
    Map.Entry<Long, List<String>> firstEntryStoragePC;

    Map<Long, List<String>> CoolingServer;
    Map.Entry<Long, List<String>> firstEntryCoolingServer;

    Map<Long, List<String>> CoolingPC;
    Map.Entry<Long, List<String>> firstEntryCoolingPC;

    Map<Long, List<String>> CoolingLaptop;
    Map.Entry<Long, List<String>> firstEntryCoolingLaptop;

    Map<Long, List<String>> BikeCity;
    Map.Entry<Long, List<String>> firstEntryBikeCity;

    Map<Long, List<String>> BikeDownhill;
    Map.Entry<Long, List<String>> firstEntryBikeDownhill;

    Map<Long, List<String>> emptyMap;

    Map<Long, List<String>> MotherboardPC;
    Map.Entry<Long, List<String>> firstEntryMotherboardPC;


    @BeforeEach
    void setUp() {

        componentTypeProcessor = ComponentTypeEntity.builder().id(1L).build();
        componentTypeMotherboard = ComponentTypeEntity.builder().id(2L).build();
        componentTypeGraphicsCard = ComponentTypeEntity.builder().id(3L).build();
        componentTypePSU = ComponentTypeEntity.builder().id(5L).build();
        componentTypeSSD = ComponentTypeEntity.builder().id(10L).build();


        typeOfConfigurationPC = "PC";
        typeOfConfigurationServer = "Server";
        typeOfConfigurationWorkstation = "Workstation";
        typeOfConfigurationLaptop = "Laptop";


        processorSpecification1 = "SATA";
        processorSpecification2 = "50";
        processorSpecification3 = "compatibility test";
        processorSpecification4 = "one more test";
        specification1and2ForProcessor =  List.of(processorSpecification1, processorSpecification2);
        specification1and2ForProcessor2 = List.of(processorSpecification3, processorSpecification4);

        //Pageable
        pageableFirstPage = PageRequest.of(1, 10);
        pageableCheckNextPage = PageRequest.of(20, 1);


        objectResponseProcessorRam = Arrays.asList(
                new Object[]{1L, "SATA 1, SATA 2"},
                new Object[]{2L, "21"});

        objectResponseProcessorRam2 = Arrays.asList(
                new Object[]{3L, null},
                new Object[]{4L, "test"});

        objectResponseProcessorRam3 = Arrays.asList(
                new Object[]{1L, "SATA 2"},
                new Object[]{4L, "test"});

        //Components
        ram1 = ComponentEntity.builder()
                .componentId(1L)
                .componentName("Ram 1")
                .componentPrice(100.0)
                .build();

        ram2 = ComponentEntity.builder()
                .componentId(2L)
                .componentName("Ram 2")
                .componentPrice(101.0)
                .build();

        psu1 = ComponentEntity.builder()
                .componentId(3L)
                .componentName("Psu 1")
                .componentPrice(100.0)
                .build();

        psu2 = ComponentEntity.builder()
                .componentId(3L)
                .componentName("Psu 2")
                .componentPrice(100.0)
                .build();

        ssd1 = ComponentEntity.builder()
                .componentId(4L)
                .componentName("Ssd 1")
                .componentPrice(100.0)
                .build();

        graphicsCard1 = ComponentEntity.builder()
                .componentId(5L)
                .componentName("GC 1")
                .componentPrice(100.0)
                .build();

        processor1 = ComponentEntity.builder()
                .componentId(6L)
                .componentName("processor 1")
                .componentPrice(100.0)
                .build();

        motherboard1 = ComponentEntity.builder()
                .componentId(7L)
                .componentName("motherboard 1")
                .componentPrice(100.0)
                .build();

        hardDrive = ComponentEntity.builder()
                .componentId(11L)
                .componentName("hard drive 1")
                .componentPrice(100.0)
                .build();

        cooling = ComponentEntity.builder()
                .componentId(8L)
                .componentName("cooling 1")
                .componentPrice(100.0)
                .build();

        processorCooling = ComponentEntity.builder()
                .componentId(7L)
                .componentName("processor cooling 1")
                .componentPrice(100.0)
                .build();

        computerCase = ComponentEntity.builder()
                .componentId(6L)
                .componentName("computer case 1")
                .componentPrice(100.0)
                .build();

        powerSupply = ComponentEntity.builder()
                .componentId(5L)
                .componentName("cooling 1")
                .componentPrice(100.0)
                .build();

        //List of components
        listOfPsu1 = List.of(psu1);
        listOfPsu2 = List.of(psu2);
        listOf10PSUs = List.of(psu1, psu2, new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity());
        listOf1Ram = List.of(ram1);
        listOf10SSD = List.of(ssd1, new ComponentEntity(), new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity());
        listOf1SSD = List.of(ssd1);
        listOf1GraphicsCard = List.of(graphicsCard1);
        listOf10GraphicsCards = List.of(graphicsCard1, new ComponentEntity(), new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity());
        listOf1Processor = List.of(processor1);
        listOf1Motherboard = List.of(motherboard1);
        listOf1HardDrive = List.of(hardDrive);
        listOf1Cooling = List.of(cooling);
        listOf1ProcessorCooling = List.of(processorCooling);
        listOf1ComputerCase = List.of(computerCase);
        listOf1PowerSupply = List.of(powerSupply);
        //Pages
        oneRam = new PageImpl<>(List.of(ram1),pageableFirstPage, 2);
        twoRams = new PageImpl<>(List.of(ram1,ram2),pageableFirstPage, 2);
        tenRams = new PageImpl<>(List.of(ram1,ram2, new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity(),new ComponentEntity()),pageableFirstPage, 2);

        //Responses
        ram1Response = GetAutomaticCompatibilityResponse.builder()
                .componentId(1L)
                .componentName("Ram 1")
                .price(100.0)
                .thereIsNextPage(false)
                .build();

        ram2Response = GetAutomaticCompatibilityResponse.builder()
                .componentId(2L)
                .componentName("Ram 2")
                .price(101.0)
                .thereIsNextPage(false)
                .build();

        ram1WithNextPageResponse = GetAutomaticCompatibilityResponse.builder()
                .componentId(1L)
                .componentName("Ram 1")
                .price(100.0)
                .thereIsNextPage(true)
                .build();
        ram2WithNextPageResponse = GetAutomaticCompatibilityResponse.builder()
                .componentId(2L)
                .componentName("Ram 2")
                .price(101.0)
                .thereIsNextPage(true)
                .build();


        psu1Response = GetAutomaticCompatibilityResponse.builder()
                .componentId(3L)
                .componentName("Psu 1")
                .price(100.0)
                .thereIsNextPage(false)
                .build();

        psu1ResponseWithNextPage = GetAutomaticCompatibilityResponse.builder()
                .componentId(3L)
                .componentName("Psu 1")
                .price(100.0)
                .thereIsNextPage(true)
                .build();

        ssd1ResponseWithoutNextPage = GetAutomaticCompatibilityResponse.builder()
                .componentId(4L)
                .componentName("Ssd 1")
                .price(100.0)
                .thereIsNextPage(false)
                .build();

        ssd1ResponseWithNextPage = GetAutomaticCompatibilityResponse.builder()
                .componentId(4L)
                .componentName("Ssd 1")
                .price(100.0)
                .thereIsNextPage(true)
                .build();

        graphicsCard1ResponseWithoutNextPage = GetAutomaticCompatibilityResponse.builder()
                .componentId(5L)
                .componentName("GC 1")
                .price(100.0)
                .thereIsNextPage(false)
                .build();

        graphicsCard1ResponseWithNextPage = GetAutomaticCompatibilityResponse.builder()
                .componentId(5L)
                .componentName("GC 1")
                .price(100.0)
                .thereIsNextPage(true)
                .build();


        processor1ResponseWithoutNextPage = GetAutomaticCompatibilityResponse.builder()
                .componentId(6L)
                .componentName("processor 1")
                .price(100.0)
                .thereIsNextPage(false)
                .build();

        motherboard1ResponseWithoutNextPage = GetAutomaticCompatibilityResponse.builder()
                .componentId(7L)
                .componentName("motherboard 1")
                .price(100.0)
                .thereIsNextPage(false)
                .build();

        hardDriveResponse = GetAutomaticCompatibilityResponse.builder()
                .componentId(11L)
                .componentName("hard drive 1")
                .price(100.0)
                .thereIsNextPage(false)
                .build();

        coolingResponse = GetAutomaticCompatibilityResponse.builder()
                .componentId(8L)
                .componentName("cooling 1")
                .price(100.0)
                .thereIsNextPage(false)
                .build();

        processorCoolingResponse = GetAutomaticCompatibilityResponse.builder()
                .componentId(7L)
                .componentName("processor cooling 1")
                .price(100.0)
                .thereIsNextPage(false)
                .build();

        computerCaseResponse = GetAutomaticCompatibilityResponse.builder()
                .componentId(6L)
                .componentName("computer case 1")
                .price(100.0)
                .thereIsNextPage(false)
                .build();

        powerSupplyResponse = GetAutomaticCompatibilityResponse.builder()
                .componentId(5L)
                .componentName("power supply 1")
                .price(100.0)
                .thereIsNextPage(false)
                .build();

        expectedResponseSearchedRamsForProcessor = List.of(ram1Response,ram2Response);
        expectedResponseSearchedRamsForProcessorWithoutNextPage = List.of(ram1Response,ram2Response,new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse());
        expectedResponseHandlePowerSupply = List.of(psu1Response);
        expectedResponseHandlePowerSupplyWithNextPage = List.of(psu1ResponseWithNextPage,new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse());
        expectedResponseHandlePowerSupplyWithoutNextPage = List.of(psu1Response,new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse());
        expectedResponse1RamNoNextPage = List.of(ram1Response);
        expectedResponseSSDWithNextPage = List.of(ssd1ResponseWithNextPage,new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse(),new GetAutomaticCompatibilityResponse());
        expectedResponseSSDWithoutNextPage = List.of(ssd1ResponseWithoutNextPage);
        expectedResponse1GraphicCardWithoutNextPage = List.of(graphicsCard1ResponseWithoutNextPage);
        expectedResponse1ProcessorNoNextPage = List.of(processor1ResponseWithoutNextPage);
        expectedResponse1MotherboardNoNextPage = List.of(motherboard1ResponseWithoutNextPage);
        expectedResponse1HardDriveNoNextPage = List.of(hardDriveResponse);
        expectedResponse1CoolingNoNextPage = List.of(coolingResponse);
        expectedResponse1ProcessorCoolingNoNextPage = List.of(processorCoolingResponse);
        expectedResponse1ComputerCaseNoNextPage = List.of(computerCaseResponse);
        expectedResponse1PowerSupplyNoNextPage = List.of(powerSupplyResponse);


        //Requests
      processorProvidedRamSearchedPC = new ConfiguratorRequest();
      processorProvidedRamSearchedPC.setComponentIds(List.of(1L));
      processorProvidedRamSearchedPC.setSearchedComponentTypeId(4L);
      processorProvidedRamSearchedPC.setPageNumber(1);
      processorProvidedRamSearchedPC.setTypeOfConfiguration("PC");

      processorProvidedRamSearchedWorkstation = new ConfiguratorRequest();
        processorProvidedRamSearchedWorkstation.setComponentIds(List.of(1L));
      processorProvidedRamSearchedWorkstation.setSearchedComponentTypeId(4L);
      processorProvidedRamSearchedWorkstation.setPageNumber(1);
      processorProvidedRamSearchedWorkstation.setTypeOfConfiguration("Workstation");

      processorProvidedRamSearchedServer = new ConfiguratorRequest();
        processorProvidedRamSearchedServer.setComponentIds(List.of(1L));
      processorProvidedRamSearchedServer.setSearchedComponentTypeId(4L);
      processorProvidedRamSearchedServer.setPageNumber(1);
      processorProvidedRamSearchedServer.setTypeOfConfiguration("Server");

      processorProvidedRamSearchedLaptop = new ConfiguratorRequest();
      processorProvidedRamSearchedLaptop.setComponentIds(Arrays.asList(1L));
      processorProvidedRamSearchedLaptop.setSearchedComponentTypeId(4L);
      processorProvidedRamSearchedLaptop.setPageNumber(1);
      processorProvidedRamSearchedLaptop.setTypeOfConfiguration("Laptop");

      requestWithProcessorProvidedSSDSearchedPC = new ConfiguratorRequest();
      requestWithProcessorProvidedSSDSearchedPC.setComponentIds(List.of(1L));
      requestWithProcessorProvidedSSDSearchedPC.setSearchedComponentTypeId(10L);
      requestWithProcessorProvidedSSDSearchedPC.setPageNumber(1);
      requestWithProcessorProvidedSSDSearchedPC.setTypeOfConfiguration("PC");

      requestWithProcessorProvidedSSDSearchedWorkstation = new ConfiguratorRequest();
      requestWithProcessorProvidedSSDSearchedWorkstation.setComponentIds(Arrays.asList(1L)); // Adjust the list as necessary
      requestWithProcessorProvidedSSDSearchedWorkstation.setSearchedComponentTypeId(10L);
      requestWithProcessorProvidedSSDSearchedWorkstation.setPageNumber(1);
      requestWithProcessorProvidedSSDSearchedWorkstation.setTypeOfConfiguration("Workstation");

      requestWithProcessorProvidedSSDSearchedServer = new ConfiguratorRequest();
      requestWithProcessorProvidedSSDSearchedServer.setComponentIds(Arrays.asList(1L)); // Adjust the list as necessary
      requestWithProcessorProvidedSSDSearchedServer.setSearchedComponentTypeId(10L);
      requestWithProcessorProvidedSSDSearchedServer.setPageNumber(1);
      requestWithProcessorProvidedSSDSearchedServer.setTypeOfConfiguration("Server");

      requestWithProcessorProvidedSSDSearchedLaptop = new ConfiguratorRequest();
      requestWithProcessorProvidedSSDSearchedLaptop.setComponentIds(Arrays.asList(1L)); // Adjust the list as necessary
      requestWithProcessorProvidedSSDSearchedLaptop.setSearchedComponentTypeId(10L);
      requestWithProcessorProvidedSSDSearchedLaptop.setPageNumber(1);
      requestWithProcessorProvidedSSDSearchedLaptop.setTypeOfConfiguration("Laptop");

      requestWithProcessorProvidedHardDriveSearchedPC = new ConfiguratorRequest();
      requestWithProcessorProvidedHardDriveSearchedPC.setComponentIds(Arrays.asList(1L)); // Adjust the list as necessary
      requestWithProcessorProvidedHardDriveSearchedPC.setSearchedComponentTypeId(11L);
      requestWithProcessorProvidedHardDriveSearchedPC.setPageNumber(1);
      requestWithProcessorProvidedHardDriveSearchedPC.setTypeOfConfiguration("PC");

      requestWithProcessorProvidedHardDriveSearchedWorkstation = new ConfiguratorRequest();
      requestWithProcessorProvidedHardDriveSearchedWorkstation.setComponentIds(Arrays.asList(1L)); // Adjust the list as necessary
      requestWithProcessorProvidedHardDriveSearchedWorkstation.setSearchedComponentTypeId(11L);
      requestWithProcessorProvidedHardDriveSearchedWorkstation.setPageNumber(1);
      requestWithProcessorProvidedHardDriveSearchedWorkstation.setTypeOfConfiguration("Workstation");

      requestWithProcessorProvidedHardDriveSearchedServer = new ConfiguratorRequest();
      requestWithProcessorProvidedHardDriveSearchedServer.setComponentIds(Arrays.asList(1L)); // Adjust the list as necessary
      requestWithProcessorProvidedHardDriveSearchedServer.setSearchedComponentTypeId(11L);
      requestWithProcessorProvidedHardDriveSearchedServer.setPageNumber(1);
      requestWithProcessorProvidedHardDriveSearchedServer.setTypeOfConfiguration("Server");

      requestWithProcessorProvidedCoolingSearchedPC = new ConfiguratorRequest();
      requestWithProcessorProvidedCoolingSearchedPC.setComponentIds(Arrays.asList(1L)); // Adjust the list as necessary
      requestWithProcessorProvidedCoolingSearchedPC.setSearchedComponentTypeId(8L);
      requestWithProcessorProvidedCoolingSearchedPC.setPageNumber(1);
      requestWithProcessorProvidedCoolingSearchedPC.setTypeOfConfiguration("PC");

      requestWithProcessorProvidedCoolingSearchedLaptop = new ConfiguratorRequest();
      requestWithProcessorProvidedCoolingSearchedLaptop.setComponentIds(Arrays.asList(1L)); // Adjust the list as necessary
      requestWithProcessorProvidedCoolingSearchedLaptop.setSearchedComponentTypeId(8L);
      requestWithProcessorProvidedCoolingSearchedLaptop.setPageNumber(1);
      requestWithProcessorProvidedCoolingSearchedLaptop.setTypeOfConfiguration("Laptop");

      requestWithProcessorProvidedCoolingSearchedServer = new ConfiguratorRequest();
      requestWithProcessorProvidedCoolingSearchedServer.setComponentIds(Arrays.asList(1L)); // Adjust the list as necessary
      requestWithProcessorProvidedCoolingSearchedServer.setSearchedComponentTypeId(8L);
      requestWithProcessorProvidedCoolingSearchedServer.setPageNumber(1);
      requestWithProcessorProvidedCoolingSearchedServer.setTypeOfConfiguration("Server");






      requestWithProcessorProvidedProcessorCoolingSearchedPC = new ConfiguratorRequest();
      requestWithProcessorProvidedProcessorCoolingSearchedPC.setComponentIds(Arrays.asList(1L)); // Adjust the list as necessary
      requestWithProcessorProvidedProcessorCoolingSearchedPC.setSearchedComponentTypeId(7L);
      requestWithProcessorProvidedProcessorCoolingSearchedPC.setPageNumber(1);
      requestWithProcessorProvidedProcessorCoolingSearchedPC.setTypeOfConfiguration("PC");

      requestWithProcessorProvidedProcessorCoolingSearchedLaptop = new ConfiguratorRequest();
      requestWithProcessorProvidedProcessorCoolingSearchedLaptop.setComponentIds(Arrays.asList(1L)); // Adjust the list as necessary
      requestWithProcessorProvidedProcessorCoolingSearchedLaptop.setSearchedComponentTypeId(7L);
      requestWithProcessorProvidedProcessorCoolingSearchedLaptop.setPageNumber(1);
      requestWithProcessorProvidedProcessorCoolingSearchedLaptop.setTypeOfConfiguration("Laptop");

      requestWithProcessorProvidedProcessorCoolingSearchedServer = new ConfiguratorRequest();
      requestWithProcessorProvidedProcessorCoolingSearchedServer.setComponentIds(Arrays.asList(1L)); // Adjust the list as necessary
      requestWithProcessorProvidedProcessorCoolingSearchedServer.setSearchedComponentTypeId(7L);
      requestWithProcessorProvidedProcessorCoolingSearchedServer.setPageNumber(1);
      requestWithProcessorProvidedProcessorCoolingSearchedServer.setTypeOfConfiguration("Server");

      requestWithProcessorProvidedComputerCaseSearchedPC = new ConfiguratorRequest();
      requestWithProcessorProvidedComputerCaseSearchedPC.setComponentIds(Arrays.asList(1L)); // Adjust the list as necessary
      requestWithProcessorProvidedComputerCaseSearchedPC.setSearchedComponentTypeId(6L);
      requestWithProcessorProvidedComputerCaseSearchedPC.setPageNumber(1);
      requestWithProcessorProvidedComputerCaseSearchedPC.setTypeOfConfiguration("PC");

      requestWithProcessorProvidedPowerSupplySearchedPC = new ConfiguratorRequest();
      requestWithProcessorProvidedPowerSupplySearchedPC.setComponentIds(Arrays.asList(1L)); // Adjust the list as necessary
      requestWithProcessorProvidedPowerSupplySearchedPC.setSearchedComponentTypeId(5L);
      requestWithProcessorProvidedPowerSupplySearchedPC.setPageNumber(1);
      requestWithProcessorProvidedPowerSupplySearchedPC.setTypeOfConfiguration("PC");

      requestWithProcessorProvidedPowerSupplySearchedWorkstation = new ConfiguratorRequest();
      requestWithProcessorProvidedPowerSupplySearchedWorkstation.setComponentIds(Arrays.asList(1L));
      requestWithProcessorProvidedPowerSupplySearchedWorkstation.setSearchedComponentTypeId(5L);
      requestWithProcessorProvidedPowerSupplySearchedWorkstation.setPageNumber(1);
      requestWithProcessorProvidedPowerSupplySearchedWorkstation.setTypeOfConfiguration("Workstation");

// Request: Processor Provided Power Supply Searched for Server
      requestWithProcessorProvidedPowerSupplySearchedServer = new ConfiguratorRequest();
      requestWithProcessorProvidedPowerSupplySearchedServer.setComponentIds(Arrays.asList(1L));
      requestWithProcessorProvidedPowerSupplySearchedServer.setSearchedComponentTypeId(5L);
      requestWithProcessorProvidedPowerSupplySearchedServer.setPageNumber(1);
      requestWithProcessorProvidedPowerSupplySearchedServer.setTypeOfConfiguration("Server");

// Request: Processor Provided Graphics Card Searched for PC
      requestWithProcessorProvidedGraphicsCardSearchedPC = new ConfiguratorRequest();
      requestWithProcessorProvidedGraphicsCardSearchedPC.setComponentIds(Arrays.asList(1L));
      requestWithProcessorProvidedGraphicsCardSearchedPC.setSearchedComponentTypeId(3L);
      requestWithProcessorProvidedGraphicsCardSearchedPC.setPageNumber(1);
      requestWithProcessorProvidedGraphicsCardSearchedPC.setTypeOfConfiguration("PC");

// Request: Graphics Card Provided Processor Searched for PC
      requestWithGraphicsCardProvidedProcessorSearchedPC = new ConfiguratorRequest();
      requestWithGraphicsCardProvidedProcessorSearchedPC.setComponentIds(Arrays.asList(3L));
      requestWithGraphicsCardProvidedProcessorSearchedPC.setSearchedComponentTypeId(1L);
      requestWithGraphicsCardProvidedProcessorSearchedPC.setPageNumber(1);
      requestWithGraphicsCardProvidedProcessorSearchedPC.setTypeOfConfiguration("PC");

// Request: Graphics Card Provided Processor Searched for Server
      requestWithGraphicsCardProvidedProcessorSearchedServer = new ConfiguratorRequest();
      requestWithGraphicsCardProvidedProcessorSearchedServer.setComponentIds(Arrays.asList(3L));
      requestWithGraphicsCardProvidedProcessorSearchedServer.setSearchedComponentTypeId(1L);
      requestWithGraphicsCardProvidedProcessorSearchedServer.setPageNumber(1);
      requestWithGraphicsCardProvidedProcessorSearchedServer.setTypeOfConfiguration("Server");

// Request: Graphics Card Provided Processor Searched for Workstation
      requestWithGraphicsCardProvidedProcessorSearchedWorkstation = new ConfiguratorRequest();
      requestWithGraphicsCardProvidedProcessorSearchedWorkstation.setComponentIds(Arrays.asList(3L));
      requestWithGraphicsCardProvidedProcessorSearchedWorkstation.setSearchedComponentTypeId(1L);
      requestWithGraphicsCardProvidedProcessorSearchedWorkstation.setPageNumber(1);
      requestWithGraphicsCardProvidedProcessorSearchedWorkstation.setTypeOfConfiguration("Workstation");

// Request: Graphics Card Provided Motherboard Searched for PC
      requestWithGraphicsCardProvidedMotherboardSearchedPC = new ConfiguratorRequest();
      requestWithGraphicsCardProvidedMotherboardSearchedPC.setComponentIds(Arrays.asList(3L));
      requestWithGraphicsCardProvidedMotherboardSearchedPC.setSearchedComponentTypeId(2L);
      requestWithGraphicsCardProvidedMotherboardSearchedPC.setPageNumber(1);
      requestWithGraphicsCardProvidedMotherboardSearchedPC.setTypeOfConfiguration("PC");


        requestWithGraphicsCardProvidedMotherboardSearchedServer = new ConfiguratorRequest();
        requestWithGraphicsCardProvidedMotherboardSearchedServer.setComponentIds(Arrays.asList(3L));
        requestWithGraphicsCardProvidedMotherboardSearchedServer.setSearchedComponentTypeId(2L);
        requestWithGraphicsCardProvidedMotherboardSearchedServer.setPageNumber(1);
        requestWithGraphicsCardProvidedMotherboardSearchedServer.setTypeOfConfiguration("Server");

// Request: Graphics Card Provided Motherboard Searched for Workstation
        requestWithGraphicsCardProvidedMotherboardSearchedWorkstation = new ConfiguratorRequest();
        requestWithGraphicsCardProvidedMotherboardSearchedWorkstation.setComponentIds(Arrays.asList(3L));
        requestWithGraphicsCardProvidedMotherboardSearchedWorkstation.setSearchedComponentTypeId(2L);
        requestWithGraphicsCardProvidedMotherboardSearchedWorkstation.setPageNumber(1);
        requestWithGraphicsCardProvidedMotherboardSearchedWorkstation.setTypeOfConfiguration("Workstation");

// Request: Provided Component ID Same as Searched One
        requestWithProvidedComponentIdTheSameAsSearchedOne = new ConfiguratorRequest();
        requestWithProvidedComponentIdTheSameAsSearchedOne.setComponentIds(Arrays.asList(1L));
        requestWithProvidedComponentIdTheSameAsSearchedOne.setSearchedComponentTypeId(1L);
        requestWithProvidedComponentIdTheSameAsSearchedOne.setPageNumber(1);
        requestWithProvidedComponentIdTheSameAsSearchedOne.setTypeOfConfiguration("PC");

// Request: Max Provided Components and Last Searched PSU
        requestWithMaxProvidedComponentsAndLastSearchedPSU = new ConfiguratorRequest();
        requestWithMaxProvidedComponentsAndLastSearchedPSU.setComponentIds(Arrays.asList(1L, 2L, 3L, 4L, 7L, 8L, 11L));
        requestWithMaxProvidedComponentsAndLastSearchedPSU.setSearchedComponentTypeId(5L);
        requestWithMaxProvidedComponentsAndLastSearchedPSU.setPageNumber(1);
        requestWithMaxProvidedComponentsAndLastSearchedPSU.setTypeOfConfiguration("PC");

// Request: Max Provided Components and Last Searched PSU (Variation 2)
        requestWithMaxProvidedComponentsAndLastSearchedPSU2 = new ConfiguratorRequest();
        requestWithMaxProvidedComponentsAndLastSearchedPSU2.setComponentIds(Arrays.asList(1L, 2L, 3L, 4L, 9L, 10L, 11L));
        requestWithMaxProvidedComponentsAndLastSearchedPSU2.setSearchedComponentTypeId(5L);
        requestWithMaxProvidedComponentsAndLastSearchedPSU2.setPageNumber(1);
        requestWithMaxProvidedComponentsAndLastSearchedPSU2.setTypeOfConfiguration("PC");

// Request: Processor and Graphics Card Provided RAM Searched for PC
        requestProcessorAndGraphicsCardProvidedRamSearchedPC = new ConfiguratorRequest();
        requestProcessorAndGraphicsCardProvidedRamSearchedPC.setComponentIds(Arrays.asList(1L, 3L));
        requestProcessorAndGraphicsCardProvidedRamSearchedPC.setSearchedComponentTypeId(4L);
        requestProcessorAndGraphicsCardProvidedRamSearchedPC.setPageNumber(1);
        requestProcessorAndGraphicsCardProvidedRamSearchedPC.setTypeOfConfiguration("PC");

// Request: Processor, Graphics Card, and Motherboard Provided RAM Searched for PC
        requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC = new ConfiguratorRequest();
        requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.setComponentIds(Arrays.asList(1L, 3L, 2L));
        requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.setSearchedComponentTypeId(4L);
        requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.setPageNumber(1);
        requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.setTypeOfConfiguration("PC");
        //Specification IDs
        allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam = List.of(1L,2L);


        ProcessorPC = new HashMap<>();
        ProcessorPC.put(1070L,List.of("Workstation"));
        firstEntryProcessorPC = ProcessorPC.entrySet().iterator().next();

        RamPC = new HashMap<>();
        RamPC.put(1070L,List.of("PC"));
        firstEntryRamPC = RamPC.entrySet().iterator().next();

        ProcessorServer = new HashMap<>();
        ProcessorServer.put(1070L, List.of("Server"));
        firstEntryProcessorServer = ProcessorServer.entrySet().iterator().next();

        ProcessorWorkstation = new HashMap<>();
        ProcessorWorkstation.put(1070L, List.of("Workstation"));
        firstEntryProcessorWorkstation = ProcessorWorkstation.entrySet().iterator().next();

        RamServer = new HashMap<>();
        RamServer.put(1070L, List.of("Server"));
        firstEntryRamServer = RamServer.entrySet().iterator().next();

        RamWorkstation = new HashMap<>();
        RamWorkstation.put(1070L, List.of("Workstation"));
        firstEntryRamWorkstation = RamWorkstation.entrySet().iterator().next();

        StorageServer = new HashMap<>();
        StorageServer.put(947L, List.of("Server", "server"));
        firstEntryStorageServer = StorageServer.entrySet().iterator().next();

        StoragePC = new HashMap<>();
        StoragePC.put(947L, List.of("PC"));
        firstEntryStoragePC = StoragePC.entrySet().iterator().next();

        CoolingServer = new HashMap<>();
        CoolingServer.put(954L, List.of("Fan", "Fan module"));
        firstEntryCoolingServer = CoolingServer.entrySet().iterator().next();

        CoolingPC = new HashMap<>();
        CoolingPC.put(954L, List.of("Liquid cooling kit", "Heatsink", "Radiatior", "Air cooler",
                "Radiator block", "Cooler", "All-in-one liquid cooler"));
        firstEntryCoolingPC = CoolingPC.entrySet().iterator().next();

        CoolingLaptop = new HashMap<>();
        CoolingLaptop.put(954L, List.of("Thermal paste"));
        firstEntryCoolingLaptop = CoolingLaptop.entrySet().iterator().next();

        BikeCity = new HashMap<>();
        BikeCity.put(1792L, List.of("CITY BIKE"));
        firstEntryBikeCity = BikeCity.entrySet().iterator().next();

        BikeDownhill = new HashMap<>();
        BikeDownhill.put(1792L, List.of("DOWNHILL"));
        firstEntryBikeDownhill = BikeDownhill.entrySet().iterator().next();

        SSDPC = new HashMap<>();
        SSDPC.put(1070L, List.of("PC"));
        firstEntrySSDPC = SSDPC.entrySet().iterator().next();

        SSDServer = new HashMap<>();
        SSDServer.put(1070L, List.of("Server"));
        firstEntrySSDServer = SSDServer.entrySet().iterator().next();

        emptyMap = new HashMap<>();

        MotherboardPC = new HashMap<>();
        MotherboardPC.put(1070L, List.of("PC"));
        firstEntryMotherboardPC = MotherboardPC.entrySet().iterator().next();
    }


    @Test
    void givenOneComponentAndASearchedComponentType_whenGettingCompatibility_returnsCompatibleComponentFromSearchedComponentType()
    {
        //Used variables:
        //requestOneComponentProvided
        //componentTypeProcessor (provided)
        //typeOfConfigurationPC
        //specification1and2ForProcessor
        //objectResponseProcessorRam
        //specification1and2ForProcessor2
        //objectResponseProcessorRam2
        //twoRams
        //expectedResponseSearchedRamsForProcessor

        when(componentRepository.existsById(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(processorProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(true);


        when(componentRepository.findComponentTypeIdByComponentId(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedPC.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(processorProvidedRamSearchedPC.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);

        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam);

        when(componentSpecificationListRepository.findValuesForAComponentSpecificationTypeBySpecificationTypeComponentRelationAndComponentId(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(0),processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(specification1and2ForProcessor);
        when(compatibilityRepository.findSpecification2IdsAndValuesOfSecondSpecification2(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC,allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(0),specification1and2ForProcessor)).thenReturn(objectResponseProcessorRam);

        when(componentSpecificationListRepository.findValuesForAComponentSpecificationTypeBySpecificationTypeComponentRelationAndComponentId(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(1),processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(specification1and2ForProcessor2);
        when(compatibilityRepository.findSpecification2IdsAndValuesOfSecondSpecification2(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC,allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(1),specification1and2ForProcessor2)).thenReturn(objectResponseProcessorRam2);

        when(componentRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(twoRams);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(processorProvidedRamSearchedPC);
        assertEquals(expectedResponseSearchedRamsForProcessor.get(0).getComponentName(), actualResponse.get(0).getComponentName());
        assertEquals(expectedResponseSearchedRamsForProcessor.get(0).getPrice(), actualResponse.get(0).getPrice());

    }

    @Test
    void givenOneComponentAndASearchedComponentType_whenGettingCompatibilityAndConfigurationTypeHasBeenChanged_returnsCompatibleComponentFromSearchedComponentType()
    {
        //Used variables:
        //requestOneComponentProvided
        //componentTypeProcessor (provided)
        //typeOfConfigurationPC
        //specification1and2ForProcessor
        //objectResponseProcessorRam
        //specification1and2ForProcessor2
        //objectResponseProcessorRam2
        //twoRams
        //expectedResponseSearchedRamsForProcessor

        when(componentRepository.existsById(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(processorProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(true);


        when(componentRepository.findComponentTypeIdByComponentId(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedPC.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(processorProvidedRamSearchedPC.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(false);

        CompatibilityError exception = assertThrows(CompatibilityError.class, () -> {
            compatibilityBetweenComponents.automaticCompatibility(processorProvidedRamSearchedPC);
        });
        assertEquals("One of the selected components is meant to be used in a different configuration", exception.getReason());

    }


    @Test
    void givenOneComponentAndASearchedComponentType_whenGettingCompatibilityAndProvidedComponentIdIsInvalid_thenThrowsErrorForNotFound()
    {
        //Used variables:
        //requestOneComponentProvided
        when(componentRepository.existsById(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(false);

        ObjectNotFound exception = assertThrows(ObjectNotFound.class, () -> {
            compatibilityBetweenComponents.automaticCompatibility(processorProvidedRamSearchedPC);
        });
        assertEquals("Components not found: [1]", exception.getReason());
    }

    @Test
    void givenOneComponentAndASearchedComponentType_whenGettingCompatibilityAndSearchedComponentTypeIsInvalid_thenThrowsErrorForNotFound()
    {
        //Used variables:
        //requestOneComponentProvided
        when(componentRepository.existsById(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(processorProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(false);

        ObjectNotFound exception = assertThrows(ObjectNotFound.class, () -> {
            compatibilityBetweenComponents.automaticCompatibility(processorProvidedRamSearchedPC);
        });
        assertEquals("Component type not found", exception.getReason());
    }


    @Test
    void givenComponentIdAndSearchedComponentTypeFromTheSameComponentTypeAsProvidedComponent_whenGettingCompatibility_thenThrowsCompatibilityError()
    {
        //Used variables:
        //requestWithProvidedComponentIdTheSameAsSearchedOne
        //componentTypeProcessor

        when(componentRepository.existsById(requestWithProvidedComponentIdTheSameAsSearchedOne.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(requestWithProvidedComponentIdTheSameAsSearchedOne.getSearchedComponentTypeId())).thenReturn(true);
        when(componentRepository.findComponentTypeIdByComponentId(requestWithProvidedComponentIdTheSameAsSearchedOne.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());

        CompatibilityError exception = assertThrows(CompatibilityError.class, () -> {
            compatibilityBetweenComponents.automaticCompatibility(requestWithProvidedComponentIdTheSameAsSearchedOne);
        });
        assertEquals("Once a component is selected, other components from the same category can not be searched.", exception.getReason());
    }

    @Test
    void givenAllComponents_whenSearchingForPSU_thenGivesPSUsThatCanHandleThePowerConsumption()
    {
        //Used variables:
        //requestWithMaxProvidedComponentsAndLastSearchedPSU
        //componentTypePSU
        //typeOfConfigurationPC
        //pageable
        //listOfPsu1
        //listOf10PSUs

        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(0))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(1))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(2))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(3))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(4))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(5))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(6))).thenReturn(true);
        when(componentTypeRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU.getSearchedComponentTypeId())).thenReturn(true);

        //when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestWithMaxProvidedComponentsAndLastSearchedPSU.getTypeOfConfiguration(),componentTypePSU.getId()));

        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(0))).thenReturn(1L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(1))).thenReturn(2L); //50
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(2))).thenReturn(3L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(3))).thenReturn(4L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(4))).thenReturn(7L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(5))).thenReturn(8L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(6))).thenReturn(11L); //10


        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(0),1120L)).thenReturn(10.0);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(2),937L)).thenReturn(10.0);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU.getComponentIds().get(6),1144L)).thenReturn(10.0);

        when(componentRepository.findComponentsBySpecificationsNative(110.0,typeOfConfigurationPC,20.0,pageableFirstPage)).thenReturn(listOfPsu1);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithMaxProvidedComponentsAndLastSearchedPSU);
        //assertEquals(Set.copyOf(expectedResponseHandlePowerSupply), Set.copyOf(actualResponse));
        assertEquals(expectedResponseHandlePowerSupply.get(0).getComponentName(), actualResponse.get(0).getComponentName());

    }


    @Test
    void givenAllComponentsSomeOfWhichDoNotHavePowerSupplySpecifiedAtAllOfForFirstIdFromSwitchStatement_whenSearchingForPSU_thenGivesPSUsThatCanHandleThePowerConsumption()
    {
        //Used variables:
        //requestWithMaxProvidedComponentsAndLastSearchedPSU2
        //componentTypePSU
        //typeOfConfigurationPC
        //pageable
        //listOfPsu1

        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(0))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(1))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(2))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(3))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(4))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(5))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(6))).thenReturn(true);
        when(componentTypeRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getSearchedComponentTypeId())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(0))).thenReturn(1L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(1))).thenReturn(2L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(2))).thenReturn(3L);
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(3))).thenReturn(4L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(4))).thenReturn(9L); //30
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(5))).thenReturn(10L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(6))).thenReturn(11L); //10

        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(0),1120L)).thenReturn(10.0);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(2),937L)).thenReturn(null);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(5),1144L)).thenReturn(null);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(5),1145L)).thenReturn(10.0);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(6),1144L)).thenReturn(null);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(6),922L)).thenReturn(10.0);

        when(componentRepository.findComponentsBySpecificationsNative(120.0,typeOfConfigurationPC,10.0,pageableFirstPage)).thenReturn(listOf10PSUs);
        when(componentRepository.findComponentsBySpecificationsNative(120.0,typeOfConfigurationPC,10.0,pageableCheckNextPage)).thenReturn(listOfPsu2);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithMaxProvidedComponentsAndLastSearchedPSU2);
        assertEquals(expectedResponseHandlePowerSupplyWithNextPage.get(0).getComponentName(), actualResponse.get(0).getComponentName());
    }

    @Test
    void givenComponentsAndLookingForPSU_whenNoPSUThatCanHandleThePowerConsumptionIsFound_thenThrowsAnError()
    {
        //Used variables:
        //requestWithMaxProvidedComponentsAndLastSearchedPSU2
        //componentTypePSU
        //typeOfConfigurationPC
        //pageable
        //listOfPsu1

        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(0))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(1))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(2))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(3))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(4))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(5))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(6))).thenReturn(true);
        when(componentTypeRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getSearchedComponentTypeId())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(0))).thenReturn(1L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(1))).thenReturn(2L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(2))).thenReturn(3L);
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(3))).thenReturn(4L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(4))).thenReturn(9L); //30
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(5))).thenReturn(10L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(6))).thenReturn(11L); //10

        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(0),1120L)).thenReturn(10.0);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(2),937L)).thenReturn(null);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(5),1144L)).thenReturn(null);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(5),1145L)).thenReturn(10.0);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(6),1144L)).thenReturn(null);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(6),922L)).thenReturn(10.0);

        when(componentRepository.findComponentsBySpecificationsNative(120.0,typeOfConfigurationPC,10.0,pageableFirstPage)).thenReturn(List.of());

        ObjectNotFound exception = assertThrows(ObjectNotFound.class, () -> {
            compatibilityBetweenComponents.automaticCompatibility(requestWithMaxProvidedComponentsAndLastSearchedPSU2);});
        assertEquals("PSUs that can handle the power consumption were not found", exception.getReason());
    }

    @Test
    void givenComponentsAndLookingForPSU_whenLookingForPSUThatCanHandleThePowerConsumptionWithoutHavingANextPage_thenThrowsAnError()
    {
        //Used variables:
        //requestWithMaxProvidedComponentsAndLastSearchedPSU2
        //componentTypePSU
        //typeOfConfigurationPC
        //pageable
        //listOfPsu1

        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(0))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(1))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(2))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(3))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(4))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(5))).thenReturn(true);
        when(componentRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(6))).thenReturn(true);
        when(componentTypeRepository.existsById(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getSearchedComponentTypeId())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(0))).thenReturn(1L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(1))).thenReturn(2L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(2))).thenReturn(3L);
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(3))).thenReturn(4L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(4))).thenReturn(9L); //30
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(5))).thenReturn(10L); //10
        when(componentRepository.findComponentTypeIdByComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(6))).thenReturn(11L); //10

        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(0),1120L)).thenReturn(10.0);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(2),937L)).thenReturn(null);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(5),1144L)).thenReturn(null);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(5),1145L)).thenReturn(10.0);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(6),1144L)).thenReturn(null);
        when(componentSpecificationListRepository.findValuesBySpecificationTypeIdAndComponentId(requestWithMaxProvidedComponentsAndLastSearchedPSU2.getComponentIds().get(6),922L)).thenReturn(10.0);

        when(componentRepository.findComponentsBySpecificationsNative(120.0,typeOfConfigurationPC,10.0,pageableFirstPage)).thenReturn(listOf10PSUs);
        when(componentRepository.findComponentsBySpecificationsNative(120.0,typeOfConfigurationPC,10.0,pageableCheckNextPage)).thenReturn(List.of());

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithMaxProvidedComponentsAndLastSearchedPSU2);
        assertEquals(expectedResponseHandlePowerSupplyWithoutNextPage.get(0).getComponentName(), actualResponse.get(0).getComponentName());
    }


    @Test
    void givenProcessorAndASearchedComponentTypeForPCConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
    {
        //Used variables:
        //requestOneComponentProvided
        //componentTypeProcessor (provided)
        //typeOfConfigurationPC
        //listOf1Ram
        //expectedResponse1RamNoNextPage

        when(componentRepository.existsById(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(processorProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
        when(componentRepository.findComponentTypeIdByComponentId(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedPC.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(processorProvidedRamSearchedPC.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);
        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedPC.getTypeOfConfiguration(),processorProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(RamPC);
        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(processorProvidedRamSearchedPC.getSearchedComponentTypeId(),firstEntryRamPC.getKey(),firstEntryRamPC.getValue(),pageableFirstPage)).thenReturn(listOf1Ram);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(processorProvidedRamSearchedPC);
        //assertEquals(Set.copyOf(expectedResponse1RamNoNextPage), Set.copyOf(actualResponse));
        assertEquals(expectedResponse1RamNoNextPage.get(0).getComponentName(), actualResponse.get(0).getComponentName());

    }

    @Test
    void givenProcessorAndASearchedComponentTypeForServerConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
    {
        //Used variables:
        //processorProvidedRamSearchedServer
        //componentTypeProcessor (provided)
        //typeOfConfigurationServer
        //listOf1Ram
        //expectedResponse1RamNoNextPage

        when(componentRepository.existsById(processorProvidedRamSearchedServer.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(processorProvidedRamSearchedServer.getSearchedComponentTypeId())).thenReturn(true);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedServer.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorServer);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(processorProvidedRamSearchedServer.getComponentIds().get(0),firstEntryProcessorServer.getKey(),firstEntryProcessorServer.getValue())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(processorProvidedRamSearchedServer.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),processorProvidedRamSearchedServer.getSearchedComponentTypeId(),typeOfConfigurationServer)).thenReturn(List.of());
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedServer.getTypeOfConfiguration(),processorProvidedRamSearchedServer.getSearchedComponentTypeId())).thenReturn(RamServer);

        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(processorProvidedRamSearchedServer.getSearchedComponentTypeId(),1070L,List.of("Server"),pageableFirstPage)).thenReturn(listOf1Ram);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(processorProvidedRamSearchedServer);
//        assertEquals(Set.copyOf(expectedResponse1RamNoNextPage), Set.copyOf(actualResponse));
        assertEquals(expectedResponse1RamNoNextPage.get(0).getComponentName(), actualResponse.get(0).getComponentName());

    }

    @Test
    void givenProcessorAndASearchedComponentTypeForWorkstationConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
    {
        //Used variables:
        //requestOneComponentProvided
        //componentTypeProcessor (provided)
        //typeOfConfigurationWorkstation
        //listOf1Ram
        //expectedResponse1RamNoNextPage

        when(componentRepository.existsById(processorProvidedRamSearchedWorkstation.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(processorProvidedRamSearchedWorkstation.getSearchedComponentTypeId())).thenReturn(true);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedWorkstation.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorWorkstation);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(processorProvidedRamSearchedWorkstation.getComponentIds().get(0),firstEntryProcessorWorkstation.getKey(),firstEntryProcessorWorkstation.getValue())).thenReturn(true);


        when(componentRepository.findComponentTypeIdByComponentId(processorProvidedRamSearchedWorkstation.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),processorProvidedRamSearchedWorkstation.getSearchedComponentTypeId(),typeOfConfigurationWorkstation)).thenReturn(List.of());
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedWorkstation.getTypeOfConfiguration(),processorProvidedRamSearchedWorkstation.getSearchedComponentTypeId())).thenReturn(RamWorkstation);

        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(processorProvidedRamSearchedWorkstation.getSearchedComponentTypeId(),1070L,List.of("Workstation"),pageableFirstPage)).thenReturn(listOf1Ram);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(processorProvidedRamSearchedWorkstation);
        //assertEquals(Set.copyOf(expectedResponse1RamNoNextPage), Set.copyOf(actualResponse));
        assertEquals(expectedResponse1RamNoNextPage.get(0).getComponentName(), actualResponse.get(0).getComponentName());

    }

    @Test
    void givenProcessorAndASearchedComponentTypeForPCConfiguration_whenNoComponentAreFound_throwsAnError()
    {
        //Used variables:
        //processorProvidedRamSearchedLaptop
        //componentTypeProcessor (provided)
        //typeOfConfigurationLaptop
        //listOf1Ram
        //expectedResponse1RamNoNextPage

        when(componentRepository.existsById(processorProvidedRamSearchedLaptop.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(processorProvidedRamSearchedLaptop.getSearchedComponentTypeId())).thenReturn(true);
       // when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedLaptop.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        //when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(processorProvidedRamSearchedLaptop.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);

//        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedLaptop.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(Proce);
//        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(processorProvidedRamSearchedLaptop.getComponentIds().get(0),firstEntryProcessorWorkstation.getKey(),firstEntryProcessorWorkstation.getValue())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(processorProvidedRamSearchedLaptop.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
        //when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),processorProvidedRamSearchedLaptop.getSearchedComponentTypeId(),typeOfConfigurationLaptop)).thenReturn(List.of());
        //when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(processorProvidedRamSearchedLaptop.getSearchedComponentTypeId(),1070L,List.of("PC"),pageableFirstPage)).thenReturn(List.of());

        CompatibilityError exception = assertThrows(CompatibilityError.class, () -> {
            compatibilityBetweenComponents.automaticCompatibility(processorProvidedRamSearchedLaptop);});
        assertEquals("COMPONENTS_FROM_CATEGORY_NOT_FOUND", exception.getReason());
    }

    @Test
    void givenProcessorAndASearchedComponentTypeForPCConfigurationWithNextPage_whenSearchingForComponentsFromSSD_returnsSSDComponents()
    {
        //Used variables:
        //requestWithProcessorProvidedSSDSearchedPC
        //componentTypeProcessor (provided)
        //typeOfConfigurationLaptop
        //listOf1Ram
        //expectedResponse1RamNoNextPage

        when(componentRepository.existsById(requestWithProcessorProvidedSSDSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(requestWithProcessorProvidedSSDSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestWithProcessorProvidedSSDSearchedPC.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(requestWithProcessorProvidedSSDSearchedPC.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);


        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedSSDSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedSSDSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestWithProcessorProvidedSSDSearchedPC.getTypeOfConfiguration(),componentTypeSSD.getId())).thenReturn(SSDPC);

        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedSSDSearchedPC.getSearchedComponentTypeId(),1070L,List.of("PC"),pageableFirstPage)).thenReturn(listOf10SSD);
        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedSSDSearchedPC.getSearchedComponentTypeId(),1070L,List.of("PC"),pageableCheckNextPage)).thenReturn(listOf1SSD);


        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedSSDSearchedPC);
        assertEquals(expectedResponseSSDWithNextPage.get(0).getComponentName(), actualResponse.get(0).getComponentName());
    }

    @Test
    void givenProcessorAndASearchedComponentTypeForPCConfigurationWithoutNextPage_whenSearchingForComponentsFromSSD_returnsSSDComponents()
    {
        //Used variables:
        //requestWithProcessorProvidedSSDSearchedPC
        //componentTypeProcessor (provided)
        //typeOfConfigurationLaptop
        //listOf1Ram
        //expectedResponse1RamNoNextPage

        when(componentRepository.existsById(requestWithProcessorProvidedSSDSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(requestWithProcessorProvidedSSDSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestWithProcessorProvidedSSDSearchedPC.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(requestWithProcessorProvidedSSDSearchedPC.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedSSDSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedSSDSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestWithProcessorProvidedSSDSearchedPC.getTypeOfConfiguration(),componentTypeSSD.getId())).thenReturn(SSDPC);

        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedSSDSearchedPC.getSearchedComponentTypeId(),1070L,List.of("PC"),pageableFirstPage)).thenReturn(listOf10SSD);
        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedSSDSearchedPC.getSearchedComponentTypeId(),1070L,List.of("PC"),pageableCheckNextPage)).thenReturn(List.of());

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedSSDSearchedPC);
        assertEquals(expectedResponseSSDWithoutNextPage.get(0).getComponentName(), actualResponse.get(0).getComponentName());
    }



    @Test
    void givenProcessorAndASearchedComponentTypeForSSDServerConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
    {
        //Used variables:
        //processorProvidedRamSearchedServer
        //componentTypeProcessor (provided)
        //typeOfConfigurationServer
        //listOf1Ram
        //expectedResponse1RamNoNextPage

        when(componentRepository.existsById(requestWithProcessorProvidedSSDSearchedServer.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(requestWithProcessorProvidedSSDSearchedServer.getSearchedComponentTypeId())).thenReturn(true);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestWithProcessorProvidedSSDSearchedServer.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(requestWithProcessorProvidedSSDSearchedServer.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedSSDSearchedServer.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedSSDSearchedServer.getSearchedComponentTypeId(),typeOfConfigurationServer)).thenReturn(List.of());
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestWithProcessorProvidedSSDSearchedServer.getTypeOfConfiguration(),componentTypeSSD.getId())).thenReturn(SSDServer);

        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedSSDSearchedServer.getSearchedComponentTypeId(),1070L,List.of("Server"),pageableFirstPage)).thenReturn(listOf1Ram);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedSSDSearchedServer);
        assertEquals(expectedResponse1RamNoNextPage.get(0).getComponentName(),actualResponse.get(0).getComponentName());
    }

//    @Test
//    void givenProcessorAndASearchedComponentTypeForSSDWorkstationConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        //Used variables:
//        //processorProvidedRamSearchedServer
//        //componentTypeProcessor (provided)
//        //typeOfConfigurationServer
//        //listOf1Ram
//        //expectedResponse1RamNoNextPage
//
//        when(componentRepository.existsById(requestWithProcessorProvidedSSDSearchedWorkstation.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithProcessorProvidedSSDSearchedWorkstation.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedSSDSearchedWorkstation.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedSSDSearchedWorkstation.getSearchedComponentTypeId(),typeOfConfigurationWorkstation)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedSSDSearchedWorkstation.getSearchedComponentTypeId(),1070L,List.of("Workstation"),pageableFirstPage)).thenReturn(listOf1Ram);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedSSDSearchedWorkstation);
//        assertEquals(Set.copyOf(expectedResponse1RamNoNextPage), Set.copyOf(actualResponse));
//    }
//
//    @Test
//    void givenProcessorAndASearchedComponentTypeForSSDLaptopConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        //Used variables:
//        //processorProvidedRamSearchedServer
//        //componentTypeProcessor (provided)
//        //typeOfConfigurationServer
//        //listOf1Ram
//        //expectedResponse1RamNoNextPage
//
//        when(componentRepository.existsById(requestWithProcessorProvidedSSDSearchedLaptop.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithProcessorProvidedSSDSearchedLaptop.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedSSDSearchedLaptop.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedSSDSearchedLaptop.getSearchedComponentTypeId(),typeOfConfigurationLaptop)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedSSDSearchedLaptop.getSearchedComponentTypeId(),1070L,List.of("Notebook"),pageableFirstPage)).thenReturn(listOf1Ram);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedSSDSearchedLaptop);
//        assertEquals(Set.copyOf(expectedResponse1RamNoNextPage), Set.copyOf(actualResponse));
//    }
//
    @Test
    void givenProcessorAndASearchedComponentTypeForGraphicsCartPCConfigurationWithoutNextPage_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
    {
        when(componentRepository.existsById(requestWithProcessorProvidedGraphicsCardSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(requestWithProcessorProvidedGraphicsCardSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestWithProcessorProvidedGraphicsCardSearchedPC.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(requestWithProcessorProvidedGraphicsCardSearchedPC.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedGraphicsCardSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedGraphicsCardSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
        when(componentRepository.findByComponentType_Id(requestWithProcessorProvidedGraphicsCardSearchedPC.getSearchedComponentTypeId(),pageableFirstPage)).thenReturn(listOf1GraphicsCard);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedGraphicsCardSearchedPC);
        assertEquals(expectedResponse1GraphicCardWithoutNextPage.get(0).getComponentName(), actualResponse.get(0).getComponentName());
    }

    @Test
    void givenProcessorAndASearchedComponentTypeForGraphicsCartPCConfigurationWithNextPage_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
    {
        when(componentRepository.existsById(requestWithProcessorProvidedGraphicsCardSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(requestWithProcessorProvidedGraphicsCardSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestWithProcessorProvidedGraphicsCardSearchedPC.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(requestWithProcessorProvidedGraphicsCardSearchedPC.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedGraphicsCardSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedGraphicsCardSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
        when(componentRepository.findByComponentType_Id(requestWithProcessorProvidedGraphicsCardSearchedPC.getSearchedComponentTypeId(),pageableFirstPage)).thenReturn(listOf10GraphicsCards);
        when(componentRepository.findByComponentType_Id(requestWithProcessorProvidedGraphicsCardSearchedPC.getSearchedComponentTypeId(),pageableCheckNextPage)).thenReturn(List.of());

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedGraphicsCardSearchedPC);
        assertEquals(expectedResponse1GraphicCardWithoutNextPage.get(0).getComponentName(), actualResponse.get(0).getComponentName());

    }
//
//    @Test
//    void givenGraphicsCardAndASearchedComponentTypeForProcessorPCConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        when(componentRepository.existsById(requestWithGraphicsCardProvidedProcessorSearchedPC.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithGraphicsCardProvidedProcessorSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithGraphicsCardProvidedProcessorSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeGraphicsCard.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeGraphicsCard.getId(),requestWithGraphicsCardProvidedProcessorSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithGraphicsCardProvidedProcessorSearchedPC.getSearchedComponentTypeId(),1070L,List.of("Workstation"),pageableFirstPage)).thenReturn(listOf1Processor);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithGraphicsCardProvidedProcessorSearchedPC);
//        assertEquals(Set.copyOf(expectedResponse1ProcessorNoNextPage), Set.copyOf(actualResponse));
//
//    }
//
//    @Test
//    void givenGraphicsCardAndASearchedComponentTypeForProcessorServerConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        when(componentRepository.existsById(requestWithGraphicsCardProvidedProcessorSearchedServer.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithGraphicsCardProvidedProcessorSearchedServer.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithGraphicsCardProvidedProcessorSearchedServer.getComponentIds().get(0))).thenReturn(componentTypeGraphicsCard.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeGraphicsCard.getId(),requestWithGraphicsCardProvidedProcessorSearchedServer.getSearchedComponentTypeId(),typeOfConfigurationServer)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithGraphicsCardProvidedProcessorSearchedServer.getSearchedComponentTypeId(),1070L,List.of("Server"),pageableFirstPage)).thenReturn(listOf1Processor);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithGraphicsCardProvidedProcessorSearchedServer);
//        assertEquals(Set.copyOf(expectedResponse1ProcessorNoNextPage), Set.copyOf(actualResponse));
//    }
//
//    @Test
//    void givenGraphicsCardAndASearchedComponentTypeForProcessorWorkstationConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        when(componentRepository.existsById(requestWithGraphicsCardProvidedProcessorSearchedWorkstation.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithGraphicsCardProvidedProcessorSearchedWorkstation.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithGraphicsCardProvidedProcessorSearchedWorkstation.getComponentIds().get(0))).thenReturn(componentTypeGraphicsCard.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeGraphicsCard.getId(),requestWithGraphicsCardProvidedProcessorSearchedWorkstation.getSearchedComponentTypeId(),typeOfConfigurationWorkstation)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithGraphicsCardProvidedProcessorSearchedWorkstation.getSearchedComponentTypeId(),1070L,List.of("Workstation"),pageableFirstPage)).thenReturn(listOf1Processor);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithGraphicsCardProvidedProcessorSearchedWorkstation);
//        assertEquals(Set.copyOf(expectedResponse1ProcessorNoNextPage), Set.copyOf(actualResponse));
//    }
//
//
//    @Test
//    void givenGraphicsCardAndASearchedComponentTypeForMotherboardPCConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        when(componentRepository.existsById(requestWithGraphicsCardProvidedMotherboardSearchedPC.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithGraphicsCardProvidedMotherboardSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithGraphicsCardProvidedMotherboardSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeGraphicsCard.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeGraphicsCard.getId(),requestWithGraphicsCardProvidedMotherboardSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithGraphicsCardProvidedMotherboardSearchedPC.getSearchedComponentTypeId(),1070L,List.of("PC"),pageableFirstPage)).thenReturn(listOf1Motherboard);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithGraphicsCardProvidedMotherboardSearchedPC);
//        assertEquals(Set.copyOf(expectedResponse1MotherboardNoNextPage), Set.copyOf(actualResponse));
//
//    }
//
//    @Test
//    void givenGraphicsCardAndASearchedComponentTypeForMotherboardServerConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        when(componentRepository.existsById(requestWithGraphicsCardProvidedMotherboardSearchedServer.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithGraphicsCardProvidedMotherboardSearchedServer.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithGraphicsCardProvidedMotherboardSearchedServer.getComponentIds().get(0))).thenReturn(componentTypeGraphicsCard.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeGraphicsCard.getId(),requestWithGraphicsCardProvidedMotherboardSearchedServer.getSearchedComponentTypeId(),typeOfConfigurationServer)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithGraphicsCardProvidedMotherboardSearchedServer.getSearchedComponentTypeId(),1070L,List.of("Server"),pageableFirstPage)).thenReturn(listOf1Motherboard);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithGraphicsCardProvidedMotherboardSearchedServer);
//        assertEquals(Set.copyOf(expectedResponse1MotherboardNoNextPage), Set.copyOf(actualResponse));
//    }
//
//    @Test
//    void givenGraphicsCardAndASearchedComponentTypeForMotherboardWorkstationConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        when(componentRepository.existsById(requestWithGraphicsCardProvidedMotherboardSearchedWorkstation.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithGraphicsCardProvidedMotherboardSearchedWorkstation.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithGraphicsCardProvidedMotherboardSearchedWorkstation.getComponentIds().get(0))).thenReturn(componentTypeGraphicsCard.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeGraphicsCard.getId(),requestWithGraphicsCardProvidedMotherboardSearchedWorkstation.getSearchedComponentTypeId(),typeOfConfigurationWorkstation)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithGraphicsCardProvidedMotherboardSearchedWorkstation.getSearchedComponentTypeId(),1070L,List.of("Workstation"),pageableFirstPage)).thenReturn(listOf1Motherboard);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithGraphicsCardProvidedMotherboardSearchedWorkstation);
//        assertEquals(Set.copyOf(expectedResponse1MotherboardNoNextPage), Set.copyOf(actualResponse));
//    }

    @Test
    void givenRequestWithProcessorAndGraphicsCardProvidedAdnSearchingForRam_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
    {
        when(componentRepository.existsById(requestProcessorAndGraphicsCardProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentRepository.existsById(requestProcessorAndGraphicsCardProvidedRamSearchedPC.getComponentIds().get(1))).thenReturn(true);
        when(componentTypeRepository.existsById(requestProcessorAndGraphicsCardProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestProcessorAndGraphicsCardProvidedRamSearchedPC.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(requestProcessorAndGraphicsCardProvidedRamSearchedPC.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestProcessorAndGraphicsCardProvidedRamSearchedPC.getTypeOfConfiguration(),componentTypeGraphicsCard.getId())).thenReturn(emptyMap);

        when(componentRepository.findComponentTypeIdByComponentId(requestProcessorAndGraphicsCardProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
        when(componentRepository.findComponentTypeIdByComponentId(requestProcessorAndGraphicsCardProvidedRamSearchedPC.getComponentIds().get(1))).thenReturn(componentTypeGraphicsCard.getId());

        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestProcessorAndGraphicsCardProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeGraphicsCard.getId(),requestProcessorAndGraphicsCardProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestProcessorAndGraphicsCardProvidedRamSearchedPC.getTypeOfConfiguration(),requestProcessorAndGraphicsCardProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(RamPC);

        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestProcessorAndGraphicsCardProvidedRamSearchedPC.getSearchedComponentTypeId(),1070L,List.of("PC"),pageableFirstPage)).thenReturn(listOf1Ram);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestProcessorAndGraphicsCardProvidedRamSearchedPC);
        assertEquals(expectedResponse1RamNoNextPage.get(0).getComponentName(), actualResponse.get(0).getComponentName());
    }

    @Test
    void givenRequestWithProcessorAndGraphicsCardAndMotherboardProvidedAdnSearchingForRam_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
    {
        when(componentRepository.existsById(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentRepository.existsById(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getComponentIds().get(1))).thenReturn(true);
        when(componentRepository.existsById(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getComponentIds().get(2))).thenReturn(true);

        when(componentTypeRepository.existsById(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(true);

        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);

        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getTypeOfConfiguration(),componentTypeGraphicsCard.getId())).thenReturn(emptyMap);

        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getTypeOfConfiguration(),componentTypeMotherboard.getId())).thenReturn(MotherboardPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getComponentIds().get(2),firstEntryMotherboardPC.getKey(),firstEntryMotherboardPC.getValue())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
        when(componentRepository.findComponentTypeIdByComponentId(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getComponentIds().get(1))).thenReturn(componentTypeGraphicsCard.getId());
        when(componentRepository.findComponentTypeIdByComponentId(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getComponentIds().get(2))).thenReturn(componentTypeMotherboard.getId());

        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeGraphicsCard.getId(),requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeMotherboard.getId(),requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getTypeOfConfiguration(),requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(RamPC);

        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC.getSearchedComponentTypeId(),1070L,List.of("PC"),pageableFirstPage)).thenReturn(listOf1Ram);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestProcessorAndGraphicsCardAndMotherboardProvidedRamSearchedPC);
        assertEquals(expectedResponse1RamNoNextPage.get(0).getComponentName(), actualResponse.get(0).getComponentName());
    }

    @Test
    void givenOneComponentAndASearchedComponentType_whenGettingCompatibilityWithDuplicateKeysForTheSearchedValuesMap_returnsCompatibleComponentFromSearchedComponentType()
    {
        //Used variables:
        //requestOneComponentProvided
        //componentTypeProcessor (provided)
        //typeOfConfigurationPC
        //specification1and2ForProcessor
        //objectResponseProcessorRam
        //specification1and2ForProcessor2
        //objectResponseProcessorRam2
        //twoRams
        //expectedResponseSearchedRamsForProcessor

        when(componentRepository.existsById(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(processorProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedPC.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(processorProvidedRamSearchedPC.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());

        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam);

        when(componentSpecificationListRepository.findValuesForAComponentSpecificationTypeBySpecificationTypeComponentRelationAndComponentId(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(0),processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(specification1and2ForProcessor);
        when(compatibilityRepository.findSpecification2IdsAndValuesOfSecondSpecification2(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC,allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(0),specification1and2ForProcessor)).thenReturn(objectResponseProcessorRam);

        when(componentSpecificationListRepository.findValuesForAComponentSpecificationTypeBySpecificationTypeComponentRelationAndComponentId(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(1),processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(specification1and2ForProcessor2);
        when(compatibilityRepository.findSpecification2IdsAndValuesOfSecondSpecification2(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC,allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(1),specification1and2ForProcessor2)).thenReturn(objectResponseProcessorRam3);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedPC.getTypeOfConfiguration(),processorProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(RamPC);

        when(componentRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(twoRams);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(processorProvidedRamSearchedPC);
//        assertEquals(Set.copyOf(expectedResponseSearchedRamsForProcessor), Set.copyOf(actualResponse));
        assertEquals(expectedResponseSearchedRamsForProcessor.get(0).getComponentName(), actualResponse.get(0).getComponentName());

    }

    @Test
    void givenOneComponentAndASearchedComponentType_whenGettingCompatibilityAndProvidedComponentDoesNotHaveSpecificationFromRule_throwsAnError()
    {
        //Used variables:
        //requestOneComponentProvided
        //componentTypeProcessor (provided)
        //typeOfConfigurationPC
        //specification1and2ForProcessor

        when(componentRepository.existsById(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(processorProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedPC.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(processorProvidedRamSearchedPC.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());

        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam);

        when(componentSpecificationListRepository.findValuesForAComponentSpecificationTypeBySpecificationTypeComponentRelationAndComponentId(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(0),processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(List.of());

        ObjectNotFound exception = assertThrows(ObjectNotFound.class, () -> {
            compatibilityBetweenComponents.automaticCompatibility(processorProvidedRamSearchedPC);});
        assertEquals("One of the selected components does not respect any of the rules between it and the searched one;", exception.getReason());

    }

    @Test
    void givenOneComponentAndASearchedComponentType_whenGettingCompatibilityAndProvidedComponentDoesNotHaveSpecificationsFromRule_throwsAnError()
    {
        //Used variables:
        //requestOneComponentProvided
        //componentTypeProcessor (provided)
        //typeOfConfigurationPC
        //specification1and2ForProcessor
        //objectResponseProcessorRam
        //specification1and2ForProcessor2
        //objectResponseProcessorRam2
        //twoRams
        //expectedResponseSearchedRamsForProcessor

        when(componentRepository.existsById(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(processorProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedPC.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(processorProvidedRamSearchedPC.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());

        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam);

        when(componentSpecificationListRepository.findValuesForAComponentSpecificationTypeBySpecificationTypeComponentRelationAndComponentId(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(0),processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(specification1and2ForProcessor);
        when(compatibilityRepository.findSpecification2IdsAndValuesOfSecondSpecification2(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC,allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(0),specification1and2ForProcessor)).thenReturn(List.of());
        //when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedPC.getTypeOfConfiguration(),processorProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(RamPC);


        ObjectNotFound exception = assertThrows(ObjectNotFound.class, () -> {
            compatibilityBetweenComponents.automaticCompatibility(processorProvidedRamSearchedPC);});
        assertEquals("Compatible components from searched component type were not found", exception.getReason());

    }


    @Test
    void givenOneComponentAndASearchedComponentType_whenGettingCompatibilityAndThereAreNoCompatibleComponentsAtAll_returnsCompatibleComponentFromSearchedComponentType()
    {
        //Used variables:
        //requestOneComponentProvided
        //componentTypeProcessor (provided)
        //typeOfConfigurationPC
        //specification1and2ForProcessor
        //objectResponseProcessorRam
        //specification1and2ForProcessor2
        //objectResponseProcessorRam2
        //twoRams
        //expectedResponseSearchedRamsForProcessor

        when(componentRepository.existsById(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(processorProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedPC.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(processorProvidedRamSearchedPC.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());

        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam);

        when(componentSpecificationListRepository.findValuesForAComponentSpecificationTypeBySpecificationTypeComponentRelationAndComponentId(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(0),processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(specification1and2ForProcessor);
        when(compatibilityRepository.findSpecification2IdsAndValuesOfSecondSpecification2(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC,allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(0),specification1and2ForProcessor)).thenReturn(objectResponseProcessorRam);

        when(componentSpecificationListRepository.findValuesForAComponentSpecificationTypeBySpecificationTypeComponentRelationAndComponentId(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(1),processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(specification1and2ForProcessor2);
        when(compatibilityRepository.findSpecification2IdsAndValuesOfSecondSpecification2(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC,allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(1),specification1and2ForProcessor2)).thenReturn(objectResponseProcessorRam2);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedPC.getTypeOfConfiguration(),processorProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(RamPC);

        when(componentRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(new PageImpl<>(List.of(),pageableFirstPage, 2));

        ObjectNotFound exception = assertThrows(ObjectNotFound.class, () -> {
            compatibilityBetweenComponents.automaticCompatibility(processorProvidedRamSearchedPC);});
        assertEquals("Compatible components from searched component type were not found", exception.getReason());
    }

    @Test
    void givenOneComponentAndASearchedComponentType_whenGettingCompatibilityAndThereIsNextPage_returnsCompatibleComponentFromSearchedComponentType()
    {
        //Used variables:
        //requestOneComponentProvided
        //componentTypeProcessor (provided)
        //typeOfConfigurationPC
        //specification1and2ForProcessor
        //objectResponseProcessorRam
        //specification1and2ForProcessor2
        //objectResponseProcessorRam2
        //twoRams
        //expectedResponseSearchedRamsForProcessor

        when(componentRepository.existsById(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(true);
        when(componentTypeRepository.existsById(processorProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedPC.getTypeOfConfiguration(),componentTypeProcessor.getId())).thenReturn(ProcessorPC);
        when(componentSpecificationListRepository.existsByComponentIdAndSpecificationTypeIdAndValueIn(processorProvidedRamSearchedPC.getComponentIds().get(0),firstEntryProcessorPC.getKey(),firstEntryProcessorPC.getValue())).thenReturn(true);

        when(componentRepository.findComponentTypeIdByComponentId(processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());

        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam);

        when(componentSpecificationListRepository.findValuesForAComponentSpecificationTypeBySpecificationTypeComponentRelationAndComponentId(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(0),processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(specification1and2ForProcessor);
        when(compatibilityRepository.findSpecification2IdsAndValuesOfSecondSpecification2(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC,allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(0),specification1and2ForProcessor)).thenReturn(objectResponseProcessorRam);

        when(componentSpecificationListRepository.findValuesForAComponentSpecificationTypeBySpecificationTypeComponentRelationAndComponentId(allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(1),processorProvidedRamSearchedPC.getComponentIds().get(0))).thenReturn(specification1and2ForProcessor2);
        when(compatibilityRepository.findSpecification2IdsAndValuesOfSecondSpecification2(componentTypeProcessor.getId(),processorProvidedRamSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC,allDistinctSpecificationsThatShouldBeConsideredBetweenProcessorAndRam.get(1),specification1and2ForProcessor2)).thenReturn(objectResponseProcessorRam2);

        when(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(processorProvidedRamSearchedPC.getTypeOfConfiguration(),processorProvidedRamSearchedPC.getSearchedComponentTypeId())).thenReturn(RamPC);

        when(componentRepository.findAll(any(Specification.class),any(Pageable.class))).thenReturn(tenRams);

        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(processorProvidedRamSearchedPC);
        assertEquals(expectedResponseSearchedRamsForProcessorWithoutNextPage.get(0).getComponentName(), actualResponse.get(0).getComponentName());

    }


//
//
//    @Test
//    void givenProcessorAndASearchedComponentTypeForHardDrivePCConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        //Used variables:
//        //processorProvidedRamSearchedServer
//        //componentTypeProcessor (provided)
//        //typeOfConfigurationServer
//        //listOf1Ram
//        //expectedResponse1RamNoNextPage
//
//        when(componentRepository.existsById(requestWithProcessorProvidedHardDriveSearchedPC.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithProcessorProvidedHardDriveSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedHardDriveSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedHardDriveSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedHardDriveSearchedPC.getSearchedComponentTypeId(),1070L,List.of("PC"),pageableFirstPage)).thenReturn(listOf1HardDrive);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedHardDriveSearchedPC);
//        assertEquals(Set.copyOf(expectedResponse1HardDriveNoNextPage), Set.copyOf(actualResponse));
//    }
//
//    @Test
//    void givenProcessorAndASearchedComponentTypeForHardDriveWorkstationConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        //Used variables:
//        //processorProvidedRamSearchedServer
//        //componentTypeProcessor (provided)
//        //typeOfConfigurationServer
//        //listOf1Ram
//        //expectedResponse1RamNoNextPage
//
//        when(componentRepository.existsById(requestWithProcessorProvidedHardDriveSearchedWorkstation.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithProcessorProvidedHardDriveSearchedWorkstation.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedHardDriveSearchedWorkstation.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedHardDriveSearchedWorkstation.getSearchedComponentTypeId(),typeOfConfigurationWorkstation)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedHardDriveSearchedWorkstation.getSearchedComponentTypeId(),1070L,List.of("Workstation","workstation"),pageableFirstPage)).thenReturn(listOf1HardDrive);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedHardDriveSearchedWorkstation);
//        assertEquals(Set.copyOf(expectedResponse1HardDriveNoNextPage), Set.copyOf(actualResponse));
//    }
//
//    @Test
//    void givenProcessorAndASearchedComponentTypeForHardDriveServerConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        //Used variables:
//        //processorProvidedRamSearchedServer
//        //componentTypeProcessor (provided)
//        //typeOfConfigurationServer
//        //listOf1Ram
//        //expectedResponse1RamNoNextPage
//
//        when(componentRepository.existsById(requestWithProcessorProvidedHardDriveSearchedServer.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithProcessorProvidedHardDriveSearchedServer.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedHardDriveSearchedServer.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedHardDriveSearchedServer.getSearchedComponentTypeId(),typeOfConfigurationServer)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedHardDriveSearchedServer.getSearchedComponentTypeId(),1070L,List.of("Server"),pageableFirstPage)).thenReturn(listOf1HardDrive);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedHardDriveSearchedServer);
//        assertEquals(Set.copyOf(expectedResponse1HardDriveNoNextPage), Set.copyOf(actualResponse));
//    }
//
//
//    @Test
//    void givenProcessorAndASearchedComponentTypeForCoolingPCConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        //Used variables:
//        //processorProvidedRamSearchedServer
//        //componentTypeProcessor (provided)
//        //typeOfConfigurationServer
//        //listOf1Ram
//        //expectedResponse1RamNoNextPage
//
//        when(componentRepository.existsById(requestWithProcessorProvidedCoolingSearchedPC.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithProcessorProvidedCoolingSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedCoolingSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedCoolingSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedCoolingSearchedPC.getSearchedComponentTypeId(),954L,List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler"),pageableFirstPage)).thenReturn(listOf1Cooling);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedCoolingSearchedPC);
//        assertEquals(Set.copyOf(expectedResponse1CoolingNoNextPage), Set.copyOf(actualResponse));
//    }
//
//    @Test
//    void givenProcessorAndASearchedComponentTypeForCoolingLaptopConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        //Used variables:
//        //processorProvidedRamSearchedServer
//        //componentTypeProcessor (provided)
//        //typeOfConfigurationServer
//        //listOf1Ram
//        //expectedResponse1RamNoNextPage
//
//        when(componentRepository.existsById(requestWithProcessorProvidedCoolingSearchedLaptop.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithProcessorProvidedCoolingSearchedLaptop.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedCoolingSearchedLaptop.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedCoolingSearchedLaptop.getSearchedComponentTypeId(),typeOfConfigurationLaptop)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedCoolingSearchedLaptop.getSearchedComponentTypeId(),954L,List.of("Thermal paste"),pageableFirstPage)).thenReturn(listOf1Cooling);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedCoolingSearchedLaptop);
//        assertEquals(Set.copyOf(expectedResponse1CoolingNoNextPage), Set.copyOf(actualResponse));
//    }
//
//    @Test
//    void givenProcessorAndASearchedComponentTypeForCoolingServerConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        //Used variables:
//        //processorProvidedRamSearchedServer
//        //componentTypeProcessor (provided)
//        //typeOfConfigurationServer
//        //listOf1Ram
//        //expectedResponse1RamNoNextPage
//
//        when(componentRepository.existsById(requestWithProcessorProvidedCoolingSearchedServer.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithProcessorProvidedCoolingSearchedServer.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedCoolingSearchedServer.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedCoolingSearchedServer.getSearchedComponentTypeId(),typeOfConfigurationServer)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedCoolingSearchedServer.getSearchedComponentTypeId(),954L,List.of("Fan","Fan tray","Cooler"),pageableFirstPage)).thenReturn(listOf1Cooling);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedCoolingSearchedServer);
//        assertEquals(Set.copyOf(expectedResponse1CoolingNoNextPage), Set.copyOf(actualResponse));
//    }
//
//
//
//    @Test
//    void givenProcessorAndASearchedComponentTypeForProcessorCoolingPCConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        //Used variables:
//        //processorProvidedRamSearchedServer
//        //componentTypeProcessor (provided)
//        //typeOfConfigurationServer
//        //listOf1Ram
//        //expectedResponse1RamNoNextPage
//
//        when(componentRepository.existsById(requestWithProcessorProvidedProcessorCoolingSearchedPC.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithProcessorProvidedProcessorCoolingSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedProcessorCoolingSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedProcessorCoolingSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedProcessorCoolingSearchedPC.getSearchedComponentTypeId(),954L,List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler","Cooler"),pageableFirstPage)).thenReturn(listOf1ProcessorCooling);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedProcessorCoolingSearchedPC);
//        assertEquals(Set.copyOf(expectedResponse1ProcessorCoolingNoNextPage), Set.copyOf(actualResponse));
//    }
//
//    @Test
//    void givenProcessorAndASearchedComponentTypeForProcessorCoolingLaptopConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        //Used variables:
//        //processorProvidedRamSearchedServer
//        //componentTypeProcessor (provided)
//        //typeOfConfigurationServer
//        //listOf1Ram
//        //expectedResponse1RamNoNextPage
//
//        when(componentRepository.existsById(requestWithProcessorProvidedProcessorCoolingSearchedLaptop.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithProcessorProvidedProcessorCoolingSearchedLaptop.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedProcessorCoolingSearchedLaptop.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedProcessorCoolingSearchedLaptop.getSearchedComponentTypeId(),typeOfConfigurationLaptop)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedProcessorCoolingSearchedLaptop.getSearchedComponentTypeId(),954L,List.of("Thermal paste"),pageableFirstPage)).thenReturn(listOf1ProcessorCooling);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedProcessorCoolingSearchedLaptop);
//        assertEquals(Set.copyOf(expectedResponse1ProcessorCoolingNoNextPage), Set.copyOf(actualResponse));
//    }
//
//    @Test
//    void givenProcessorAndASearchedComponentTypeForProcessorCoolingServerConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        //Used variables:
//        //processorProvidedRamSearchedServer
//        //componentTypeProcessor (provided)
//        //typeOfConfigurationServer
//        //listOf1Ram
//        //expectedResponse1RamNoNextPage
//
//        when(componentRepository.existsById(requestWithProcessorProvidedProcessorCoolingSearchedServer.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithProcessorProvidedProcessorCoolingSearchedServer.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedProcessorCoolingSearchedServer.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedProcessorCoolingSearchedServer.getSearchedComponentTypeId(),typeOfConfigurationServer)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedProcessorCoolingSearchedServer.getSearchedComponentTypeId(),954L,List.of("Fan","Fan module"),pageableFirstPage)).thenReturn(listOf1ProcessorCooling);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedProcessorCoolingSearchedServer);
//        assertEquals(Set.copyOf(expectedResponse1ProcessorCoolingNoNextPage), Set.copyOf(actualResponse));
//    }
//
//
//
//    @Test
//    void givenProcessorAndASearchedComponentTypeForComputerCasePCConfiguration_whenThereAreNoRulesForTheirCompatibility_returnsFirstTenComponentsFromSearchedComponentType()
//    {
//        //Used variables:
//        //processorProvidedRamSearchedServer
//        //componentTypeProcessor (provided)
//        //typeOfConfigurationServer
//        //listOf1Ram
//        //expectedResponse1RamNoNextPage
//
//        when(componentRepository.existsById(requestWithProcessorProvidedComputerCaseSearchedPC.getComponentIds().get(0))).thenReturn(true);
//        when(componentTypeRepository.existsById(requestWithProcessorProvidedComputerCaseSearchedPC.getSearchedComponentTypeId())).thenReturn(true);
//        when(componentRepository.findComponentTypeIdByComponentId(requestWithProcessorProvidedComputerCaseSearchedPC.getComponentIds().get(0))).thenReturn(componentTypeProcessor.getId());
//        when(compatibilityRepository.findDistinctSpecification1IdsForCoupleOfComponentTypesAndTypeOfConfiguration(componentTypeProcessor.getId(),requestWithProcessorProvidedComputerCaseSearchedPC.getSearchedComponentTypeId(),typeOfConfigurationPC)).thenReturn(List.of());
//        when(componentRepository.findComponentsByGivenComponentTypeAndSpecificationForMeantFor(requestWithProcessorProvidedComputerCaseSearchedPC.getSearchedComponentTypeId(),954L,List.of("PC"),pageableFirstPage)).thenReturn(listOf1ComputerCase);
//
//        List<GetAutomaticCompatibilityResponse> actualResponse = compatibilityBetweenComponents.automaticCompatibility(requestWithProcessorProvidedComputerCaseSearchedPC);
//        assertEquals(Set.copyOf(expectedResponse1ComputerCaseNoNextPage), Set.copyOf(actualResponse));
//    }



}