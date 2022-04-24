package com.almet.task1;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Class with main logic
 * </br>
 * You can find the links to Codeforces submissions in CodeforcesSubmissions.txt file
 * (com.almet.CodeforcesSubmissions.txt)
 *
 * @author Kamil Almetov BS21-05
 */
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
        Sorting.countSortByDates(spendings);
        spendings = addEmptyDays(spendings);
        int alerts = 0;
        // last d days will be in that queue
        Queue<Spending> window = new ArrayDeque<>(d);
        Spending currentDaySpending = new Spending(LocalDate.MIN, 0);
        double median = 0;
        for (Spending spending : spendings) {
            if (window.size() < d) {
                // if currentDaySpending is the same date as spending we are considering now
                // just increase the amount of currentDaySpending
                if (spending.getDate().equals(currentDaySpending.getDate())) {
                    currentDaySpending.setAmount(currentDaySpending.getAmount() + spending.getAmount());
                } else {
                    // if something was already considered
                    // add it to the queue
                    if (!currentDaySpending.getDate().equals(LocalDate.MIN)) {
                        window.offer(currentDaySpending);
                    }
                    // update the currentDaySpending
                    currentDaySpending = spending;
                    // if the window is full, we start alerting, so calculate the median (or update)
                    if (window.size() == d) {
                        median = getMedian(window);
                    }
                }
            } else {
                // the considering spending might invoke the alert
                //1 - check the alert, update amount
                //2 - check the alert, go to the next day spending
                // if date is not new
                // just update the amount
                if (spending.getDate().equals(currentDaySpending.getDate())) {
                    currentDaySpending.setAmount(currentDaySpending.getAmount() + spending.getAmount());
                }
                // otherwise, shift the window, update the median and currentDaySpending
                else {
                    window.poll();
                    window.offer(currentDaySpending);
                    median = getMedian(window);
                    currentDaySpending = spending;
                }
            }
            // count the alert if necessary
            if (spending.getAmount() != 0 && window.size() == d) {
                if (currentDaySpending.getAmount() >= 2 * median) {
                    alerts++;
                }
            }
        }
        System.out.println(alerts);
    }

    /**
     * Function that calculates the median of the elements in the 'window' queue
     *
     * @param window - queue with spendings
     * @return median of 'window' elements
     */
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

    /**
     * Function that adds spendings with 0 amount to the 'spendings' array
     * to get rid of spaces between spending days
     *
     * @param spendings array of spendings
     * @return modified array with empty days
     */
    public static Spending[] addEmptyDays(Spending[] spendings) {
        ArrayList<Spending> result = new ArrayList<>(spendings.length);
        result.add(spendings[0]);
        for (int i = 1; i < spendings.length; i++) {
            int period = (int) ChronoUnit.DAYS.between(spendings[i - 1].getDate(), spendings[i].getDate());
            LocalDate currentDay = spendings[i - 1].getDate();
            for (int j = 0; j < period - 1; j++) {
                currentDay = currentDay.plusDays(1);
                result.add(new Spending(currentDay, 0));
            }
            result.add(spendings[i]);
        }
        return result.toArray(new Spending[0]);
    }
}

/**
 * Class sorting contains two sorting functions and some supporting functions
 * The sorting algorithms were tested using random input data
 *
 * @author Kamil Almetov BS21-05
 */
class Sorting {

    /**
     * This function is used to merge two sorted parts
     * (the first - [begin, middle], the second - [middle+1, end]) of the array:
     *
     * @param array an array that will be merged
     * @param begin starting index of the first part that will be merged
     * @param end   index of the last element of the second part that will be merged
     * @param <T>   the type of elements in the array
     */
    private static <T extends Comparable<T>> void merge(T[] array, int begin, int end) {
        T[] result = (T[]) new Comparable[end - begin + 1];
        int middle = (begin + end) / 2;
        // ind1 is for iterating through first part
        int ind1 = begin;
        // ind2 is for iterating through second part
        int ind2 = middle + 1;
        for (int i = 0; i <= end - begin; ++i) {
            // if an element from the first part is less than or equal to an element from the second part,
            // and if there are no more elements in the second part,
            // add the element from the first part
            if (ind2 > end || (ind1 <= middle && array[ind1].compareTo(array[ind2]) <= 0)) {
                result[i] = array[ind1++];
            }
            // otherwise, add the element from the second part
            else {
                result[i] = array[ind2++];
            }
        }
        // return already sorted elements in the result to their position in the initial array: [begin, end]
        System.arraycopy(result, 0, array, begin, end - begin + 1);
    }

    /**
     * This is the function that sorts elements in the array using merge sort algorithm
     *
     * @param array an array that will be sorted
     * @param begin starting index of current segment
     * @param end   index of the last element of current segment
     * @param <T>   type of elements in the array
     */
    private static <T extends Comparable<T>> void mergeSort(T[] array, int begin, int end) {
        if (end - begin + 1 == 1) return;
        int middle = (begin + end) / 2;
        mergeSort(array, begin, middle);
        mergeSort(array, middle + 1, end);
        merge(array, begin, end);
    }

    /**
     * This function sorts the whole array using private function with the same name.
     * (For convenience)
     *
     * @param array an array that will be sorted
     * @param <T>   type of elements in the array
     */
    public static <T extends Comparable<T>> void mergeSort(T[] array) {
        mergeSort(array, 0, array.length - 1);
    }

    /**
     * This function sorts the array of spendings by date using counting sort algorithm
     *
     * @param A an array that will be sorted
     */
    public static void countSortByDates(Spending[] A) {
        // first and last day
        LocalDate minDate = LocalDate.MAX, maxDate = LocalDate.MIN;
        // find them
        for (Spending spending : A) {
            if (spending.getDate().compareTo(minDate) < 0) {
                minDate = spending.getDate();
            }
            if (spending.getDate().compareTo(maxDate) > 0) {
                maxDate = spending.getDate();
            }
        }
        // period - number of considering days (between first and last day)
        int period = (int) ChronoUnit.DAYS.between(minDate, maxDate);
        // C[date] - number of appearances of 'date'
        int[] C = new int[period + 1];
        // count dates
        for (Spending spending : A) {
            C[(int) ChronoUnit.DAYS.between(minDate, spending.getDate())]++;
        }
        // make prefix sum
        for (int i = 1; i < C.length; i++) {
            C[i] += C[i - 1];
        }
        // shift all elements to the right by 1 element
        System.arraycopy(C, 0, C, 1, C.length - 1);
        C[0] = 0;
        // now for C[i] i is the position in B where to add date number i
        Spending[] B = new Spending[A.length];
        // fill B array
        for (Spending spending : A) {
            B[C[(int) ChronoUnit.DAYS.between(minDate, spending.getDate())]] = spending;
            C[(int) ChronoUnit.DAYS.between(minDate, spending.getDate())]++;
        }
        // copy result from B to A
        System.arraycopy(B, 0, A, 0, A.length);
    }
}

/**
 * Class that represents Spending structure.
 */
class Spending implements Comparable<Spending> {
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

    /**
     * Spendings are compared by their amounts
     *
     * @param t the object to be compared.
     * @return
     */
    @Override
    public int compareTo(Spending t) {
        Double amount1 = amount;
        Double amount2 = t.amount;
        return amount1.compareTo(amount2);
    }

    /**
     * @return string representation of the spending
     */
    @Override
    public String toString() {
        return date + " $" + amount;
    }

}





