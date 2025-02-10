// EmailInterface.java
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailInterface extends JFrame {
    private JTable emailTable;
    private DefaultTableModel tableModel;
    private JTextArea emailContent;
    private String currentUser;
    private String sessionToken;
    private JLabel statusLabel;

    public EmailInterface(String username, String sessionToken) {
        this.currentUser = username;
        this.sessionToken = sessionToken;

        setTitle("Email - " + username);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create menu bar
        createMenuBar();

        // Create main split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // Create email list
        createEmailList();
        JScrollPane emailScrollPane = new JScrollPane(emailTable);

        // Create email content viewer
        createEmailViewer();
        JScrollPane contentScrollPane = new JScrollPane(emailContent);

        splitPane.setTopComponent(emailScrollPane);
        splitPane.setBottomComponent(contentScrollPane);
        splitPane.setDividerLocation(300);

        // Create status bar
        statusLabel = new JLabel(" ");
        add(statusLabel, BorderLayout.SOUTH);

        add(splitPane);

        // Create toolbar
        createToolbar();

        // Load emails
        loadEmails();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newEmailItem = new JMenuItem("New Email");
        JMenuItem settingsItem = new JMenuItem("Settings");
        JMenuItem exitItem = new JMenuItem("Exit");

        newEmailItem.addActionListener(e -> showNewEmailDialog());
        settingsItem.addActionListener(e -> showSettings());
        exitItem.addActionListener(e -> handleExit());

        fileMenu.add(newEmailItem);
        fileMenu.add(settingsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem deleteItem = new JMenuItem("Delete");
        JMenuItem markReadItem = new JMenuItem("Mark as Read");
        JMenuItem markUnreadItem = new JMenuItem("Mark as Unread");

        deleteItem.addActionListener(e -> deleteSelectedEmail());
        markReadItem.addActionListener(e -> markSelectedAsRead());
        markUnreadItem.addActionListener(e -> markSelectedAsUnread());

        editMenu.add(deleteItem);
        editMenu.add(markReadItem);
        editMenu.add(markUnreadItem);

        // View menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem refreshItem = new JMenuItem("Refresh");
        JMenu sortMenu = new JMenu("Sort By");

        JMenuItem sortDateItem = new JMenuItem("Date");
        JMenuItem sortSenderItem = new JMenuItem("Sender");
        JMenuItem sortSubjectItem = new JMenuItem("Subject");

        refreshItem.addActionListener(e -> refreshEmails());
        sortDateItem.addActionListener(e -> sortEmails("date"));
        sortSenderItem.addActionListener(e -> sortEmails("sender"));
        sortSubjectItem.addActionListener(e -> sortEmails("subject"));

        sortMenu.add(sortDateItem);
        sortMenu.add(sortSenderItem);
        sortMenu.add(sortSubjectItem);

        viewMenu.add(refreshItem);
        viewMenu.add(sortMenu);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);

        setJMenuBar(menuBar);
    }

    private void createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        // Create buttons
        JButton newEmailBtn = new JButton("New Email");
        JButton replyBtn = new JButton("Reply");
        JButton forwardBtn = new JButton("Forward");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        // Add icons (if available)
        // newEmailBtn.setIcon(new ImageIcon("path_to_icon"));

        // Add tooltips
        newEmailBtn.setToolTipText("Create new email");
        replyBtn.setToolTipText("Reply to selected email");
        forwardBtn.setToolTipText("Forward selected email");
        deleteBtn.setToolTipText("Delete selected email");
        refreshBtn.setToolTipText("Refresh email list");

        // Add buttons to toolbar
        toolbar.add(newEmailBtn);
        toolbar.add(replyBtn);
        toolbar.add(forwardBtn);
        toolbar.add(deleteBtn);
        toolbar.addSeparator();
        toolbar.add(refreshBtn);

        // Add button listeners
        newEmailBtn.addActionListener(e -> showNewEmailDialog());
        replyBtn.addActionListener(e -> replyToEmail());
        forwardBtn.addActionListener(e -> forwardEmail());
        deleteBtn.addActionListener(e -> deleteSelectedEmail());
        refreshBtn.addActionListener(e -> refreshEmails());

        add(toolbar, BorderLayout.NORTH);
    }

    private void createEmailList() {
        String[] columns = {"From", "Subject", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        emailTable = new JTable(tableModel);
        emailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add selection listener
        emailTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedEmail();
            }
        });

        // Set column widths
        emailTable.getColumnModel().getColumn(0).setPreferredWidth(150);  // From
        emailTable.getColumnModel().getColumn(1).setPreferredWidth(300);  // Subject
        emailTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Date
        emailTable.getColumnModel().getColumn(3).setPreferredWidth(100);  // Status
    }

    private void createEmailViewer() {
        emailContent = new JTextArea();
        emailContent.setEditable(false);
        emailContent.setLineWrap(true);
        emailContent.setWrapStyleWord(true);
        emailContent.setFont(new Font("Monospaced", Font.PLAIN, 12));
        emailContent.setMargin(new Insets(10, 10, 10, 10));
    }

    private void loadEmails() {
        // This would normally fetch emails from database
        // For now, adding sample data
        Object[][] sampleEmails = {
            {"john@example.com", "Meeting Tomorrow", "2024-02-07 10:30", "Unread"},
            {"kyle@example.com", "Project Update", "2024-02-07 09:15", "Read"},
            {"team@company.com", "Weekly Newsletter", "2024-02-06 16:45", "Read"}
        };

        for (Object[] email : sampleEmails) {
            tableModel.addRow(email);
        }
    }

    private void showNewEmailDialog() {
        JDialog newEmailDialog = new JDialog(this, "New Email", true);
        newEmailDialog.setSize(600, 400);
        newEmailDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());

        // Create header panel
        JPanel headerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);

        // To field
        gbc.gridx = 0; gbc.gridy = 0;
        headerPanel.add(new JLabel("To:"), gbc);
        gbc.gridx = 1;
        JTextField toField = new JTextField(40);
        headerPanel.add(toField, gbc);

        // Subject field
        gbc.gridx = 0; gbc.gridy = 1;
        headerPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1;
        JTextField subjectField = new JTextField(40);
        headerPanel.add(subjectField, gbc);

        // Email content
        JTextArea content = new JTextArea(15, 40);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(content);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton sendButton = new JButton("Send");
        JButton saveButton = new JButton("Save Draft");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(sendButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add components to panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add button listeners
        sendButton.addActionListener(e -> {
            sendEmail(toField.getText(), subjectField.getText(), content.getText());
            newEmailDialog.dispose();
        });

        saveButton.addActionListener(e -> {
            saveDraft(toField.getText(), subjectField.getText(), content.getText());
            newEmailDialog.dispose();
        });

        cancelButton.addActionListener(e -> {
            if (confirmDiscard()) {
                newEmailDialog.dispose();
            }
        });

        newEmailDialog.add(panel);
        newEmailDialog.setVisible(true);
    }

    private void replyToEmail() {
        int selectedRow = emailTable.getSelectedRow();
        if (selectedRow >= 0) {
            String originalFrom = (String) tableModel.getValueAt(selectedRow, 0);
            String originalSubject = (String) tableModel.getValueAt(selectedRow, 1);

            showEmailDialog("Re: " + originalSubject, originalFrom, "");
        } else {
            JOptionPane.showMessageDialog(this, "Please select an email to reply to.");
        }
    }

    private void forwardEmail() {
        int selectedRow = emailTable.getSelectedRow();
        if (selectedRow >= 0) {
            String originalSubject = (String) tableModel.getValueAt(selectedRow, 1);
            String originalContent = emailContent.getText();

            showEmailDialog("Fwd: " + originalSubject, "", 
                          "\n\n-------- Forwarded Message --------\n" + originalContent);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an email to forward.");
        }
    }

    private void showEmailDialog(String subject, String to, String initialContent) {
        JDialog emailDialog = new JDialog(this, "Compose Email", true);
        emailDialog.setSize(600, 400);
        emailDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());

        // Create header panel
        JPanel headerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);

        // To field
        gbc.gridx = 0; gbc.gridy = 0;
        headerPanel.add(new JLabel("To:"), gbc);
        gbc.gridx = 1;
        JTextField toField = new JTextField(to, 40);
        headerPanel.add(toField, gbc);

        // Subject field
        gbc.gridx = 0; gbc.gridy = 1;
        headerPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1;
        JTextField subjectField = new JTextField(subject, 40);
        headerPanel.add(subjectField, gbc);

        // Email content
        JTextArea content = new JTextArea(15, 40);
        content.setText(initialContent);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(content);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton sendButton = new JButton("Send");
        JButton saveButton = new JButton("Save Draft");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(sendButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> {
            sendEmail(toField.getText(), subjectField.getText(), content.getText());
            emailDialog.dispose();
        });

        saveButton.addActionListener(e -> {
            saveDraft(toField.getText(), subjectField.getText(), content.getText());
            emailDialog.dispose();
        });

        cancelButton.addActionListener(e -> {
            if (confirmDiscard()) {
                emailDialog.dispose();
            }
        });

        emailDialog.add(panel);
        emailDialog.setVisible(true);
    }

    private boolean confirmDiscard() {
        return JOptionPane.showConfirmDialog(this,
            "Discard this email?",
            "Confirm Cancel",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void deleteSelectedEmail() {
        int selectedRow = emailTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this email?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // This would normally delete from database
                tableModel.removeRow(selectedRow);
                emailContent.setText("");
                updateStatus("Email deleted successfully");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an email to delete.");
        }
    }

    private void markSelectedAsRead() {
        int selectedRow = emailTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.setValueAt("Read", selectedRow, 3);
            updateStatus("Email marked as read");
        }
    }

    private void markSelectedAsUnread() {
        int selectedRow = emailTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.setValueAt("Unread", selectedRow, 3);
            updateStatus("Email marked as unread");
        }
    }

    private void refreshEmails() {
        // This would normally refresh from database
        tableModel.setRowCount(0);
        loadEmails();
        updateStatus("Emails refreshed");
    }

      private void sortEmails(String criteria) {
          // Implement sorting based on the selected criteria
          // Note: This is a simple implementation. In a real app, you'd want to sort the actual data
          int rowCount = tableModel.getRowCount();
          Object[][] data = new Object[rowCount][4];

          // Copy data to array
          for (int i = 0; i < rowCount; i++) {
              for (int j = 0; j < 4; j++) {
                  data[i][j] = tableModel.getValueAt(i, j);
              }
          }

          // Sort based on criteria
          switch (criteria) {
              case "date":
                  java.util.Arrays.sort(data, (a, b) -> ((String)b[2]).compareTo((String)a[2]));
                  break;
              case "sender":
                  java.util.Arrays.sort(data, (a, b) -> ((String)a[0]).compareTo((String)b[0]));
                  break;
              case "subject":
                  java.util.Arrays.sort(data, (a, b) -> ((String)a[1]).compareTo((String)b[1]));
                  break;
          }

          // Update table
          tableModel.setRowCount(0);
          for (Object[] row : data) {
              tableModel.addRow(row);
          }

          updateStatus("Emails sorted by " + criteria);
      }

      private void sendEmail(String to, String subject, String content) {
          // This would normally send the email through SMTP server
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
          String currentTime = LocalDateTime.now().format(formatter);

          Object[] newRow = {currentUser, subject, currentTime, "Sent"};
          tableModel.addRow(newRow);

          updateStatus("Email sent successfully");
      }

      private void saveDraft(String to, String subject, String content) {
          // This would normally save to database
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
          String currentTime = LocalDateTime.now().format(formatter);

          Object[] newRow = {to, subject, currentTime, "Draft"};
          tableModel.addRow(newRow);

          updateStatus("Draft saved successfully");
      }

      private void displaySelectedEmail() {
          int selectedRow = emailTable.getSelectedRow();
          if (selectedRow >= 0) {
              String from = (String) tableModel.getValueAt(selectedRow, 0);
              String subject = (String) tableModel.getValueAt(selectedRow, 1);
              String date = (String) tableModel.getValueAt(selectedRow, 2);

              // This would normally fetch the full email content from database
              String content = "From: " + from + "\n" +
                             "Subject: " + subject + "\n" +
                             "Date: " + date + "\n\n" +
                             "This is a sample email content.\n" +
                             "In a real application, this would be the actual email content " +
                             "fetched from the database.";

              emailContent.setText(content);

              // Mark as read
              tableModel.setValueAt("Read", selectedRow, 3);
          }
      }

      private void showSettings() {
          JDialog settingsDialog = new JDialog(this, "Settings", true);
          settingsDialog.setSize(400, 300);
          settingsDialog.setLocationRelativeTo(this);

          JPanel panel = new JPanel(new GridBagLayout());
          GridBagConstraints gbc = new GridBagConstraints();
          gbc.insets = new Insets(5, 5, 5, 5);
          gbc.fill = GridBagConstraints.HORIZONTAL;

          // Email signature
          gbc.gridx = 0; gbc.gridy = 0;
          panel.add(new JLabel("Email Signature:"), gbc);

          gbc.gridx = 0; gbc.gridy = 1;
          gbc.gridwidth = 2;
          JTextArea signatureArea = new JTextArea(4, 30);
          signatureArea.setLineWrap(true);
          signatureArea.setWrapStyleWord(true);
          panel.add(new JScrollPane(signatureArea), gbc);

          // Font size
          gbc.gridx = 0; gbc.gridy = 2;
          gbc.gridwidth = 1;
          panel.add(new JLabel("Font Size:"), gbc);

          gbc.gridx = 1;
          String[] fontSizes = {"Small", "Medium", "Large"};
          JComboBox<String> fontSizeCombo = new JComboBox<>(fontSizes);
          panel.add(fontSizeCombo, gbc);

          // Save button
          gbc.gridx = 0; gbc.gridy = 3;
          gbc.gridwidth = 2;
          JButton saveButton = new JButton("Save Settings");
          saveButton.addActionListener(e -> {
              // This would normally save settings to user preferences
              updateStatus("Settings saved");
              settingsDialog.dispose();
          });
          panel.add(saveButton, gbc);

          settingsDialog.add(panel);
          settingsDialog.setVisible(true);
      }

      private void handleExit() {
          int confirm = JOptionPane.showConfirmDialog(this,
              "Are you sure you want to exit?",
              "Confirm Exit",
              JOptionPane.YES_NO_OPTION);

          if (confirm == JOptionPane.YES_OPTION) {
              System.exit(0);
          }
