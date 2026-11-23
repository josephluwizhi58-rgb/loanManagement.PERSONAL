package group1;

public class AgriculturalLoan extends Loan{
	public AgriculturalLoan(double principal,double interestRate,int repaymentPeriod) {
			super(principal,interestRate,repaymentPeriod);
		}
	public AgriculturalLoan(String loanId, Customer customer, LoanType loanType, double principal,
			int repaymentPeriod, java.time.LocalDate approvalDate, java.time.LocalDate startDate) {
		super(loanId, customer, loanType, principal, repaymentPeriod, approvalDate, startDate);
	}
		@Override
		public double  calculateInterest() {
			return new SimpleInterestCalculator().calculateInterest(principal, interestRate, repaymentPeriod);
		
				
			}
		
		public static void main(String[] args){
			AgriculturalLoan loan = new AgriculturalLoan(500000,9,24);
			loan.displayLoanDetails();
			
			System.out.println("Interest only: " + loan.calculateInterest());
			System.out.println("Total Payable: " + loan.calculateTotalPayable());}
		}

