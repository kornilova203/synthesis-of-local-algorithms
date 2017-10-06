package com.github.kornilova_l.formal_da.implementation.VC3;

import com.github.kornilova_l.formal_da.simulator.vertex.Input;

public final class Vc3Input extends Input {
    private final boolean hasMatching;

    Vc3Input(boolean hasMatching) {
        this.hasMatching = hasMatching;
    }

    public boolean hasMatching() {
        return hasMatching;
    }
}
