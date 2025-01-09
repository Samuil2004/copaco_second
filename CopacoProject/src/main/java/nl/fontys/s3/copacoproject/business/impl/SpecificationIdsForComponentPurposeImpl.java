package nl.fontys.s3.copacoproject.business.impl;

import lombok.AllArgsConstructor;
import nl.fontys.s3.copacoproject.business.SpecificationIdsForComponentPurpose;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
            }
            return serverConfig;
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
            }
            return serverConfig;
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
            }
            return serverConfig;
        }
        else {
            return null;
        }
    }

    @Override
    public List<String> getConfigurationTypesForSpecificationValueAndComponentType(String specificationValue, Long componentTypeId) {
        List<String> configurationTypes = new ArrayList<>();

        if (componentTypeId == 1) {
            if (List.of("Server").contains(specificationValue)) {
                configurationTypes.add("Server");
            } else if (List.of("Workstation").contains(specificationValue)) {
                configurationTypes.add("PC");
                configurationTypes.add("Workstation");
            }
        } else if (componentTypeId == 2) {
            if (List.of("Server").contains(specificationValue)) {
                configurationTypes.add("Server");
            } else if (List.of("PC").contains(specificationValue)) {
                configurationTypes.add("PC");
            } else if (List.of("Workstation").contains(specificationValue)) {
                configurationTypes.add("Workstation");
            }
        } else if (componentTypeId == 4) {
            if (List.of("Server").contains(specificationValue)) {
                configurationTypes.add("Server");
            } else if (List.of("PC").contains(specificationValue)) {
                configurationTypes.add("PC");
            } else if (List.of("Workstation").contains(specificationValue)) {
                configurationTypes.add("Workstation");
            } else if (List.of("Notebook").contains(specificationValue)) {
                configurationTypes.add("Laptop");
            }
        } else if (componentTypeId == 5) {
            if (List.of("Server", "server").contains(specificationValue)) {
                configurationTypes.add("Server");
            } else if (List.of("PC").contains(specificationValue)) {
                configurationTypes.add("PC");
                configurationTypes.add("Workstation");
            }
        } else if (componentTypeId == 6) {
            if (List.of("PC").contains(specificationValue)) {
                configurationTypes.add("PC");
            }
        } else if (componentTypeId == 7) {
            if (List.of("Fan", "Fan module").contains(specificationValue)) {
                configurationTypes.add("Server");
            } else if (List.of("Liquid cooling kit", "Heatsink", "Radiatior", "Air cooler", "Radiator block", "Cooler", "All-in-one liquid cooler", "Cooler").contains(specificationValue)) {
                configurationTypes.add("PC");
            } else if (List.of("Thermal paste").contains(specificationValue)) {
                configurationTypes.add("Laptop");
            }
        } else if (componentTypeId == 8) {
            if (List.of("Fan", "Fan tray", "Cooler").contains(specificationValue)) {
                configurationTypes.add("Server");
            } else if (List.of("Liquid cooling kit", "Heatsink", "Radiatior", "Air cooler", "Radiator block", "Cooler", "All-in-one liquid cooler").contains(specificationValue)) {
                configurationTypes.add("PC");
            } else if (List.of("Thermal paste").contains(specificationValue)) {
                configurationTypes.add("Laptop");
            }
        } else if (componentTypeId == 10) {
            if (List.of("Server").contains(specificationValue)) {
                configurationTypes.add("Server");
            } else if (List.of("Workstation").contains(specificationValue)) {
                configurationTypes.add("Workstation");
            } else if (List.of("PC").contains(specificationValue)) {
                configurationTypes.add("PC");
            } else if (List.of("Notebook").contains(specificationValue)) {
                configurationTypes.add("Laptop");
            }
        } else if (componentTypeId == 11) {
            if (List.of("Server").contains(specificationValue)) {
                configurationTypes.add("Server");
            } else if (List.of("Workstation", "workstation").contains(specificationValue)) {
                configurationTypes.add("Workstation");
            } else if (List.of("PC").contains(specificationValue)) {
                configurationTypes.add("PC");
            }
        } else if (componentTypeId == 12 || componentTypeId == 13) {
            if (List.of("CITY BIKE").contains(specificationValue)) {
                configurationTypes.add("CITY BIKE");
            } else if (List.of("DOWNHILL").contains(specificationValue)) {
                configurationTypes.add("DOWNHILL");
            }
        }

        return configurationTypes.isEmpty() ? null : configurationTypes;
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
