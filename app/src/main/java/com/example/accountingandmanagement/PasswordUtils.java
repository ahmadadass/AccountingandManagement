package com.example.accountingandmanagement;

import org.mindrot.jbcrypt.BCrypt;
public class PasswordUtils {

    /**
     * Hashes a password exactly like:
     * await bcrypt.hash(plainTextPassword, 10);
     */
    public static String hashPassword(String plainTextPassword) {
        // '10' is the log_rounds
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(10));
    }

    /**
     * Verifies a password exactly like:
     * await bcrypt.compare(password, user.password);
     */
    public static boolean verifyPassword(String plainText, String hashed) {
        try {
            return BCrypt.checkpw(plainText, hashed);
        } catch (Exception e) {
            return false;
        }
    }
}