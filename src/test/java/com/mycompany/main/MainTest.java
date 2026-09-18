package com.mycompany.main;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Main.
 *
 * Main keeps its state in static fields, so every test resets those fields
 * first. Tests live in the same package so they can read and write them.
 */
public class MainTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void resetState() {
        Main.username = null;
        Main.password = null;
        Main.cellPhone = null;
        Main.registeredUsername = null;
        Main.registeredPassword = null;
        Main.registeredCellPhone = null;

        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        Main.input = new Scanner(System.in);
    }

    /** Feeds the given lines to Main's Scanner so registerUser() can be driven. */
    private void supplyInput(String... lines) {
        String data = String.join(System.lineSeparator(), lines) + System.lineSeparator();
        Main.input = new Scanner(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)));
    }

    private String consoleOutput() {
        return capturedOut.toString(StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------------
    // checkUsername
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("checkUsername")
    class CheckUsername {

        @Test
        @DisplayName("accepts 5 characters containing an underscore")
        void acceptsValidUsername() {
            assertTrue(Main.checkUsername("kyl_1"));
        }

        @Test
        @DisplayName("accepts an underscore in any position")
        void acceptsUnderscoreAnywhere() {
            assertTrue(Main.checkUsername("_abcd"));
            assertTrue(Main.checkUsername("abcd_"));
            assertTrue(Main.checkUsername("ab_cd"));
        }

        @Test
        @DisplayName("rejects a username without an underscore")
        void rejectsMissingUnderscore() {
            assertFalse(Main.checkUsername("kyle1"));
        }

        @Test
        @DisplayName("rejects a username shorter than 5 characters")
        void rejectsTooShort() {
            assertFalse(Main.checkUsername("ky_1"));
        }

        @Test
        @DisplayName("rejects a username longer than 5 characters")
        void rejectsTooLong() {
            assertFalse(Main.checkUsername("kyle_1"));
        }

        @Test
        @DisplayName("rejects an empty username")
        void rejectsEmpty() {
            assertFalse(Main.checkUsername(""));
        }
    }

    // ------------------------------------------------------------------
    // checkPasswordComplexity
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("checkPasswordComplexity")
    class CheckPasswordComplexity {

        @Test
        @DisplayName("accepts a password meeting every rule")
        void acceptsValidPassword() {
            assertTrue(Main.checkPasswordComplexity("Ch&&sec@ke99!"));
        }

        @Test
        @DisplayName("accepts a password of exactly 8 characters")
        void acceptsBoundaryLength() {
            assertTrue(Main.checkPasswordComplexity("Ab1!cdef"));
        }

        @Test
        @DisplayName("rejects a password shorter than 8 characters")
        void rejectsTooShort() {
            assertFalse(Main.checkPasswordComplexity("Ab1!cde"));
        }

        @Test
        @DisplayName("rejects a password with no uppercase letter")
        void rejectsNoUppercase() {
            assertFalse(Main.checkPasswordComplexity("ab1!cdefg"));
        }

        @Test
        @DisplayName("rejects a password with no lowercase letter")
        void rejectsNoLowercase() {
            assertFalse(Main.checkPasswordComplexity("AB1!CDEFG"));
        }

        @Test
        @DisplayName("rejects a password with no digit")
        void rejectsNoDigit() {
            assertFalse(Main.checkPasswordComplexity("Abc!defgh"));
        }

        @Test
        @DisplayName("rejects a password with no special character")
        void rejectsNoSpecialCharacter() {
            assertFalse(Main.checkPasswordComplexity("Abc1defgh"));
        }

        @Test
        @DisplayName("rejects an empty password")
        void rejectsEmpty() {
            assertFalse(Main.checkPasswordComplexity(""));
        }
    }

    // ------------------------------------------------------------------
    // checkCellPhoneNumber
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("checkCellPhoneNumber")
    class CheckCellPhoneNumber {

        @Test
        @DisplayName("accepts +27 followed by exactly 9 digits")
        void acceptsValidNumber() {
            assertTrue(Main.checkCellPhoneNumber("+27838968976"));
        }

        @Test
        @DisplayName("rejects a number missing the international code")
        void rejectsMissingCode() {
            assertFalse(Main.checkCellPhoneNumber("0838968976"));
        }

        @Test
        @DisplayName("rejects a number missing the plus sign")
        void rejectsMissingPlus() {
            assertFalse(Main.checkCellPhoneNumber("27838968976"));
        }

        @Test
        @DisplayName("rejects a number with fewer than 9 digits after +27")
        void rejectsTooFewDigits() {
            assertFalse(Main.checkCellPhoneNumber("+2783896897"));
        }

        @Test
        @DisplayName("rejects a number with more than 9 digits after +27")
        void rejectsTooManyDigits() {
            assertFalse(Main.checkCellPhoneNumber("+278389689761"));
        }

        @Test
        @DisplayName("rejects a number containing non-digit characters")
        void rejectsNonDigits() {
            assertFalse(Main.checkCellPhoneNumber("+2783896897a"));
            assertFalse(Main.checkCellPhoneNumber("+27 83 896 8976"));
        }

        @Test
        @DisplayName("rejects the wrong country code")
        void rejectsWrongCountryCode() {
            assertFalse(Main.checkCellPhoneNumber("+44838968976"));
        }

        @Test
        @DisplayName("rejects an empty number")
        void rejectsEmpty() {
            assertFalse(Main.checkCellPhoneNumber(""));
        }
    }

    // ------------------------------------------------------------------
    // registerUser
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("registerUser")
    class RegisterUser {

        @Test
        @DisplayName("stores the details and reports success when everything is valid")
        void registersValidUser() {
            supplyInput("kyl_1", "Ch&&sec@ke99!", "+27838968976");

            Main.registerUser();

            assertEquals("kyl_1", Main.registeredUsername);
            assertEquals("Ch&&sec@ke99!", Main.registeredPassword);
            assertEquals("+27838968976", Main.registeredCellPhone);
            assertTrue(consoleOutput().contains("User registered successfully."));
        }

        @Test
        @DisplayName("re-prompts until a correctly formatted username is entered")
        void repromptsForBadUsername() {
            supplyInput("kyle!!!!", "kyle1", "kyl_1", "Ch&&sec@ke99!", "+27838968976");

            Main.registerUser();

            assertEquals("kyl_1", Main.registeredUsername);
            String out = consoleOutput();
            assertTrue(out.contains("Username is incorrectly formatted. Please try again."));
            assertTrue(out.contains("User registered successfully."));
        }

        @Test
        @DisplayName("fails and reports the password when the password is badly formatted")
        void failsOnBadPassword() {
            supplyInput("kyl_1", "password", "+27838968976");

            Main.registerUser();

            assertNull(Main.registeredUsername);
            assertNull(Main.registeredPassword);
            assertNull(Main.registeredCellPhone);
            String out = consoleOutput();
            assertTrue(out.contains("Registration failed."));
            assertTrue(out.contains("Password is incorrectly formatted."));
            assertFalse(out.contains("Cellphone number is incorrectly formatted."));
        }

        @Test
        @DisplayName("fails and reports the number when the cellphone is badly formatted")
        void failsOnBadCellPhone() {
            supplyInput("kyl_1", "Ch&&sec@ke99!", "0838968976");

            Main.registerUser();

            assertNull(Main.registeredCellPhone);
            String out = consoleOutput();
            assertTrue(out.contains("Registration failed."));
            assertTrue(out.contains("Cellphone number is incorrectly formatted."));
            assertFalse(out.contains("Password is incorrectly formatted."));
        }

        @Test
        @DisplayName("reports both problems when password and number are badly formatted")
        void failsOnBothFields() {
            supplyInput("kyl_1", "password", "0838968976");

            Main.registerUser();

            String out = consoleOutput();
            assertTrue(out.contains("Password is incorrectly formatted."));
            assertTrue(out.contains("Cellphone number is incorrectly formatted."));
        }
    }

    // ------------------------------------------------------------------
    // loginUser
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("loginUser")
    class LoginUser {

        @BeforeEach
        void registerAUser() {
            Main.registeredUsername = "kyl_1";
            Main.registeredPassword = "Ch&&sec@ke99!";
            Main.registeredCellPhone = "+27838968976";
        }

        @Test
        @DisplayName("succeeds when both credentials match")
        void succeedsWithMatchingCredentials() {
            assertTrue(Main.loginUser("kyl_1", "Ch&&sec@ke99!"));
        }

        @Test
        @DisplayName("fails when the password is wrong")
        void failsWithWrongPassword() {
            assertFalse(Main.loginUser("kyl_1", "WrongP@ss1"));
        }

        @Test
        @DisplayName("fails when the username is wrong")
        void failsWithWrongUsername() {
            assertFalse(Main.loginUser("abc_1", "Ch&&sec@ke99!"));
        }

        @Test
        @DisplayName("is case sensitive")
        void isCaseSensitive() {
            assertFalse(Main.loginUser("KYL_1", "Ch&&sec@ke99!"));
            assertFalse(Main.loginUser("kyl_1", "ch&&sec@ke99!"));
        }
    }

    // ------------------------------------------------------------------
    // returnLoginStatus
    // ------------------------------------------------------------------

    @Nested
    @DisplayName("returnLoginStatus")
    class ReturnLoginStatus {

        @Test
        @DisplayName("returns the welcome message on a successful login")
        void returnsWelcomeMessage() {
            assertEquals("Welcome, it is great to see you again.",
                    Main.returnLoginStatus(true));
        }

        @Test
        @DisplayName("returns the error message on a failed login")
        void returnsErrorMessage() {
            assertEquals("Username or password incorrect, please try again.",
                    Main.returnLoginStatus(false));
        }
    }

    // ------------------------------------------------------------------
    // End-to-end
    // ------------------------------------------------------------------

    @Test
    @DisplayName("a registered user can log in straight afterwards")
    void registerThenLogin() {
        supplyInput("kyl_1", "Ch&&sec@ke99!", "+27838968976");

        Main.registerUser();
        boolean loginSuccessful = Main.loginUser(Main.username, Main.password);

        assertTrue(loginSuccessful);
        assertEquals("Welcome, it is great to see you again.",
                Main.returnLoginStatus(loginSuccessful));
    }
}
