// EmailLoginUI.java
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.Timer;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EmailLoginUI extends JFrame {
    private final UserManager userManager;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton registerButton;
    private final JLabel statusLabel;

    public EmailLoginUI() {
        userManager = new UserManager();

        setTitle("Email Login System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        setBackground(Color.WHITE);
        getContentPane().setBackground(Color.WHITE);

        // Create panels
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel userLabel = new JLabel("Username:", SwingConstants.RIGHT);
        mainPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(200, 25));
        mainPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passLabel = new JLabel("Password:", SwingConstants.RIGHT);
        mainPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(200, 25));
        mainPanel.add(passwordField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        Dimension buttonSize = new Dimension(100, 30);
        loginButton.setPreferredSize(buttonSize);
        registerButton.setPreferredSize(buttonSize);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBackground(Color.WHITE);
        statusLabel.setOpaque(true);
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 0, 10);
        mainPanel.add(statusLabel, gbc);

        add(mainPanel);

        // Add button listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        // Add Enter key listener
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        // Request focus for username field
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                usernameField.requestFocusInWindow();
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        UserManager.AuthResult result = userManager.login(username, password);

        if (result.success) {
            statusLabel.setForeground(Color.GREEN);
            statusLabel.setText("Login successful!");
            EmailInterface emailInterface = new EmailInterface(username, result.sessionToken);
            emailInterface.setVisible(true);
            this.dispose();
        } else {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText(result.message);
            passwordField.setText("");
        }
    }

    private void handleRegister() {
        final JDialog registerDialog = new JDialog(this, "Register New Account", true);
        registerDialog.setSize(450, 300);
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.0;
        JLabel userLabel = new JLabel("Username:", SwingConstants.RIGHT);
        panel.add(userLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        final JTextField regUsernameField = new JTextField();
        regUsernameField.setPreferredSize(new Dimension(200, 25));
        panel.add(regUsernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel passLabel = new JLabel("Password:", SwingConstants.RIGHT);
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        final JPasswordField regPasswordField = new JPasswordField();
        regPasswordField.setPreferredSize(new Dimension(200, 25));
        panel.add(regPasswordField, gbc);

        // Confirm password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel confirmLabel = new JLabel("Confirm Password:", SwingConstants.RIGHT);
        panel.add(confirmLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        final JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(200, 25));
        panel.add(confirmPasswordField, gbc);

        // Status label
        final JLabel regStatusLabel = new JLabel(" ");
        regStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        regStatusLabel.setBackground(Color.WHITE);
        regStatusLabel.setOpaque(true);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(regStatusLabel, gbc);

        // Register button
        JButton submitButton = new JButton("Register");
        submitButton.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = regUsernameField.getText();
                String password = new String(regPasswordField.getPassword());
                String confirmPass = new String(confirmPasswordField.getPassword());

                if (!password.equals(confirmPass)) {
                    regStatusLabel.setForeground(Color.RED);
                    regStatusLabel.setText("Passwords do not match!");
                    return;
                }

                UserManager.AuthResult result = userManager.registerUser(username, password);

                if (result.success) {
                    regStatusLabel.setForeground(Color.GREEN);
                    regStatusLabel.setText("Registration successful!");
                    Timer timer = new Timer(1500, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            registerDialog.dispose();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    regStatusLabel.setForeground(Color.RED);
                    regStatusLabel.setText(result.message);
                }
            }
        });

        // Add Enter key listener
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    submitButton.doClick();
                }
            }
        };
        regUsernameField.addKeyListener(enterKeyListener);
        regPasswordField.addKeyListener(enterKeyListener);
        confirmPasswordField.addKeyListener(enterKeyListener);

        // Set dialog properties
        registerDialog.getContentPane().setBackground(Color.WHITE);
        registerDialog.add(panel);
        registerDialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                EmailLoginUI ui = new EmailLoginUI();
                ui.setVisible(true);
            }
        });
    }
}
