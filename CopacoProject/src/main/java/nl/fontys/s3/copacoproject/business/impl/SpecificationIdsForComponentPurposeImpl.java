package nl.fontys.s3.copacoproject.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.SpecificationIdsForComponentPurpose;
import nl.fontys.s3.copacoproject.business.exception.ObjectNotFound;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class SpecificationIdsForComponentPurposeImpl implements SpecificationIdsForComponentPurpose {
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
                case "Server":
                    serverConfig.put(1070L, List.of("Server"));
                    break;
                case "PC":
                    serverConfig.put(1070L, List.of("Workstation"));
                    break;
                case "Workstation":
                    serverConfig.put(1070L, List.of("Workstation"));
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
                case "Server":
                    serverConfig.put(1070L, List.of("Server"));
                    break;
                case "PC":
                    serverConfig.put(1070L, List.of("PC"));
                    break;
                case "Workstation":
                    serverConfig.put(1070L, List.of("Workstation"));
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
                case "Server":
                    return serverConfig;
                case "PC":
                    return serverConfig;
                case "Workstation":
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
                case "Server":
                    serverConfig.put(1070L, List.of("Server"));
                    break;
                case "PC":
                    serverConfig.put(1070L, List.of("PC"));
                    break;
                case "Workstation":
                    serverConfig.put(1070L, List.of("Workstation"));
                    break;
                case "Laptop":
                    serverConfig.put(1070L, List.of("Notebook"));
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
                case "Server":
                    serverConfig.put(947L, List.of("Server","server"));
                    break;
                case "PC":
                    serverConfig.put(947L, List.of("PC"));
                    break;
                case "Workstation":
                    serverConfig.put(947L, List.of("PC"));
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
                case "PC":
                    serverConfig.put(954L, List.of("PC"));
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
                case "Server":
                    serverConfig.put(954L, List.of("Fan","Fan module"));
                    break;
                case "PC":
                    serverConfig.put(954L, List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler","Cooler"));
                    break;
                case "Laptop":
                    serverConfig.put(954L, List.of("Thermal paste"));
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
                case "Server":
                    serverConfig.put(954L, List.of("Fan","Fan tray","Cooler"));
                    break;
                case "PC":
                    serverConfig.put(954L, List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler"));
                    break;
                case "Laptop":
                    serverConfig.put(954L, List.of("Thermal paste"));
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
                case "Workstation":
                    return serverConfig;
                case "PC":
                    return serverConfig;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
        }
        else if(componentTypeId == 10)
        {
            switch(configurationType)
            {
                case "Server":
                    serverConfig.put(1070L, List.of("Server"));
                    break;
                case "Workstation":
                    serverConfig.put(1070L, List.of("Workstation"));
                    break;
                case "PC":
                    serverConfig.put(1070L, List.of("PC"));
                    break;
                case "Laptop":
                    serverConfig.put(1070L, List.of("Notebook"));
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
                case "Server":
                    serverConfig.put(1070L, List.of("Server"));
                    break;
                case "Workstation":
                    serverConfig.put(1070L, List.of("Workstation","workstation"));
                    break;
                case "PC":
                    serverConfig.put(1070L, List.of("PC"));
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
                case "CITY BIKE":
                    serverConfig.put(1792L, List.of("CITY BIKE"));
                    break;
                case "DOWNHILL":
                    serverConfig.put(1792L, List.of("DOWNHILL"));
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
                case "CITY BIKE":
                    serverConfig.put(1792L, List.of("CITY BIKE"));
                    break;
                case "DOWNHILL":
                    serverConfig.put(1792L, List.of("DOWNHILL"));
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
            return 1070L;
        }
        else if(categoryId == 2)
        {
            return 1792L;
        }
        return 1070L;
    }

    @Override
    public List<Long> getAllDistinctSpecificationIdsThatHoldConfigurationType() {
        return List.of(947L, 954L, 1070L,1792L);
    }

}
