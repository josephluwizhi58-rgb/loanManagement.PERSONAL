package group1;
public class BusinessLoan extends Loan {
	public BusinessLoan(double principal,double interestRate,int repaymentPeriod) {
			super(principal,interestRate,repaymentPeriod);
		}
	public BusinessLoan(String loanId, Customer customer, LoanType loanType, double principal,
			int repaymentPeriod, java.time.LocalDate approvalDate, java.time.LocalDate startDate) {
		super(loanId, customer, loanType, principal, repaymentPeriod, approvalDate, startDate);
	}
		@Override
		public double  calculateInterest() {
				return new SimpleInterestCalculator().calculateInterest(principal, interestRate, repaymentPeriod);
		
				
			}
		
		}
		
			

		
