package com.hapi.fhir.observation.client.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ObservationNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ObservationNotFoundException(String arg0) {
		super(arg0);
	}

}
