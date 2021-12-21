package com.company;

public class SocialDistanceMaximizer {

    public static int maxDistance(int[] seats) {
        int maxZeroSequence = 0;
        int biggestSubsequenceStart = 0;
        int biggetSubsequenceEnd = 0;
        int currentZeroSequence = 0;
        int start = 0;
        for (int i = 0; i < seats.length; ++i) {
            if (seats[i] == 0) {
                ++currentZeroSequence;
            } else {
                if (currentZeroSequence > maxZeroSequence) {
                    biggestSubsequenceStart = start;
                    biggetSubsequenceEnd = seats[i];
                    maxZeroSequence = currentZeroSequence;
                }
                currentZeroSequence = 0;
                start = seats[i];
            }
        }
        if (currentZeroSequence > maxZeroSequence) {
            biggestSubsequenceStart = start;
            biggetSubsequenceEnd = seats[seats.length - 1];
            maxZeroSequence = currentZeroSequence;
        }
        if (biggestSubsequenceStart == biggetSubsequenceEnd) {
            return maxZeroSequence / 2 + 1;
        } else {
            return maxZeroSequence;
        }
    }
}
