package bg.sofia.uni.fmi.mjt.password.vault.algorithm;

import java.io.File;
import java.security.SecureRandom;
import java.util.Locale;

public class PasswordGenerator {

    private static final String LOWERCASE_ALPHABET = "abcdefghijklmnopqrstuvxyz";
    private static final String UPPERCASE_ALPHABET = LOWERCASE_ALPHABET.toUpperCase(Locale.ROOT);
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "£$&()*+[]@#^-_!?";
    private static final int MIN = 15;
    private static final int MAX = 30;
    private static final int CATEGORIES = 4;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generate() {
        int randomPasswordLength = SECURE_RANDOM.nextInt(MAX - MIN + 1) + MIN;
        String password = "";
        for (int i = 0; i < randomPasswordLength; i++) {

            int randomCategory = SECURE_RANDOM.nextInt(CATEGORIES);
            switch (randomCategory) {
                case 0 -> password += LOWERCASE_ALPHABET.charAt(SECURE_RANDOM.nextInt(LOWERCASE_ALPHABET.length()));
                case 1 -> password += UPPERCASE_ALPHABET.charAt(SECURE_RANDOM.nextInt(LOWERCASE_ALPHABET.length()));
                case 2 -> password += NUMBERS.charAt(SECURE_RANDOM.nextInt(NUMBERS.length()));
                case 3 -> password += SPECIAL_CHARACTERS.charAt(SECURE_RANDOM.nextInt(SPECIAL_CHARACTERS.length()));
            }
        }
        return password;
    }

}
