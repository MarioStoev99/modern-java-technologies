package com.company;

public class Remembrall {

    private static final int ZERO_SEPARATOR = 0;
    private static final int ONE_SEPARATOR = 1;
    private static final int TWO_SEPARATORS = 2;

    private static final char WHITE_SPACE_SEPARATOR = ' ';
    private static final char DASH_SEPARATOR = '-';

    public static boolean isPhoneNumberForgettable(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            return false;
        }

        boolean difficultToRemember = true;
        boolean[] digitMeetings = new boolean[10];
        boolean hasLetter = false;
        int dashSeparatorCount = 0;
        int whiteSpaceSeparatorCount = 0;

        for (int i = 0; i < phoneNumber.length(); i++) {
            if (isLetter(phoneNumber.charAt(i))) {
                hasLetter = true;
            } else if (isDigit(phoneNumber.charAt(i))) {
                int digit = phoneNumber.charAt(i) - '0';
                if (digitMeetings[digit] == true) {
                    difficultToRemember = false;
                } else {
                    digitMeetings[digit] = true;
                }
            } else if (phoneNumber.charAt(i) == WHITE_SPACE_SEPARATOR) {
                whiteSpaceSeparatorCount++;
            } else if (phoneNumber.charAt(i) == DASH_SEPARATOR) {
                dashSeparatorCount++;
            }
        }
        if (hasLetter) {
            difficultToRemember = true;
            difficultToRemember &= checkSeparator(dashSeparatorCount, whiteSpaceSeparatorCount, ONE_SEPARATOR);
        } else {
            difficultToRemember &= checkSeparator(dashSeparatorCount, whiteSpaceSeparatorCount, TWO_SEPARATORS);
        }
        return difficultToRemember;
    }

    private static boolean checkSeparator(int dashSeparatorCount, int whiteSpaceSeparatorCount, int maxSeparatorsAllowed) {
        if (dashSeparatorCount == maxSeparatorsAllowed && whiteSpaceSeparatorCount == ZERO_SEPARATOR) {
            return true;
        } else if (dashSeparatorCount == ZERO_SEPARATOR && whiteSpaceSeparatorCount == maxSeparatorsAllowed) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isLetter(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

}
