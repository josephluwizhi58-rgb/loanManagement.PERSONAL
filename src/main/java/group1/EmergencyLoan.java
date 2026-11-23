package group1;

public class EmergencyLoan extends Loan{
		public EmergencyLoan(double principal,double interestRate,int repaymentPeriod) {
				super(principal,interestRate,repaymentPeriod);
			}
	public EmergencyLoan(String loanId, Customer customer, LoanType loanType, double principal,
			int repaymentPeriod, java.time.LocalDate approvalDate, java.time.LocalDate startDate) {
		super(loanId, customer, loanType, principal, repaymentPeriod, approvalDate, startDate);
	}
			@Override
			public double  calculateInterest() {
				return new SimpleInterestCalculator().calculateInterest(principal, interestRate, repaymentPeriod);
			
					
				}
			
			}

