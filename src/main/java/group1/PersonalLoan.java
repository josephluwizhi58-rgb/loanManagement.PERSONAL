package group1;
public class PersonalLoan extends Loan{
	public PersonalLoan(double principal,double interestRate,int repaymentPeriod) {
		super(principal,interestRate,repaymentPeriod);
	}
	public PersonalLoan(String loanId, Customer customer, LoanType loanType, double principal,
			int repaymentPeriod, java.time.LocalDate approvalDate, java.time.LocalDate startDate) {
		super(loanId, customer, loanType, principal, repaymentPeriod, approvalDate, startDate);
	}
	@Override
	public double  calculateInterest() {
		return new SimpleInterestCalculator().calculateInterest(principal, interestRate, repaymentPeriod);
	
			
		}
	
	public static void main(String[] args) {
	  PersonalLoan loan = new PersonalLoan(500000,15,12);
			loan.displayLoanDetails();
			
			System.out.println("Interest only: " + loan.calculateInterest());
			System.out.println("Total Payable: " + loan.calculateTotalPayable());
		}
	
}
	




