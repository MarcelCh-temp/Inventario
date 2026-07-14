package vetnova.inventario.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Beans de configuración - pruebas unitarias")
class ConfigBeansTest {

    @Test
    @DisplayName("RestTemplateConfig construye un RestTemplate utilizable")
    void creaRestTemplate() {
        RestTemplate restTemplate = new RestTemplateConfig().restTemplate();
        assertThat(restTemplate).isNotNull();
    }

    @Test
    @DisplayName("OpenApiConfig construye la metadata de OpenAPI con título y versión")
    void creaOpenApiInfo() {
        OpenAPI openAPI = new OpenApiConfig().inventarioOpenAPI();
        assertThat(openAPI.getInfo().getTitle()).contains("Inventario");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
    }
}
