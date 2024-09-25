package nl.fontys.s3.copacoproject.domain;

import lombok.Builder;

import java.util.List;

@Builder
public class CustomProduct {

    private Integer customProductId;
    private Double price;
    private Template template;
    private User user;
    private List<Component> componentsIncluded;
}
