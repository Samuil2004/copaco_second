package nl.fontys.s3.copacoproject.persistence.impl;

import nl.fontys.s3.copacoproject.persistence.ComponentRepository;
import nl.fontys.s3.copacoproject.persistence.entity.ComponentEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FakeComponentRepositoryImpl implements ComponentRepository {
    ComponentEntity c1 = ComponentEntity.builder()
            .componentId(1L)
            .componentName("Processor")
            .componentImageUrl("Link Here")
            .componentId(1L)
            .componentName("Intel® Core™ i7 processor 14700K")
            .componentPrice(415.89)
            .componentImageUrl("https://cdn01.nbb.com/media/fc/80/1d/1697605051/ae96ae62e31cd09c516ebae405e1b8b7.jpg?nwidth=1200")
            .build();


    ComponentEntity c2 = ComponentEntity.builder()
            .componentId(1L)
            .componentName("Processor")
            .componentImageUrl("Link Here")
            .componentId(2L)
            .componentName("Intel® Core™ i7 processor 14700K")
            .componentPrice(415.89)
            .componentImageUrl("https://cdn01.nbb.com/media/fc/80/1d/1697605051/ae96ae62e31cd09c516ebae405e1b8b7.jpg?nwidth=1200")
            .build();

    ComponentEntity c3 = ComponentEntity.builder()
            .componentId(2L)
            .componentName("GraphicsCard")
            .componentImageUrl("https://image-link-to-gpu-type")
            .componentId(101L)
            .componentName("NVIDIA GeForce RTX 4090")
            .componentPrice(1599.99)
            .componentImageUrl("https://example.com/images/rtx4090.jpg")
            .build();

    List<ComponentEntity> allComponents = List.of(c1,c2,c3);

    @Override
    public List<ComponentEntity> getAllComponents() {
        return allComponents;
    }

    @Override
    public List<ComponentEntity> getComponentsByType(String type) {
        return this.allComponents.stream().filter(componentsEntities -> componentsEntities.getComponentName().toLowerCase().equals(type.toLowerCase())).toList();
    }

}
