package com.almet.task1;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


/**
 * Class sorting contains two sorting functions and some supporting functions
 * The sorting algorithms were tested using random input data
 * @author Kamil Almetov BS21-05
 */
public class Sorting {

    /**
     * This function is used to merge two sorted parts
     * (the first - [begin, middle], the second - [middle+1, end]) of the array:
     * @param array an array that will be merged
     * @param begin starting index of the first part that will be merged
     * @param end index of the last element of the second part that will be merged
     * @param <T> the type of elements in the array
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
     * @param array an array that will be sorted
     * @param begin starting index of current segment
     * @param end index of the last element of current segment
     * @param <T> type of elements in the array
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
     * @param array an array that will be sorted
     * @param <T> type of elements in the array
     */
    public static <T extends Comparable<T>> void mergeSort(T[] array) {
        mergeSort(array, 0, array.length - 1);
    }

    /**
     * This function sorts the array of spendings by date using counting sort algorithm
     * @param A - an array that will be sorted
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
        // period - number of days considering days (between first and last day)
        int period = (int) ChronoUnit.DAYS.between(minDate, maxDate);
        // C[date] - number of appearances of 'date'
        int[] C = new int[period + 1];
        // count dates
        for (Spending spending : A) {
            C[(int)ChronoUnit.DAYS.between(minDate, spending.getDate())]++;
        }
        // make prefix sum
        for (int i = 1; i < C.length; i++) {
            C[i] += C[i-1];
        }
        // shift all elements to the right by 1 element
        System.arraycopy(C, 0, C, 1, C.length - 1);
        C[0] = 0;
        // now for C[i] i is the position where
        Spending[] B = new Spending[A.length];
        for (Spending spending : A) {
            B[C[(int)ChronoUnit.DAYS.between(minDate, spending.getDate())]] = spending;
            C[(int)ChronoUnit.DAYS.between(minDate, spending.getDate())]++;
        }
        System.arraycopy(B, 0, A, 0, A.length);
    }
}
