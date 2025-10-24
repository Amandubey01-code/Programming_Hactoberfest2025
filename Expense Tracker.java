import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

class Expense {
    String date, category, description;
    double amount;

    public Expense(String date, String category, String description, double amount) {
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return date + "," + category + "," + description + "," + amount;
    }
}

public class ExpenseTracker extends JFrame {
    private ArrayList<Expense> expenses;
    private DefaultTableModel model;
    private JTable table;
    private JTextField dateField, categoryField, descField, amountField;

    private final String FILE_NAME = "expenses.txt";

    public ExpenseTracker() {
        setTitle("💰 Expense Tracker");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        expenses = new ArrayList<>();
        loadExpenses();

        // Top panel for adding expense
        JPanel inputPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        dateField = new JTextField();
        categoryField = new JTextField();
        descField = new JTextField();
        amountField = new JTextField();
        JButton addButton = new JButton("Add Expense");

        inputPanel.add(new JLabel("Date (dd-mm-yyyy)"));
        inputPanel.add(new JLabel("Category"));
        inputPanel.add(new JLabel("Description"));
        inputPanel.add(new JLabel("Amount"));
        inputPanel.add(new JLabel("Action"));

        inputPanel.add(dateField);
        inputPanel.add(categoryField);
        inputPanel.add(descField);
        inputPanel.add(amountField);
        inputPanel.add(addButton);

        add(inputPanel, BorderLayout.NORTH);

        // Table for displaying expenses
        String[] columns = {"Date", "Category", "Description", "Amount"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Delete button
        JButton deleteButton = new JButton("Delete Selected");
        add(deleteButton, BorderLayout.SOUTH);

        refreshTable();

        // Add expense action
        addButton.addActionListener(e -> {
            String date = dateField.getText();
            String category = categoryField.getText();
            String desc = descField.getText();
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount!");
                return;
            }

            Expense expense = new Expense(date, category, desc, amount);
            expenses.add(expense);
            saveExpenses();
            refreshTable();

            dateField.setText("");
            categoryField.setText("");
            descField.setText("");
            amountField.setText("");
        });

        // Delete expense action
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                expenses.remove(selectedRow);
                saveExpenses();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Select a row to delete!");
            }
        });

        setVisible(true);
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Expense e : expenses) {
            model.addRow(new Object[]{e.date, e.category, e.description, e.amount});
        }
    }

    private void saveExpenses() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Expense e : expenses) {
                pw.println(e.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadExpenses() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    expenses.add(new Expense(parts[0], parts[1], parts[2], Double.parseDouble(parts[3])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseTracker::new);
    }
}
