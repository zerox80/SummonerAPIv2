package com.zerox80.riotapi.config;

import org.springframework.boot.web.server.Compression;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.util.unit.DataSize;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.time.Duration;

/**
 * Spring Web MVC configuration for static resources, caching, and compression.
 * 
 * <p>This configuration class sets up optimal settings for serving static resources,
 * including content hash versioning for cache busting, long-term caching policies,
 * ETag support for conditional requests, and HTTP compression for bandwidth optimization.
 * These settings improve performance and reduce server load for static assets.</p>
 * 
 * <p>Key features configured:</p>
 * <ul>
 *   <li>Content hash versioning for CSS/JS/images/webjars</li>
 *   <li>30-day cache control for static resources</li>
 *   <li>Weak ETag generation for dynamic responses</li>
 *   <li>HTTP compression (GZIP/Brotli) for text-based content</li>
 *   <li>Resource chain with version and path resolvers</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures resource handlers for static content with versioning and caching.
     * 
     * <p>This method sets up content hash versioning for static resources to enable
     * efficient cache busting. Resources are served with a 30-day cache control header
     * and include content-based version strings in URLs. The resource chain includes
     * both version and path resolvers for optimal resource resolution.</p>
     * 
     * <p>Configured resource handlers:</p>
     * <ul>
     *   <li>/css/** - Stylesheets from classpath:/static/css/</li>
     *   <li>/js/** - JavaScript files from classpath:/static/js/</li>
     *   <li>/images/** - Images from classpath:/static/images/</li>
     *   <li>/webjars/** - WebJar dependencies from META-INF/resources/webjars/</li>
     * </ul>
     * 
     * @param registry The ResourceHandlerRegistry to configure
     */
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

    /**
     * Note: ResourceUrlEncodingFilter is automatically provided by Spring Boot's Thymeleaf autoconfiguration.
     * 
     * <p>This filter automatically rewrites URLs in Thymeleaf templates to include
     * content hash versions when using the version strategy. No manual configuration
     * is required for this functionality.</p>
     */

    /**
     * Creates a ShallowEtagHeaderFilter for conditional GET support.
     * 
     * <p>This filter generates weak ETags for dynamic responses to enable
     * conditional GET requests. When clients send an If-None-Match header
     * with a matching ETag, the server can respond with 304 Not Modified,
     * saving bandwidth and processing time. The filter is "shallow" meaning
     * it only considers the response body, not headers.</p>
     * 
     * @return A configured ShallowEtagHeaderFilter instance
     */
    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    /**
     * Configures HTTP compression for the embedded web server.
     * 
     * <p>This method enables HTTP compression (GZIP/Brotli depending on server
     * capabilities) to reduce bandwidth usage for text-based content. Compression
     * is applied to responses larger than 1KB for common text MIME types including
     * HTML, XML, JSON, CSS, JavaScript, and SVG. This significantly improves
     * page load times and reduces data transfer costs.</p>
     * 
     * <p>Compression settings:</p>
     * <ul>
     *   <li>Enabled for all text-based content</li>
     *   <li>Minimum response size: 1KB</li>
     *   <li>Supported MIME types: HTML, XML, JSON, CSS, JS, SVG</li>
     *   <li>Server automatically chooses best algorithm (GZIP/Brotli)</li>
     * </ul>
     * 
     * @return A WebServerFactoryCustomizer that configures compression settings
     */
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
