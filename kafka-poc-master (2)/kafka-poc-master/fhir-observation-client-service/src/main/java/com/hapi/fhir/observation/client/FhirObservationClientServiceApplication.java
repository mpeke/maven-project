package com.hapi.fhir.observation.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class FhirObservationClientServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FhirObservationClientServiceApplication.class, args);
	}

}
