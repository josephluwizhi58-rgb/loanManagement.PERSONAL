package group1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {
	private static final long serialVersionUID = 1L;
	private String customerid;
	private String firstname;
	private String lastname;
	private String nationalid;
	private String emailaddress;
	private String physicaladdress;
	private String phonenumber;
	private double monthlyIncome;
	private String employmentStatus;
	private String customerStatus;
	private final List<Loan> loans = new ArrayList<>();

	public Customer(String customerid, String firstname, String lastname, String nationalid,
			String emailaddress, String physicaladdress,String phonenumber, double monthlyIncome) {
		this(customerid, firstname, lastname, nationalid, emailaddress, physicaladdress,
				null, phonenumber, monthlyIncome, null);
	}

	public Customer(String customerid, String firstname, String lastname, String nationalid,
			String emailaddress, String physicaladdress, String employmentStatus,
			String phonenumber, double monthlyIncome, String customerStatus) {
		this.customerid = requireText(customerid, "Customer ID");
		this.firstname = requireText(firstname, "First name");
		this.lastname = requireText(lastname, "Last name");
		this.nationalid = requireText(nationalid, "National ID");
		this.emailaddress = requireText(emailaddress, "Email address");
		this.physicaladdress = requireText(physicaladdress, "Physical address");
		this.phonenumber = requireText(phonenumber, "Phone number");
		this.employmentStatus = employmentStatus == null ? "Unknown" : employmentStatus.trim();
		this.customerStatus = customerStatus == null ? "Active" : customerStatus.trim();
		setMonthlyIncome(monthlyIncome);
		}

	private static String requireText(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(fieldName + " cannot be empty");
		}
		return value.trim();
	}
		
		public String getCustomerId() {
			return customerid;
		}
		
		public String getFirstName() {
			return firstname;
		}
		
		public String getLastName() {
			return lastname;
		}
		
		public String getPhoneNumber() {
			return phonenumber;
		}
		
		public double getMonthlyIncome() {
			return monthlyIncome;
		}
		
		public String getEmailAddress() {
			return emailaddress;
		} 
		
		public String getNationalId() {
			return nationalid;
		}
		
		public String getPhysicalAddress() {
			return physicaladdress;
		}

		public void setMonthlyIncome(double monthlyIncome) {
			if (monthlyIncome < 0) {
				throw new IllegalArgumentException("Monthly income cannot be negative");
			}
			this.monthlyIncome = monthlyIncome;
		}

	public String getEmploymentStatus() {
		return employmentStatus;
	}

	public String getCustomerStatus() {
		return customerStatus;
	}

	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = requireText(employmentStatus, "Employment status");
	}

	public void setCustomerStatus(String customerStatus) {
		this.customerStatus = requireText(customerStatus, "Customer status");
	}

	public void addLoan(Loan loan) {
		if (loan != null && !loans.contains(loan)) {
			loans.add(loan);
		}
	}

	public List<Loan> getLoans() {
		return new ArrayList<>(loans);
	}
		
		}

		
