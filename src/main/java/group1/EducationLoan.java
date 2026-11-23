package group1;


	public class EducationLoan extends Loan{
		public EducationLoan(double principal,double interestRate,int repaymentPeriod) {
				super(principal,interestRate,repaymentPeriod);
			}
		public EducationLoan(String loanId, Customer customer, LoanType loanType, double principal,
				int repaymentPeriod, java.time.LocalDate approvalDate, java.time.LocalDate startDate) {
			super(loanId, customer, loanType, principal, repaymentPeriod, approvalDate, startDate);
		}
			@Override
			public double  calculateInterest() {
					return new SimpleInterestCalculator().calculateInterest(principal, interestRate, repaymentPeriod);
			
					
				}
			
			public static void main(String[] args){
				EducationLoan loan = new EducationLoan(500000,8,36);
				loan.displayLoanDetails();
				
				System.out.println("Interest only: " + loan.calculateInterest());
				System.out.println("Total Payable: " + loan.calculateTotalPayable());}
			}


