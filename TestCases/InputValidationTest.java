// InputValidationTest.java

// Import required JUnit packages for testing
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class InputValidationTest {
    // Test class variables
    private UserManager userManager;
    private Email email;

    // This method runs before each test
    @BeforeEach
    void setUp() {
        // Initialize with null connection since we're only testing validation
        userManager = new UserManager(null);
        email = new Email();
    }

    // USERNAME VALIDATION TESTS

    @Test
    // Test for a perfectly valid username
    void testValidUsername() {
        String validUsername = "john.doe123";
        assertTrue(isValidUsername(validUsername),
            "Username should be valid with letters, numbers, and dots");
    }

    @Test
    // Test username that's too short (less than 6 characters)
    void testUsernameTooShort() {
        String shortUsername = "john";
        assertFalse(isValidUsername(shortUsername),
            "Username less than 6 characters should be invalid");
    }

    @Test
    // Test username that's too long (more than 30 characters)
    void testUsernameTooLong() {
        String longUsername = "thisusernameiswaytoolongtobevalid123456789";
        assertFalse(isValidUsername(longUsername),
            "Username more than 30 characters should be invalid");
    }

    @Test
    // Test username with invalid characters
    void testUsernameInvalidCharacters() {
        String invalidUsername = "john@doe";  // @ is not allowed
        assertFalse(isValidUsername(invalidUsername),
            "Username with special characters should be invalid");
    }

    @Test
    // Test username starting with a dot
    void testUsernameStartsWithDot() {
        String invalidUsername = ".johndoe";
        assertFalse(isValidUsername(invalidUsername),
            "Username starting with dot should be invalid");
    }

    @Test
    // Test username with consecutive dots
    void testUsernameConsecutiveDots() {
        String invalidUsername = "john..doe";
        assertFalse(isValidUsername(invalidUsername),
            "Username with consecutive dots should be invalid");
    }

    // PASSWORD VALIDATION TESTS

    @Test
    // Test for a perfectly valid password
    void testValidPassword() {
        String validPassword = "SecurePass123!";
        assertTrue(isValidPassword(validPassword),
            "Password with proper complexity should be valid");
    }

    @Test
    // Test password that's too short
    void testPasswordTooShort() {
        String shortPassword = "Pass1!";
        assertFalse(isValidPassword(shortPassword),
            "Password less than 8 characters should be invalid");
    }

    @Test
    // Test password missing uppercase letter
    void testPasswordNoUppercase() {
        String noUpperPassword = "securepass123!";
        assertFalse(isValidPassword(noUpperPassword),
            "Password without uppercase should be invalid");
    }

    @Test
    // Test password missing number
    void testPasswordNoNumber() {
        String noNumberPassword = "SecurePass!";
        assertFalse(isValidPassword(noNumberPassword),
            "Password without number should be invalid");
    }

    @Test
    // Test password missing special character
    void testPasswordNoSpecial() {
        String noSpecialPassword = "SecurePass123";
        assertFalse(isValidPassword(noSpecialPassword),
            "Password without special character should be invalid");
    }

    // Helper methods to simulate validation logic
    // In real implementation, these would be in UserManager class
    private boolean isValidUsername(String username) {
        if (username == null || username.length() < 6 || username.length() > 30) {
            return false;
        }

        // Check if username contains only allowed characters
        if (!username.matches("^[a-zA-Z0-9._]+$")) {
            return false;
        }

        // Check if username starts or ends with dot
        if (username.startsWith(".") || username.endsWith(".")) {
            return false;
        }

        // Check for consecutive dots
        if (username.contains("..")) {
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()].*");

        return hasUpper && hasLower && hasNumber && hasSpecial;
    }
}

/*
HOW THIS WORKS:

1. Test Setup (@BeforeEach):
   - Before each test method runs, setUp() is called
   - Creates fresh instances of UserManager and Email
   - Ensures each test starts with clean objects

2. Test Methods (@Test):
   - Each method tests one specific aspect of validation
   - Name clearly indicates what's being tested
   - Contains assertion(s) to verify expected behavior
   - Includes descriptive message for test failure

3. Assertions:
   - assertTrue(): Verifies condition is true
   - assertFalse(): Verifies condition is false
   - Failure messages explain what went wrong

4. Test Organization:
   - Grouped by functionality (username, password)
   - Each test covers one specific case
   - Both valid and invalid scenarios are tested

5. Running Tests:
   - JUnit runs each @Test method independently
   - Reports success/failure for each test
   - Shows detailed error messages if tests fail

6. Test Coverage:
   - Tests boundary conditions (length limits)
   - Tests special cases (dots, special characters)
   - Tests all password complexity requirements

USAGE:
1. Put this file in src/test/java directory
2. Make sure JUnit dependencies are in build.gradle
3. Run tests with: gradle test
4. View results in build/reports/tests/test/index.html
*/
