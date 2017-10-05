package com.github.kornilova_l.formal_da.vertex;

import java.util.List;

public abstract class Vertex {
    public abstract void init(Input input);

    public abstract List<Message> send(List<Vertex> vertices);

    public abstract void receive(List<Message> messages);

    public abstract boolean isStopped();
}
