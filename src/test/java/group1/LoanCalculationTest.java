package group1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoanCalculationTest {
	@Test
	void personalLoanUsesAnnualSimpleInterest() {
		PersonalLoan loan = new PersonalLoan(1000000, 15, 12);
		assertEquals(150000, loan.calculateInterest(), 0.001);
		assertEquals(1150000, loan.calculateTotalPayable(), 0.001);
	}

	@Test
	void loanRejectsInvalidValues() {
		assertThrows(IllegalArgumentException.class,
				() -> new PersonalLoan(0, 15, 12));
		assertThrows(IllegalArgumentException.class,
				() -> new PersonalLoan(100000, -1, 12));
		assertThrows(IllegalArgumentException.class,
				() -> new PersonalLoan(100000, 15, 0));
	}
}
