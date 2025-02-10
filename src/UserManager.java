// UserManager.java
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class UserManager {
    // In-memory storage
    private final Map<String, String> users = new HashMap<>();  // username -> password hash
    private final Map<String, Integer> loginAttempts = new HashMap<>();  // username -> failed attempts
    private final Map<String, String> sessions = new HashMap<>();  // token -> username

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public static class AuthResult {
        public final boolean success;
        public final String message;
        public final String sessionToken;

        public AuthResult(boolean success, String message, String sessionToken) {
            this.success = success;
            this.message = message;
            this.sessionToken = sessionToken;
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public AuthResult login(String username, String password) {
        // Check for empty fields
        if (username == null || username.trim().isEmpty()) {
            return new AuthResult(false, "Please enter a username", null);
        }
        if (password == null || password.trim().isEmpty()) {
            return new AuthResult(false, "Please enter a password", null);
        }

        // Check if user exists
        if (!users.containsKey(username)) {
            return new AuthResult(false, "Invalid username or password", null);
        }

        // Check if account is locked
        int attempts = loginAttempts.getOrDefault(username, 0);
        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            return new AuthResult(false, "Account is locked due to too many failed attempts. Please try again later.", null);
        }

        // Verify password
        String storedHash = users.get(username);
        String hashedInput = hashPassword(password);

        if (hashedInput != null && hashedInput.equals(storedHash)) {
            // Successful login - reset attempts and create session
            loginAttempts.remove(username);
            String sessionToken = UUID.randomUUID().toString();
            sessions.put(sessionToken, username);
            return new AuthResult(true, "Login successful", sessionToken);
        } else {
            // Failed login - increment attempts
            loginAttempts.put(username, attempts + 1);
            int remainingAttempts = MAX_LOGIN_ATTEMPTS - attempts - 1;
            return new AuthResult(false, "Invalid username or password. " + 
                (remainingAttempts > 0 ? remainingAttempts + " attempts remaining." : "Account will be locked."), null);
        }
    }

    public AuthResult registerUser(String username, String password) {
        // Validate username
        if (username == null || username.trim().isEmpty()) {
            return new AuthResult(false, "Username cannot be empty", null);
        }

        // Username length check
        if (username.length() < 6 || username.length() > 30) {
            return new AuthResult(false, "Username must be between 6-30 characters", null);
        }

        // Username character validation
        if (!username.matches("^[a-zA-Z0-9._]+$")) {
            return new AuthResult(false, "Username can only contain letters, numbers, dots, and underscores", null);
        }

        // Check start/end characters
        if (username.startsWith(".") || username.startsWith("_") || 
            username.endsWith(".") || username.endsWith("_")) {
            return new AuthResult(false, "Username cannot start or end with dots or underscores", null);
        }

        // Check consecutive special characters
        if (username.contains("..") || username.contains("__")) {
            return new AuthResult(false, "Username cannot contain consecutive dots or underscores", null);
        }

        // Check for at least one letter
        if (!username.matches(".*[a-zA-Z].*")) {
            return new AuthResult(false, "Username must contain at least one letter", null);
        }

        // Check if username exists
        if (users.containsKey(username)) {
            return new AuthResult(false, "Username already exists", null);
        }

        // Validate password
        if (password == null || password.trim().isEmpty()) {
            return new AuthResult(false, "Password cannot be empty", null);
        }

        // Password length check
        if (password.length() < 8) {
            return new AuthResult(false, "Password must be at least 8 characters long", null);
        }

        // Password complexity checks
        if (!password.matches(".*[A-Z].*")) {
            return new AuthResult(false, "Password must contain at least one uppercase letter", null);
        }
        if (!password.matches(".*[a-z].*")) {
            return new AuthResult(false, "Password must contain at least one lowercase letter", null);
        }
        if (!password.matches(".*[0-9].*")) {
            return new AuthResult(false, "Password must contain at least one number", null);
        }
        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            return new AuthResult(false, "Password must contain at least one special character", null);
        }

        // Hash password and store user
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            return new AuthResult(false, "Error processing password", null);
        }

        users.put(username, hashedPassword);
        return new AuthResult(true, "Registration successful", null);
    }

    public boolean validateSession(String sessionToken) {
        return sessionToken != null && sessions.containsKey(sessionToken);
    }

    public void invalidateSession(String sessionToken) {
        if (sessionToken != null) {
            sessions.remove(sessionToken);
        }
    }

    public String getUsernameFromSession(String sessionToken) {
        return sessions.get(sessionToken);
    }
}
