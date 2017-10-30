package com.github.kornilova_l.formal_da;

import com.github.kornilova_l.formal_da.implementation.grid.GridAlgorithmRunner;

public class GridMain {
    public static void main(String[] args) {
        GridAlgorithmRunner algorithmRunner = GridAlgorithmRunner.createRunner(3, 4);
        algorithmRunner.run();
        System.out.println(algorithmRunner);
    }
}
