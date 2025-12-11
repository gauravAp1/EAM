package com.example.eam.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);
        
        // Allow all origins - Development approach
        // For production, replace with specific origins:
        // config.setAllowedOrigins(Arrays.asList("https://yourdomain.com"));
        config.addAllowedOriginPattern("*");
        
        // Allow all headers
        config.addAllowedHeader("*");
        
        // Allow all HTTP methods
        config.setAllowedMethods(Arrays.asList(
            "GET", 
            "POST", 
            "PUT", 
            "DELETE", 
            "PATCH", 
            "OPTIONS"
        ));
        
        // Expose common headers to the client
        config.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Disposition",
            "Content-Type"
        ));
        
        // Max age for preflight requests cache (1 hour)
        config.setMaxAge(3600L);
        
        // Apply CORS configuration to all paths (including Swagger)
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
