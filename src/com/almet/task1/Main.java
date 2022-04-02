package com.almet.task1;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Main {

    static Comparator<Spending> amountComparator =
            (spending1, spending2) -> (int) (spending1.getAmount() - spending2.getAmount());

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int d = in.nextInt();
        Spending[] spendings = new Spending[n];
        for (int i = 0; i < n; ++i) {
            String date = in.next();
            String amountStr = in.next();
            amountStr = amountStr.replace("$", "");
            double amount = Double.parseDouble(amountStr);
            spendings[i] = new Spending(date, amount);
        }
        System.out.println(Arrays.toString(spendings));
        Sorting.sort(spendings, amountComparator);
        System.out.println(Arrays.toString(spendings));
    }
}
