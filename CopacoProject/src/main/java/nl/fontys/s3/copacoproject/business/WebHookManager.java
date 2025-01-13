package nl.fontys.s3.copacoproject.business;

import nl.fontys.s3.copacoproject.business.dto.component.ComponentInConfigurationResponse;
import nl.fontys.s3.copacoproject.persistence.entity.CustomProductEntity;

import java.util.List;

public interface WebHookManager {
    void sendWebhook(CustomProductEntity productEntity, List<ComponentInConfigurationResponse> componentsResponse);
}
