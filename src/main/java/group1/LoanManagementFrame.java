package group1;

import group1.enums.*;
import group1.service.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.io.File;
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
	private static final Color GLASS = new Color(255, 255, 255, 205);
	private static final Color GLASS_BORDER = new Color(255, 255, 255, 190);
	private static final Color TAB_GREEN = new Color(34, 197, 94);
	private static final Color TAB_GREEN_DARK = new Color(21, 128, 61);
	private static final Color GOLD = new Color(212, 175, 55);
	private static final Color GOLD_LIGHT = new Color(255, 215, 100);
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
		setContentPane(createGlassBackground());
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
		UIManager.put("TabbedPane.foreground", TAB_GREEN);
		UIManager.put("Table.foreground", INK);
		UIManager.put("TableHeader.foreground", GOLD_LIGHT);
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
		tabs.setOpaque(false);
		tabs.setBackground(new Color(255, 255, 255, 120));
		tabs.setForeground(TAB_GREEN);
		tabs.setBorder(new EmptyBorder(4, 12, 0, 12));
		addGlassTab(tabs, "Dashboard", createDashboard());
		addGlassTab(tabs, "Customers", createCustomersPanel());
		addGlassTab(tabs, "Loan Types", createLoanTypesPanel());
		addGlassTab(tabs, "Applications", createApplicationsPanel());
		addGlassTab(tabs, "Loans & Repayments", createLoansPanel());
		add(tabs, BorderLayout.CENTER);
		makeTextVisible(this);
		updateAllTables();
	}

	private JPanel createGlassBackground() {
		return new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics graphics) {
				super.paintComponent(graphics);
				Graphics2D g = (Graphics2D) graphics.create();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setPaint(new GradientPaint(0, 0, new Color(222, 245, 232),
						getWidth(), getHeight(), new Color(226, 238, 250)));
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(new Color(74, 222, 128, 28));
				g.fillOval(-150, -180, 500, 500);
				g.setColor(new Color(96, 165, 250, 22));
				g.fillOval(getWidth() - 420, getHeight() - 380, 520, 520);
				g.dispose();
			}
		};
	}

	private void addGlassTab(JTabbedPane tabs, String title, JComponent content) {
		tabs.addTab(title, content);
		int index = tabs.getTabCount() - 1;
		JPanel tabTitle = new JPanel(new BorderLayout());
		tabTitle.setOpaque(false);
		tabTitle.setBorder(new EmptyBorder(6, 10, 6, 10));
		JLabel label = new JLabel(title);
		label.setFont(UI_BOLD);
		label.setForeground(TAB_GREEN_DARK);
		tabTitle.add(label, BorderLayout.CENTER);
		tabs.setTabComponentAt(index, tabTitle);
	}

	private static class GlassPanel extends JPanel {
		private final Color fill;
		private final Color border;

		private GlassPanel(LayoutManager layout, Color fill, Color border) {
			super(layout);
			this.fill = fill;
			this.border = border;
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics.create();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(fill);
			g.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
			g.setColor(border);
			g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
			g.dispose();
		}
	}

	private JPanel createHeader() {
		JPanel header = new GlassPanel(new BorderLayout(),
				new Color(22, 33, 62, 235), new Color(255, 255, 255, 70));
		header.setBorder(new EmptyBorder(18, 26, 18, 26));
		JLabel title = new JLabel("Loan Management System");
		title.setFont(new Font("Segoe UI", Font.BOLD, 22));
		title.setForeground(GOLD_LIGHT);
		JLabel subtitle = new JLabel("NRC 2026-NRC-1  /  Lending Operations");
		subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		subtitle.setForeground(GOLD);
		JPanel labels = new JPanel(new GridLayout(2, 1));
		labels.setOpaque(false);
		labels.add(title);
		labels.add(subtitle);
		header.add(labels, BorderLayout.WEST);
		JButton save = new JButton("Save changes");
		styleButton(save, AMBER);
		save.addActionListener(e -> saveData());
		JButton settings = new JButton("Settings");
		styleButton(settings, BLUE);
		settings.addActionListener(e -> showSettingsDialog());
		JButton export = new JButton("Export CSV");
		styleButton(export, TEAL_DARK);
		export.addActionListener(e -> showExportDialog());
		JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		headerActions.setOpaque(false);
		headerActions.add(settings);
		headerActions.add(export);
		headerActions.add(save);
		header.add(headerActions, BorderLayout.EAST);
		return header;
	}

	private JPanel createDashboard() {
		JPanel panel = new GlassPanel(new BorderLayout(15, 15), GLASS, GLASS_BORDER);
		panel.setBorder(new EmptyBorder(22, 22, 22, 22));
		panel.setOpaque(false);

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
		JPanel panel = new GlassPanel(new BorderLayout(10, 10), GLASS, GLASS_BORDER);
		panel.setBorder(new EmptyBorder(15, 15, 15, 15));
		panel.setOpaque(false);
		customerModel = new DefaultTableModel(
				new Object[]{"Customer ID", "Name", "National ID", "Phone", "Income", "Status"}, 0) {
			public boolean isCellEditable(int r, int c) { return false; }
		};
		JTable table = professionalTable(customerModel);
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(customerModel);
		table.setRowSorter(sorter);
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField search = new JTextField(18);
		search.putClientProperty("JTextField.placeholderText", "Search customers");
		search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(sorter, search.getText()); }
			public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(sorter, search.getText()); }
			public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(sorter, search.getText()); }
		});
		JButton add = new JButton("Register Customer");
		JButton deactivate = new JButton("Deactivate");
		styleButton(add, TEAL_DARK);
		styleButton(deactivate, RED);
		add.addActionListener(e -> showAddCustomerDialog());
		deactivate.addActionListener(e -> deactivateSelected(table, customerManager));
		buttons.add(new JLabel("Search"));
		buttons.add(search);
		buttons.add(add);
		buttons.add(deactivate);
		panel.add(buttons, BorderLayout.NORTH);
		return panel;
	}

	private JPanel createLoanTypesPanel() {
		JPanel panel = new GlassPanel(new BorderLayout(10, 10), GLASS, GLASS_BORDER);
		panel.setBorder(new EmptyBorder(15, 15, 15, 15));
		panel.setOpaque(false);
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
		JPanel panel = new GlassPanel(new BorderLayout(10, 10), GLASS, GLASS_BORDER);
		panel.setBorder(new EmptyBorder(15, 15, 15, 15));
		panel.setOpaque(false);
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
		JPanel panel = new GlassPanel(new BorderLayout(10, 10), GLASS, GLASS_BORDER);
		panel.setBorder(new EmptyBorder(15, 15, 15, 15));
		panel.setOpaque(false);
		loanModel = new DefaultTableModel(
				new Object[]{"Loan Ref", "Customer", "Loan Type", "Principal", "Paid", "Outstanding", "Status"}, 0) {
			public boolean isCellEditable(int r, int c) { return false; }
		};
		JTable loans = professionalTable(loanModel);
		TableRowSorter<DefaultTableModel> loanSorter = new TableRowSorter<>(loanModel);
		loans.setRowSorter(loanSorter);

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
		JTextField search = new JTextField(18);
		search.putClientProperty("JTextField.placeholderText", "Search loans");
		search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(loanSorter, search.getText()); }
			public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(loanSorter, search.getText()); }
			public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(loanSorter, search.getText()); }
		});
		JButton repay = new JButton("Record Repayment");
		styleButton(repay, TEAL_DARK);
		repay.addActionListener(e -> showRepaymentDialog());
		buttons.add(new JLabel("Search"));
		buttons.add(search);
		buttons.add(repay);
		panel.add(buttons, BorderLayout.NORTH);
		return panel;
	}

	private JPanel createMetricCard(JLabel value, String label, Color accent) {
		JPanel card = new GlassPanel(new BorderLayout(),
				new Color(255, 255, 255, 190), new Color(255, 255, 255, 220));
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
		button.setForeground(GOLD_LIGHT);
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setFont(new Font("Segoe UI", Font.BOLD, 13));
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setForeground(GOLD_LIGHT);
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
		table.getTableHeader().setForeground(GOLD_LIGHT);
		table.getTableHeader().setPreferredSize(new Dimension(0, 34));
		return table;
	}

	private void filterTable(TableRowSorter<DefaultTableModel> sorter, String text) {
		if (text == null || text.trim().isEmpty()) {
			sorter.setRowFilter(null);
		} else {
			sorter.setRowFilter(RowFilter.regexFilter("(?i)" +
					java.util.regex.Pattern.quote(text.trim())));
		}
	}

	private void makeTextVisible(Container container) {
		for (Component component : container.getComponents()) {
			if (component instanceof JLabel label) {
				if (label.getForeground().equals(Color.WHITE)
						|| label.getForeground().equals(new Color(120, 130, 126))
						|| label.getForeground().equals(new Color(191, 205, 235))) {
					label.setForeground(GOLD);
				}
			} else if (component instanceof JTextField field) {
				field.setForeground(INK);
				field.setCaretColor(INK);
			} else if (component instanceof JComboBox<?> combo) {
				combo.setForeground(INK);
			} else if (component instanceof JTable table) {
				table.setForeground(INK);
				table.setFont(UI_FONT);
				table.getTableHeader().setForeground(GOLD_LIGHT);
				table.getTableHeader().setBackground(INK);
			} else if (component instanceof JTabbedPane tabs) {
				tabs.setForeground(TAB_GREEN);
				tabs.setFont(UI_BOLD);
				tabs.setOpaque(false);
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
			SystemSettings saved = state.getSettings();
			if (saved != null) {
				SystemSettings current = loanManager.getSettings();
				current.setAffordabilityPercentage(saved.getAffordabilityPercentage());
				current.setMaximumActiveLoans(saved.getMaximumActiveLoans());
				current.setLateFeePercentage(saved.getLateFeePercentage());
				current.setLateFeesEnabled(saved.isLateFeesEnabled());
			}
		} catch (Exception e) {
			seedData();
		}
	}

	private void saveData() {
		try {
			persistence.save(customerManager.listCustomers(), loanTypeManager.listLoanTypes(),
					loanManager.getApplications(), loanManager.getLoans(),
					paymentManager.getRepayments(), loanManager.getSettings());
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
					loan.getLoanId(), loan.getCustomer().getFirstName() + " "
							+ loan.getCustomer().getLastName(),
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
					"Loan approved for " + loan.getCustomer().getFirstName() + " "
							+ loan.getCustomer().getLastName() + ".\nLoan reference: "
							+ loan.getLoanId());
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
		List<Loan> availableLoans = loanManager.getLoans().stream()
				.filter(loan -> loan.getOutstandingBalance() > 0)
				.toList();
		if (availableLoans.isEmpty()) {
			JOptionPane.showMessageDialog(this, "There are no loans with an outstanding balance.");
			return;
		}
		JComboBox<String> loanChoice = new JComboBox<>(availableLoans.stream()
				.map(loan -> loan.getCustomer().getFirstName() + " "
						+ loan.getCustomer().getLastName() + " | "
						+ loan.getLoanType().getName() + " | MWK "
						+ String.format("%.2f", loan.getOutstandingBalance())
						+ " outstanding | Ref " + loan.getLoanId())
				.toArray(String[]::new));
		JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
		addField(form, "Choose borrower and loan", loanChoice);
		JTextField amount = new JTextField();
		JTextField reference = new JTextField();
		JComboBox<PaymentMethod> method = new JComboBox<>(PaymentMethod.values());
		JLabel outstanding = new JLabel();
		loanChoice.addActionListener(e -> outstanding.setText(String.format("MWK %.2f",
				availableLoans.get(loanChoice.getSelectedIndex()).getOutstandingBalance())));
		addField(form, "Amount", amount);
		addField(form, "Outstanding balance", outstanding);
		addField(form, "Method", method);
		addField(form, "Reference", reference);
		outstanding.setText(String.format("MWK %.2f",
				availableLoans.get(loanChoice.getSelectedIndex()).getOutstandingBalance()));
		if (JOptionPane.showConfirmDialog(this, form, "Record Repayment",
				JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
		Loan loan = availableLoans.get(loanChoice.getSelectedIndex());
		try {
			Repayment payment = paymentManager.recordRepayment(loan.getLoanId(),
					Double.parseDouble(amount.getText()),
					(PaymentMethod) method.getSelectedItem(), reference.getText());
			updateAllTables();
			updateSchedule(loan.getLoanId());
			JOptionPane.showMessageDialog(this,
					"Payment " + payment.getPaymentId() + " recorded for "
							+ loan.getCustomer().getFirstName() + " "
							+ loan.getCustomer().getLastName() + ".");
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
				builder.append(loan.getCustomer().getFirstName()).append(" ")
						.append(loan.getCustomer().getLastName()).append(" | ")
						.append(loan.getLoanType().getName()).append(" | MWK ")
						.append(String.format("%.2f", loan.getOutstandingBalance()))
						.append(" outstanding | Ref ").append(loan.getLoanId())
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

		private void showSettingsDialog() {
			SystemSettings settings = loanManager.getSettings();
			JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
			JTextField affordability = new JTextField(String.valueOf(settings.getAffordabilityPercentage()));
			JTextField maximumLoans = new JTextField(String.valueOf(settings.getMaximumActiveLoans()));
			JTextField lateFee = new JTextField(String.valueOf(settings.getLateFeePercentage()));
			JCheckBox enableLateFees = new JCheckBox("Enable late fees",
					settings.isLateFeesEnabled());
			addField(form, "Affordability ratio", affordability);
			addField(form, "Maximum active loans", maximumLoans);
			addField(form, "Late fee rate", lateFee);
			addField(form, "Late fees", enableLateFees);
			if (JOptionPane.showConfirmDialog(this, form, "System Settings",
					JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				try {
					settings.setAffordabilityPercentage(Double.parseDouble(affordability.getText()));
					settings.setMaximumActiveLoans(Integer.parseInt(maximumLoans.getText()));
					settings.setLateFeePercentage(Double.parseDouble(lateFee.getText()));
					settings.setLateFeesEnabled(enableLateFees.isSelected());
					JOptionPane.showMessageDialog(this, "Settings updated.");
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, e.getMessage(), "Settings Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		private void showExportDialog() {
			String[] options = {"Customers - CSV", "Loans - CSV", "Customers - PDF", "Loans - PDF"};
			int choice = JOptionPane.showOptionDialog(this, "Choose a report to export",
					"Export CSV", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, options, options[0]);
			if (choice < 0) return;
			JFileChooser chooser = new JFileChooser();
			String fileName = switch (choice) {
				case 1 -> "loans.csv";
				case 2 -> "customers.pdf";
				case 3 -> "loans.pdf";
				default -> "customers.csv";
			};
			chooser.setSelectedFile(new File(fileName));
			if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
			try {
				if (choice == 0) {
					new CsvReportExporter().exportCustomers(chooser.getSelectedFile().toPath(),
							customerManager.listCustomers());
				} else if (choice == 1) {
					new CsvReportExporter().exportLoans(chooser.getSelectedFile().toPath(),
							loanManager.getLoans());
				} else if (choice == 2) {
					new PdfReportExporter().exportCustomers(chooser.getSelectedFile().getAbsolutePath(),
							customerManager.listCustomers());
				} else {
					new PdfReportExporter().exportLoans(chooser.getSelectedFile().getAbsolutePath(),
							loanManager.getLoans());
				}
				JOptionPane.showMessageDialog(this, "Report exported.");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Export Error",
						JOptionPane.ERROR_MESSAGE);
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
