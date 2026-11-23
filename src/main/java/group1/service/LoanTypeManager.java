package group1.service;

import group1.LoanType;
import java.util.ArrayList;
import java.util.List;

public class LoanTypeManager {
	private final List<LoanType> loanTypes = new ArrayList<>();

	public void addLoanType(LoanType loanType) {
		if (loanType == null) {
			throw new IllegalArgumentException("Loan type cannot be null");
		}
		if (findOptional(loanType.getLoanTypeId()) != null) {
			throw new IllegalArgumentException("Loan type ID already exists");
		}
		loanTypes.add(loanType);
	}

	public LoanType findLoanType(String loanTypeId) {
		LoanType loanType = findOptional(loanTypeId);
		if (loanType == null) {
			throw new IllegalArgumentException("Loan type not found: " + loanTypeId);
		}
		return loanType;
	}

	private LoanType findOptional(String loanTypeId) {
		if (loanTypeId == null) {
			return null;
		}
		for (LoanType loanType : loanTypes) {
			if (loanType.getLoanTypeId().equalsIgnoreCase(loanTypeId.trim())) {
				return loanType;
			}
		}
		return null;
	}

	public List<LoanType> listLoanTypes() {
		return new ArrayList<>(loanTypes);
	}

	public List<LoanType> listActiveLoanTypes() {
		List<LoanType> active = new ArrayList<>();
		for (LoanType loanType : loanTypes) {
			if (loanType.isActive()) {
				active.add(loanType);
			}
		}
		return active;
	}

	public void loadLoanTypes(List<LoanType> savedLoanTypes) {
		loanTypes.clear();
		if (savedLoanTypes != null) {
			loanTypes.addAll(savedLoanTypes);
		}
	}
}
