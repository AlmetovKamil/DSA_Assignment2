package com.almet.task1;

import java.util.Comparator;

public class Sorting {
    private static <T extends Comparable<T>> int compare(T t1, T t2, Comparator<T> comparator) {
        if (comparator == null) return t1.compareTo(t2);
        else return comparator.compare(t1, t2);
    }

    private static <T extends Comparable<T>> void sort(T[] array, int begin, int end, Comparator<T> comparator) {
        if (end - begin + 1 == 1) return;
        int middle = (begin + end) / 2;
        sort(array, begin, middle, comparator);
        sort(array, middle + 1, end, comparator);
        merge(array, begin, end, comparator);
    }

    private static <T extends Comparable<T>> void merge(T[] array, int begin, int end, Comparator<T> comparator) {
        T[] result = (T[]) new Comparable[end - begin + 1];
        int middle = (begin + end) / 2;
        int ind1 = begin;
        int ind2 = middle + 1;
        for (int i = 0; i <= end - begin; ++i) {
            if (ind2 > end || (ind1 <= middle && compare(array[ind1], array[ind2], comparator) <= 0)) {
                result[i] = array[ind1++];
            } else {
                result[i] = array[ind2++];
            }
        }
        System.arraycopy(result, 0, array, begin, end - begin + 1);
    }

    public static <T extends Comparable<T>> void sort(T[] array) {
        sort(array, 0, array.length - 1, null);
    }

    public static <T extends Comparable<T>> void sort(T[] array, Comparator<T> comparator) {
        sort(array, 0, array.length - 1, comparator);
    }
}
