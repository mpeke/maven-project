package com.hapi.fhir.patient.client.service;

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
import org.springframework.stereotype.Component;

import com.hapi.fhir.patient.client.util.FHIRUtil;

@Component
public class PatientServiceImpl {

	
	@Autowired
	FHIRUtil fhirutil;
	public String retiievePatient(long id) {

		IGenericClient client = fhirutil.getFhirClient();
		FhirContext ctx = fhirutil.getFhirContext();

		IReadTyped<Patient> iReadTyped = client.read().resource(Patient.class);
		IReadExecutable<Patient> executable = iReadTyped.withId(id);
		Patient patient = executable.execute();

		// Print the output
		String string = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
		System.out.println("======================== " + string);
		return string;

	}

	public String createPatient(Patient patient) {
		IGenericClient client = fhirutil.getFhirClient();
		FhirContext ctx = fhirutil.getFhirContext();
		String encoded = "";
		try {
			MethodOutcome outcome = client.create().resource(patient).prettyPrint().encodedXml().execute();

			IdType id = (IdType) outcome.getId();
			//System.out.println("Resource is available at: " + id.getValue());

			/*
			 * IParser xmlParser = ctx.newJsonParser().setPrettyPrint(true);
			 * Patient receivedPatient = (Patient) outcome.getResource();
			 * encoded = xmlParser.encodeResourceToString(receivedPatient);
			 */
			return id.getValue();
		} catch (DataFormatException e) {
			//System.out.println("An error occurred trying to upload:");
			e.printStackTrace();
		}

		System.out.println(encoded);
		return encoded;

	}

	public void deletePatient(long id) {
		// Create a context

		//FhirContext ctx = FhirContext.forR4();
		// Create a client
		//IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/hapi-fhir-jpaserver/fhir");
		IGenericClient client = fhirutil.getFhirClient();
		FhirContext ctx = fhirutil.getFhirContext();

		IBaseOperationOutcome response = client.delete().resourceById(new IdType("Patient", id)).execute();

		// outcome may be null if the server didn't return one
		// MethodOutcome outcome = response.

		if (response != null) {
			System.out.println("Patient deleted Successfully" + response.toString());
			// outcome.getIssueFirstRep().getDetails().getCodingFirstRep().getCode()
		}

	}

	public void updatePatient(Patient patient) {
        // Create a context
       // FhirContext ctx = FhirContext.forR4();
        // Create a client
      //  IGenericClient client = ctx.newRestfulGenericClient("http://localhost:8080/hapi-fhir-jpaserver/fhir");
		IGenericClient client = fhirutil.getFhirClient();
		FhirContext ctx = fhirutil.getFhirContext();

        // Invoke the server update method
        MethodOutcome outcome = client.update().resource(patient).execute();
        IdType id1 = (IdType) outcome.getId();
        System.out.println("Got ID: " + id1.getValue());
    }

	

}
