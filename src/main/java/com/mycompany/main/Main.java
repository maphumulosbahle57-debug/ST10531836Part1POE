package com.mycompany.main;

import java.util.Scanner;

public class Main {

    static Scanner input = new Scanner(System.in);

    // Stores the user's entered details
    static String username;
    static String password;
    static String cellPhone;

    // Stores the successfully registered details
    static String registeredUsername;
    static String registeredPassword;
    static String registeredCellPhone;

    public static boolean checkUsername(String username) {
        // Username must have exactly 5 characters
        // and must contain an underscore
        if (username.length() == 5 && username.contains("_")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkPasswordComplexity(String password) {
        // Password must have at least 8 characters
        // and contain uppercase, lowercase, number and special character
        if (password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkCellPhoneNumber(String number) {
        // Number must start with +27
        // followed by exactly 9 digits
        if (number.matches("^\\+27[0-9]{9}$")) {
            return true;
        } else {
            return false;
        }
    }

    public static void registerUser() {

        // Ask for username until it is valid
        while (true) {
            System.out.print("Enter your username: ");
            username = input.nextLine();

            boolean usernameCorrect = checkUsername(username);

            if (usernameCorrect) {
                break;
            } else {
                System.out.println(
                    "Username is incorrectly formatted. Please try again."
                );
            }
        }

        // Ask for password
        System.out.print("Enter your password: ");
        password = input.nextLine();

        // Ask for cellphone number
        System.out.print("Enter your cellphone number (+27): ");
        cellPhone = input.nextLine();

        // Check password and cellphone number
        boolean passwordCorrect = checkPasswordComplexity(password);
        boolean phoneCorrect = checkCellPhoneNumber(cellPhone);

        // Check if all registration details are correct
        if (passwordCorrect && phoneCorrect) {

            // Save the valid registration details
            registeredUsername = username;
            registeredPassword = password;
            registeredCellPhone = cellPhone;

            // Display success message
            System.out.println("User registered successfully.");

        } else {
            System.out.println("Registration failed.");

            if (!passwordCorrect) {
                System.out.println("Password is incorrectly formatted.");
            }

            if (!phoneCorrect) {
                System.out.println("Cellphone number is incorrectly formatted.");
            }
        }
    }

    public static boolean loginUser(String username, String password) {
        // Compare entered details with registered details
        if (username.equals(registeredUsername)
                && password.equals(registeredPassword)) {
            return true;
        } else {
            return false;
        }
    }

    public static String returnLoginStatus(boolean loginSuccessful) {

        if (loginSuccessful) {
            return "Welcome, it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    public static void main(String[] args) {

        // Call registration method
        registerUser();

        // Attempt login using the registered details
        boolean loginSuccessful = loginUser(username, password);

        // Display login status
        System.out.println(returnLoginStatus(loginSuccessful));
    }
}