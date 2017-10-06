package com.github.kornilova_l.formal_da;

import com.github.kornilova_l.formal_da.algorithm.AlgorithmRunner;
import com.github.kornilova_l.formal_da.implementation.BMM.BmmAlgorithmRunner;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        AlgorithmRunner algorithmRunner =
                BmmAlgorithmRunner.createRunner(new File("test_resources/BMM/01.txt"));

        algorithmRunner.initVertices();
        while (!algorithmRunner.areAllNodesStopped()) {
            algorithmRunner.doIteration();
        }
        algorithmRunner.outputResult();
    }
}
