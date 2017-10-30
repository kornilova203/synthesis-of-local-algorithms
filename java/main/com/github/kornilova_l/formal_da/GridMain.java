package com.github.kornilova_l.formal_da;

import com.github.kornilova_l.formal_da.implementation.grid.GridAlgorithmRunner;

public class GridMain {
    public static void main(String[] args) {
        GridAlgorithmRunner algorithmRunner = GridAlgorithmRunner.createGridAR(3, 4);
        System.out.println(algorithmRunner);
    }
}
