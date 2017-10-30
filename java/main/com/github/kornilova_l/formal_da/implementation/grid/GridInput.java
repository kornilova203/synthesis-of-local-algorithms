package com.github.kornilova_l.formal_da.implementation.grid;

import com.github.kornilova_l.formal_da.simulator.vertex.Input;

class GridInput extends Input {
    private final int id;
    /**
     * power of the grid
     */
    private final int k;

    GridInput(int id, int k) {
        this.id = id;
        this.k = k;
    }

    int getId() {
        return id;
    }

    int getK() {
        return k;
    }
}
