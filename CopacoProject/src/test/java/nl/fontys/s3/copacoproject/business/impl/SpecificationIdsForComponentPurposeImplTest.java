package nl.fontys.s3.copacoproject.business.impl;

import nl.fontys.s3.copacoproject.business.exception.ObjectNotFound;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)

class SpecificationIdsForComponentPurposeImplTest {
    @InjectMocks
    SpecificationIdsForComponentPurposeImpl specificationIdsForComponentPurpose;

    private Long ID_Component_Voor=1070L;
    private Long ID_Bedoel_Voor=947L;
    private Long ID_Soort=954L;
    private Long ID_Bike_Type=1792L;
    private Long ID_Car_Type=1797L;

    private Long ID_THERMAL_DESIGN_POWER=1120L;
    private Long ID_Minimum_system_power_voorraad=937L;
    private Long ID_Stroomverbruik_lezeN=1144L;
    private Long ID_Stroomverbruik_schrijven=1145L;
    private Long ID_Stroomverbruik_typisch=922L;
    private Long ID_Totaal_vermogen=1036L;
    private Long ID_Gecombineerd_vermogen=1293L;

    private void assertObjectNotFound(String configurationType, Long componentTypeId) {
        ObjectNotFound exception = assertThrows(ObjectNotFound.class, () ->
                specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose(configurationType, componentTypeId)
        );
        assertEquals("One of the selected component type does not have any components for this type of configuration; ", exception.getReason());
    }

    @Test
    void testComponentType1() {

        assertEquals(Map.of(1070L, List.of("Server")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Server", 1L));
        assertEquals(Map.of(1070L, List.of("Workstation")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("PC", 1L));
        assertEquals(Map.of(1070L, List.of("Workstation")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Workstation", 1L));
        assertObjectNotFound("InvalidType", 1L);
    }

    @Test
    void testComponentType2() {
        assertEquals(Map.of(1070L, List.of("Server")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Server", 2L));
        assertEquals(Map.of(1070L, List.of("PC")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("PC", 2L));
        assertEquals(Map.of(1070L, List.of("Workstation")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Workstation", 2L));
        assertObjectNotFound("InvalidType", 2L);
    }

    @Test
    void testComponentType3() {
        assertTrue(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Server", 3L).isEmpty());
        assertTrue(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("PC", 3L).isEmpty());
        assertTrue(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Workstation", 3L).isEmpty());
        assertObjectNotFound("InvalidType", 3L);
    }

    @Test
    void testComponentType4() {
        assertEquals(Map.of(1070L, List.of("Server")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Server", 4L));
        assertEquals(Map.of(1070L, List.of("PC")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("PC", 4L));
        assertEquals(Map.of(1070L, List.of("Workstation")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Workstation", 4L));
        assertEquals(Map.of(1070L, List.of("Notebook")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Laptop", 4L));
        assertObjectNotFound("InvalidType", 4L);
    }

    @Test
    void testComponentType5() {
        assertEquals(Map.of(947L, List.of("Server", "server")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Server", 5L));
        assertEquals(Map.of(947L, List.of("PC")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("PC", 5L));
        assertEquals(Map.of(947L, List.of("PC")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Workstation", 5L));
        assertObjectNotFound("InvalidType", 5L);
    }

    @Test
    void testComponentType6() {
        assertEquals(Map.of(954L, List.of("PC")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("PC", 6L));
        assertObjectNotFound("Server", 6L);
        assertObjectNotFound("Laptop", 6L);
    }

    @Test
    void testComponentType7() {
        assertEquals(Map.of(954L, List.of("Fan", "Fan module")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Server", 7L));
        assertEquals(Map.of(954L, List.of("Liquid cooling kit", "Heatsink", "Radiatior", "Air cooler", "Radiator block", "Cooler", "All-in-one liquid cooler", "Cooler")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("PC", 7L));
        assertEquals(Map.of(954L, List.of("Thermal paste")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Laptop", 7L));
        assertObjectNotFound("InvalidType", 7L);
    }

    @Test
    void testComponentType8() {
        assertEquals(Map.of(954L, List.of("Fan", "Fan tray", "Cooler")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Server", 8L));
        assertEquals(Map.of(954L, List.of("Liquid cooling kit", "Heatsink", "Radiatior", "Air cooler", "Radiator block", "Cooler", "All-in-one liquid cooler","PC")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("PC", 8L));
        assertEquals(Map.of(954L, List.of("Thermal paste")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Laptop", 8L));
        assertObjectNotFound("InvalidType", 8L);
    }

    @Test
    void testComponentType9() {
        assertTrue(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Workstation", 9L).isEmpty());
        assertTrue(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("PC", 9L).isEmpty());
        assertObjectNotFound("InvalidType", 9L);
    }

    @Test
    void testComponentType10() {
        assertEquals(Map.of(1070L, List.of("Server")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Server", 10L));
        assertEquals(Map.of(1070L, List.of("Workstation")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Workstation", 10L));
        assertEquals(Map.of(1070L, List.of("PC")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("PC", 10L));
        assertEquals(Map.of(1070L, List.of("Notebook")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Laptop", 10L));
        assertObjectNotFound("InvalidType", 10L);
    }

    @Test
    void testComponentType11() {
        assertEquals(Map.of(1070L, List.of("Server")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Server", 11L));
        assertEquals(Map.of(1070L, List.of("Workstation", "workstation")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Workstation", 11L));
        assertEquals(Map.of(1070L, List.of("PC")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("PC", 11L));
        assertObjectNotFound("InvalidType", 11L);
    }

    @Test
    void testComponentType12() {
        assertEquals(Map.of(1792L, List.of("CITY BIKE")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("CITY BIKE", 12L));
        assertEquals(Map.of(1792L, List.of("DOWNHILL")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("DOWNHILL", 12L));
        assertObjectNotFound("InvalidType", 12L);
    }

    @Test
    void testComponentType13() {
        assertEquals(Map.of(1792L, List.of("CITY BIKE")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("CITY BIKE", 13L));
        assertEquals(Map.of(1792L, List.of("DOWNHILL")), specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("DOWNHILL", 13L));
        assertObjectNotFound("InvalidType", 13L);
    }

    @Test
    void testNonExistingComponentType() {
        assertTrue(specificationIdsForComponentPurpose.getSpecificationIdAndValuesForComponentPurpose("Workstation", 20L).isEmpty());
    }

    @Test
    void testGetTheSpecificationIdWhereTheDifferentConfigurationTypesCanBeFoundForCategory() {
        assertEquals(1070L, specificationIdsForComponentPurpose.getTheSpecificationIdWhereTheDifferentConfigurationTypesCanBeFoundForCategory(1L));
        assertEquals(1792L, specificationIdsForComponentPurpose.getTheSpecificationIdWhereTheDifferentConfigurationTypesCanBeFoundForCategory(2L));
        assertEquals(1797L, specificationIdsForComponentPurpose.getTheSpecificationIdWhereTheDifferentConfigurationTypesCanBeFoundForCategory(3L));
    }

    @Test
    void testGetAllDistinctSpecificationIdsThatHoldConfigurationType() {
        List<Long> expectedSpecificationIds = List.of(947L, 954L, 1070L, 1792L,1797L);
        assertEquals(expectedSpecificationIds, specificationIdsForComponentPurpose.getAllDistinctSpecificationIdsThatHoldConfigurationType());

    }
}