package group1;

public class SimpleInterestCalculator implements InterestCalculator {
	@Override
	public double calculateInterest(double principal, double annualRate, int periodInMonths) {
		return principal * (annualRate / 100.0) * (periodInMonths / 12.0);
	}
}
