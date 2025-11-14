// Package-Deklaration: Definiert die Zugehörigkeit dieser Klasse zum config-Paket
package com.zerox80.riotapi.config;

// Import für Compression-Konfiguration (GZIP)
import org.springframework.boot.web.server.Compression;
// Import für ConfigurableServletWebServerFactory zur Server-Konfiguration
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
// Import für WebServerFactoryCustomizer zur Server-Anpassung
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
// Import für @Bean Annotation zum Definieren von Spring-verwalteten Objekten
import org.springframework.context.annotation.Bean;
// Import für @Configuration Annotation um diese Klasse als Konfigurations-Klasse zu markieren
import org.springframework.context.annotation.Configuration;
// Import für CacheControl zur HTTP-Cache-Steuerung
import org.springframework.http.CacheControl;
// Import für DataSize zur Angabe von Datengrößen (KB, MB, etc.)
import org.springframework.util.unit.DataSize;
// Import für ShallowEtagHeaderFilter zum automatischen ETag-Generation
import org.springframework.web.filter.ShallowEtagHeaderFilter;
// Import für ResourceHandlerRegistry zur Konfiguration statischer Ressourcen
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
// Import für WebMvcConfigurer zum Konfigurieren von Spring MVC
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
// Import für PathResourceResolver zum Auflösen von Ressourcen-Pfaden
import org.springframework.web.servlet.resource.PathResourceResolver;
// Import für VersionResourceResolver zum Versionieren von Ressourcen (wird importiert aber nicht genutzt)
import org.springframework.web.servlet.resource.VersionResourceResolver;

// Import für Duration zur Zeitangabe (Tage, Stunden, etc.)
import java.time.Duration;


// @Configuration: Markiert diese Klasse als Quelle von Bean-Definitionen
@Configuration
// implements WebMvcConfigurer: Ermöglicht Anpassung von Spring MVC Konfiguration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Konfiguriert Handler für statische Ressourcen (Assets, Favicon).
     * PERFORMANCE: Setzt aggressive Browser-Caching für Assets zur Bandbreiten-Reduzierung.
     *
     * @param registry ResourceHandlerRegistry zum Registrieren von Resource-Handlern
     */
    // @Override: Überschreibt Methode aus WebMvcConfigurer Interface
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Registriert Handler für alle Requests zu /assets/**
        registry.addResourceHandler("/assets/**")
                // Mapped auf Dateien im classpath:/static/assets/ Verzeichnis
                .addResourceLocations("classpath:/static/assets/")
                // Cache-Control: max-age=30 Tage, public (Cache in Proxy erlaubt)
                // PERFORMANCE: Assets werden 30 Tage im Browser gecached
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                // Aktiviert Resource-Chain für erweiterte Features
                .resourceChain(true)
                // Fügt PathResourceResolver hinzu zum einfachen Pfad-Mapping
                .addResolver(new PathResourceResolver());

        // Registriert Handler für Favicon
        registry.addResourceHandler("/favicon.svg")
                // Mapped auf Dateien im classpath:/static/ Verzeichnis
                .addResourceLocations("classpath:/static/")
                // Cache-Control: max-age=7 Tage, public
                // PERFORMANCE: Favicon wird 1 Woche im Browser gecached
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(7)).cachePublic());
    }

    /**
     * Create a ShallowEtagHeaderFilter bean.
     *
     * @return ShallowEtagHeaderFilter
     */
    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    /**
     * Create a WebServerFactoryCustomizer bean for compression.
     *
     * @return WebServerFactoryCustomizer
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
