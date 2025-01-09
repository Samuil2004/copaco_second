package nl.fontys.s3.copacoproject.business;

import java.util.List;
import java.util.Map;

public interface SpecificationIdsForComponentPurpose {
    Map<Long, List<String>> getSpecificationIdAndValuesForComponentPurpose(String configurationType, Long componentTypeId);
    List<String> getConfigurationTypesForSpecificationValueAndComponentType(String specificationValue, Long componentTypeId);
    Long getTheSpecificationIdWhereTheDifferentConfigurationTypesCanBeFoundForCategory(Long categoryId);
    List<Long> getAllDistinctSpecificationIdsThatHoldConfigurationType();

}
