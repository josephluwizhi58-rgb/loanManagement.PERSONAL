package group1;

import group1.enums.PaymentMethod;
import group1.enums.LoanStatus;
import group1.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoanWorkflowTest {
	private CustomerManager customers;
	private LoanTypeManager types;
	private LoanManager loans;
	private PaymentManager payments;
	private Customer customer;
	private LoanType type;

	@BeforeEach
	void setUp() throws Exception {
		customers = new CustomerManager();
		types = new LoanTypeManager();
		loans = new LoanManager(customers, types);
		payments = new PaymentManager(loans);
		customer = new Customer("C001", "John", "Banda", "NID001",
				"john@example.com", "Lilongwe", "Employed",
				"0999000000", 500000, "Active");
		type = new LoanType("LT001", "Personal Loan", 50000, 1000000,
				15, 1, 12, "Personal financing");
		customers.addCustomer(customer);
		types.addLoanType(type);
	}

	@Test
	void approvedApplicationCreatesLoanAndSchedule() throws Exception {
		LoanApplication application = loans.applyForLoan(
				"C001", "LT001", 100000, 12, "Personal use");
		Loan loan = loans.approveLoan(application.getApplicationId());

		assertEquals(LoanStatus.ACTIVE, loan.getStatus());
		assertEquals(12, loan.getSchedule().size());
		assertEquals(115000, loan.getTotalPayable(), 0.001);
	}

	@Test
	void repaymentUpdatesBalanceAndCannotBeRepeatedForPaidLoan() throws Exception {
		LoanApplication application = loans.applyForLoan(
				"C001", "LT001", 100000, 12, "Personal use");
		Loan loan = loans.approveLoan(application.getApplicationId());

		payments.recordRepayment(loan.getLoanId(), 115000,
				PaymentMethod.CASH, "REF001");

		assertEquals(0, loan.getOutstandingBalance(), 0.005);
		assertEquals(LoanStatus.PAID, loan.getStatus());
		assertThrows(Exception.class, () -> payments.recordRepayment(
				loan.getLoanId(), 100, PaymentMethod.CASH, "REF002"));
	}

	@Test
	void overpaymentIsRejected() throws Exception {
		LoanApplication application = loans.applyForLoan(
				"C001", "LT001", 100000, 12, "Personal use");
		Loan loan = loans.approveLoan(application.getApplicationId());

		assertThrows(Exception.class, () -> payments.recordRepayment(
				loan.getLoanId(), loan.getOutstandingBalance() + 1,
				PaymentMethod.CASH, "REF001"));
	}

	@Test
	void systemSettingsControlEligibilityAndLateFees() {
		assertThrows(IllegalArgumentException.class,
				() -> loans.getSettings().setAffordabilityPercentage(0));
		assertThrows(IllegalArgumentException.class,
				() -> loans.getSettings().setMaximumActiveLoans(0));
		assertThrows(IllegalArgumentException.class,
				() -> loans.getSettings().setLateFeePercentage(-0.01));

		loans.getSettings().setAffordabilityPercentage(0.50);
		loans.getSettings().setMaximumActiveLoans(5);
		loans.getSettings().setLateFeePercentage(0.02);
		assertEquals(0.50, loans.getSettings().getAffordabilityPercentage(), 0.001);
		assertEquals(5, loans.getSettings().getMaximumActiveLoans());
		assertEquals(0.02, loans.getSettings().getLateFeePercentage(), 0.001);
	}
}
