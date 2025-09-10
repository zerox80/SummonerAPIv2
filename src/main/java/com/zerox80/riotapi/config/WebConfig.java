package com.zerox80.riotapi.config;

import jakarta.servlet.Filter;
import org.springframework.boot.web.server.Compression;
import org.springframework.boot.web.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.util.unit.DataSize;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.time.Duration;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Enable content hash versioning for static resources (css/js) and long-term caching
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        VersionResourceResolver versionResolver = new VersionResourceResolver()
                .addContentVersionStrategy("/**");

        registry.addResourceHandler("/css/**", "/js/**", "/images/**", "/webjars/**")
                .addResourceLocations("classpath:/static/css/", "classpath:/static/js/", "classpath:/static/images/", "classpath:/META-INF/resources/webjars/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .resourceChain(true)
                .addResolver(versionResolver)
                .addResolver(new PathResourceResolver());
    }

    // Make Thymeleaf rewrite resource URLs to include the content version (e.g., app.css?v=<hash>)
    @Bean
    public Filter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }

    // Add weak ETag to dynamic responses to enable conditional GETs and save bandwidth
    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    // Turn on HTTP compression programmatically (GZIP/Brotli depending on server)
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> compressionCustomizer() {
        return factory -> {
            Compression compression = new Compression();
            compression.setEnabled(true);
            compression.setMinResponseSize(DataSize.ofKilobytes(1));
            compression.setMimeTypes(new String[]{
                    "text/html", "text/xml", "text/plain", "text/css", "text/javascript",
                    "application/javascript", "application/json", "application/xml", "image/svg+xml"
            });
            factory.setCompression(compression);
        };
    }
}
