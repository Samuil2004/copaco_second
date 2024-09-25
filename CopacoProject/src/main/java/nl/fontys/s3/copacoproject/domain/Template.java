package nl.fontys.s3.copacoproject.domain;

import lombok.Builder;


@Builder
public class Template {
    private Integer templateId;
    private Category category;
    private String name;
    private String brand;
    private String Manufacturer;
    private String imageUrl;
}
