package group1.service;

import group1.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FilePersistenceManager {
	private final Path file;

	public FilePersistenceManager(String fileName) {
		this.file = Path.of(fileName);
	}

	public void save(List<Customer> customers, List<LoanType> loanTypes,
			List<LoanApplication> applications, List<Loan> loans,
			List<Repayment> repayments) throws IOException {
		save(customers, loanTypes, applications, loans, repayments, new SystemSettings());
	}

	public void save(List<Customer> customers, List<LoanType> loanTypes,
			List<LoanApplication> applications, List<Loan> loans,
			List<Repayment> repayments, SystemSettings settings) throws IOException {
		PersistentState state = new PersistentState(customers, loanTypes, applications, loans, repayments, settings);
		Path temporary = Path.of(file.toString() + ".tmp");
		try (ObjectOutputStream output = new ObjectOutputStream(
				new FileOutputStream(temporary.toFile()))) {
			output.writeObject(state);
		}
		try {
			Files.move(temporary, file, java.nio.file.StandardCopyOption.REPLACE_EXISTING,
					java.nio.file.StandardCopyOption.ATOMIC_MOVE);
		} catch (java.nio.file.AtomicMoveNotSupportedException e) {
			Files.move(temporary, file, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public PersistentState load() throws IOException, ClassNotFoundException {
		if (!Files.exists(file)) {
			return null;
		}
		try (ObjectInputStream input = new ObjectInputStream(
				new FileInputStream(file.toFile()))) {
			return (PersistentState) input.readObject();
		}
	}

	public static class PersistentState implements Serializable {
		private static final long serialVersionUID = 1L;
		private final List<Customer> customers;
		private final List<LoanType> loanTypes;
		private final List<LoanApplication> applications;
		private final List<Loan> loans;
		private final List<Repayment> repayments;
		private final SystemSettings settings;

		public PersistentState(List<Customer> customers, List<LoanType> loanTypes,
				List<LoanApplication> applications, List<Loan> loans,
				List<Repayment> repayments) {
			this(customers, loanTypes, applications, loans, repayments, new SystemSettings());
		}

		public PersistentState(List<Customer> customers, List<LoanType> loanTypes,
				List<LoanApplication> applications, List<Loan> loans,
				List<Repayment> repayments, SystemSettings settings) {
			this.customers = customers;
			this.loanTypes = loanTypes;
			this.applications = applications;
			this.loans = loans;
			this.repayments = repayments;
			this.settings = settings == null ? new SystemSettings() : settings;
		}

		public List<Customer> getCustomers() { return customers; }
		public List<LoanType> getLoanTypes() { return loanTypes; }
		public List<LoanApplication> getApplications() { return applications; }
		public List<Loan> getLoans() { return loans; }
		public List<Repayment> getRepayments() { return repayments; }
		public SystemSettings getSettings() { return settings; }
	}
}
