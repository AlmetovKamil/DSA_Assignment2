package com.almet.task3;

public class TestGenerator {
    public static void main(String[] args) {
        int n = 10;
        System.out.println(2*n);
        for (int i = 0; i < n; i++) {
            System.out.println("ADD " + i + " " + i);
        }
        for (int i = 0; i < n; i++) {
            System.out.println("PRINT_MIN");
        }
    }
}
