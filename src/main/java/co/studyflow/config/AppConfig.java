package co.studyflow.config;

import co.studyflow.infrastructure.GeminiHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de beans para el backend
 */
@Configuration
public class AppConfig {
    
    @Bean
    @ConditionalOnMissingBean
    public GeminiHttpClient geminiHttpClient() {
        return new GeminiHttpClient();
    }
}
