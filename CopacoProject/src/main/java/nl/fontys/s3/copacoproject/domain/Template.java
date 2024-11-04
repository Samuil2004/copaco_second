package nl.fontys.s3.copacoproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Dictionary;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Template {
    private long templateId;
    private Category category;
    private String name;
    private Brand brand;
    private String imageUrl;
    private Dictionary<ComponentType, Integer> components; //integer for order of importance
}
