import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class SystemManagerGUI extends JFrame implements PickupRequestListener {
    private SystemManager manager;

    private DefaultListModel<PickupRequest> pickupModel = new DefaultListModel<>();
    private JList<PickupRequest> pickupList = new JList<>(pickupModel);

    private DefaultListModel<Recipient> recipientModel = new DefaultListModel<>();
    private JList<Recipient> recipientList = new JList<>(recipientModel);

    private DefaultListModel<FoodItem> foodModel = new DefaultListModel<>();
    private JList<FoodItem> foodList = new JList<>(foodModel);

    private DefaultListModel<Delivery> deliveryModel = new DefaultListModel<>();
    private JList<Delivery> deliveryList = new JList<>(deliveryModel);

    public SystemManagerGUI(SystemManager manager) {
        super("System Manager — Dashboard");
        this.manager = manager;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationByPlatform(true);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("System Manager Dashboard", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(new Color(20, 90, 120));
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(18f));
        header.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 12, 12));

        // Left: Pickup requests
        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createTitledBorder("Pickup Requests"));
        pickupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        left.add(new JScrollPane(pickupList), BorderLayout.CENTER);

        JPanel leftButtons = new JPanel();
        JButton approve = new JButton("Approve");
        JButton reject = new JButton("Reject");
        JButton refresh = new JButton("Refresh");
        JButton loginBtn = new JButton("Login");
        JButton logoutBtn = new JButton("Logout");
        leftButtons.add(approve);
        leftButtons.add(reject);
        leftButtons.add(refresh);
        leftButtons.add(loginBtn);
        leftButtons.add(logoutBtn);
        left.add(leftButtons, BorderLayout.SOUTH);

        // Right: Recipients, Food Items and Deliveries
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JPanel recipientsPanel = new JPanel(new BorderLayout());
        recipientsPanel.setBorder(BorderFactory.createTitledBorder("Recipients"));
        recipientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recipientsPanel.add(new JScrollPane(recipientList), BorderLayout.CENTER);

        JPanel addRecipient = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField rid = new JTextField(4);
        JTextField rname = new JTextField(10);
        JTextField rloc = new JTextField(8);
        JTextField rtype = new JTextField(8);
        JButton addR = new JButton("Add Recipient");
        addRecipient.add(new JLabel("ID")); addRecipient.add(rid);
        addRecipient.add(new JLabel("Name")); addRecipient.add(rname);
        addRecipient.add(new JLabel("Loc")); addRecipient.add(rloc);
        addRecipient.add(new JLabel("Type")); addRecipient.add(rtype);
        addRecipient.add(addR);
        recipientsPanel.add(addRecipient, BorderLayout.SOUTH);

        JPanel foodPanel = new JPanel(new BorderLayout());
        foodPanel.setBorder(BorderFactory.createTitledBorder("Manager Food Items"));
        foodList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        foodPanel.add(new JScrollPane(foodList), BorderLayout.CENTER);

        JPanel deliveryPanel = new JPanel(new BorderLayout());
        deliveryPanel.setBorder(BorderFactory.createTitledBorder("Deliveries"));
        deliveryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        deliveryPanel.add(new JScrollPane(deliveryList), BorderLayout.CENTER);

        JPanel assignPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton assign = new JButton("Assign Delivery");
        assignPanel.add(assign);

        right.add(recipientsPanel);
        right.add(Box.createVerticalStrut(8));
        right.add(foodPanel);
        right.add(assignPanel);
        right.add(Box.createVerticalStrut(8));
        right.add(deliveryPanel);

        center.add(left);
        center.add(right);

        add(center, BorderLayout.CENTER);

        // Actions
        approve.addActionListener((ActionEvent e) -> {
            PickupRequest sel = pickupList.getSelectedValue();
            if (sel == null) return;
            manager.reviewPickupRequest(sel, true);
            refreshAll();
        });

        reject.addActionListener((ActionEvent e) -> {
            PickupRequest sel = pickupList.getSelectedValue();
            if (sel == null) return;
            manager.reviewPickupRequest(sel, false);
            refreshAll();
        });

        addR.addActionListener((ActionEvent e) -> {
            try {
                int id = Integer.parseInt(rid.getText().trim());
                String name = rname.getText().trim();
                String loc = rloc.getText().trim();
                String type = rtype.getText().trim();
                Recipient r = new Recipient(id, name, loc, type);
                manager.addRecipient(r);
                refreshAll();
                rid.setText(""); rname.setText(""); rloc.setText(""); rtype.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Recipient ID must be numeric.", "Bad Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        assign.addActionListener((ActionEvent e) -> {
            FoodItem f = foodList.getSelectedValue();
            Recipient r = recipientList.getSelectedValue();
            if (f == null || r == null) {
                JOptionPane.showMessageDialog(this, "Select a food item and a recipient first.", "Missing Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String driver = JOptionPane.showInputDialog(this, "Enter driver name (or leave blank):", "Driver Name", JOptionPane.PLAIN_MESSAGE);
            Delivery d = manager.assignDelivery(f, r, driver);
            refreshAll();
            JOptionPane.showMessageDialog(this, "Assigned delivery to " + r.getName() + " (driver: " + (driver==null||driver.isEmpty()?d.getDriverName():driver) + ")");
        });

        // Manual refresh button
        refresh.addActionListener((ActionEvent e) -> refreshAll());

        // Confirm delivery button
        JButton confirmDeliveryBtn = new JButton("Confirm Delivered");
        assignPanel.add(confirmDeliveryBtn);

        // Login/logout handling for manager
        final boolean[] loggedIn = {false};
        Runnable setControls = () -> {
            boolean en = loggedIn[0];
            approve.setEnabled(en);
            reject.setEnabled(en);
            assign.setEnabled(en);
            confirmDeliveryBtn.setEnabled(en);
            addR.setEnabled(en);
        };
        // initially locked
        loggedIn[0] = false;
        setControls.run();

        loginBtn.addActionListener((ActionEvent e) -> {
            String pass = JOptionPane.showInputDialog(this, "Enter manager password:", "Login", JOptionPane.PLAIN_MESSAGE);
            if (pass != null && pass.equals("admin")) {
                loggedIn[0] = true;
                JOptionPane.showMessageDialog(this, "Manager logged in.");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
            setControls.run();
        });
        logoutBtn.addActionListener((ActionEvent e) -> {
            loggedIn[0] = false;
            JOptionPane.showMessageDialog(this, "Manager logged out.");
            setControls.run();
        });

        confirmDeliveryBtn.addActionListener((ActionEvent e) -> {
            Delivery sel = deliveryList.getSelectedValue();
            if (sel == null) {
                JOptionPane.showMessageDialog(this, "Select a delivery first.", "Missing Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            manager.confirmDelivery(sel);
            refreshAll();
            JOptionPane.showMessageDialog(this, "Order is delivered.");
        });

        // Register as listener for live updates from SystemManager
        manager.addPickupRequestListener(this);
        // Ensure we unregister when window closes
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                manager.removePickupRequestListener(SystemManagerGUI.this);
            }
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                manager.removePickupRequestListener(SystemManagerGUI.this);
            }
        });

        refreshAll();
        setVisible(true);
    }

    @Override
    public void onPickupRequestsChanged() {
        // Ensure updates happen on the EDT
        SwingUtilities.invokeLater(() -> refreshAll());
    }

    private void refreshAll() {
        // pickup requests
        pickupModel.clear();
        for (PickupRequest p : manager.getPickupRequest()) pickupModel.addElement(p);

        recipientModel.clear();
        for (Recipient r : manager.getRecipients()) recipientModel.addElement(r);

        foodModel.clear();
        for (FoodItem f : manager.getFoodItems()) foodModel.addElement(f);

        deliveryModel.clear();
        for (Delivery d : manager.getDeliveries()) deliveryModel.addElement(d);
    }
}
