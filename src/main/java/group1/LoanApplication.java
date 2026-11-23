package group1;

import group1.enums.ApplicationStatus;
import java.io.Serializable;
import java.time.LocalDate;

public class LoanApplication implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String applicationId;
	private final Customer customer;
	private final LoanType loanType;
	private final double requestedAmount;
	private final int requestedRepaymentPeriod;
	private final LocalDate applicationDate;
	private final String purpose;
	private ApplicationStatus status;

	public LoanApplication(String applicationId, Customer customer, LoanType loanType,
			double requestedAmount, int requestedRepaymentPeriod, LocalDate applicationDate, String purpose) {
		if (applicationId == null || applicationId.trim().isEmpty()) {
			throw new IllegalArgumentException("Application ID cannot be empty");
		}
		if (customer == null || loanType == null) {
			throw new IllegalArgumentException("Customer and loan type are required");
		}
		this.applicationId = applicationId;
		this.customer = customer;
		this.loanType = loanType;
		this.requestedAmount = requestedAmount;
		this.requestedRepaymentPeriod = requestedRepaymentPeriod;
		this.applicationDate = applicationDate == null ? LocalDate.now() : applicationDate;
		this.purpose = purpose == null ? "" : purpose.trim();
		this.status = ApplicationStatus.PENDING;
	}

	public String getApplicationId() { return applicationId; }
	public Customer getCustomer() { return customer; }
	public LoanType getLoanType() { return loanType; }
	public double getRequestedAmount() { return requestedAmount; }
	public int getRequestedRepaymentPeriod() { return requestedRepaymentPeriod; }
	public LocalDate getApplicationDate() { return applicationDate; }
	public String getPurpose() { return purpose; }
	public ApplicationStatus getStatus() { return status; }
	public void setStatus(ApplicationStatus status) { this.status = status; }
}
