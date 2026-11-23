package group1.service;

import java.io.Serializable;

public class SystemSettings implements Serializable {
	private static final long serialVersionUID = 1L;
	private double affordabilityPercentage = 0.40;
	private int maximumActiveLoans = 3;
	private double lateFeePercentage = 0.02;
	private boolean lateFeesEnabled = true;

	public double getAffordabilityPercentage() { return affordabilityPercentage; }
	public int getMaximumActiveLoans() { return maximumActiveLoans; }
	public double getLateFeePercentage() { return lateFeePercentage; }
	public boolean isLateFeesEnabled() { return lateFeesEnabled; }

	public void setAffordabilityPercentage(double percentage) {
		if (percentage <= 0 || percentage > 1) {
			throw new IllegalArgumentException("Affordability percentage must be between 0 and 1");
		}
		this.affordabilityPercentage = percentage;
	}

	public void setMaximumActiveLoans(int maximum) {
		if (maximum < 1) {
			throw new IllegalArgumentException("Maximum active loans must be at least 1");
		}
		this.maximumActiveLoans = maximum;
	}

	public void setLateFeePercentage(double percentage) {
		if (percentage < 0) {
			throw new IllegalArgumentException("Late fee percentage cannot be negative");
		}
		this.lateFeePercentage = percentage;
	}

	public void setLateFeesEnabled(boolean enabled) {
		this.lateFeesEnabled = enabled;
	}
}
