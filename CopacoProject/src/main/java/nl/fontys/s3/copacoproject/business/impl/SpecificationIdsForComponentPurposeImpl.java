package nl.fontys.s3.copacoproject.business.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.SpecificationIdsForComponentPurpose;
import nl.fontys.s3.copacoproject.business.exception.ObjectNotFound;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpecificationIdsForComponentPurposeImpl implements SpecificationIdsForComponentPurpose {
    private static final String LAPTOP = "Laptop";
    private static final String SERVER = "Server";
    private static final String PC = "PC";
    private static final String WORKSTATION = "Workstation";
    private static final String NOTEBOOK = "Notebook";
    private static final String CITY_BIKE = "CITY BIKE";
    private static final String DOWNHILL = "DOWNHILL";

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    private Long getDynamicSpecificationId(Long devId, Long stagingId) {
        if (databaseUrl.contains("dbi527531_testdb")) {
            return devId;
        } else if (databaseUrl.contains("dbi527531_stagingcop")) {
            return stagingId;
        } else {
            throw new IllegalStateException("Unknown database in use: " + databaseUrl);
        }
    }

    @Override
    public Map<Long, List<String>> getSpecificationIdAndValuesForComponentPurpose(String configurationType, Long componentTypeId) {
        //Component voor - 1070 - Hardware
        //Bedoel voor - 947 - Hardware
        //Soort - 954 - Hardware
        //Bike type - 1792 - Hardware
        Map<Long, List<String>> serverConfig = new HashMap<>();
        if(componentTypeId == 1)
        {
            switch(configurationType)
            {
                case SERVER:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(SERVER));
                    break;
                case PC:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(WORKSTATION));
                    break;
                case WORKSTATION:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(WORKSTATION));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            return serverConfig;
        }
        else if(componentTypeId == 2)
        {
            switch(configurationType)
            {
                case SERVER:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(SERVER));
                    break;
                case PC:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(PC));
                    break;
                case WORKSTATION:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(WORKSTATION));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            return serverConfig;
        }
        else if(componentTypeId == 3)
        {
            switch(configurationType)
            {
                case SERVER:
                    return serverConfig;
                case PC:
                    return serverConfig;
                case WORKSTATION:
                    return serverConfig;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            //return serverConfig;
        }
        else if(componentTypeId == 4)
        {
            switch(configurationType)
            {
                case SERVER:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(SERVER));
                    break;
                case PC:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(PC));
                    break;
                case WORKSTATION:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(WORKSTATION));
                    break;
                case LAPTOP:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(NOTEBOOK));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            return serverConfig;
        }
        else if(componentTypeId == 5)
        {
            switch(configurationType)
            {
                case SERVER:
                    serverConfig.put(getDynamicSpecificationId(947L,29L), List.of(SERVER,"server"));
                    break;
                case PC:
                    serverConfig.put(getDynamicSpecificationId(947L,29L), List.of(PC));
                    break;
                case WORKSTATION:
                    serverConfig.put(getDynamicSpecificationId(947L,29L), List.of(PC));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            return serverConfig;
        }
        else if(componentTypeId == 6)
        {
            switch(configurationType)
            {
                case PC:
                    serverConfig.put(getDynamicSpecificationId(954L,36L), List.of(PC));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            return serverConfig;
        }
        else if(componentTypeId == 7)
        {
            switch(configurationType)
            {
                case SERVER:
                    serverConfig.put(getDynamicSpecificationId(954L,36L), List.of("Fan","Fan module"));
                    break;
                case PC:
                    serverConfig.put(getDynamicSpecificationId(954L,36L), List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler","Cooler"));
                    break;
                case LAPTOP:
                    serverConfig.put(getDynamicSpecificationId(954L,36L), List.of("Thermal paste"));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            return serverConfig;
        }
        else if(componentTypeId == 8)
        {
            switch(configurationType)
            {
                case SERVER:
                    serverConfig.put(getDynamicSpecificationId(954L,36L), List.of("Fan","Fan tray","Cooler"));
                    break;
                case PC:
                    serverConfig.put(getDynamicSpecificationId(954L,36L), List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler"));
                    break;
                case LAPTOP:
                    serverConfig.put(getDynamicSpecificationId(954L,36L), List.of("Thermal paste"));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            return serverConfig;
        }
        else if(componentTypeId == 9)
        {
            switch(configurationType)
            {
                case WORKSTATION:
                    return serverConfig;
                case PC:
                    return serverConfig;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
        }
        else if(componentTypeId == 10)
        {
            switch(configurationType)
            {
                case SERVER:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(SERVER));
                    break;
                case WORKSTATION:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(WORKSTATION));
                    break;
                case PC:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(PC));
                    break;
                case LAPTOP:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(NOTEBOOK));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            return serverConfig;
        }
        else if(componentTypeId == 11)
        {
            switch(configurationType)
            {
                case SERVER:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(SERVER));
                    break;
                case WORKSTATION:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(WORKSTATION,"workstation"));
                    break;
                case PC:
                    serverConfig.put(getDynamicSpecificationId(1070L,152L), List.of(PC));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            return serverConfig;
        }
        if(componentTypeId == 12)
        {
            switch(configurationType)
            {
                case CITY_BIKE:
                    serverConfig.put(1792L, List.of(CITY_BIKE));
                    break;
                case DOWNHILL:
                    serverConfig.put(1792L, List.of(DOWNHILL));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            return serverConfig;
        }
        if(componentTypeId == 13)
        {
            switch(configurationType)
            {
                case CITY_BIKE:
                    serverConfig.put(1792L, List.of(CITY_BIKE));
                    break;
                case DOWNHILL:
                    serverConfig.put(1792L, List.of(DOWNHILL));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");

            }
            return serverConfig;
        }
        else {
            return serverConfig;
        }
    }

    @Override
    public Long getTheSpecificationIdWhereTheDifferentConfigurationTypesCanBeFoundForCategory(Long categoryId) {
        if(categoryId == 1)
        {
            return getDynamicSpecificationId(1070L,152L);
        }
        else if(categoryId == 2)
        {
            return 1792L;
        }
        return getDynamicSpecificationId(1070L,152L);
    }

    @Override
    public List<Long> getAllDistinctSpecificationIdsThatHoldConfigurationType() {
        if (databaseUrl.contains("dbi527531_testdb")) {
            return List.of(947L, 954L, 1070L,1792L);

        } else if (databaseUrl.contains("dbi527531_stagingcop")) {
            return List.of(29L, 36L, 152L,1792L);
        }
        return List.of(947L, 954L, 1070L,1792L);
    }

}
