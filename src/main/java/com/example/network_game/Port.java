package com.example.network_game;

import java.awt.*;

public class Port implements ValidDevice {
    private Hop hopConnected;
    private PortType type;
    private ShapeType shape;
    private Wire wire;
    private int x, y;

    @Override
    public boolean IsValid() {
        if (hopConnected == null || wire == null)
            return false;
        return wire.IsValid();
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setHopConnected(Hop hop) { this.hopConnected = hop; }
    public void setType(PortType type) { this.type = type; }
    public void setShape(ShapeType shape) { this.shape = shape; }
    public void setWire(Wire wire) { this.wire = wire; }

    public int getX() { return x; }
    public int getY() { return y; }
    public Point getPosition() { return new Point(x, y); }
    public Hop getHopConnected() { return hopConnected; }
    public PortType getType() { return type; }
    public ShapeType getShape() { return shape; }
    public Wire getWire() { return wire; }

    public boolean contains(Point p) {
        return Math.abs(p.x - x) < 10 && Math.abs(p.y - y) < 10;
    }
}