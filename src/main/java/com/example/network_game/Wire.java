package com.example.network_game;

public class Wire implements ValidDevice{
    private double length;
    private Port destination;
    private Port source;

    public void setSource(Port source) { this.source = source; }
    public void setDestination(Port destination) { this.destination = destination; }
    public void setLength(double length) { this.length = length; }

    public Port getDestination() { return destination; }
    public Port getSource() { return source; }
    public double getLength() { return length; }

    @Override
    public boolean IsValid() {
        return source.getType() == PortType.Output && source != null && destination != null
                && destination.getHopConnected() != null && source.getWire() != null
                && destination.getHopConnected() != source.getHopConnected()
                && destination.getType() == PortType.Input;
    }
}