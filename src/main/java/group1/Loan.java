package group1;

import group1.enums.LoanStatus;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Loan implements Serializable {
	private static final long serialVersionUID = 1L;
	protected String loanId;
	protected Customer customer;
	protected LoanType loanType;
	protected double principal;
	protected double interestRate;
	protected int repaymentPeriod;
	protected LocalDate approvalDate;
	protected LocalDate startDate;
	protected LocalDate maturityDate;
	protected double totalInterest;
	protected double totalPayable;
	protected double amountPaid;
	protected double outstandingBalance;
	protected LoanStatus status;
	protected final List<Repayment> repayments = new ArrayList<>();
	protected final List<RepaymentSchedule> schedule = new ArrayList<>();

	public Loan(double principal, double interestRate, int repaymentPeriod) {
		validate(principal, interestRate, repaymentPeriod);
		this.principal = principal;
		this.interestRate = interestRate;
		this.repaymentPeriod = repaymentPeriod;
		this.status = LoanStatus.ACTIVE;
		recalculate();
	}

	public Loan(String loanId, Customer customer, LoanType loanType, double principal,
			int repaymentPeriod, LocalDate approvalDate, LocalDate startDate) {
		if (customer == null || loanType == null) {
			throw new IllegalArgumentException("Customer and loan type are required");
		}
		validate(principal, loanType.getInterestRate(), repaymentPeriod);
		this.loanId = loanId;
		this.customer = customer;
		this.loanType = loanType;
		this.principal = principal;
		this.interestRate = loanType.getInterestRate();
		this.repaymentPeriod = repaymentPeriod;
		this.approvalDate = approvalDate;
		this.startDate = startDate;
		this.maturityDate = startDate.plusMonths(repaymentPeriod);
		this.amountPaid = 0;
		this.status = LoanStatus.ACTIVE;
		recalculate();
		customer.addLoan(this);
	}

	private void validate(double principal, double interestRate, int repaymentPeriod) {
		if (principal <= 0) {
			throw new IllegalArgumentException("Principal must be greater than zero");
		}
		if (interestRate < 0) {
			throw new IllegalArgumentException("Interest rate cannot be negative");
		}
		if (repaymentPeriod <= 0) {
			throw new IllegalArgumentException("Repayment period must be greater than zero");
		}
	}

	public abstract double calculateInterest();

	protected void recalculate() {
		totalInterest = calculateInterest();
		totalPayable = principal + totalInterest;
		outstandingBalance = totalPayable - amountPaid;
	}

	public double calculateTotalPayable() {
		recalculate();
		return totalPayable;
	}

	public void recordRepayment(Repayment repayment) {
		repayments.add(repayment);
		amountPaid += repayment.getAmount();
		recalculate();
		if (outstandingBalance <= 0.005) {
			outstandingBalance = 0;
			status = LoanStatus.PAID;
		}
	}

	public void updateOverdueStatus(LocalDate today) {
		if (status == LoanStatus.PAID || status == LoanStatus.CANCELLED) {
			return;
		}
		boolean overdueInstallment = false;
		for (RepaymentSchedule entry : schedule) {
			if (today.isAfter(entry.getDueDate())
					&& entry.getAmountPaid() < entry.getAmountDue()) {
				entry.setStatus(group1.enums.PaymentStatus.OVERDUE);
				overdueInstallment = true;
			}
		}
		status = overdueInstallment || (maturityDate != null
				&& today.isAfter(maturityDate) && outstandingBalance > 0)
				? LoanStatus.OVERDUE : LoanStatus.ACTIVE;
	}

	public void displayLoanDetails() {
		System.out.printf("Loan amount: MWK %.2f%n", principal);
		System.out.printf("Interest: MWK %.2f%n", calculateInterest());
		System.out.printf("Total amount payable: MWK %.2f%n", calculateTotalPayable());
	}

	public String getLoanId() { return loanId; }
	public Customer getCustomer() { return customer; }
	public LoanType getLoanType() { return loanType; }
	public double getPrincipal() { return principal; }
	public double getInterestRate() { return interestRate; }
	public int getRepaymentPeriod() { return repaymentPeriod; }
	public LocalDate getApprovalDate() { return approvalDate; }
	public LocalDate getStartDate() { return startDate; }
	public LocalDate getMaturityDate() { return maturityDate; }
	public double getTotalInterest() { return totalInterest; }
	public double getTotalPayable() { return totalPayable; }
	public double getAmountPaid() { return amountPaid; }
	public double getOutstandingBalance() { return outstandingBalance; }
	public LoanStatus getStatus() { return status; }
	public List<Repayment> getRepayments() { return new ArrayList<>(repayments); }
	public List<RepaymentSchedule> getSchedule() { return new ArrayList<>(schedule); }
	public void setStatus(LoanStatus status) { this.status = status; }
	public void setOutstandingBalance(double outstandingBalance) { this.outstandingBalance = outstandingBalance; }
	public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
	public void setSchedule(List<RepaymentSchedule> schedule) {
		this.schedule.clear();
		this.schedule.addAll(schedule);
	}
}
