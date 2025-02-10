// DatabaseHelper.java
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper implements AutoCloseable {
    private final Connection connection;

    public static class Email {
        public int id;
        public String fromUser;
        public String toUser;
        public String subject;
        public String content;
        public LocalDateTime sentDate;
        public String status;
        public boolean isRead;

        public Email(int id, String fromUser, String toUser, String subject,
                    String content, LocalDateTime sentDate, String status, boolean isRead) {
            this.id = id;
            this.fromUser = fromUser;
            this.toUser = toUser;
            this.subject = subject;
            this.content = content;
            this.sentDate = sentDate;
            this.status = status;
            this.isRead = isRead;
        }
    }

    public DatabaseHelper(String url, String username, String password) throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
        connection.setAutoCommit(false);  // Enable transaction support
    }

    // Email operations
    public List<Email> getInboxEmails(String username) throws SQLException {
        List<Email> emails = new ArrayList<>();
        String query = "SELECT * FROM emails WHERE to_user = ? ORDER BY sent_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                emails.add(new Email(
                    rs.getInt("email_id"),
                    rs.getString("from_user"),
                    rs.getString("to_user"),
                    rs.getString("subject"),
                    rs.getString("content"),
                    rs.getTimestamp("sent_date").toLocalDateTime(),
                    rs.getString("status"),
                    rs.getBoolean("is_read")
                ));
            }
        }

        return emails;
    }

    public List<Email> getSentEmails(String username) throws SQLException {
        List<Email> emails = new ArrayList<>();
        String query = "SELECT * FROM emails WHERE from_user = ? AND status = 'SENT' ORDER BY sent_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                emails.add(new Email(
                    rs.getInt("email_id"),
                    rs.getString("from_user"),
                    rs.getString("to_user"),
                    rs.getString("subject"),
                    rs.getString("content"),
                    rs.getTimestamp("sent_date").toLocalDateTime(),
                    rs.getString("status"),
                    rs.getBoolean("is_read")
                ));
            }
        }

        return emails;
    }

    public List<Email> getDraftEmails(String username) throws SQLException {
        List<Email> emails = new ArrayList<>();
        String query = "SELECT * FROM emails WHERE from_user = ? AND status = 'DRAFT' ORDER BY sent_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                emails.add(new Email(
                    rs.getInt("email_id"),
                    rs.getString("from_user"),
                    rs.getString("to_user"),
                    rs.getString("subject"),
                    rs.getString("content"),
                    rs.getTimestamp("sent_date").toLocalDateTime(),
                    rs.getString("status"),
                    rs.getBoolean("is_read")
                ));
            }
        }

        return emails;
    }

    public int sendEmail(String fromUser, String toUser, String subject, 
                        String content) throws SQLException {
        String query = "INSERT INTO emails (from_user, to_user, subject, content, sent_date, status, is_read) " +
                      "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, 'SENT', false)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fromUser);
            stmt.setString(2, toUser);
            stmt.setString(3, subject);
            stmt.setString(4, content);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int emailId = rs.getInt(1);
                connection.commit();
                return emailId;
            }
            throw new SQLException("Failed to get generated email ID");
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public int saveDraft(String fromUser, String toUser, String subject, 
                        String content) throws SQLException {
        String query = "INSERT INTO emails (from_user, to_user, subject, content, sent_date, status, is_read) " +
                      "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, 'DRAFT', false)";

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fromUser);
            stmt.setString(2, toUser);
            stmt.setString(3, subject);
            stmt.setString(4, content);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int emailId = rs.getInt(1);
                connection.commit();
                return emailId;
            }
            throw new SQLException("Failed to get generated email ID");
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void updateDraft(int emailId, String toUser, String subject, 
                          String content) throws SQLException {
        String query = "UPDATE emails SET to_user = ?, subject = ?, content = ?, " +
                      "sent_date = CURRENT_TIMESTAMP WHERE email_id = ? AND status = 'DRAFT'";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, toUser);
            stmt.setString(2, subject);
            stmt.setString(3, content);
            stmt.setInt(4, emailId);
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                connection.commit();
            } else {
                throw new SQLException("Draft email not found or already sent");
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void deleteEmail(int emailId, String username) throws SQLException {
        String query = "DELETE FROM emails WHERE email_id = ? AND (from_user = ? OR to_user = ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, emailId);
            stmt.setString(2, username);
            stmt.setString(3, username);
            int deleted = stmt.executeUpdate();

            if (deleted > 0) {
                connection.commit();
            } else {
                throw new SQLException("Email not found or permission denied");
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void markAsRead(int emailId, String username) throws SQLException {
        String query = "UPDATE emails SET is_read = true WHERE email_id = ? AND to_user = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, emailId);
            stmt.setString(2, username);
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                connection.commit();
            } else {
                throw new SQLException("Email not found or permission denied");
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void markAsUnread(int emailId, String username) throws SQLException {
        String query = "UPDATE emails SET is_read = false WHERE email_id = ? AND to_user = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, emailId);
            stmt.setString(2, username);
            int updated = stmt.executeUpdate();

            if (updated > 0) {
                connection.commit();
            } else {
                throw new SQLException("Email not found or permission denied");
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    // Session management
    public boolean validateSession(String username, String sessionToken) throws SQLException {
        String query = "SELECT COUNT(*) FROM sessions WHERE user_id = " +
                      "(SELECT user_id FROM users WHERE username = ?) " +
                      "AND token = ? AND is_active = true AND expires_at > CURRENT_TIMESTAMP";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, sessionToken);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public void invalidateSession(String sessionToken) throws SQLException {
        String query = "UPDATE sessions SET is_active = false WHERE token = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, sessionToken);
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public void createSession(int userId, String token, LocalDateTime expiresAt) throws SQLException {
        String query = "INSERT INTO sessions (user_id, token, created_at, expires_at, is_active) " +
                      "VALUES (?, ?, CURRENT_TIMESTAMP, ?, true)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, token);
            stmt.setTimestamp(3, Timestamp.valueOf(expiresAt));
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
