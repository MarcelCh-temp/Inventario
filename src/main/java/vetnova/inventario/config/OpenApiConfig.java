package vetnova.inventario.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventarioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VetNova - Microservicio de Inventario y Bodega")
                        .description("Controla el stock de medicamentos, alimentos e insumos, los movimientos de "
                                + "bodega y los pedidos a proveedores. Expone endpoints consumidos por el "
                                + "microservicio de Ventas para verificar disponibilidad y registrar salidas de stock.")
                        .version("1.0.0")
                        .contact(new Contact().name("Equipo VetNova").email("contacto@vetnova.cl"))
                        .license(new License().name("Uso académico - Duoc UC")));
    }
}
