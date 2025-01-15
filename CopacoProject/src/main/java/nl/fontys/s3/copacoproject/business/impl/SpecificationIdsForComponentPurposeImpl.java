package nl.fontys.s3.copacoproject.business.impl;

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
    private static final String SUV = "SUV";
    private static final String Sedan = "Sedan";

    @Value("${ID_Component_Voor}")
    private Long ID_Component_Voor;

    @Value("${ID_Bedoel_Voor}")
    private Long ID_Bedoel_Voor;

    @Value("${ID_Soort}")
    private Long ID_Soort;

    @Value("${ID_Bike_Type}")
    private Long ID_Bike_Type;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${ID_Car_Type}")
    private Long ID_Car_Type;

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
                    serverConfig.put(ID_Component_Voor, List.of(SERVER));
                    break;
                case PC:
                    serverConfig.put(ID_Component_Voor, List.of(WORKSTATION));
                    break;
                case WORKSTATION:
                    serverConfig.put(ID_Component_Voor, List.of(WORKSTATION));
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
                    serverConfig.put(ID_Component_Voor, List.of(SERVER));
                    break;
                case PC:
                    serverConfig.put(ID_Component_Voor, List.of(PC));
                    break;
                case WORKSTATION:
                    serverConfig.put(ID_Component_Voor, List.of(WORKSTATION));
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
                    serverConfig.put(ID_Component_Voor, List.of(SERVER));
                    break;
                case PC:
                    serverConfig.put(ID_Component_Voor, List.of(PC));
                    break;
                case WORKSTATION:
                    serverConfig.put(ID_Component_Voor, List.of(WORKSTATION));
                    break;
                case LAPTOP:
                    serverConfig.put(ID_Component_Voor, List.of(NOTEBOOK));
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
                    serverConfig.put(ID_Bedoel_Voor, List.of(SERVER,"server"));
                    break;
                case PC:
                    serverConfig.put(ID_Bedoel_Voor, List.of(PC));
                    break;
                case WORKSTATION:
                    serverConfig.put(ID_Bedoel_Voor, List.of(PC));
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
                    serverConfig.put(ID_Soort, List.of(PC));
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
                    serverConfig.put(ID_Soort, List.of("Fan","Fan module"));
                    break;
                case PC:
                    serverConfig.put(ID_Soort, List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler","Cooler"));
                    break;
                case LAPTOP:
                    serverConfig.put(ID_Soort, List.of("Thermal paste"));
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
                    serverConfig.put(ID_Soort, List.of("Fan","Fan tray","Cooler"));
                    break;
                case PC:
                    serverConfig.put(ID_Soort, List.of("Liquid cooling kit","Heatsink","Radiatior","Air cooler","Radiator block","Cooler","All-in-one liquid cooler"));
                    break;
                case LAPTOP:
                    serverConfig.put(ID_Soort, List.of("Thermal paste"));
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
                    serverConfig.put(ID_Component_Voor, List.of(SERVER));
                    break;
                case WORKSTATION:
                    serverConfig.put(ID_Component_Voor, List.of(WORKSTATION));
                    break;
                case PC:
                    serverConfig.put(ID_Component_Voor, List.of(PC));
                    break;
                case LAPTOP:
                    serverConfig.put(ID_Component_Voor, List.of(NOTEBOOK));
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
                    serverConfig.put(ID_Component_Voor, List.of(SERVER));
                    break;
                case WORKSTATION:
                    serverConfig.put(ID_Component_Voor, List.of(WORKSTATION,"workstation"));
                    break;
                case PC:
                    serverConfig.put(ID_Component_Voor, List.of(PC));
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
                    serverConfig.put(ID_Bike_Type, List.of(CITY_BIKE));
                    break;
                case DOWNHILL:
                    serverConfig.put(ID_Bike_Type, List.of(DOWNHILL));
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
                    serverConfig.put(ID_Bike_Type, List.of(CITY_BIKE));
                    break;
                case DOWNHILL:
                    serverConfig.put(ID_Bike_Type, List.of(DOWNHILL));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");

            }
            return serverConfig;
        }
        if(componentTypeId == 14)
        {
            switch(configurationType)
            {
                case SUV:
                    serverConfig.put(ID_Car_Type, List.of(SUV));
                    break;
                case Sedan:
                    serverConfig.put(ID_Car_Type, List.of(Sedan));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            return serverConfig;
        }
        if(componentTypeId == 15)
        {
            switch(configurationType)
            {
                case SUV:
                    serverConfig.put(ID_Car_Type, List.of(SUV));
                    break;
                case Sedan:
                    serverConfig.put(ID_Car_Type, List.of(Sedan));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            return serverConfig;
        }
        if(componentTypeId == 16)
        {
            switch(configurationType)
            {
                case SUV:
                    serverConfig.put(ID_Car_Type, List.of(SUV));
                    break;
                case Sedan:
                    serverConfig.put(ID_Car_Type, List.of(Sedan));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            return serverConfig;
        }
        if(componentTypeId == 17)
        {
            switch(configurationType)
            {
                case SUV:
                    serverConfig.put(ID_Car_Type, List.of(SUV));
                    break;
                case Sedan:
                    serverConfig.put(ID_Car_Type, List.of(Sedan));
                    break;
                default:
                    throw new ObjectNotFound("One of the selected component type does not have any components for this type of configuration; ");
            }
            return serverConfig;
        }
        if(componentTypeId == 18)
        {
            switch(configurationType)
            {
                case SUV:
                    serverConfig.put(ID_Car_Type, List.of(SUV));
                    break;
                case Sedan:
                    serverConfig.put(ID_Car_Type, List.of(Sedan));
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
            return ID_Component_Voor;
        }
        else if(categoryId == 2)
        {
            return ID_Bike_Type;
        }
        else if(categoryId == 3)
        {
            return ID_Car_Type;
        }
        return ID_Component_Voor;
    }

    @Override
    public List<Long> getAllDistinctSpecificationIdsThatHoldConfigurationType() {
        if (databaseUrl.contains("dbi527531_testdb")) {
            return List.of(ID_Bedoel_Voor, ID_Soort, ID_Component_Voor,ID_Bike_Type,ID_Car_Type);

        } else if (databaseUrl.contains("dbi527531_stagingcop")) {
            return List.of(ID_Bedoel_Voor, ID_Soort, ID_Component_Voor);
        }
        return List.of(ID_Bedoel_Voor, ID_Soort, ID_Component_Voor,ID_Bike_Type,ID_Car_Type);
    }

}
