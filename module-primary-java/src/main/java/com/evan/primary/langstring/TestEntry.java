package com.evan.primary.langstring;

public class TestEntry {
    public static void main(String[] args) {
        System.out.println("TestEntry. main");
        SuperClass subClass = new SubClass();
        System.out.println("TestEntry. subClass = "+subClass);
    }
}
