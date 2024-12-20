package bank.app.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .servers(List.of(new io.swagger.v3.oas.models.servers.Server().url("http://localhost:8080")))
                .info(new Info().title("BankPortal"))
                .addTagsItem(new io.swagger.v3.oas.models.tags.Tag().name("auth-сontroller")
                        .description("Authentication and Authorization API"));
    }
}