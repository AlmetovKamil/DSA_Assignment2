package com.almet.task2;

import java.time.LocalDate;

public class TestGenerator {
    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        int n = 20;
        int m = 20;
        System.out.println(n + m);
        for (int i = 0; i < n; i++) {
            System.out.println(date + " DEPOSIT " + 1);
            date = date.plusDays(1);
        }
        for (int i = 0; i < m; i++) {
            System.out.println("RECORD FROM " + LocalDate.now() + " TO " + LocalDate.now().plusDays(i));
        }
    }
}
