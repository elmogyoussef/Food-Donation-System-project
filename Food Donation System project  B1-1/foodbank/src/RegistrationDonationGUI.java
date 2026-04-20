import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class RegistrationDonationGUI extends JFrame {
    private SystemManager manager;
    private Provider registeredProvider;

    private JTextField nameField = new JTextField(20);
    private JTextField emailField = new JTextField(20);
    private JTextField phoneField = new JTextField(15);
    private JPasswordField passwordField = new JPasswordField(15);
    private JTextField locationField = new JTextField(20);
    private JComboBox<String> providerType = new JComboBox<>(new String[]{"Family", "Bakery", "Hotel", "SuperMarket", "Restaurant"});

    private JTextField itemNameField = new JTextField(20);
    private JTextField itemQtyField = new JTextField(5);
    private JTextField itemExpiryField = new JTextField(10); // yyyy-MM-dd
    private JComboBox<String> itemUnit = new JComboBox<>(new String[]{"pcs", "kg", "lbs", "liters", "boxes"});

    private JButton donateButton = new JButton("Donate & Request Pickup");

    public RegistrationDonationGUI(SystemManager manager) {
        super("Provider — Register & Donate");
        this.manager = manager;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 420);
        setLocationByPlatform(true);
        setLayout(new BorderLayout());

        // Modern-ish header
        JLabel header = new JLabel("Food Donation — Provider Portal", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(45, 62, 80));
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(18f));
        header.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        content.setBackground(new Color(245,245,247));

        // Registration card
        JPanel regPanel = new JPanel(new GridBagLayout());
        regPanel.setBorder(BorderFactory.createTitledBorder("Register (required)"));
        regPanel.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;

        int row = 0;
        addLabelAndComponent(regPanel, c, row++, "Name:", nameField);
        addLabelAndComponent(regPanel, c, row++, "Email:", emailField);
        addLabelAndComponent(regPanel, c, row++, "Phone:", phoneField);
        addLabelAndComponent(regPanel, c, row++, "Password:", passwordField);
        addLabelAndComponent(regPanel, c, row++, "Location:", locationField);
        addLabelAndComponent(regPanel, c, row++, "Provider Type:", providerType);

        JButton registerButton = new JButton("Register");
        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        regPanel.add(registerButton, c);

        content.add(regPanel);

        // Donation card - disabled until registered
        JPanel donatePanel = new JPanel(new GridBagLayout());
        donatePanel.setBorder(BorderFactory.createTitledBorder("Donate Food"));
        donatePanel.setBackground(Color.WHITE);
        c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;
        row = 0;
        // Item ID is generated automatically
        addLabelAndComponent(donatePanel, c, row++, "Item Name:", itemNameField);
        // Quantity and unit share the same row
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,4,0));
        qtyPanel.setOpaque(false);
        qtyPanel.add(itemQtyField);
        qtyPanel.add(itemUnit);
        addLabelAndComponent(donatePanel, c, row++, "Quantity:", qtyPanel);
        addLabelAndComponent(donatePanel, c, row++, "Expiry (yyyy-MM-dd):", itemExpiryField);

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        donatePanel.add(donateButton, c);
        donatePanel.setEnabled(false);
        setPanelEnabled(donatePanel, false);

        content.add(Box.createVerticalStrut(10));
        content.add(donatePanel);

        add(content, BorderLayout.CENTER);

        // Actions
        registerButton.addActionListener((ActionEvent e) -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword());
            String location = locationField.getText().trim();
            String type = (String) providerType.getSelectedItem();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill name, email and password.", "Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // email validation - restrict to common providers
            String[] allowed = new String[]{"@gmail.com", "@hotmail.com", "@icloud.com"};
            boolean okEmail = false;
            for (String d : allowed) if (email.endsWith(d)) okEmail = true;
            if (!okEmail) {
                JOptionPane.showMessageDialog(this, "Please use a valid email provider (gmail, hotmail, icloud).", "Invalid Email", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // phone numeric check
            if (!phone.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "Phone number must contain only digits.", "Invalid Phone", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = Math.abs(name.hashCode()) % 100000 + 1;
            if (type.equals("Family")) {
                registeredProvider = new Family(id, false, name, email, phone, password, location, name);
            } else if (type.equals("Bakery")) {
                registeredProvider = new Bakery(id, false, name, email, phone, password, location, id);
            } else if (type.equals("Hotel")) {
                registeredProvider = new Hotel(id, false, name, email, phone, password, location, id);
            } else if (type.equals("Restaurant")) {
                registeredProvider = new Restaurant(id, false, name, email, phone, password, location, id);
            } else { // SuperMarket
                registeredProvider = new SuperMarket(id, false, name, email, phone, password, location, id);
            }

            // register provider in system and auto-login them
            manager.addProvider(registeredProvider);
            registeredProvider.login(password);
            // open the dedicated donation window and close this registration GUI
            new DonationWindow(manager, registeredProvider);
            dispose();
        });

        donateButton.addActionListener((ActionEvent e) -> {
            if (registeredProvider == null) {
                JOptionPane.showMessageDialog(this, "Please register first.", "Not Registered", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int itemId = FoodItem.generateId();
                String itemName = itemNameField.getText().trim();
                int qty = Integer.parseInt(itemQtyField.getText().trim());
                String expiryText = itemExpiryField.getText().trim();
                String unit = (String) itemUnit.getSelectedItem();
                Date expiry = null;
                if (!expiryText.isEmpty()) {
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                    expiry = fmt.parse(expiryText);
                }
                FoodItem item = new FoodItem(itemId, itemName, qty, expiry, "pending", registeredProvider, unit);
                // Provider donates (does not add to global registry)
                registeredProvider.donateFood(item);
                // Create pickup request and add to manager
                PickupRequest req = registeredProvider.RequestPickUp(item);
                if (item.isExpired()) {
                    req.reject();
                    item.updateStatus("expired");
                    manager.addPickupRequest(req);
                    JOptionPane.showMessageDialog(this, "Donation rejected: item already expired.", "Rejected", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    manager.addPickupRequest(req);
                    JOptionPane.showMessageDialog(this, "Donation submitted and pickup requested.", "Done", JOptionPane.INFORMATION_MESSAGE);
                }
                // clear
                itemNameField.setText(""); itemExpiryField.setText(""); itemQtyField.setText(""); itemUnit.setSelectedIndex(0);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values for ID and Quantity.", "Bad Input", JOptionPane.ERROR_MESSAGE);
            } catch (ParseException pe) {
                JOptionPane.showMessageDialog(this, "Expiry date must be yyyy-MM-dd or empty.", "Bad Date", JOptionPane.ERROR_MESSAGE);
            }
        });

        // UI polish: buttons and background
        donateButton.setBackground(new Color(67,133,255));
        donateButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(34,177,76));
        registerButton.setForeground(Color.WHITE);

        setVisible(true);
    }

    private void addLabelAndComponent(JPanel panel, GridBagConstraints c, int row, String label, Component comp) {
        c.gridx = 0; c.gridy = row; c.gridwidth = 1;
        panel.add(new JLabel(label), c);
        c.gridx = 1; c.gridy = row; c.gridwidth = 1;
        panel.add(comp, c);
    }

    private void setPanelEnabled(JPanel panel, boolean enabled) {
        panel.setEnabled(enabled);
        for (Component comp : panel.getComponents()) {
            comp.setEnabled(enabled);
            if (comp instanceof JPanel) setPanelEnabled((JPanel) comp, enabled);
        }
    }
}
