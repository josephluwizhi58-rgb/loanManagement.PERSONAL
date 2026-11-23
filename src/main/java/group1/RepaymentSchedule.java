package group1;

import group1.enums.PaymentStatus;
import java.io.Serializable;
import java.time.LocalDate;

public class RepaymentSchedule implements Serializable {
	private static final long serialVersionUID = 1L;
	private final int installmentNumber;
	private final LocalDate dueDate;
	private final double amountDue;
	private double amountPaid;
	private double remainingBalance;
	private PaymentStatus status;

	public RepaymentSchedule(int installmentNumber, LocalDate dueDate, double amountDue, double remainingBalance) {
		this.installmentNumber = installmentNumber;
		this.dueDate = dueDate;
		this.amountDue = amountDue;
		this.amountPaid = 0;
		this.remainingBalance = remainingBalance;
		this.status = PaymentStatus.PENDING;
	}

	public void applyPayment(double amount, LocalDate today) {
		amountPaid += amount;
		if (amountPaid >= amountDue - 0.005) {
			amountPaid = amountDue;
			status = PaymentStatus.PAID;
		} else if (amountPaid > 0) {
			status = PaymentStatus.PARTIALLY_PAID;
		}
		remainingBalance = Math.max(0, remainingBalance - amount);
		if (status != PaymentStatus.PAID && today.isAfter(dueDate)) {
			status = PaymentStatus.OVERDUE;
		}
	}

	public int getInstallmentNumber() { return installmentNumber; }
	public LocalDate getDueDate() { return dueDate; }
	public double getAmountDue() { return amountDue; }
	public double getAmountPaid() { return amountPaid; }
	public double getRemainingBalance() { return remainingBalance; }
	public PaymentStatus getStatus() { return status; }
	public void setStatus(PaymentStatus status) { this.status = status; }
}
