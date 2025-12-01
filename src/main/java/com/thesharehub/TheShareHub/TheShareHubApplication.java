package com.thesharehub.TheShareHub;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.AbstractEnvironment;

@SpringBootApplication
public class TheShareHubApplication {

	public static void main(String[] args) {
        if (System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME) == null &&
                System.getenv("SPRING_PROFILES_ACTIVE") == null) {
            System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "dev");
        }

        String activeProfile = System.getProperty("spring.profiles.active",
                System.getenv("SPRING_PROFILES_ACTIVE"));

        if ("dev".equals(activeProfile)) {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                if (System.getProperty(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });
        }

        SpringApplication.run(TheShareHubApplication.class, args);
	}

}
