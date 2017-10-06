package com.github.kornilova_l.formal_da.implementation.VC3;

import com.github.kornilova_l.formal_da.simulator.vertex.Input;
import com.github.kornilova_l.formal_da.simulator.vertex.Message;
import com.github.kornilova_l.formal_da.simulator.vertex.Vertex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Vc3Vertex extends Vertex {
    private boolean isCover;

    @Override
    public void init(@Nullable Input input) {
        if (input == null) {
            isCover = false;
            return;
        }
        if (!(input instanceof Vc3Input)) {
            isCover = false;
            return;
        }
        isCover = ((Vc3Input) input).hasMatching();
    }

    @Override
    @NotNull
    public Map<Vertex, Message> send() {
        return new HashMap<>();
    }

    @Override
    public void receive(Map<Vertex, Message> messages) {

    }

    @Override
    public boolean isStopped() {
        return true;
    }

    boolean isCover() {
        return isCover;
    }
}
