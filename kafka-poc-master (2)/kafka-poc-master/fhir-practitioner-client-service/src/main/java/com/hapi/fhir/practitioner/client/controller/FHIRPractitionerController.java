package com.hapi.fhir.practitioner.client.controller;

import java.net.URI;

import org.hl7.fhir.r4.model.Practitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.hapi.fhir.practitioner.client.exceptions.PractitionerNotFoundException;
import com.hapi.fhir.practitioner.client.service.PractitionerServiceImpl;
import com.hapi.fhir.practitioner.client.util.FHIRUtil;

import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

/**
 * @author Kamlesh
 *
 */
@RestController
public class FHIRPractitionerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FHIRPractitionerController.class);

	@Autowired
	private PractitionerServiceImpl practitionerServiceImpl;
	
	@Autowired
	FHIRUtil fhirutil;

	/**
	 * get the Practitioner details.
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/fhirclient/practitioner/{id}")
	public String getPractitioner(@PathVariable long id) {
		String patientDetaisl = null;
		try {

			LOGGER.info(" Fetching practitioner  id ::: " + id);
			patientDetaisl = practitionerServiceImpl.retrievePractitioner(id);
		} catch (ResourceNotFoundException exception) {
          
			throw new PractitionerNotFoundException("Practitioner:- " + id);
		}
		return patientDetaisl;
	}

	/**
	 * create Practitioner resources
	 * 
	 * @param pat
	 * @return
	 */
	@PostMapping("/fhirclient/practitioner")
	public ResponseEntity<Object> createPractitioner(@RequestBody String pat) {
		URI location = null;

		LOGGER.info("  create Practitioner has been called");
		try {
			
			IParser parser = fhirutil.getFhirContext().newJsonParser();
			Practitioner pract = parser.parseResource(Practitioner.class, pat);
			LOGGER.info(" Practitioner Id  ::: " + pract.getId());
			LOGGER.info(" Practitioner Type ::: " + pract.fhirType());

			String practitionerDetails = practitionerServiceImpl.createPractitioner(pract);

			LOGGER.info(" Practitioner details ::: " + practitionerDetails);

			String practionerId = practitionerDetails.substring(practitionerDetails.indexOf("Practitioner/") + 13,
					practitionerDetails.indexOf("/_"));
			LOGGER.info(" Practitioner Id created ::: " + practionerId);
			location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(practionerId)
					.toUri();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ResponseEntity.created(location).build();

		// return patientDetaisl;
	}

	/**
	 * update Practitioner resource
	 * 
	 * @param pat
	 */
	@PutMapping("/fhirclient/practitioner")
	public void updatePatient(@RequestBody String pat) {
		try {
			IParser parser = fhirutil.getFhirContext().newJsonParser();
			Practitioner practitioner = parser.parseResource(Practitioner.class, pat);
			LOGGER.info(" Practitioner Id to be updated ::: " + practitioner.getId());
			LOGGER.info(" Practitioner Type to be updated ::: " + practitioner.fhirType());
			practitionerServiceImpl.updatePractitioner(practitioner);
		} catch (ResourceNotFoundException exception) {

			LOGGER.error("error:  " + exception.toString());
			throw new PractitionerNotFoundException("Practitioner:- " + pat);
		}
	}

	/**
	 * delete Practitioner resource
	 * 
	 * @param id
	 */
	@DeleteMapping("/fhirclient/practitioner/{patientId}")
	public void deletePatient(@PathVariable("patientId") long id) {
		LOGGER.info("Practitioner to be deleted:::" + id);
		practitionerServiceImpl.deletePractitioner(id);
	}

}
