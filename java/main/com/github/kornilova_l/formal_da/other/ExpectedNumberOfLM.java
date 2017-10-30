package com.github.kornilova_l.formal_da.other;

import java.util.Map;
import java.util.TreeMap;

/**
 * Expected number of local maxima
 */
public class ExpectedNumberOfLM {
    private static final int nodesCount = 10;

    private static int[] createInitArr() {
        int[] arr = new int[nodesCount];
        for (int i = 0; i < nodesCount; i++) {
            arr[i] = i;
        }
        return arr;
    }

    /**
     * Get next permutation
     * 1, 2, 3, 4, 5 -> 1, 2, 3, 5, 4
     * Code of this method from here: {@link "https://www.nayuki.io/page/next-lexicographical-permutation-algorithm"}
     */
    private static boolean nextPermutation(int[] array) {
        // Find longest non-increasing suffix
        int i = array.length - 1;
        while (i > 0 && array[i - 1] >= array[i])
            i--;
        // Now i is the head index of the suffix

        // Are we at the last permutation already?
        if (i <= 0)
            return false;

        // Let array[i - 1] be the pivot
        // Find rightmost element that exceeds the pivot
        int j = array.length - 1;
        while (array[j] <= array[i - 1])
            j--;
        // Now the value array[j] will become the new pivot
        // Assertion: j >= i

        // Swap the pivot with j
        int temp = array[i - 1];
        array[i - 1] = array[j];
        array[j] = temp;

        // Reverse the suffix
        j = array.length - 1;
        while (i < j) {
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
            i++;
            j--;
        }

        // Successfully computed the next permutation
        return true;
    }

    private static int countMaxima(int[] arr) {
        int localMaximaCount = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > arr[(i + nodesCount - 1) % nodesCount] // if a local maxima
                    && arr[i] > arr[(i + 1) % nodesCount]) {
                localMaximaCount++;
            }
        }
        return localMaximaCount;
    }

    private static void printResults(Map<Integer, Integer> countLM) {
        for (Map.Entry<Integer, Integer> entry : countLM.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        long numOfPermutations = countPermutations(countLM);
        System.out.println("Total number of permutations: " + numOfPermutations);
        double expected = 0;
        for (Map.Entry<Integer, Integer> entry : countLM.entrySet()) {
            expected += entry.getKey() * ((double) entry.getValue() / numOfPermutations);
        }
        System.out.println("Expected number of local maxima: " + expected);
    }

    private static long countPermutations(Map<Integer, Integer> countLM) {
        long res = 0;
        for (Integer val : countLM.values()) {
            res += val;
        }
        return res;
    }

    public static void main(String[] args) {
        int[] arr = createInitArr();
        Map<Integer, Integer> countLM = new TreeMap<>();
        boolean hasPermutation = true;
        while (hasPermutation) {
            int res = countMaxima(arr);
            Integer count = countLM.computeIfAbsent(res, r -> 0);
            count += 1;
            countLM.put(res, count);
            hasPermutation = nextPermutation(arr);
        }
        printResults(countLM);
    }
}
