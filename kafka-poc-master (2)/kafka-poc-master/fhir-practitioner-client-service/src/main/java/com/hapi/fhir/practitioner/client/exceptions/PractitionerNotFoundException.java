package com.hapi.fhir.practitioner.client.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PractitionerNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PractitionerNotFoundException(String arg0) {
		super(arg0);
	}

}
