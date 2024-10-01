package nl.fontys.s3.copacoproject.persistence.impl;

import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FakeComponentRepositoryImpl implements ComponentRepository {
    ComponentEntity c1 = ComponentEntity.builder()
            .componentTypeId(1L)
            .componentTypeName("Processor")
            .orderOfImportance(1)
            .componentTypeImageUrl("Link Here")
            .categoryId(1L)
            .componentId(1L)
            .componentName("Intel® Core™ i7 processor 14700K")
            .componentUnit("GHz")
            .componentValue("5.60")
            .componentPrice(415.89)
            .componentImageUrl("https://cdn01.nbb.com/media/fc/80/1d/1697605051/ae96ae62e31cd09c516ebae405e1b8b7.jpg?nwidth=1200")
            .brandId(2L).build();


    ComponentEntity c2 = ComponentEntity.builder()
            .componentTypeId(1L)
            .componentTypeName("Processor")
            .orderOfImportance(1)
            .componentTypeImageUrl("Link Here")
            .categoryId(1L)
            .componentId(2L)
            .componentName("Intel® Core™ i7 processor 14700K")
            .componentUnit("GHz")
            .componentValue("5.60")
            .componentPrice(415.89)
            .componentImageUrl("https://cdn01.nbb.com/media/fc/80/1d/1697605051/ae96ae62e31cd09c516ebae405e1b8b7.jpg?nwidth=1200")
            .brandId(2L).build();

    ComponentEntity c3 = ComponentEntity.builder()
            .componentTypeId(2L)
            .componentTypeName("GraphicsCard")
            .orderOfImportance(2)
            .componentTypeImageUrl("https://image-link-to-gpu-type")
            .categoryId(2L)
            .componentId(101L)
            .componentName("NVIDIA GeForce RTX 4090")
            .componentUnit("MHz")
            .componentValue("2520")
            .componentPrice(1599.99)
            .componentImageUrl("https://example.com/images/rtx4090.jpg")
            .brandId(3L)
            .build();

    List<ComponentEntity> allComponents = List.of(c1,c2,c3);

    @Override
    public List<ComponentEntity> getAllComponents() {
        return allComponents;
    }

    @Override
    public List<ComponentEntity> getComponentsByType(String type) {
        return this.allComponents.stream().filter(componentsEntities -> componentsEntities.getComponentTypeName().toLowerCase().equals(type.toLowerCase())).toList();
    }

}
