package com.github.kornilova_l.formal_da;

import com.github.kornilova_l.formal_da.implementation.VC3.Vc3AlgorithmRunner;
import com.github.kornilova_l.formal_da.simulator.AlgorithmRunner;

import java.io.File;

public class Vc3Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Specify path to file");
            System.exit(0);
        }
        AlgorithmRunner algorithmRunner =
                Vc3AlgorithmRunner.createRunner(new File(args[0]));

        algorithmRunner.run();
        algorithmRunner.outputResult();
    }
}
