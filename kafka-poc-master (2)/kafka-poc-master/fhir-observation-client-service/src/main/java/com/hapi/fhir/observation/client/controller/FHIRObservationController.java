package com.hapi.fhir.observation.client.controller;

import java.net.URI;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hapi.fhir.observation.client.service.ObservationServiceImpl;
import com.hapi.fhir.observation.client.util.FHIRUtil;

import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author AmitK11
 *
 */
@RestController
public class FHIRObservationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FHIRObservationController.class);
	
	@Autowired
	ObservationServiceImpl obService;
	@Autowired
	FHIRUtil fhirutil;
	/**
	 * method to create the Observation resource.
	 * @param obs
	 * @return
	 */
	@PostMapping("/fhirclient/observation")
	public ResponseEntity<Object> createObservation(@RequestBody String obs) {
	
	
		LOGGER.info("  create Observation has been called");
		URI location = null;
		try{
		IParser parser = fhirutil.getFhirContext().newJsonParser();
		Observation obser = parser.parseResource(Observation.class, obs);

		LOGGER.info(" Observation Id  ::: "+ obser.getId());
		LOGGER.info(" Observation Type ::: "+ obser.fhirType());
		
		 String obserdetails = obService.createObservation(obser);
		 String observationId = obserdetails.substring(obserdetails.indexOf("Observation/")+8, obserdetails.indexOf("/_"));
		
		 
		 LOGGER.info(" Observation Id created ::: "+ observationId);
		  location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(observationId).toUri();
		
		  LOGGER.info(" URL of the Observation ::: "+ location.toString());
		}catch(Exception ex){
			ex.printStackTrace();
			
			 LOGGER.error( "error:"+ex.toString());
			
			
		}

		return ResponseEntity.created(location).build();

		
	}

	/**
	 * 
	 * method to get the Observation resource.
	 * @param obsid
	 * @return
	 */
	@GetMapping("/fhirclient/observation/{obsid}")
	public String getObservation(@PathVariable long obsid) {

		String obsDetails = null;
		try {
			
			LOGGER.info(" Fetching Observation  id ::: "+ obsid);
			obsDetails = obService.retrieveObservarions(obsid);
			
		} catch (ResourceNotFoundException exception) {
			
			System.out.println(exception.toString());
			 LOGGER.error( "error:"+exception.toString());
		}
		return obsDetails;

	}

	/**
	 * method to update the Observation resource.
	 * @param obs
	 */
	@PutMapping("/fhirclient/observation")
	public void updateObservation(@RequestBody String obs) {
		try {
			IParser parser = fhirutil.getFhirContext().newJsonParser();
			Observation obser = parser.parseResource(Observation.class, obs);
			LOGGER.info(" Observation Id to be updated ::: "+ obser.getId());
			LOGGER.info(" Observation Type to be updated ::: "+ obser.fhirType());
			
			obService.updateObservation(obser);
		} catch (ResourceNotFoundException exception) {
			LOGGER.error( "error:"+exception.toString());
			
			throw new ResourceNotFoundException("Observation:- " + obs);
		}
	}
	

	/**
	 * method to delete the Observation resource.
	 * @param id
	 */
	@DeleteMapping("/fhirclient/observation/{obserId}")
	public void deletePatient(@PathVariable("obserId") long id) {
		obService.deleteObservation(id);
		LOGGER.info(" Observation Id deleted ::: "+ id);
	}

}
