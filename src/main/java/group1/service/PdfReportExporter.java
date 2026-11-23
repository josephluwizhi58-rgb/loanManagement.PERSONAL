package group1.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import group1.Customer;
import group1.Loan;
import java.io.FileOutputStream;
import java.util.List;

public class PdfReportExporter {
	public void exportCustomers(String fileName, List<Customer> customers) throws Exception {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(fileName));
		document.open();
		document.add(new Paragraph("Loan Management - Customer Report"));
		PdfPTable table = new PdfPTable(5);
		table.addCell("Customer ID");
		table.addCell("Name");
		table.addCell("Phone");
		table.addCell("Income");
		table.addCell("Status");
		for (Customer customer : customers) {
			table.addCell(customer.getCustomerId());
			table.addCell(customer.getFirstName() + " " + customer.getLastName());
			table.addCell(customer.getPhoneNumber());
			table.addCell(String.format("MWK %.2f", customer.getMonthlyIncome()));
			table.addCell(customer.getCustomerStatus());
		}
		document.add(table);
		document.close();
	}

	public void exportLoans(String fileName, List<Loan> loans) throws Exception {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(fileName));
		document.open();
		document.add(new Paragraph("Loan Management - Loan Report"));
		PdfPTable table = new PdfPTable(6);
		table.addCell("Loan ID");
		table.addCell("Customer");
		table.addCell("Type");
		table.addCell("Paid");
		table.addCell("Outstanding");
		table.addCell("Status");
		for (Loan loan : loans) {
			table.addCell(loan.getLoanId());
			table.addCell(loan.getCustomer().getCustomerId());
			table.addCell(loan.getLoanType().getName());
			table.addCell(String.format("MWK %.2f", loan.getAmountPaid()));
			table.addCell(String.format("MWK %.2f", loan.getOutstandingBalance()));
			table.addCell(loan.getStatus().name());
		}
		document.add(table);
		document.close();
	}
}
