package group1.service;

import group1.Customer;
import group1.exceptions.CustomerNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CustomerManager {
	private final List<Customer> customers = new ArrayList<>();

	public void addCustomer(Customer customer) {
		if (customer == null) {
			throw new IllegalArgumentException("Customer cannot be null");
		}
		if (findOptional(customer.getCustomerId()) != null) {
			throw new IllegalArgumentException("Customer ID already exists");
		}
		customers.add(customer);
	}

	public Customer findCustomer(String customerId) throws CustomerNotFoundException {
		Customer customer = findOptional(customerId);
		if (customer == null) {
			throw new CustomerNotFoundException("Customer not found: " + customerId);
		}
		return customer;
	}

	private Customer findOptional(String customerId) {
		if (customerId == null) {
			return null;
		}
		for (Customer customer : customers) {
			if (customer.getCustomerId().equalsIgnoreCase(customerId.trim())) {
				return customer;
			}
		}
		return null;
	}

	public List<Customer> listCustomers() {
		return new ArrayList<>(customers);
	}

	public void loadCustomers(List<Customer> savedCustomers) {
		customers.clear();
		if (savedCustomers != null) {
			customers.addAll(savedCustomers);
		}
	}

	public boolean removeCustomer(String customerId) throws CustomerNotFoundException {
		Customer customer = findCustomer(customerId);
		customer.setCustomerStatus("INACTIVE");
		return true;
	}
}
