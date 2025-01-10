package nl.fontys.s3.copacoproject.business;

import java.util.List;
import java.util.Map;

public interface SpecificationIdsForComponentPurpose {
    Map<Long, List<String>> getSpecificationIdAndValuesForComponentPurpose(String configurationType, Long componentTypeId);
    Long getTheSpecificationIdWhereTheDifferentConfigurationTypesCanBeFoundForCategory(Long categoryId);
    List<Long> getAllDistinctSpecificationIdsThatHoldConfigurationType();

}
