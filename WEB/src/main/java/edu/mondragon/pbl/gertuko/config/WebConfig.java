package edu.mondragon.pbl.gertuko.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String userUploadPath = Paths.get("uploads/usuarios").toAbsolutePath().toUri().toString();
        String productUploadPath = Paths.get("uploads/productos").toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/uploads/usuarios/**")
                .addResourceLocations(userUploadPath);

        registry.addResourceHandler("/uploads/productos/**")
                .addResourceLocations(productUploadPath);
    }
}
