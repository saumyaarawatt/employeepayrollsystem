package payroll.ui;

import payroll.model.Employee;
import payroll.service.AuthService;

import javax.swing.*;
import java.awt.*;

/**
 * Simple login panel. On success, switches to admin or employee view.
 */
public class LoginPanel extends JPanel {
    private final MainWindow main;
    private final AuthService auth;

    public LoginPanel(MainWindow main, AuthService auth) {
        this.main = main;
        this.auth = auth;
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel logo = new JLabel(UIResources.loadLogo(180, 50));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(logo, gbc);

        gbc.gridwidth = 1;
        JTextField user = new JTextField(20);
        JPasswordField pass = new JPasswordField(20);

        gbc.gridy = 1; gbc.gridx = 0; add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; add(user, gbc);

        gbc.gridy = 2; gbc.gridx = 0; add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; add(pass, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JButton loginBtn = new JButton("Login");
        add(loginBtn, gbc);

        loginBtn.addActionListener(e -> {
            String u = user.getText().trim();
            String p = new String(pass.getPassword());
            Employee emp = auth.authenticate(u, p);
            if (emp == null) {
                JOptionPane.showMessageDialog(this, "Invalid username/password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if ("ADMIN".equalsIgnoreCase(emp.getRole())) main.showAdmin();
            else main.showEmployee(emp.getId());
        });
    }
}
