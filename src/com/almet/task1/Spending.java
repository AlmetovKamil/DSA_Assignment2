package com.almet.task1;

import java.time.LocalDate;

public class Spending implements Comparable<Spending> {
    private final LocalDate date;
    private double amount;

    public Spending(LocalDate date, double amount) {
        this.date = date;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public int compareTo(Spending t) {
        Double amount1 = amount;
        Double amount2 = t.amount;
        return amount1.compareTo(amount2);
    }

    @Override
    public String toString() {
        return date + " $" + amount;
    }

}


