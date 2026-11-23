package group1;

import group1.enums.PaymentMethod;
import java.io.Serializable;
import java.time.LocalDate;

public class Repayment implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String paymentId;
	private final Loan loan;
	private final LocalDate paymentDate;
	private final double amount;
	private final PaymentMethod paymentMethod;
	private final String referenceNumber;

	public Repayment(String paymentId, Loan loan, LocalDate paymentDate, double amount,
			PaymentMethod paymentMethod, String referenceNumber) {
		if (paymentId == null || paymentId.trim().isEmpty()) {
			throw new IllegalArgumentException("Payment ID cannot be empty");
		}
		if (loan == null) {
			throw new IllegalArgumentException("Loan is required");
		}
		if (amount <= 0) {
			throw new IllegalArgumentException("Payment amount must be greater than zero");
		}
		this.paymentId = paymentId;
		this.loan = loan;
		this.paymentDate = paymentDate == null ? LocalDate.now() : paymentDate;
		this.amount = amount;
		this.paymentMethod = paymentMethod;
		this.referenceNumber = referenceNumber == null ? "" : referenceNumber.trim();
	}

	public String getPaymentId() { return paymentId; }
	public Loan getLoan() { return loan; }
	public LocalDate getPaymentDate() { return paymentDate; }
	public double getAmount() { return amount; }
	public PaymentMethod getPaymentMethod() { return paymentMethod; }
	public String getReferenceNumber() { return referenceNumber; }
}
