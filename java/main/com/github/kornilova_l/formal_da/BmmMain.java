package com.github.kornilova_l.formal_da;

import com.github.kornilova_l.formal_da.implementation.BMM.BmmAlgorithmRunner;
import com.github.kornilova_l.formal_da.simulator.AlgorithmRunner;

import java.io.File;

public class BmmMain {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Specify path to file");
            System.exit(0);
        }
        AlgorithmRunner algorithmRunner =
                BmmAlgorithmRunner.createRunner(new File(args[0]));

        algorithmRunner.run();
        algorithmRunner.outputResult();
    }
}
