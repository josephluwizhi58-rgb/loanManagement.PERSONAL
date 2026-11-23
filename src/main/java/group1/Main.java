package group1;

import group1.enums.LoanStatus;
import group1.enums.PaymentMethod;
import group1.exceptions.LoanManagementException;
import group1.service.*;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.List;
import java.util.Scanner;

public class Main {
	private static final Scanner scanner = new Scanner(System.in);
	private static final CustomerManager customerManager = new CustomerManager();
	private static final LoanTypeManager loanTypeManager = new LoanTypeManager();
	private static final LoanManager loanManager = new LoanManager(customerManager, loanTypeManager);
	private static final PaymentManager paymentManager = new PaymentManager(loanManager);
	private static final FilePersistenceManager persistenceManager =
			new FilePersistenceManager("loan-management.dat");

	public static void main(String[] args) {
		if (args.length > 0 && "--console".equalsIgnoreCase(args[0])) {
			runConsole();
			return;
		}
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ignored) {
			}
			new LoanManagementFrame().setVisible(true);
		});
	}

	private static void runConsole() {
		loadData();
		while (true) {
			printMainMenu();
			String choice = scanner.nextLine().trim();
			try {
				switch (choice) {
					case "1" -> manageCustomers();
					case "2" -> manageLoanTypes();
					case "3" -> applyForLoan();
					case "4" -> viewApplications();
					case "5" -> approveLoan();
					case "6" -> rejectLoan();
					case "7" -> makeRepayment();
					case "8" -> viewSchedule();
					case "9" -> viewCustomerLoans();
					case "10" -> viewOverdue();
					case "11" -> showReports();
					case "0" -> {
						saveData();
						System.out.println("Goodbye.");
						return;
				 }
					default -> System.out.println("Invalid menu choice.");
				}
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
		 }
		}
	}

	private static void seedData() {
		loanTypeManager.addLoanType(new LoanType("LT001", "Personal Loan", 50000, 1000000, 15, 1, 12,
				"Personal and household financing"));
		loanTypeManager.addLoanType(new LoanType("LT002", "Business Loan", 100000, 5000000, 12, 1, 24,
				"Small and medium business financing"));
		loanTypeManager.addLoanType(new LoanType("LT003", "Emergency Loan", 20000, 500000, 10, 1, 6,
				"Short-term emergency financing"));
		loanTypeManager.addLoanType(new LoanType("LT004", "Education Loan", 100000, 3000000, 8, 1, 36,
				"Education and tuition financing"));
		loanTypeManager.addLoanType(new LoanType("LT005", "Agricultural Loan", 100000, 4000000, 9, 1, 24,
				"Farm production and agricultural financing"));

		customerManager.addCustomer(new Customer("C001", "Johnny", "Alubi", "NID001",
				"john@example.com", "Mangochi", "Employed", "0999123456", 500000, "Active"));
	}

	private static void loadData() {
		try {
			FilePersistenceManager.PersistentState state = persistenceManager.load();
			if (state == null) {
				seedData();
				return;
			}
			customerManager.loadCustomers(state.getCustomers());
			loanTypeManager.loadLoanTypes(state.getLoanTypes());
			loanManager.loadState(state.getApplications(), state.getLoans());
			paymentManager.loadRepayments(state.getRepayments());
			System.out.println("Saved data loaded.");
		} catch (Exception e) {
			System.out.println("Could not load saved data; starting with sample data.");
			seedData();
		}
	}

	private static void saveData() {
		try {
			persistenceManager.save(
					customerManager.listCustomers(),
					loanTypeManager.listLoanTypes(),
					loanManager.getApplications(),
					loanManager.getLoans(),
					paymentManager.getRepayments());
			System.out.println("Data saved.");
		} catch (Exception e) {
			System.out.println("Could not save data: " + e.getMessage());
		}
	}

	private static void printMainMenu() {
		System.out.println("\n===================================");
		System.out.println("        LOAN MANAGEMENT SYSTEM");
		System.out.println("===================================");
		System.out.println("1. Manage Customers");
		System.out.println("2. Manage Loan Types");
		System.out.println("3. Apply for Loan");
		System.out.println("4. View Loan Applications");
		System.out.println("5. Approve Loan");
		System.out.println("6. Reject Loan");
		System.out.println("7. Make Loan Repayment");
		System.out.println("8. View Repayment Schedule");
		System.out.println("9. View Customer Loans");
		System.out.println("10. View Overdue Loans");
		System.out.println("11. Loan and Financial Reports");
		System.out.println("0. Exit");
		System.out.print("Enter your choice: ");
	}

	private static void manageCustomers() {
		System.out.println("\n1. Add customer");
		System.out.println("2. Search customer");
		System.out.println("3. View all customers");
		System.out.println("4. Update monthly income");
		System.out.println("5. Deactivate customer");
		System.out.print("Choice: ");
		String choice = scanner.nextLine().trim();
		try {
			switch (choice) {
				case "1" -> addCustomer();
				case "2" -> {
					System.out.print("Customer ID: ");
					Customer customer = customerManager.findCustomer(scanner.nextLine().trim());
					printCustomer(customer);
				}
				case "3" -> {
					for (Customer customer : customerManager.listCustomers()) {
						printCustomer(customer);
					}
				}
				case "4" -> {
					System.out.print("Customer ID: ");
					Customer customer = customerManager.findCustomer(scanner.nextLine().trim());
					System.out.print("New monthly income: ");
					customer.setMonthlyIncome(readDouble());
					System.out.println("Customer updated.");
				}
				case "5" -> {
					System.out.print("Customer ID: ");
					customerManager.removeCustomer(scanner.nextLine().trim());
					System.out.println("Customer deactivated.");
				}
				default -> System.out.println("Invalid choice.");
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private static void addCustomer() throws Exception {
		System.out.print("Customer ID: ");
		String id = scanner.nextLine().trim();
		System.out.print("First name: ");
		String first = scanner.nextLine().trim();
		System.out.print("Last name: ");
		String last = scanner.nextLine().trim();
		System.out.print("National ID: ");
		String nationalId = scanner.nextLine().trim();
		System.out.print("Email: ");
		String email = scanner.nextLine().trim();
		System.out.print("Physical address: ");
		String address = scanner.nextLine().trim();
		System.out.print("Employment status: ");
		String employment = scanner.nextLine().trim();
		System.out.print("Phone number: ");
		String phone = scanner.nextLine().trim();
		if (!phone.matches("[0-9+() -]{7,20}")) {
			throw new IllegalArgumentException("Phone number has an invalid format");
		}
		System.out.print("Monthly income: ");
		double income = readDouble();
		Customer customer = new Customer(id, first, last, nationalId, email, address,
				employment, phone, income, "Active");
		customerManager.addCustomer(customer);
		System.out.println("Customer added successfully.");
	}

	private static void printCustomer(Customer customer) {
		System.out.printf("ID: %s | Name: %s %s | National ID: %s%n",
				customer.getCustomerId(), customer.getFirstName(), customer.getLastName(),
				customer.getNationalId());
		System.out.printf("Phone: %s | Email: %s | Income: MWK %.2f | Status: %s%n",
				customer.getPhoneNumber(), customer.getEmailAddress(),
				customer.getMonthlyIncome(), customer.getCustomerStatus());
	}

	private static void manageLoanTypes() {
		System.out.println("\n1. Create loan type");
		System.out.println("2. Modify loan type");
		System.out.println("3. View loan type");
		System.out.println("4. List loan types");
		System.out.println("5. Deactivate loan type");
		System.out.println("6. Activate loan type");
		System.out.print("Choice: ");
		String choice = scanner.nextLine().trim();
		switch (choice) {
			case "1" -> createLoanType();
			case "2" -> modifyLoanType();
			case "3" -> viewLoanType();
			case "4" -> loanTypeManager.listLoanTypes().forEach(Main::printLoanType);
			case "5" -> setLoanTypeActive(false);
			case "6" -> setLoanTypeActive(true);
			default -> System.out.println("Invalid choice.");
		}
	}

	private static void createLoanType() {
		try {
			System.out.print("Loan type ID: ");
			String id = scanner.nextLine().trim();
			System.out.print("Name: ");
			String name = scanner.nextLine().trim();
			System.out.print("Minimum amount: ");
			double minimum = readDouble();
			System.out.print("Maximum amount: ");
			double maximum = readDouble();
			System.out.print("Interest rate: ");
			double rate = readDouble();
			System.out.print("Minimum repayment period: ");
			int minimumPeriod = readInt();
			System.out.print("Maximum repayment period: ");
			int maximumPeriod = readInt();
			System.out.print("Description: ");
			String description = scanner.nextLine().trim();
			loanTypeManager.addLoanType(new LoanType(id, name, minimum, maximum, rate,
					minimumPeriod, maximumPeriod, description));
			System.out.println("Loan type created.");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private static void modifyLoanType() {
		try {
			System.out.print("Loan type ID: ");
			LoanType type = loanTypeManager.findLoanType(scanner.nextLine().trim());
			System.out.print("New name [" + type.getName() + "]: ");
			String name = scanner.nextLine().trim();
			if (!name.isEmpty()) type.setName(name);
			System.out.print("New minimum amount [" + type.getMinimumAmount() + "]: ");
			modifyDouble(scanner.nextLine(), type.getMinimumAmount(), type::setMinimumAmount);
			System.out.print("New maximum amount [" + type.getMaximumAmount() + "]: ");
			modifyDouble(scanner.nextLine(), type.getMaximumAmount(), type::setMaximumAmount);
			if (type.getMinimumAmount() > type.getMaximumAmount()) {
				throw new IllegalArgumentException("Minimum amount cannot exceed maximum amount");
			}
			System.out.print("New interest rate [" + type.getInterestRate() + "]: ");
			modifyDouble(scanner.nextLine(), type.getInterestRate(), type::setInterestRate);
			if (type.getInterestRate() < 0) {
				throw new IllegalArgumentException("Interest rate cannot be negative");
			}
			System.out.print("New minimum period [" + type.getMinimumRepaymentPeriod() + "]: ");
			modifyInt(scanner.nextLine(), type.getMinimumRepaymentPeriod(), type::setMinimumRepaymentPeriod);
			System.out.print("New maximum period [" + type.getMaximumRepaymentPeriod() + "]: ");
			modifyInt(scanner.nextLine(), type.getMaximumRepaymentPeriod(), type::setMaximumRepaymentPeriod);
			if (type.getMinimumRepaymentPeriod() <= 0
					|| type.getMaximumRepaymentPeriod() < type.getMinimumRepaymentPeriod()) {
				throw new IllegalArgumentException("Invalid repayment period range");
			}
			System.out.print("New description [" + type.getDescription() + "]: ");
			String description = scanner.nextLine().trim();
			if (!description.isEmpty()) type.setDescription(description);
			System.out.println("Loan type modified.");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private static void viewLoanType() {
		try {
			System.out.print("Loan type ID: ");
			printLoanType(loanTypeManager.findLoanType(scanner.nextLine().trim()));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private static void setLoanTypeActive(boolean active) {
		try {
			System.out.print("Loan type ID: ");
			LoanType type = loanTypeManager.findLoanType(scanner.nextLine().trim());
			if (active) type.activate();
			else type.deactivate();
			System.out.println("Loan type " + (active ? "activated." : "deactivated."));
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private static void modifyDouble(String input, double fallback,
			java.util.function.DoubleConsumer setter) {
		if (!input.trim().isEmpty()) setter.accept(Double.parseDouble(input.trim()));
	}

	private static void modifyInt(String input, int fallback,
			java.util.function.IntConsumer setter) {
		if (!input.trim().isEmpty()) setter.accept(Integer.parseInt(input.trim()));
	}

	private static void printLoanType(LoanType type) {
		System.out.printf("%s | %s | MWK %.2f-%.2f | %.2f%% | %d-%d months | %s%n",
				type.getLoanTypeId(), type.getName(), type.getMinimumAmount(),
				type.getMaximumAmount(), type.getInterestRate(),
				type.getMinimumRepaymentPeriod(), type.getMaximumRepaymentPeriod(),
				type.isActive() ? "ACTIVE" : "INACTIVE");
	}

	private static void applyForLoan() {
		try {
			System.out.print("Customer ID: ");
			String customerId = scanner.nextLine().trim();
			System.out.println("Available loan types:");
			for (LoanType type : loanTypeManager.listActiveLoanTypes()) {
				System.out.println(type.getLoanTypeId() + ": " + type.getName());
			}
			System.out.print("Loan type ID: ");
			String typeId = scanner.nextLine().trim();
			System.out.print("Requested amount: ");
			double amount = readDouble();
			System.out.print("Repayment period (months): ");
			int period = readInt();
			System.out.print("Purpose: ");
			String purpose = scanner.nextLine().trim();
			LoanApplication application = loanManager.applyForLoan(
					customerId, typeId, amount, period, purpose);
			System.out.println("Application created: " + application.getApplicationId()
					+ " (" + application.getStatus() + ")");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private static void viewApplications() {
		if (loanManager.getApplications().isEmpty()) {
			System.out.println("No loan applications.");
			return;
		}
		for (LoanApplication application : loanManager.getApplications()) {
			System.out.printf("%s | Customer: %s | Type: %s | MWK %.2f | %d months | %s%n",
					application.getApplicationId(), application.getCustomer().getCustomerId(),
					application.getLoanType().getName(), application.getRequestedAmount(),
					application.getRequestedRepaymentPeriod(), application.getStatus());
		}
	}

	private static void approveLoan() {
		try {
			viewApplications();
			System.out.print("Application ID: ");
			Loan loan = loanManager.approveLoan(scanner.nextLine().trim());
			System.out.printf("Approved. Loan ID: %s | Total payable: MWK %.2f%n",
					loan.getLoanId(), loan.getTotalPayable());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private static void rejectLoan() {
		try {
			viewApplications();
			System.out.print("Application ID: ");
			String applicationId = scanner.nextLine().trim();
			System.out.print("Reject or Cancel (R/C): ");
			String action = scanner.nextLine().trim().toUpperCase();
			if ("C".equals(action)) {
				loanManager.cancelApplication(applicationId);
				System.out.println("Application cancelled.");
			} else {
				loanManager.rejectLoan(applicationId);
				System.out.println("Application rejected.");
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private static void makeRepayment() {
		try {
			System.out.print("Loan ID: ");
			String loanId = scanner.nextLine().trim();
			Loan loan = loanManager.findLoan(loanId);
			System.out.printf("Outstanding balance: MWK %.2f%n", loan.getOutstandingBalance());
			System.out.print("Payment amount: ");
			double amount = readDouble();
			System.out.println("Payment method: CASH, BANK_TRANSFER, MOBILE_MONEY, CARD");
			System.out.print("Method: ");
			PaymentMethod method = PaymentMethod.valueOf(scanner.nextLine().trim().toUpperCase());
			System.out.print("Reference number: ");
			String reference = scanner.nextLine().trim();
			Repayment repayment = paymentManager.recordRepayment(
					loanId, amount, method, reference);
			System.out.printf("Payment %s recorded. Outstanding: MWK %.2f | Status: %s%n",
					repayment.getPaymentId(), loan.getOutstandingBalance(), loan.getStatus());
		} catch (IllegalArgumentException e) {
			System.out.println("Error: Invalid payment method or amount");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private static void viewSchedule() {
		try {
			System.out.print("Loan ID: ");
			Loan loan = loanManager.findLoan(scanner.nextLine().trim());
			System.out.printf("%-12s %-12s %-14s %-12s %-14s %s%n",
					"Installment", "Due Date", "Amount Due", "Paid", "Balance", "Status");
			for (RepaymentSchedule entry : loan.getSchedule()) {
				System.out.printf("%-12d %-12s %-14.2f %-12.2f %-14.2f %s%n",
						entry.getInstallmentNumber(), entry.getDueDate(),
						entry.getAmountDue(), entry.getAmountPaid(),
						entry.getRemainingBalance(), entry.getStatus());
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private static void viewCustomerLoans() {
		try {
			System.out.print("Customer ID: ");
			Customer customer = customerManager.findCustomer(scanner.nextLine().trim());
			if (customer.getLoans().isEmpty()) {
				System.out.println("Customer has no loans.");
				return;
			}
			for (Loan loan : customer.getLoans()) {
				System.out.printf("%s | %s | Principal: MWK %.2f | Outstanding: MWK %.2f | %s%n",
						loan.getLoanId(), loan.getLoanType().getName(), loan.getPrincipal(),
						loan.getOutstandingBalance(), loan.getStatus());
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private static void viewOverdue() {
		List<Loan> overdueLoans = loanManager.getOverdueLoans();
		if (overdueLoans.isEmpty()) {
			System.out.println("No overdue loans.");
		} else {
			for (Loan loan : overdueLoans) {
				System.out.printf("%s | Customer: %s | Outstanding: MWK %.2f%n",
						loan.getLoanId(), loan.getCustomer().getCustomerId(),
						loan.getOutstandingBalance());
			}
		}
		List<RepaymentSchedule> overdueInstallments = paymentManager.getOverdueInstallments();
		if (!overdueInstallments.isEmpty()) {
			System.out.println("Overdue installments:");
			for (RepaymentSchedule entry : overdueInstallments) {
				System.out.printf("Installment %d | Due %s | Paid %.2f | Due amount %.2f%n",
						entry.getInstallmentNumber(), entry.getDueDate(),
						entry.getAmountPaid(), entry.getAmountDue());
			}
		}
	}

	private static void showReports() {
		List<Loan> loans = loanManager.getLoans();
		double lent = 0;
		double interest = 0;
		double repaid = 0;
		double outstanding = 0;
		int active = 0;
		int paid = 0;
		int overdue = 0;
		int defaulted = 0;
		for (Loan loan : loans) {
			lent += loan.getPrincipal();
			interest += loan.getTotalInterest();
			repaid += loan.getAmountPaid();
			outstanding += loan.getOutstandingBalance();
			if (loan.getStatus() == LoanStatus.ACTIVE) active++;
			if (loan.getStatus() == LoanStatus.PAID) paid++;
			if (loan.getStatus() == LoanStatus.OVERDUE) overdue++;
			if (loan.getStatus() == LoanStatus.DEFAULTED) defaulted++;
		}
		System.out.println("\nLOAN REPORT");
		System.out.println("All loans: " + loans.size());
		System.out.println("Active: " + active + " | Paid: " + paid
				+ " | Overdue: " + overdue + " | Defaulted: " + defaulted);
		System.out.printf("Total amount lent: MWK %.2f%n", lent);
		System.out.printf("Total interest expected: MWK %.2f%n", interest);
		System.out.printf("Total amount repaid: MWK %.2f%n", repaid);
		System.out.printf("Total outstanding: MWK %.2f%n", outstanding);
		System.out.println("Customers: " + customerManager.listCustomers().size());
		System.out.println("\nDetailed loan report:");
		System.out.println("1. All  2. Active  3. Paid  4. Overdue  5. Defaulted");
		System.out.print("Choice: ");
		String choice = scanner.nextLine().trim();
		for (Loan loan : loans) {
			boolean include = switch (choice) {
				case "2" -> loan.getStatus() == LoanStatus.ACTIVE;
				case "3" -> loan.getStatus() == LoanStatus.PAID;
				case "4" -> loan.getStatus() == LoanStatus.OVERDUE;
				case "5" -> loan.getStatus() == LoanStatus.DEFAULTED;
				default -> true;
			};
			if (include) {
				System.out.printf("%s | %s | Principal MWK %.2f | Repaid MWK %.2f | Outstanding MWK %.2f | %s%n",
						loan.getLoanId(), loan.getLoanType().getName(),
						loan.getPrincipal(), loan.getAmountPaid(),
						loan.getOutstandingBalance(), loan.getStatus());
			}
		}
	}

	private static double readDouble() throws LoanManagementException {
		try {
			return Double.parseDouble(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			throw new LoanManagementException("Please enter a valid number");
		}
	}

	private static int readInt() throws LoanManagementException {
		try {
			return Integer.parseInt(scanner.nextLine().trim());
		} catch (NumberFormatException e) {
			throw new LoanManagementException("Please enter a valid whole number");
		}
	}
}
