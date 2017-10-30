package com.github.kornilova_l.formal_da.implementation.grid;

import com.github.kornilova_l.formal_da.simulator.vertex.Input;
import com.github.kornilova_l.formal_da.simulator.vertex.Message;
import com.github.kornilova_l.formal_da.simulator.vertex.Vertex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GridVertex extends Vertex {
    private int id = 0;

    @Override
    public void init(@Nullable Input input) {
        if (input instanceof GridInput) {
            this.id = ((GridInput) input).getId();
        } else {
            throw new IllegalArgumentException("Input must be a GridInput instance");
        }
    }

    @Override
    public @NotNull Map<Vertex, Message> send() {
        return null;
    }

    @Override
    public void receive(Map<Vertex, Message> messages) {

    }

    @Override
    public boolean isStopped() {
        return true;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }
}
