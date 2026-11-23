package group1.service;

import group1.*;
import group1.enums.LoanStatus;
import group1.enums.PaymentMethod;
import group1.exceptions.InvalidRepaymentException;
import group1.exceptions.LoanNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentManager {
	private final LoanManager loanManager;
	private final List<Repayment> repayments = new ArrayList<>();
	private int paymentSequence = 1;

	public PaymentManager(LoanManager loanManager) {
		this.loanManager = loanManager;
	}

	public Repayment recordRepayment(String loanId, double amount, PaymentMethod method,
			String referenceNumber) throws InvalidRepaymentException, LoanNotFoundException {
		if (amount <= 0) {
			throw new InvalidRepaymentException("Payment amount must be greater than zero");
		}
		Loan loan = loanManager.findLoan(loanId);
		if (loan.getStatus() == LoanStatus.PAID) {
			throw new InvalidRepaymentException("A paid loan cannot receive another repayment");
		}
		if (loan.getStatus() == LoanStatus.CANCELLED) {
			throw new InvalidRepaymentException("A cancelled loan cannot receive repayment");
		}
		if (amount > loan.getOutstandingBalance()) {
			throw new InvalidRepaymentException("Payment cannot exceed the outstanding balance");
		}
		String paymentId = "PAY" + String.format("%03d", paymentSequence++);
		Repayment repayment = new Repayment(paymentId, loan, LocalDate.now(),
				LoanManager.roundMoney(amount), method, referenceNumber);
		loan.recordRepayment(repayment);
		applyToSchedule(loan, repayment.getAmount());
		repayments.add(repayment);
		return repayment;
	}

	private void applyToSchedule(Loan loan, double amount) {
		double remainingPayment = amount;
		List<RepaymentSchedule> schedule = loan.getSchedule();
		for (RepaymentSchedule installment : schedule) {
			if (remainingPayment <= 0) {
				break;
			}
			double outstanding = installment.getAmountDue() - installment.getAmountPaid();
			double applied = Math.min(outstanding, remainingPayment);
			if (applied > 0) {
				installment.applyPayment(applied, LocalDate.now());
				remainingPayment -= applied;
			}
		}
		double cumulativeDue = 0;
		double cumulativePaid = 0;
		for (RepaymentSchedule installment : schedule) {
			cumulativeDue += installment.getAmountDue();
			cumulativePaid += installment.getAmountPaid();
			installment.setRemainingBalance(
					Math.max(0, loan.getOutstandingBalance()
							- Math.max(0, cumulativeDue - cumulativePaid)));
			installment.refreshStatus(LocalDate.now());
		}
	}

	public List<Repayment> getRepayments() {
		return new ArrayList<>(repayments);
	}

	public List<RepaymentSchedule> getOverdueInstallments() {
		List<RepaymentSchedule> overdue = new ArrayList<>();
		LocalDate today = LocalDate.now();
		for (Loan loan : loanManager.getLoans()) {
			for (RepaymentSchedule installment : loan.getSchedule()) {
				if (today.isAfter(installment.getDueDate())
						&& installment.getAmountPaid() < installment.getAmountDue()) {
					installment.setStatus(group1.enums.PaymentStatus.OVERDUE);
					overdue.add(installment);
				}
			}
		}
		return overdue;
	}

	public void loadRepayments(List<Repayment> savedRepayments) {
		repayments.clear();
		if (savedRepayments != null) {
			repayments.addAll(savedRepayments);
			paymentSequence = repayments.size() + 1;
		}
	}
}
