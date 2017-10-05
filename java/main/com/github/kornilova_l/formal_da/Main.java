package com.github.kornilova_l.formal_da;

import com.github.kornilova_l.formal_da.algorithm.AlgorithmRunner;

public class Main {
    public static void main(String[] args) {
        AlgorithmRunner algorithmRunner = ;
        algorithmRunner.initVertices();
        while (!algorithmRunner.areAllNodesStopped()) {
            algorithmRunner.doIteration();
        }
        algorithmRunner.outputResult();
    }
}
