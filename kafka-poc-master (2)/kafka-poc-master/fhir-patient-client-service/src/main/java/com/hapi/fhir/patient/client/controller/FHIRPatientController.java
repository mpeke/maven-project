package com.hapi.fhir.patient.client.controller;

import java.net.URI;

import org.apache.kafka.common.protocol.Message;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.gson.Gson;
import com.hapi.fhir.patient.client.exceptions.PatientNotFoundException;
import com.hapi.fhir.patient.client.service.PatientServiceImpl;
import com.hapi.fhir.patient.client.util.FHIRUtil;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

/**
 * @author Kamlesh
 *
 */
@RestController
public class FHIRPatientController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FHIRPatientController.class);
	@Autowired
	private PatientServiceImpl patientServiceImpl;
	@Autowired
	FHIRUtil fhirutil;
	
	@Autowired	
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired	
	private Gson jsonConverter;
	
	private static final String TOPIC= "patient";

	/*
	 * @GetMapping("/fhirclient/greetings") public String greetings(){ return
	 * "Hello, Welcome to the FHIR Client Application.."; }
	 */
	/**
	 * Getting Patient info.
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/fhirclient/patients/{id}")
	public String getPatients(@PathVariable long id) {
		String patientDetaisl = null;
		try {

			LOGGER.info(" Getting Patinent Id  " + id);
			patientDetaisl = patientServiceImpl.retiievePatient(id);
		} catch (ResourceNotFoundException exception) {
			System.out.println("Resource not found.............");
			throw new PatientNotFoundException("Patient:- " + id);
		}
		return patientDetaisl;
	}

	/**
	 * creating patient resource.
	 * 
	 * @param pat
	 * @return
	 */
	@PostMapping("/fhirclient/patients")
	public ResponseEntity<Object> createPatient(@RequestBody String pat) {
		URI location = null;
		try {
			// System.out.println(pat);
			IParser parser = fhirutil.getFhirContext().newJsonParser();
			Patient patient = parser.parseResource(Patient.class, pat);
			LOGGER.info(" Patient Id  ::: " + patient.getId());
			LOGGER.info(" Type Type ::: " + patient.fhirType());
			
			this.kafkaTemplate.send(TOPIC, jsonConverter.toJson(pat));
			//kafkaTemplate.send(TOPIC, pat);
			
			String patientDetails = patientServiceImpl.createPatient(patient);
			LOGGER.info("Patient details :::" + patientDetails);
			String patientId = patientDetails.substring(patientDetails.indexOf("Patient/") + 8,
					patientDetails.indexOf("/_"));

			LOGGER.info("Patient id created:  " + patientId);
			location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(patientId).toUri();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ResponseEntity.created(location).build();

		// return patientDetaisl;
	}

	/**
	 * Updating patient resource.
	 * 
	 * @param pat
	 */
	@PutMapping("/fhirclient/patients")
	public void updatePatient(@RequestBody String pat) {
		try {
			IParser parser = fhirutil.getFhirContext().newJsonParser();
			Patient patient = parser.parseResource(Patient.class, pat);
			LOGGER.info("Upading patient is::" + patient.getId());
			patientServiceImpl.updatePatient(patient);
		} catch (ResourceNotFoundException exception) {

			throw new ResourceNotFoundException("pat:- " + pat);
		}
	}

	/**
	 * Deleting Patient resource.
	 * 
	 * @param id
	 */
	@DeleteMapping("/fhirclient/patients/{patientId}")
	public void deletePatient(@PathVariable("patientId") long id) {
		LOGGER.info("Deleting Patient id::" + id);
		patientServiceImpl.deletePatient(id);
	}

}
