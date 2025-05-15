package com.example.network_game;

public class Port implements ValidDevice{
    private Hop hopConnected;
    private PortType type;
    private ShapeType shape;
    private Wire wire;

    @Override
    public boolean IsValid() {
        if (hopConnected == null)
            return false;
        Port dest = wire.getDestination();
        Port src = wire.getSource();
        return type == PortType.Input && src == this
                && dest.hopConnected != hopConnected && dest.type == PortType.Output ||
                type == PortType.Output && dest == this
                        && src.hopConnected != hopConnected && src.type == PortType.Input;
    }
}
