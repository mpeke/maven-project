package com.hapi.fhir.practitioner.client.service;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Patient.ContactComponent;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hapi.fhir.practitioner.client.util.FHIRUtil;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IReadExecutable;
import ca.uhn.fhir.rest.gclient.IReadTyped;

@Component
public class PractitionerServiceImpl {
	
	@Autowired
	FHIRUtil fhirutil;
	
	 private CountDownLatch latch = new CountDownLatch(1);

	  public CountDownLatch getLatch() {
	    return latch;
	  }
	
	public String retrievePractitioner(long id) {

		IGenericClient client = fhirutil.getFhirClient();
		FhirContext ctx = fhirutil.getFhirContext();
		String string = "";
      try {
		IReadTyped<Practitioner> iReadTyped = client.read().resource(Practitioner.class);
		IReadExecutable<Practitioner> executable = iReadTyped.withId(id);
		Practitioner practitioner = executable.execute();
		 string = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(practitioner);

      }catch(Exception e) {
			
			e.printStackTrace();
			
		}
		// Print the output
		
		return string;

	}

	public String createPractitioner(Practitioner practitioner) {
		IGenericClient client = fhirutil.getFhirClient();
		//FhirContext ctx = FHIRUtil.getFhirContext();
		String encoded = "";
		try {
			MethodOutcome outcome = client.create().resource(practitioner).prettyPrint().encodedXml().execute();

			IdType id = (IdType) outcome.getId();
			System.out.println("Resource is available at: " + id.getValue());

			/*
			 * IParser xmlParser = ctx.newJsonParser().setPrettyPrint(true);
			 * Patient receivedPatient = (Patient) outcome.getResource();
			 * encoded = xmlParser.encodeResourceToString(receivedPatient);
			 */
			return id.getValue();
		} catch (DataFormatException e) {
			System.out.println("An error occurred trying to upload:");
			e.printStackTrace();
		}

		return encoded;

	}

	public void deletePractitioner(long id) {
		
		IGenericClient client = fhirutil.getFhirClient();

		IBaseOperationOutcome response = client.delete().resourceById(new IdType("Practitioner", id)).execute();

		if (response != null) {
			System.out.println("Practitioner deleted Successfully" + response.toString());
			// outcome.getIssueFirstRep().getDetails().getCodingFirstRep().getCode()
		}

	}

	public void updatePractitioner(Practitioner practitioner) {
		IGenericClient client = fhirutil.getFhirClient();

        // Invoke the server update method
        MethodOutcome outcome = client.update().resource(practitioner).execute();
        IdType id1 = (IdType) outcome.getId();
        System.out.println("Got ID: " + id1.getValue());
    }
	
	 @KafkaListener(topics = "patient", groupId = "group_id")
	  public void consumePatient(String pat) {
			 System.out.println(pat);
	  }

	
}
