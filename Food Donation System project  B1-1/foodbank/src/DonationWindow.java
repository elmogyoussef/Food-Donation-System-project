import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class DonationWindow extends JFrame {
    private SystemManager manager;
    private Provider provider;

    private JLabel titleLabel;
    private JTextField itemNameField = new JTextField(20);
    private JTextField itemQtyField = new JTextField(5);
    private JComboBox<String> itemUnit = new JComboBox<>(new String[]{"pcs", "kg", "lbs", "liters", "boxes"});
    private JTextField itemExpiryField = new JTextField(10); // yyyy-MM-dd
    private JButton donateButton = new JButton("Donate & Request Pickup");
    private JButton logoutButton = new JButton("Logout");

    public DonationWindow(SystemManager manager, Provider provider) {
        super("Donate Food — " + provider.getName());
        this.manager = manager;
        this.provider = provider;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 320);
        setLocationByPlatform(true);
        setLayout(new BorderLayout());

        titleLabel = new JLabel("Logged in: " + provider.getName(), SwingConstants.CENTER);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(18,38,63));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD,14f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        add(titleLabel, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;

        int row = 0;
        c.gridx = 0; c.gridy = row; content.add(new JLabel("Item Name:"), c);
        c.gridx = 1; c.gridy = row++; content.add(itemNameField, c);

        c.gridx = 0; c.gridy = row; content.add(new JLabel("Quantity:"), c);
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,4,0)); qtyPanel.setOpaque(false);
        qtyPanel.add(itemQtyField); qtyPanel.add(itemUnit);
        c.gridx = 1; c.gridy = row++; content.add(qtyPanel, c);

        c.gridx = 0; c.gridy = row; content.add(new JLabel("Expiry (yyyy-MM-dd):"), c);
        c.gridx = 1; c.gridy = row++; content.add(itemExpiryField, c);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        donateButton.setBackground(new Color(48,120,255)); donateButton.setForeground(Color.WHITE);
        donateButton.setFocusPainted(false);
        logoutButton.setBackground(new Color(220,53,69)); logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        donateButton.setFont(donateButton.getFont().deriveFont(13f));
        logoutButton.setFont(logoutButton.getFont().deriveFont(13f));
        btns.add(logoutButton); btns.add(donateButton);

        add(content, BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);

        donateButton.addActionListener((ActionEvent e) -> {
            try {
                int itemId = FoodItem.generateId();
                String itemName = itemNameField.getText().trim();
                int qty = Integer.parseInt(itemQtyField.getText().trim());
                String unit = (String) itemUnit.getSelectedItem();
                String expiryText = itemExpiryField.getText().trim();
                Date expiry = null;
                if (!expiryText.isEmpty()) {
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                    expiry = fmt.parse(expiryText);
                }
                FoodItem item = new FoodItem(itemId, itemName, qty, expiry, "pending", provider, unit);
                provider.donateFood(item); // does not add to global registry
                PickupRequest req = provider.RequestPickUp(item);
                // If item already expired, automatically reject the pickup request
                if (item.isExpired()) {
                    req.reject();
                    item.updateStatus("expired");
                    manager.addPickupRequest(req);
                    JOptionPane.showMessageDialog(this, "Donation rejected: item already expired.");
                } else {
                    manager.addPickupRequest(req);
                    JOptionPane.showMessageDialog(this, "Donation submitted and pickup requested.");
                }
                itemNameField.setText(""); itemQtyField.setText(""); itemExpiryField.setText(""); itemUnit.setSelectedIndex(0);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Quantity must be a number.", "Bad Input", JOptionPane.ERROR_MESSAGE);
            } catch (ParseException pe) {
                JOptionPane.showMessageDialog(this, "Expiry date must be yyyy-MM-dd or empty.", "Bad Date", JOptionPane.ERROR_MESSAGE);
            }
        });

        logoutButton.addActionListener((ActionEvent e) -> {
            // logout provider and close the window; return to primary
            try { provider.logout(); } catch (Exception ex) { }
            dispose();
        });

        // subtle background and padding
        content.setBackground(new Color(250,251,253));
        setVisible(true);
    }
}
