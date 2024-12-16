package nl.fontys.s3.copacoproject.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringCorsConfiguration {
    @Bean
    public WebMvcConfigurer corsConfigurer(@Value("${cors.allowedOrigins}") String[] allowedOrigins) {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins) // Accepts multiple origins
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Explicitly allow these methods
                        .allowedHeaders("*"); // Allow all headers
//                        .allowCredentials(true); // Allow cookies and other credentials
            }
        };
    }
}