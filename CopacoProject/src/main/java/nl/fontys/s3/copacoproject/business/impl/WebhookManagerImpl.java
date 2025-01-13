package nl.fontys.s3.copacoproject.business.impl;

import lombok.RequiredArgsConstructor;
import nl.fontys.s3.copacoproject.business.WebHookManager;
import nl.fontys.s3.copacoproject.business.dto.component.ComponentInConfigurationResponse;
import nl.fontys.s3.copacoproject.business.dto.custom_product_dto.CustomProductResponse;
import nl.fontys.s3.copacoproject.persistence.entity.CustomProductEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebhookManagerImpl implements WebHookManager {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String WEBHOOK_URL = "https://webhook.site/d5e4a021-8a66-402e-8316-18b4b7eb810a";

    @Override
    public void sendWebhook(CustomProductEntity productEntity, List<ComponentInConfigurationResponse> componentsResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        CustomProductResponse productResponse = CustomProductResponse.builder()
                .customProductId(productEntity.getId())
                .componentsIncluded(componentsResponse)
                .statusId(productEntity.getStatus().getId())
                .templateId(productEntity.getTemplate().getId())
                .userId(productEntity.getUserId().getId())
                .build();

        HttpEntity<CustomProductResponse> request = new HttpEntity<>(productResponse, headers);

        try {
            String response = restTemplate.postForObject(WEBHOOK_URL, request, String.class);
            System.out.println("Webhook sent successfully: " + response);
        } catch (Exception e) {
            System.err.println("Failed to send webhook: " + e.getMessage());
        }
    }
}
