package com.website.athletearena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class AthletearenaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AthletearenaApplication.class, args);
	}

}
 