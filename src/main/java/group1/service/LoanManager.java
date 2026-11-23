package group1.service;

import group1.*;
import group1.enums.ApplicationStatus;
import group1.enums.LoanStatus;
import group1.exceptions.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanManager {
	private final List<LoanApplication> applications = new ArrayList<>();
	private final List<Loan> loans = new ArrayList<>();
	private final CustomerManager customerManager;
	private final LoanTypeManager loanTypeManager;
	private int applicationSequence = 1;
	private int loanSequence = 1;
	private final SystemSettings settings = new SystemSettings();

	public LoanManager(CustomerManager customerManager, LoanTypeManager loanTypeManager) {
		this.customerManager = customerManager;
		this.loanTypeManager = loanTypeManager;
	}

	public LoanApplication applyForLoan(String customerId, String loanTypeId, double amount,
			int period, String purpose) throws LoanManagementException {
		Customer customer = customerManager.findCustomer(customerId);
		LoanType loanType;
		try {
			loanType = loanTypeManager.findLoanType(loanTypeId);
		} catch (IllegalArgumentException e) {
			throw new InvalidLoanApplicationException(e.getMessage());
		}
		if (!"ACTIVE".equalsIgnoreCase(customer.getCustomerStatus())) {
			throw new InvalidLoanApplicationException("Customer is inactive");
		}
		if (!loanType.isActive()) {
			throw new InvalidLoanApplicationException("Loan type is inactive");
		}
		if (amount < loanType.getMinimumAmount() || amount > loanType.getMaximumAmount()) {
			throw new InvalidLoanAmountException("Loan amount must be between MWK "
					+ loanType.getMinimumAmount() + " and MWK " + loanType.getMaximumAmount());
		}
		if (period < loanType.getMinimumRepaymentPeriod()
				|| period > loanType.getMaximumRepaymentPeriod()) {
			throw new InvalidLoanApplicationException("Repayment period must be between "
					+ loanType.getMinimumRepaymentPeriod() + " and "
					+ loanType.getMaximumRepaymentPeriod() + " months");
		}

		SimpleInterestCalculator calculator = new SimpleInterestCalculator();
		double interest = calculator.calculateInterest(amount, loanType.getInterestRate(), period);
		double monthlyPayment = (amount + interest) / period;
		if (monthlyPayment > customer.getMonthlyIncome() * settings.getAffordabilityPercentage()) {
			throw new InvalidLoanApplicationException("Insufficient income for the requested loan");
		}

		int activeLoans = 0;
		for (Loan loan : customer.getLoans()) {
			if (loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) {
				activeLoans++;
			}
			if (loan.getStatus() == LoanStatus.OVERDUE) {
				throw new InvalidLoanApplicationException("Customer has an overdue loan");
			}
		}
		if (activeLoans >= settings.getMaximumActiveLoans()) {
			throw new InvalidLoanApplicationException("Customer exceeds the maximum number of active loans");
		}

		String applicationId = "APP" + String.format("%03d", applicationSequence++);
		LoanApplication application = new LoanApplication(applicationId, customer, loanType,
				amount, period, LocalDate.now(), purpose);
		applications.add(application);
		return application;
	}

	public Loan approveLoan(String applicationId) throws LoanManagementException {
		LoanApplication application = findApplication(applicationId);
		if (application.getStatus() == ApplicationStatus.APPROVED) {
			throw new InvalidLoanApplicationException("Application has already been approved");
		}
		if (application.getStatus() == ApplicationStatus.REJECTED) {
			throw new InvalidLoanApplicationException("Rejected applications cannot be approved");
		}
		if (application.getStatus() == ApplicationStatus.CANCELLED) {
			throw new InvalidLoanApplicationException("Cancelled applications cannot be approved");
		}

		String loanId = "LN" + String.format("%03d", loanSequence++);
		Loan loan = createLoan(application, loanId);
		application.setStatus(ApplicationStatus.APPROVED);
		loans.add(loan);
		generateRepaymentSchedule(loan);
		return loan;
	}

	public void rejectLoan(String applicationId) throws LoanManagementException {
		LoanApplication application = findApplication(applicationId);
		if (application.getStatus() != ApplicationStatus.PENDING) {
			throw new InvalidLoanApplicationException("Only pending applications can be rejected");
		}
		application.setStatus(ApplicationStatus.REJECTED);
	}

	public void cancelApplication(String applicationId) throws LoanManagementException {
		LoanApplication application = findApplication(applicationId);
		if (application.getStatus() != ApplicationStatus.PENDING) {
			throw new InvalidLoanApplicationException("Only pending applications can be cancelled");
		}
		application.setStatus(ApplicationStatus.CANCELLED);
	}

	private Loan createLoan(LoanApplication application, String loanId) {
		LoanType type = application.getLoanType();
		String name = type.getName().toLowerCase();
		LocalDate today = LocalDate.now();
		if (name.contains("business")) {
			return new BusinessLoan(loanId, application.getCustomer(), type,
					application.getRequestedAmount(), application.getRequestedRepaymentPeriod(), today, today);
		}
		if (name.contains("education")) {
			return new EducationLoan(loanId, application.getCustomer(), type,
					application.getRequestedAmount(), application.getRequestedRepaymentPeriod(), today, today);
		}
		if (name.contains("emergency")) {
			return new EmergencyLoan(loanId, application.getCustomer(), type,
					application.getRequestedAmount(), application.getRequestedRepaymentPeriod(), today, today);
		}
		if (name.contains("agricultural")) {
			return new AgriculturalLoan(loanId, application.getCustomer(), type,
					application.getRequestedAmount(), application.getRequestedRepaymentPeriod(), today, today);
		}
		return new PersonalLoan(loanId, application.getCustomer(), type,
				application.getRequestedAmount(), application.getRequestedRepaymentPeriod(), today, today);
	}

	public void generateRepaymentSchedule(Loan loan) {
		List<RepaymentSchedule> schedule = new ArrayList<>();
		double monthlyPayment = roundMoney(loan.getTotalPayable() / loan.getRepaymentPeriod());
		double remaining = loan.getTotalPayable();
		for (int i = 1; i <= loan.getRepaymentPeriod(); i++) {
			double due = i == loan.getRepaymentPeriod()
					? roundMoney(remaining)
					: monthlyPayment;
			double balanceAfter = roundMoney(Math.max(0, remaining - due));
			schedule.add(new RepaymentSchedule(i, loan.getStartDate().plusMonths(i),
					due, balanceAfter));
			remaining = balanceAfter;
		}
		loan.setSchedule(schedule);
	}

	public Loan findLoan(String loanId) throws LoanNotFoundException {
		for (Loan loan : loans) {
			if (loan.getLoanId().equalsIgnoreCase(loanId.trim())) {
				return loan;
			}
		}
		throw new LoanNotFoundException("Loan not found: " + loanId);
	}

	private LoanApplication findApplication(String applicationId) throws InvalidLoanApplicationException {
		for (LoanApplication application : applications) {
			if (application.getApplicationId().equalsIgnoreCase(applicationId.trim())) {
				return application;
			}
		}
		throw new InvalidLoanApplicationException("Application not found: " + applicationId);
	}

	public LoanApplication getApplication(String applicationId) throws InvalidLoanApplicationException {
		return findApplication(applicationId);
	}

	public List<LoanApplication> getApplications() {
		return new ArrayList<>(applications);
	}

	public List<Loan> getLoans() {
		return new ArrayList<>(loans);
	}

	public List<Loan> getOverdueLoans() {
		LocalDate today = LocalDate.now();
		List<Loan> overdue = new ArrayList<>();
		for (Loan loan : loans) {
			loan.updateOverdueStatus(today);
			if (loan.getStatus() == LoanStatus.OVERDUE) {
				if (settings.isLateFeesEnabled()) {
					loan.applyLateFees(settings.getLateFeePercentage());
				}
				overdue.add(loan);
			}
		}
		return overdue;
	}

	public double calculateOutstandingBalance(Loan loan) {
		return roundMoney(loan.getOutstandingBalance());
	}

	public void loadState(List<LoanApplication> savedApplications, List<Loan> savedLoans) {
		applications.clear();
		loans.clear();
		if (savedApplications != null) {
			applications.addAll(savedApplications);
			applicationSequence = applications.size() + 1;
		}
		if (savedLoans != null) {
			loans.addAll(savedLoans);
			loanSequence = loans.size() + 1;
		}
	}

	public static double roundMoney(double amount) {
		return Math.round(amount * 100.0) / 100.0;
	}

	public SystemSettings getSettings() { return settings; }
}
