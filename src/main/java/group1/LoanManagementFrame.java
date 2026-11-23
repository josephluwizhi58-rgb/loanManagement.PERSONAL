package group1;

import group1.enums.*;
import group1.service.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LoanManagementFrame extends JFrame {
	private static final Color INK = new Color(22, 33, 62);
	private static final Color TEAL = new Color(13, 148, 136);
	private static final Color TEAL_DARK = new Color(15, 118, 110);
	private static final Color BLUE = new Color(37, 99, 235);
	private static final Color AMBER = new Color(245, 158, 11);
	private static final Color GREEN = new Color(22, 163, 74);
	private static final Color RED = new Color(220, 38, 38);
	private static final Color BACKGROUND = new Color(244, 247, 252);
	private static final Color SURFACE = Color.WHITE;
	private static final Font UI_FONT = new Font("Segoe UI", Font.PLAIN, 14);
	private static final Font UI_BOLD = new Font("Segoe UI", Font.BOLD, 14);

	private final CustomerManager customerManager = new CustomerManager();
	private final LoanTypeManager loanTypeManager = new LoanTypeManager();
	private final LoanManager loanManager = new LoanManager(customerManager, loanTypeManager);
	private final PaymentManager paymentManager = new PaymentManager(loanManager);
	private final FilePersistenceManager persistence =
			new FilePersistenceManager("loan-management.dat");

	private final JLabel customersCard = metricCard();
	private final JLabel activeCard = metricCard();
	private final JLabel lentCard = metricCard();
	private final JLabel outstandingCard = metricCard();
	private DefaultTableModel customerModel;
	private DefaultTableModel loanTypeModel;
	private DefaultTableModel applicationModel;
	private DefaultTableModel loanModel;
	private DefaultTableModel scheduleModel;

	public LoanManagementFrame() {
		super("Natural Resources College - Loan Management");
		loadData();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1100, 720));
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		getContentPane().setBackground(BACKGROUND);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) {}
		UIManager.put("Button.font", UI_BOLD);
		UIManager.put("Label.font", UI_FONT);
		UIManager.put("TextField.font", UI_FONT);
		UIManager.put("ComboBox.font", UI_FONT);
		UIManager.put("Panel.background", BACKGROUND);
		UIManager.put("Label.foreground", INK);
		UIManager.put("Button.foreground", INK);
		UIManager.put("TabbedPane.foreground", INK);
		UIManager.put("Table.foreground", INK);
		UIManager.put("TableHeader.foreground", Color.WHITE);
		UIManager.put("TableHeader.background", INK);
		UIManager.put("TextField.foreground", INK);
		UIManager.put("TextArea.foreground", INK);
		UIManager.put("ComboBox.foreground", INK);
		UIManager.put("TabbedPane.selected", SURFACE);
		UIManager.put("TabbedPane.contentAreaColor", BACKGROUND);
		UIManager.put("TabbedPane.focus", new Color(0, 0, 0, 0));

		add(createHeader(), BorderLayout.NORTH);
		JTabbedPane tabs = new JTabbedPane();
		tabs.setFont(UI_BOLD);
		tabs.setBackground(SURFACE);
		tabs.setForeground(INK);
		tabs.setBorder(new EmptyBorder(4, 12, 0, 12));
		tabs.addTab("Dashboard", createDashboard());
		tabs.addTab("Customers", createCustomersPanel());
		tabs.addTab("Loan Types", createLoanTypesPanel());
		tabs.addTab("Applications", createApplicationsPanel());
		tabs.addTab("Loans & Repayments", createLoansPanel());
		add(tabs, BorderLayout.CENTER);
		makeTextVisible(this);
		updateAllTables();
	}

	private JPanel createHeader() {
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(INK);
		header.setBorder(new EmptyBorder(18, 26, 18, 26));
		JLabel title = new JLabel("Loan Management System");
		title.setFont(new Font("Segoe UI", Font.BOLD, 22));
		title.setForeground(Color.WHITE);
		JLabel subtitle = new JLabel("NRC 2026-NRC-1  /  Lending Operations");
		subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		subtitle.setForeground(new Color(191, 205, 235));
		JPanel labels = new JPanel(new GridLayout(2, 1));
		labels.setOpaque(false);
		labels.add(title);
		labels.add(subtitle);
		header.add(labels, BorderLayout.WEST);
		JButton save = new JButton("Save changes");
		styleButton(save, AMBER);
		save.addActionListener(e -> saveData());
		header.add(save, BorderLayout.EAST);
		return header;
	}

	private JPanel createDashboard() {
		JPanel panel = new JPanel(new BorderLayout(15, 15));
		panel.setBorder(new EmptyBorder(22, 22, 22, 22));
		panel.setBackground(BACKGROUND);

		JPanel cards = new JPanel(new GridLayout(1, 4, 15, 15));
		cards.setOpaque(false);
		cards.add(createMetricCard(customersCard, "CUSTOMERS", BLUE));
		cards.add(createMetricCard(activeCard, "ACTIVE LOANS", GREEN));
		cards.add(createMetricCard(lentCard, "TOTAL LENT", TEAL));
		cards.add(createMetricCard(outstandingCard, "OUTSTANDING", AMBER));
		panel.add(cards, BorderLayout.NORTH);

		JPanel actions = new JPanel(new GridLayout(2, 3, 12, 12));
		actions.setBorder(new EmptyBorder(32, 60, 32, 60));
		actions.setOpaque(false);
		actions.add(actionButton("Register Customer", e -> showAddCustomerDialog()));
		actions.add(actionButton("Apply for Loan", e -> showApplyDialog()));
		actions.add(actionButton("Approve Application", e -> showApproveDialog()));
		actions.add(actionButton("Record Repayment", e -> showRepaymentDialog()));
		actions.add(actionButton("View Overdue", e -> showOverdueDialog()));
		actions.add(actionButton("Refresh Dashboard", e -> updateAllTables()));
		panel.add(actions, BorderLayout.CENTER);
		return panel;
	}

	private JPanel createCustomersPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(new EmptyBorder(15, 15, 15, 15));
		panel.setBackground(BACKGROUND);
		customerModel = new DefaultTableModel(
				new Object[]{"Customer ID", "Name", "National ID", "Phone", "Income", "Status"}, 0) {
			public boolean isCellEditable(int r, int c) { return false; }
		};
		JTable table = professionalTable(customerModel);
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton add = new JButton("Register Customer");
		JButton deactivate = new JButton("Deactivate");
		styleButton(add, TEAL_DARK);
		styleButton(deactivate, RED);
		add.addActionListener(e -> showAddCustomerDialog());
		deactivate.addActionListener(e -> deactivateSelected(table, customerManager));
		buttons.add(add);
		buttons.add(deactivate);
		panel.add(buttons, BorderLayout.NORTH);
		return panel;
	}

	private JPanel createLoanTypesPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(new EmptyBorder(15, 15, 15, 15));
		panel.setBackground(BACKGROUND);
		loanTypeModel = new DefaultTableModel(
				new Object[]{"Type ID", "Name", "Minimum", "Maximum", "Rate", "Period", "Status"}, 0) {
			public boolean isCellEditable(int r, int c) { return false; }
		};
		JTable table = professionalTable(loanTypeModel);
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		JButton create = new JButton("Create Loan Type");
		styleButton(create, TEAL_DARK);
		create.addActionListener(e -> showCreateLoanTypeDialog());
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttons.add(create);
		panel.add(buttons, BorderLayout.NORTH);
		return panel;
	}

	private JPanel createApplicationsPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(new EmptyBorder(15, 15, 15, 15));
		panel.setBackground(BACKGROUND);
		applicationModel = new DefaultTableModel(
				new Object[]{"Application", "Customer", "Loan Type", "Amount", "Period", "Status"}, 0) {
			public boolean isCellEditable(int r, int c) { return false; }
		};
		JTable table = professionalTable(applicationModel);
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton apply = new JButton("New Application");
		JButton approve = new JButton("Approve");
		JButton reject = new JButton("Reject");
		styleButton(apply, TEAL_DARK);
		styleButton(approve, GREEN);
		styleButton(reject, RED);
		apply.addActionListener(e -> showApplyDialog());
		approve.addActionListener(e -> showApproveDialog());
		reject.addActionListener(e -> rejectSelected(table));
		buttons.add(apply);
		buttons.add(approve);
		buttons.add(reject);
		panel.add(buttons, BorderLayout.NORTH);
		return panel;
	}

	private JPanel createLoansPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(new EmptyBorder(15, 15, 15, 15));
		panel.setBackground(BACKGROUND);
		loanModel = new DefaultTableModel(
				new Object[]{"Loan ID", "Customer", "Type", "Principal", "Paid", "Outstanding", "Status"}, 0) {
			public boolean isCellEditable(int r, int c) { return false; }
		};
		JTable loans = professionalTable(loanModel);

		scheduleModel = new DefaultTableModel(
				new Object[]{"#", "Due Date", "Amount Due", "Paid", "Balance", "Status"}, 0) {
			public boolean isCellEditable(int r, int c) { return false; }
		};
		JTable schedule = professionalTable(scheduleModel);
		loans.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && loans.getSelectedRow() >= 0) {
				String loanId = String.valueOf(loans.getValueAt(loans.getSelectedRow(), 0));
				updateSchedule(loanId);
			}
		});

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				new JScrollPane(loans), new JScrollPane(schedule));
		split.setResizeWeight(0.58);
		panel.add(split, BorderLayout.CENTER);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton repay = new JButton("Record Repayment");
		styleButton(repay, TEAL_DARK);
		repay.addActionListener(e -> showRepaymentDialog());
		buttons.add(repay);
		panel.add(buttons, BorderLayout.NORTH);
		return panel;
	}

	private JPanel createMetricCard(JLabel value, String label, Color accent) {
		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(SURFACE);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 5, 0, 0, accent),
				new EmptyBorder(18, 18, 18, 18)));
		value.setFont(new Font("Segoe UI", Font.BOLD, 22));
		value.setForeground(INK);
		JLabel title = new JLabel(label);
		title.setFont(new Font("Segoe UI", Font.BOLD, 11));
		title.setForeground(accent);
		card.add(value, BorderLayout.CENTER);
		card.add(title, BorderLayout.SOUTH);
		return card;
	}

	private JLabel metricCard() {
		return new JLabel("0");
	}

	private JButton actionButton(String text, java.awt.event.ActionListener listener) {
		JButton button = new JButton(text);
		styleButton(button, TEAL_DARK);
		button.setFont(new Font("Segoe UI", Font.BOLD, 15));
		button.setPreferredSize(new Dimension(180, 58));
		button.addActionListener(listener);
		return button;
	}

	private void styleButton(JButton button, Color color) {
		button.setBackground(color);
		button.setForeground(Color.WHITE);
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setFont(new Font("Segoe UI", Font.BOLD, 13));
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setForeground(Color.WHITE);
	}

	private JTable professionalTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setRowHeight(30);
		table.setFont(UI_FONT);
		table.setGridColor(new Color(226, 232, 240));
		table.setSelectionBackground(new Color(204, 251, 241));
		table.setSelectionForeground(INK);
		table.setIntercellSpacing(new Dimension(0, 1));
		table.setShowVerticalLines(false);
		table.getTableHeader().setFont(UI_BOLD);
		table.getTableHeader().setBackground(INK);
		table.getTableHeader().setForeground(Color.WHITE);
		table.getTableHeader().setPreferredSize(new Dimension(0, 34));
		return table;
	}

	private void makeTextVisible(Container container) {
		for (Component component : container.getComponents()) {
			if (component instanceof JLabel label) {
				if (label.getForeground().equals(new Color(120, 130, 126))
						|| label.getForeground().equals(new Color(191, 205, 235))) {
					label.setForeground(INK);
				}
			} else if (component instanceof JTextField field) {
				field.setForeground(INK);
				field.setCaretColor(INK);
			} else if (component instanceof JComboBox<?> combo) {
				combo.setForeground(INK);
			} else if (component instanceof JTable table) {
				table.setForeground(INK);
				table.setFont(UI_FONT);
				table.getTableHeader().setForeground(Color.WHITE);
				table.getTableHeader().setBackground(INK);
			} else if (component instanceof JTabbedPane tabs) {
				tabs.setForeground(INK);
				tabs.setFont(UI_BOLD);
			}
			if (component instanceof Container child) {
				makeTextVisible(child);
			}
		}
	}

	private void seedData() {
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

	private void loadData() {
		try {
			FilePersistenceManager.PersistentState state = persistence.load();
			if (state == null) {
				seedData();
				return;
			}
			customerManager.loadCustomers(state.getCustomers());
			loanTypeManager.loadLoanTypes(state.getLoanTypes());
			loanManager.loadState(state.getApplications(), state.getLoans());
			paymentManager.loadRepayments(state.getRepayments());
		} catch (Exception e) {
			seedData();
		}
	}

	private void saveData() {
		try {
			persistence.save(customerManager.listCustomers(), loanTypeManager.listLoanTypes(),
					loanManager.getApplications(), loanManager.getLoans(),
					paymentManager.getRepayments());
			JOptionPane.showMessageDialog(this, "Data saved successfully.", "Saved",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Save Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateAllTables() {
		if (customerModel == null) return;
		customerModel.setRowCount(0);
		for (Customer customer : customerManager.listCustomers()) {
			customerModel.addRow(new Object[]{
					customer.getCustomerId(),
					customer.getFirstName() + " " + customer.getLastName(),
					customer.getNationalId(), customer.getPhoneNumber(),
					String.format("MWK %.2f", customer.getMonthlyIncome()),
					customer.getCustomerStatus()
			});
		}
		loanTypeModel.setRowCount(0);
		for (LoanType type : loanTypeManager.listLoanTypes()) {
			loanTypeModel.addRow(new Object[]{
					type.getLoanTypeId(), type.getName(),
					String.format("MWK %.2f", type.getMinimumAmount()),
					String.format("MWK %.2f", type.getMaximumAmount()),
					type.getInterestRate() + "%",
					type.getMinimumRepaymentPeriod() + "-" + type.getMaximumRepaymentPeriod(),
					type.isActive() ? "ACTIVE" : "INACTIVE"
			});
		}
		applicationModel.setRowCount(0);
		for (LoanApplication application : loanManager.getApplications()) {
			applicationModel.addRow(new Object[]{
					application.getApplicationId(), application.getCustomer().getCustomerId(),
					application.getLoanType().getName(),
					String.format("MWK %.2f", application.getRequestedAmount()),
					application.getRequestedRepaymentPeriod(), application.getStatus()
			});
		}
		loanModel.setRowCount(0);
		double lent = 0;
		double outstanding = 0;
		int active = 0;
		for (Loan loan : loanManager.getLoans()) {
			lent += loan.getPrincipal();
			outstanding += loan.getOutstandingBalance();
			if (loan.getStatus() == LoanStatus.ACTIVE) active++;
			loanModel.addRow(new Object[]{
					loan.getLoanId(), loan.getCustomer().getCustomerId(),
					loan.getLoanType().getName(),
					String.format("MWK %.2f", loan.getPrincipal()),
					String.format("MWK %.2f", loan.getAmountPaid()),
					String.format("MWK %.2f", loan.getOutstandingBalance()),
					loan.getStatus()
			});
		}
		customersCard.setText(String.valueOf(customerManager.listCustomers().size()));
		activeCard.setText(String.valueOf(active));
		lentCard.setText(String.format("MWK %.2f", lent));
		outstandingCard.setText(String.format("MWK %.2f", outstanding));
	}

	private void updateSchedule(String loanId) {
		scheduleModel.setRowCount(0);
		try {
			Loan loan = loanManager.findLoan(loanId);
			for (RepaymentSchedule entry : loan.getSchedule()) {
				scheduleModel.addRow(new Object[]{
						entry.getInstallmentNumber(), entry.getDueDate(),
						String.format("MWK %.2f", entry.getAmountDue()),
						String.format("MWK %.2f", entry.getAmountPaid()),
						String.format("MWK %.2f", entry.getRemainingBalance()),
						entry.getStatus()
				});
			}
		} catch (Exception ignored) {
		}
	}

	private void showAddCustomerDialog() {
		JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
		JTextField id = new JTextField();
		JTextField first = new JTextField();
		JTextField last = new JTextField();
		JTextField national = new JTextField();
		JTextField email = new JTextField();
		JTextField address = new JTextField();
		JTextField employment = new JTextField();
		JTextField phone = new JTextField();
		JTextField income = new JTextField();
		addField(form, "Customer ID", id);
		addField(form, "First name", first);
		addField(form, "Last name", last);
		addField(form, "National ID", national);
		addField(form, "Email", email);
		addField(form, "Physical address", address);
		addField(form, "Employment", employment);
		addField(form, "Phone", phone);
		addField(form, "Monthly income", income);
		if (JOptionPane.showConfirmDialog(this, form, "Register Customer",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			try {
				if (!phone.getText().matches("[0-9+() -]{7,20}")) {
					throw new IllegalArgumentException("Invalid phone number");
				}
				customerManager.addCustomer(new Customer(id.getText(), first.getText(),
						last.getText(), national.getText(), email.getText(), address.getText(),
						employment.getText(), phone.getText(),
						Double.parseDouble(income.getText()), "Active"));
				updateAllTables();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void addField(JPanel form, String label, JComponent field) {
		form.add(new JLabel(label));
		form.add(field);
	}

	private void showApplyDialog() {
		if (customerManager.listCustomers().isEmpty() || loanTypeManager.listActiveLoanTypes().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Add a customer and active loan type first.");
			return;
		}
		JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
		JTextField customerId = new JTextField();
		JTextField loanTypeId = new JTextField();
		JTextField amount = new JTextField();
		JTextField period = new JTextField();
		JTextField purpose = new JTextField();
		addField(form, "Customer ID", customerId);
		addField(form, "Loan type ID", loanTypeId);
		addField(form, "Requested amount", amount);
		addField(form, "Period (months)", period);
		addField(form, "Purpose", purpose);
		if (JOptionPane.showConfirmDialog(this, form, "Apply for Loan",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			try {
				LoanApplication application = loanManager.applyForLoan(
						customerId.getText(), loanTypeId.getText(),
						Double.parseDouble(amount.getText()),
						Integer.parseInt(period.getText()), purpose.getText());
				updateAllTables();
				JOptionPane.showMessageDialog(this,
						"Application " + application.getApplicationId() + " created.");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Application Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void showApproveDialog() {
		String id = JOptionPane.showInputDialog(this, "Application ID:");
		if (id == null || id.trim().isEmpty()) return;
		try {
			Loan loan = loanManager.approveLoan(id.trim());
			updateAllTables();
			JOptionPane.showMessageDialog(this,
					"Loan " + loan.getLoanId() + " approved.");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Approval Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void rejectSelected(JTable table) {
		if (table.getSelectedRow() < 0) {
			JOptionPane.showMessageDialog(this, "Select an application first.");
			return;
		}
		String id = String.valueOf(table.getValueAt(table.getSelectedRow(), 0));
		try {
			loanManager.rejectLoan(id);
			updateAllTables();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showRepaymentDialog() {
		String loanId = JOptionPane.showInputDialog(this, "Loan ID:");
		if (loanId == null || loanId.trim().isEmpty()) return;
		try {
			Loan loan = loanManager.findLoan(loanId.trim());
			JTextField amount = new JTextField();
			JTextField reference = new JTextField();
			JComboBox<PaymentMethod> method = new JComboBox<>(PaymentMethod.values());
			JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
			addField(form, "Outstanding balance",
					new JLabel(String.format("MWK %.2f", loan.getOutstandingBalance())));
			addField(form, "Amount", amount);
			addField(form, "Method", method);
			addField(form, "Reference", reference);
			if (JOptionPane.showConfirmDialog(this, form, "Record Repayment",
					JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				Repayment payment = paymentManager.recordRepayment(loanId.trim(),
						Double.parseDouble(amount.getText()),
						(PaymentMethod) method.getSelectedItem(), reference.getText());
				updateAllTables();
				updateSchedule(loanId.trim());
				JOptionPane.showMessageDialog(this,
						"Payment " + payment.getPaymentId() + " recorded.");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Repayment Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void showOverdueDialog() {
		List<Loan> overdue = loanManager.getOverdueLoans();
		String message;
		if (overdue.isEmpty()) {
			message = "No overdue loans.";
		} else {
			StringBuilder builder = new StringBuilder();
			for (Loan loan : overdue) {
				builder.append(loan.getLoanId()).append(" - ")
						.append(loan.getCustomer().getCustomerId()).append(" - MWK ")
						.append(String.format("%.2f", loan.getOutstandingBalance()))
						.append("\n");
			}
			message = builder.toString();
		}
		JOptionPane.showMessageDialog(this, message, "Overdue Loans",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void showCreateLoanTypeDialog() {
		JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
		JTextField id = new JTextField();
		JTextField name = new JTextField();
		JTextField minimum = new JTextField();
		JTextField maximum = new JTextField();
		JTextField rate = new JTextField();
		JTextField minPeriod = new JTextField();
		JTextField maxPeriod = new JTextField();
		JTextField description = new JTextField();
		addField(form, "Type ID", id);
		addField(form, "Name", name);
		addField(form, "Minimum amount", minimum);
		addField(form, "Maximum amount", maximum);
		addField(form, "Interest rate", rate);
		addField(form, "Minimum period", minPeriod);
		addField(form, "Maximum period", maxPeriod);
		addField(form, "Description", description);
		if (JOptionPane.showConfirmDialog(this, form, "Create Loan Type",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			try {
				loanTypeManager.addLoanType(new LoanType(id.getText(), name.getText(),
						Double.parseDouble(minimum.getText()), Double.parseDouble(maximum.getText()),
						Double.parseDouble(rate.getText()), Integer.parseInt(minPeriod.getText()),
						Integer.parseInt(maxPeriod.getText()), description.getText()));
				updateAllTables();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void deactivateSelected(JTable table, CustomerManager manager) {
		if (table.getSelectedRow() < 0) {
			JOptionPane.showMessageDialog(this, "Select a customer first.");
			return;
		}
		String id = String.valueOf(table.getValueAt(table.getSelectedRow(), 0));
		try {
			manager.removeCustomer(id);
			updateAllTables();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
