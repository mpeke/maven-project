package com.hapi.fhir.observation.client.service;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IReadExecutable;
import ca.uhn.fhir.rest.gclient.IReadTyped;

import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hapi.fhir.observation.client.util.FHIRUtil;

@Component
public class ObservationServiceImpl {
	

	@Autowired
	FHIRUtil  fhirutil;
	   
	   public String createObservation(Observation obser){
		   IGenericClient client = fhirutil.getFhirClient();
		   FhirContext ctx = fhirutil.getFhirContext();
		   String encoded = "";
		   try {
	           MethodOutcome outcome = client.create().resource(obser).prettyPrint().encodedXml().execute();
	           IdType id = (IdType) outcome.getId();
	          
	           return id.getValue();
	       } catch (DataFormatException e) {
	          
	           e.printStackTrace();
	       }

	       
	       return encoded;
	       
	   }
	   
	   
	   
	   
	   public String retrieveObservarions(long id) {

		   IGenericClient client = fhirutil.getFhirClient();
		   FhirContext ctx = fhirutil.getFhirContext();

	      IReadTyped<Observation> iReadTyped = client.read().resource(Observation.class); 
	      IReadExecutable<Observation> executable = iReadTyped.withId(id);
	      Observation observation= executable.execute();

	      // Print the output
	      String obs = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(observation);
	     
	      return obs;
	   }
	   
		public void deleteObservation(long id) {
			// Create a context

			FhirContext ctx = FhirContext.forR4();
			// Create a client
			IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/hapi-fhir-jpaserver/fhir");

			IBaseOperationOutcome response = client.delete().resourceById(new IdType("Observation", id)).execute();

			// outcome may be null if the server didn't return one
			// MethodOutcome outcome = response.

			if (response != null) {
				System.out.println("Observation deleted Successfully" + response.toString());
				// outcome.getIssueFirstRep().getDetails().getCodingFirstRep().getCode()
			}

		}

		public void updateObservation(Observation observation) {
	        // Create a context
	        FhirContext ctx = FhirContext.forR4();
	        // Create a client
	        IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/hapi-fhir-jpaserver/fhir");

	        // Invoke the server update method
	        MethodOutcome outcome = client.update().resource(observation).execute();
	        IdType id = (IdType) outcome.getId();
	        System.out.println("Got ID: " + id.getValue());
	    }
		
		 @KafkaListener(topics = "patient",groupId = "group_id")
		  public void consumePatient(String pat) {
				 System.out.println(pat);
		  }


}
