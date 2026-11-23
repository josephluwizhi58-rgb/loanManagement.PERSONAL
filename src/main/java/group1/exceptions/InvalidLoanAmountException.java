package group1.exceptions;

public class InvalidLoanAmountException extends LoanManagementException {
	public InvalidLoanAmountException(String message) {
		super(message);
	}
}
