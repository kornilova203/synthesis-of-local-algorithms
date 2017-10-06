package com.github.kornilova_l.formal_da;

import com.github.kornilova_l.formal_da.implementation.VC3.Vc3AlgorithmRunner;
import com.github.kornilova_l.formal_da.simulator.AlgorithmRunner;

import java.io.File;

public class Vc3Main {
    public static void main(String[] args) {
        AlgorithmRunner algorithmRunner =
                Vc3AlgorithmRunner.createRunner(new File("test_resources/VC3/01.txt"));

        algorithmRunner.run();
        algorithmRunner.outputResult();
    }
}
