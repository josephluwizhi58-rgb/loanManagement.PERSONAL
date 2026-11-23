package group1.service;

import group1.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvReportExporter {
	public void exportCustomers(Path path, List<Customer> customers) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writer.write("Customer ID,Name,National ID,Phone,Email,Income,Status\n");
			for (Customer customer : customers) {
				writer.write(csv(customer.getCustomerId()) + "," +
						csv(customer.getFirstName() + " " + customer.getLastName()) + "," +
						csv(customer.getNationalId()) + "," +
						csv(customer.getPhoneNumber()) + "," +
						csv(customer.getEmailAddress()) + "," +
						customer.getMonthlyIncome() + "," +
						csv(customer.getCustomerStatus()) + "\n");
			}
		}
	}

	public void exportLoans(Path path, List<Loan> loans) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			writer.write("Loan ID,Customer,Loan Type,Principal,Interest,Total Payable,Amount Paid,Outstanding,Status\n");
			for (Loan loan : loans) {
				writer.write(csv(loan.getLoanId()) + "," +
						csv(loan.getCustomer().getCustomerId()) + "," +
						csv(loan.getLoanType().getName()) + "," +
						loan.getPrincipal() + "," + loan.getTotalInterest() + "," +
						loan.getTotalPayable() + "," + loan.getAmountPaid() + "," +
						loan.getOutstandingBalance() + "," + loan.getStatus() + "\n");
			}
		}
	}

	private static String csv(String value) {
		String safe = value == null ? "" : value.replace("\"", "\"\"");
		return "\"" + safe + "\"";
	}
}
