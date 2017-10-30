package com.github.kornilova_l.formal_da.implementation.grid;

import com.github.kornilova_l.formal_da.simulator.vertex.Input;

class GridInput extends Input {
    private final int id;

    GridInput(int id) {
        this.id = id;
    }

    int getId() {
        return id;
    }
}
