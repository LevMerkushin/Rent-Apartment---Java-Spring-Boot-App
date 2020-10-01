package acs.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class EnumTypeWrongException extends RuntimeException {
	private static final long serialVersionUID = -930409820079657142L;

	public EnumTypeWrongException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EnumTypeWrongException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public EnumTypeWrongException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public EnumTypeWrongException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
	

}
