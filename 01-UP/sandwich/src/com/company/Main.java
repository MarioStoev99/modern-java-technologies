package com.company;

public class Main {
    private static void print(String[] ingredients) {
        System.out.print("{");
        for(int i = 0;i < ingredients.length;++i) {
            System.out.print(ingredients[i]);
            if(ingredients.length - 1 != i) {
                System.out.print(",");
            }
        }
        System.out.println("}");
    }
    public static void main(String[] args) {
        String[] ingredients = SandwichExtractor.extractIngredients("asdbreadham-tomato-mayobreadblabla");
        print(ingredients);
        String[] ingredients1 = SandwichExtractor.extractIngredients("asdbreadham-olives-tomato-olives-mayobreadblabla");
        print(ingredients1);
        String[] ingredients2 = SandwichExtractor.extractIngredients("asdbreadham");
        print(ingredients2);
    }
}
