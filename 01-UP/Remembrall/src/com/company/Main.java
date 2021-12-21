package com.company;

public class Main {

    public static void main(String[] args) {
        System.out.println(Remembrall.isPhoneNumberForgettable(""));
        System.out.println(Remembrall.isPhoneNumberForgettable("498-123-123"));
        System.out.println(Remembrall.isPhoneNumberForgettable("0894 123 567"));
        System.out.println(Remembrall.isPhoneNumberForgettable("(888)-FLOWERS"));
        System.out.println(Remembrall.isPhoneNumberForgettable("(444)-greens"));
        System.out.println(Remembrall.isPhoneNumberForgettable("(444)?greens"));
        System.out.println(Remembrall.isPhoneNumberForgettable("(444)*greens"));
        System.out.println(Remembrall.isPhoneNumberForgettable("0894-123 567"));
        System.out.println(Remembrall.isPhoneNumberForgettable("0894 123-567"));
        System.out.println(Remembrall.isPhoneNumberForgettable("0894-123-567-"));
        Remembrall.isPhoneNumberForgettable("498-123-123");

    }
}
