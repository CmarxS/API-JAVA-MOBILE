package br.com.goals.goals.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // Permite qualquer origem
        config.addAllowedOriginPattern("*");
        // Permite qualquer header
        config.addAllowedHeader("*");
        // Permite qualquer método HTTP
        config.addAllowedMethod("*");
        // Permite envio de credenciais (cookies, auth headers) se precisar
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica o CORS para todos os endpoints
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}

