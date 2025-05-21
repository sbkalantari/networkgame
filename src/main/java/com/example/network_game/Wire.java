package com.example.network_game;

public class Wire implements ValidDevice {
    private Port source;
    private Port destination;
    private double length;
    private long creationTime;

    public Wire() {
        this.creationTime = System.currentTimeMillis(); 
    }

    public Port getSource() {
        return source;
    }

    public void setSource(Port source) {
        this.source = source;
    }

    public Port getDestination() {
        return destination;
    }

    public void setDestination(Port destination) {
        this.destination = destination;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public boolean IsValid() {
        return source.getType() == PortType.Output && source != null && destination != null
                && destination.getHopConnected() != null && source.getWire() != null
                && destination.getHopConnected() != source.getHopConnected()
                && destination.getType() == PortType.Input
                && destination.getShape() == source.getShape();
    }
}