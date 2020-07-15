package com.hapi.fhir.patient.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableEurekaClient
@EnableKafka
public class FhirPatientClientServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FhirPatientClientServiceApplication.class, args);
	}

}
