package com.almet.task3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestGenerator {
    public static void main(String[] args) throws IOException {
        int n = 100000;
        FileWriter out = new FileWriter("testInput.txt");
        BufferedWriter writer = new BufferedWriter(out);

        System.out.println(2*n);
        for (int i = n - 1; i >= 0; i--) {
            //System.out.println("ADD " + i + " " + i);
            writer.write("ADD " + i + " " + i);
            //System.out.println("PRINT_MIN");
        }
        for (int i = 0; i < n; i++) {
            //System.out.println("PRINT_MIN");
            writer.write("PRINT_MIN");
        }
    }
}
