package group1.exceptions;

public class InvalidLoanApplicationException extends LoanManagementException {
	public InvalidLoanApplicationException(String message) {
		super(message);
	}
}
