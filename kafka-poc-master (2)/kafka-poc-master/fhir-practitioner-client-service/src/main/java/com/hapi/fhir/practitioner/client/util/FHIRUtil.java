package com.hapi.fhir.practitioner.client.util;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
@Component
public class FHIRUtil {
	private  IGenericClient client;
	private  FhirContext ctx;

	private  final Logger LOGGER = LoggerFactory.getLogger(FHIRUtil.class);

	@Value("${FHIR_SERVER_URL}")
	private  String fhirServerUrl;

	public  IGenericClient getFhirClient() {
		FhirContext ctx = FhirContext.forR4();

		if (client == null) {
			LOGGER.info("Creating FHIR client Object.......");

			client = ctx.newRestfulGenericClient(fhirServerUrl);
		}

		return client;
	}

	public  FhirContext getFhirContext() {
		if (ctx == null) {
			
			LOGGER.info("Creating FHIR contect Object..........");
			ctx = FhirContext.forR4();
		}

		return ctx;
	}

	public  IGenericClient getFhirJsonParser() {
		FhirContext ctx = FhirContext.forR4();

		if (client == null) {
			LOGGER.info("Creating FHIR JSON parser Object..........");
			client = ctx.newRestfulGenericClient(fhirServerUrl);
		}

		return client;
	}

}
