package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class JbossEapQuickstartParentApplication {

	public static void main(String[] args) {
		SpringApplication.run(JbossEapQuickstartParentApplication.class, args);
	}

}
