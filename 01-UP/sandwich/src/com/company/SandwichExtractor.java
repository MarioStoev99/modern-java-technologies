package com.company;

import java.util.Arrays;

public class SandwichExtractor {

    private static final String OLIVES = "olives";

    public static String[] extractIngredients(String sandwich) {
        String[] parts = sandwich.split("bread");
        if(parts.length != 3) {
            return new String[0];
        }
        String[] ingredients = parts[1].split("-");
        int ingredientsWithoutOlivesCount = 0;
        for(String ingredient : ingredients) {
            if(!OLIVES.equals(ingredient)) {
                ++ingredientsWithoutOlivesCount;
            }
        }
        String[] ingredientsWithoutOlives = new String[ingredientsWithoutOlivesCount];
        int br = 0;
        for(String ingredient : ingredients) {
            if(!OLIVES.equals(ingredient)) {
                ingredientsWithoutOlives[br++] = ingredient;
            }
        }
        Arrays.sort(ingredientsWithoutOlives);
        return ingredientsWithoutOlives;
    }
}
