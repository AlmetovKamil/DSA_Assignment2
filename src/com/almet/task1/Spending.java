package com.almet.task1;

import java.util.Comparator;

public class Spending implements Comparable<Spending> {
    private final String date;
    private final double amount;

    public Spending(String date, double amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public int compareTo(Spending t) {
        return date.compareTo(t.date);
    }

    @Override
    public String toString() {
        return date + " $" + amount;
    }

}


