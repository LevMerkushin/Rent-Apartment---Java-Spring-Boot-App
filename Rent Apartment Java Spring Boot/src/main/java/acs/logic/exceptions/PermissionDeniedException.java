package acs.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class PermissionDeniedException extends RuntimeException {

	private static final long serialVersionUID = 9044838698145886364L;

	public PermissionDeniedException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PermissionDeniedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public PermissionDeniedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public PermissionDeniedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
	

}
