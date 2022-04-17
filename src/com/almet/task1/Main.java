package com.almet.task1;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        int d = in.nextInt();
        Spending[] spendings = new Spending[n];
        for (int i = 0; i < n; ++i) {
            String LocalDateStr = in.next();
            String amountStr = in.next();
            amountStr = amountStr.replace("$", "");
            spendings[i] = new Spending(LocalDate.parse(LocalDateStr), Double.parseDouble(amountStr));
        }
        //System.out.println(Arrays.toString(spendings));
        Sorting.countSortByDates(spendings);
        spendings = addEmptyDays(spendings);
        //System.out.println(Arrays.toString(spendings));
        int alerts = 0;
        Queue<Spending> window = new ArrayDeque<>(d);
        Spending currentDaySpending = new Spending(LocalDate.MIN, 0);

        double median = 0;
        for (Spending spending : spendings) {
            if (window.size() < d) {
                if (spending.getDate().equals(currentDaySpending.getDate())) {
                    currentDaySpending.setAmount(currentDaySpending.getAmount() + spending.getAmount());
                } else {
                    if (!currentDaySpending.getDate().equals(LocalDate.MIN)) {
                        window.offer(currentDaySpending);
                    }
                    currentDaySpending = spending;
                    if (window.size() == d) {
                        median = getMedian(window);
                    }
                }
            } else {
                //1 - check the alert, update amount
                //2 - check the alert, go to the next day spending

                if (spending.getDate().equals(currentDaySpending.getDate())) {
                    currentDaySpending.setAmount(currentDaySpending.getAmount() + spending.getAmount());
                } else {
                    window.poll();
                    window.offer(currentDaySpending);
                    median = getMedian(window);
                    currentDaySpending = spending;
                }
            }
            if (spending.getAmount() != 0 && window.size() == d) {
                if (currentDaySpending.getAmount() >= 2 * median) {
                    alerts++;
                }
            }
        }

        System.out.println(alerts);
    }

    public static double getMedian(Queue<Spending> window) {
        Spending[] spendings = window.toArray(new Spending[0]);
        Sorting.mergeSort(spendings);
        int mid = spendings.length / 2;
        if (window.size() % 2 == 1) {
            return spendings[mid].getAmount();
        } else {
            return (spendings[mid - 1].getAmount() + spendings[mid].getAmount()) / 2;
        }
    }

    public static Spending[] addEmptyDays(Spending[] spendings) {
        ArrayList<Spending> result = new ArrayList<>(spendings.length);
        result.add(spendings[0]);
        for (int i = 1; i < spendings.length; i++) {
            int period = (int) ChronoUnit.DAYS.between(spendings[i-1].getDate(), spendings[i].getDate());
            LocalDate currentDay = spendings[i-1].getDate();
            for (int j = 0; j < period - 1; j++) {
                currentDay = currentDay.plusDays(1);
                result.add(new Spending(currentDay, 0));
            }
            result.add(spendings[i]);
        }
        return result.toArray(new Spending[0]);
    }
}

