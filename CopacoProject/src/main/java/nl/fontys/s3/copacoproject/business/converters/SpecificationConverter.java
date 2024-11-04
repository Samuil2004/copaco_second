package nl.fontys.s3.copacoproject.business.converters;

import nl.fontys.s3.copacoproject.domain.Component;
import nl.fontys.s3.copacoproject.domain.SpecificationType;
import nl.fontys.s3.copacoproject.persistence.entity.Component_SpecificationList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SpecificationConverter {
    //used when converting an item from a map of specifications, for writing to the database
    public static Component_SpecificationList convertFromBaseToEntity(Component component, SpecificationType specificationType, String value){
        return Component_SpecificationList.builder()
                .componentId(ComponentConverter.convertFromBaseToEntity(component))
                .specificationType(SpecificationTypeConverter.convertFromBaseToEntity(specificationType))
                .value(value)
                .build();
    }

    //used when trying to convert a list of specifications of a component fetched from the database
    public static Map<SpecificationType, String> convertFromEntityToBase(List<Component_SpecificationList> specification){
        Map<SpecificationType, String> specifications = new HashMap<>();
        for(Component_SpecificationList specificationEntity : specification){
            specifications.put(SpecificationTypeConverter.convertFromEntityToBase(specificationEntity.getSpecificationType()), specificationEntity.getValue());
        }

        return specifications;
    }
}
