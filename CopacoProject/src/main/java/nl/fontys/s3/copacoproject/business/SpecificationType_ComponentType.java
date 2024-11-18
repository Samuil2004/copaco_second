package nl.fontys.s3.copacoproject.business;

public interface SpecificationType_ComponentType {
    Long findIdByComponentTypeIdAndSpecificationTypeId(Long componentTypeId, Long specificationTypeId);
}
