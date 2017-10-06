package com.github.kornilova_l.formal_da;

import com.github.kornilova_l.formal_da.implementation.BMM.BmmAlgorithmRunner;
import com.github.kornilova_l.formal_da.simulator.AlgorithmRunner;

import java.io.File;

public class BmmMain {
    public static void main(String[] args) {
        AlgorithmRunner algorithmRunner =
                BmmAlgorithmRunner.createRunner(new File("test_resources/BMM/01.txt"));

        algorithmRunner.run();
        algorithmRunner.outputResult();
    }
}
