package com.company;


public class Main {

    public static void main(String[] args) {
        int[] seats1 = {1,0,0,0,1,0,1};
        int[] seats2 = {1,0,0,0};
        int[] seats3 = {0,1};
        int[] seats5 = {1,0,0,0,0,0,0,1};
        System.out.println(SocialDistanceMaximizer.maxDistance(seats1));
        System.out.println(SocialDistanceMaximizer.maxDistance(seats2));
        System.out.println(SocialDistanceMaximizer.maxDistance(seats3));
        System.out.println(SocialDistanceMaximizer.maxDistance(seats5));
    }
}
