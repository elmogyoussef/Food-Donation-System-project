import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class PrimaryWindow extends JFrame {
    private SystemManager manager;

    public PrimaryWindow(SystemManager manager) {
        super("FoodBank — Entry");
        this.manager = manager;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420,220);
        setLocationByPlatform(true);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome to FoodBank", SwingConstants.CENTER);
        title.setOpaque(true); title.setBackground(new Color(18,38,63)); title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(18f).deriveFont(Font.BOLD));
        title.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        add(title, BorderLayout.NORTH);

        JPanel main = new JPanel(new GridBagLayout());
        main.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(6,6,6,6); c.anchor = GridBagConstraints.CENTER;

        JButton providerLogin = new JButton("Provider Login");
        JButton providerRegister = new JButton("Provider Register");
        providerLogin.setBackground(new Color(48,120,255)); providerLogin.setForeground(Color.WHITE);
        providerLogin.setFocusPainted(false);
        providerRegister.setBackground(new Color(34,177,76)); providerRegister.setForeground(Color.WHITE);
        providerRegister.setFocusPainted(false);
        providerLogin.setFont(providerLogin.getFont().deriveFont(14f));
        providerRegister.setFont(providerRegister.getFont().deriveFont(14f));

        c.gridx = 0; c.gridy = 0; main.add(providerLogin, c);
        c.gridx = 1; c.gridy = 0; main.add(providerRegister, c);

        // subtle background
        main.setBackground(new Color(245,247,250));
        add(main, BorderLayout.CENTER);

        providerRegister.addActionListener((ActionEvent e) -> {
            // show registration dialog
            RegistrationDialog dlg = new RegistrationDialog(this, manager);
            dlg.setVisible(true);
        });

        providerLogin.addActionListener((ActionEvent e) -> {
            String email = JOptionPane.showInputDialog(this, "Email:");
            if (email == null) return;
            String pass = JOptionPane.showInputDialog(this, "Password:");
            if (pass == null) return;
            Provider p = manager.authenticateProvider(email.trim(), pass);
            if (p == null) {
                JOptionPane.showMessageDialog(this, "Login failed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // open donation window for provider
            new DonationWindow(manager, p);
        });

        // Manager dashboard is not exposed to providers from this entry window

        setVisible(true);
    }

    // inner registration dialog
    static class RegistrationDialog extends JDialog {
        public RegistrationDialog(Frame owner, SystemManager manager) {
            super(owner, "Register Provider", true);
            setSize(520,320);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout());

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(6,6,6,6); c.anchor = GridBagConstraints.WEST;

            JTextField name = new JTextField(18); JTextField email = new JTextField(18); JTextField phone = new JTextField(12);
            JPasswordField pass = new JPasswordField(12); JTextField location = new JTextField(12);
            JComboBox<String> type = new JComboBox<>(new String[]{"Family","Bakery","Hotel","SuperMarket","Restaurant"});

            int row=0;
            c.gridx=0; c.gridy=row; panel.add(new JLabel("Name:"), c); c.gridx=1; panel.add(name,c); row++;
            c.gridx=0; c.gridy=row; panel.add(new JLabel("Email:"), c); c.gridx=1; panel.add(email,c); row++;
            c.gridx=0; c.gridy=row; panel.add(new JLabel("Phone:"), c); c.gridx=1; panel.add(phone,c); row++;
            c.gridx=0; c.gridy=row; panel.add(new JLabel("Password:"), c); c.gridx=1; panel.add(pass,c); row++;
            c.gridx=0; c.gridy=row; panel.add(new JLabel("Location:"), c); c.gridx=1; panel.add(location,c); row++;
            c.gridx=0; c.gridy=row; panel.add(new JLabel("Type:"), c); c.gridx=1; panel.add(type,c); row++;

            JButton submit = new JButton("Register");
            c.gridx=0; c.gridy=row; c.gridwidth=2; panel.add(submit,c);

            add(panel, BorderLayout.CENTER);

            submit.addActionListener((ActionEvent e) -> {
                String n = name.getText().trim(); String em = email.getText().trim(); String ph = phone.getText().trim();
                String pw = new String(pass.getPassword()); String loc = location.getText().trim(); String t = (String)type.getSelectedItem();
                if (n.isEmpty()||em.isEmpty()||pw.isEmpty()) { JOptionPane.showMessageDialog(this,"Fill required fields."); return; }
                String[] allowed = new String[]{"@gmail.com","@hotmail.com","@icloud.com"}; boolean ok=false; for(String a:allowed) if(em.endsWith(a)) ok=true; if(!ok){ JOptionPane.showMessageDialog(this,"Use allowed email providers."); return; }
                if(!ph.matches("\\d+")) { JOptionPane.showMessageDialog(this,"Phone must be digits."); return; }
                int id = Math.abs(n.hashCode())%100000 + 1;
                Provider p=null;
                if (t.equals("Family")) p = new Family(id,false,n,em,ph,pw,loc,n);
                else if (t.equals("Bakery")) p = new Bakery(id,false,n,em,ph,pw,loc,id);
                else if (t.equals("Hotel")) p = new Hotel(id,false,n,em,ph,pw,loc,id);
                else if (t.equals("Restaurant")) p = new Restaurant(id,false,n,em,ph,pw,loc,id);
                else p = new SuperMarket(id,false,n,em,ph,pw,loc,id);
                manager.addProvider(p);
                // auto-login the newly registered provider and open donation window
                p.login(pw);
                new DonationWindow(manager, p);
                dispose();
            });
        }
    }
}
