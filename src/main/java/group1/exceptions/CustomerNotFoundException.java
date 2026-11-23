package group1.exceptions;

public class CustomerNotFoundException extends LoanManagementException {
	public CustomerNotFoundException(String message) {
		super(message);
	}
}
