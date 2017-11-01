package com.github.kornilova_l.formal_da.other;

import java.util.Random;

public class ProbabilityToStop {
    private static Random random = new Random(System.currentTimeMillis());

    private static boolean ifSucceeds(int n) {
        int vertex = random.nextInt(2 * n);
        for (int i = 0; i < n; i++) {
            if (random.nextInt(2 * n) == vertex) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        int iterationCount = 1000000;
        int n = 100;
        int succeedSum = 0;
        for (int i = 0; i < iterationCount; i++) {
            if (ifSucceeds(n)) {
                succeedSum++;
            }
        }
        System.out.println(succeedSum / (double) iterationCount);
    }
}
