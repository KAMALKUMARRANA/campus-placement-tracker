package com.example.campusplacementtracker;

import android.util.Patterns;
import java.util.regex.Pattern;

public class ValidationHelper {
    // Regex: characters, numbers, and special characters (standard email)
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        // At least 6 chars, 1 digit, 1 special char (recommended)
        if (password.length() < 6) return false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        String specialChars = "!@#$%^&*()-_=+[]{}|;:',.<>?/";
        
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            if (specialChars.indexOf(c) != -1) hasSpecial = true;
        }
        return hasDigit && hasSpecial;
    }
}
