package com.smartlogi.sdms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI smartLogiOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:" + serverPort);
        server.setDescription("Serveur de développement");

        Contact contact = new Contact();
        contact.setEmail("support@smartlogi.com");
        contact.setName("SmartLogi Support");

        License license = new License();
        license.setName("Apache 2.0");
        license.setUrl("http://www.apache.org/licenses/LICENSE-2.0");

        Info info = new Info()
                .title("Smart Delivery Management System API")
                .version("1.0.0")
                .contact(contact)
                .description("API REST complète pour gérer et suivre les livraisons de colis de SmartLogi. " +
                            "Cette API permet aux clients d'envoyer des colis, aux gestionnaires de suivre " +
                            "les livraisons et aux livreurs de mettre à jour les statuts.")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
