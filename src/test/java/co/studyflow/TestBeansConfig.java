package co.studyflow;

import co.studyflow.patterns.observer.ProgresoObserverManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestBeansConfig {

    @Bean
    public ProgresoObserverManager progresoObserverManager() {
        return new ProgresoObserverManager();
    }
}
