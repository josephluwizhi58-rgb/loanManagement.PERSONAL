package group1;

import java.io.Serializable;

public class LoanType implements Serializable {
	private static final long serialVersionUID = 1L;
	private String loanTypeId;
	private String name;
	private double minimumAmount;
	private double maximumAmount;
	private double interestRate;
	private int maximumRepaymentPeriod;
	private int minimumRepaymentPeriod;
	private String description;
	private boolean active;

	public LoanType(String loanTypeId, String name, double minimumAmount, double maximumAmount,
			double interestRate, int minimumRepaymentPeriod, int maximumRepaymentPeriod,
			String description) {
		if (loanTypeId == null || loanTypeId.trim().isEmpty()) {
			throw new IllegalArgumentException("Loan type ID cannot be empty");
		}
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Loan type name cannot be empty");
		}
		if (interestRate < 0) {
			throw new IllegalArgumentException("Interest rate cannot be negative");
		}
		if (minimumAmount < 0 || minimumAmount > maximumAmount) {
			throw new IllegalArgumentException("Minimum amount cannot exceed maximum amount");
		}
		if (minimumRepaymentPeriod <= 0 || maximumRepaymentPeriod < minimumRepaymentPeriod) {
			throw new IllegalArgumentException("Invalid repayment period range");
		}
		this.loanTypeId = loanTypeId.trim();
		this.name = name.trim();
		this.minimumAmount = minimumAmount;
		this.maximumAmount = maximumAmount;
		this.interestRate = interestRate;
		this.minimumRepaymentPeriod = minimumRepaymentPeriod;
		this.maximumRepaymentPeriod = maximumRepaymentPeriod;
		this.description = description == null ? "" : description.trim();
		this.active = true;
	}

	public String getLoanTypeId() { return loanTypeId; }
	public String getName() { return name; }
	public double getMinimumAmount() { return minimumAmount; }
	public double getMaximumAmount() { return maximumAmount; }
	public double getInterestRate() { return interestRate; }
	public int getMaximumRepaymentPeriod() { return maximumRepaymentPeriod; }
	public int getMinimumRepaymentPeriod() { return minimumRepaymentPeriod; }
	public String getDescription() { return description; }
	public boolean isActive() { return active; }
	public void deactivate() { active = false; }
	public void activate() { active = true; }
	public void setName(String name) { this.name = name; }
	public void setMinimumAmount(double amount) { this.minimumAmount = amount; }
	public void setMaximumAmount(double amount) { this.maximumAmount = amount; }
	public void setInterestRate(double rate) { this.interestRate = rate; }
	public void setMinimumRepaymentPeriod(int period) { this.minimumRepaymentPeriod = period; }
	public void setMaximumRepaymentPeriod(int period) { this.maximumRepaymentPeriod = period; }
	public void setDescription(String description) { this.description = description; }
}
