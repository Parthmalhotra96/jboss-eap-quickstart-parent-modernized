package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.config.documentation;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8090");
        server.setDescription("Development");

        Contact myContact = new Contact();
        myContact.setName("Parth Malhotra");
        myContact.setEmail("parthmalhotra.96@gmail.com");

        Info information = new Info()
                .title("Member Management System API")
                .version("1.0")
                .description("APIs exposes endpoints to manage members.")
                .contact(myContact);
        return new OpenAPI().info(information).servers(List.of(server));
    }
}
