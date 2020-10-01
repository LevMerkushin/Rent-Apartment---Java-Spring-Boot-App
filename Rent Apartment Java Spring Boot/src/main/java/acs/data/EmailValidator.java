package acs.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import acs.boundaries.UserBoundaryEmail;
import acs.logic.exceptions.InputWrongException;

public class EmailValidator {
	private Pattern pattern;
	private Matcher matcher;

	private static final String EMAIL_PATTERN = 
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public EmailValidator() {
		pattern = Pattern.compile(EMAIL_PATTERN);
	}
	
	public Boolean isEmail(final String email) {
		if(email == null)
			 return null;
		matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	public boolean isEmail(final UserBoundaryEmail email) {
		if(email == null)
			return false;
		matcher = pattern.matcher(email.getEmail());
		return matcher.matches();
	}
	
	public static void validateEmail(String email) {
		EmailValidator emailValidator = new EmailValidator();
		Boolean validEmail = emailValidator.isEmail(email);
		if(validEmail == null)
			throw new InputWrongException("Email is mandatory");
		else if(!validEmail)
			throw new InputWrongException("Incorrect email pattern. Expected pattern: address@domain1.domain2");
	}
}