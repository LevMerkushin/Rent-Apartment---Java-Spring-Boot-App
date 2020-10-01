package acs.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class InputWrongException extends RuntimeException {
	private static final long serialVersionUID = 4965130156786210348L;
	
	public InputWrongException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InputWrongException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InputWrongException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InputWrongException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
