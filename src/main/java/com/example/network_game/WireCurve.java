package com.example.network_game;

import java.awt.*;

public class WireCurve {
    private Point position;
    private double radius;
    private boolean active;

    public WireCurve(Point position, double radius) {
        this.position = new Point(position);
        this.radius = radius;
        this.active = true;
    }

    public Point getPosition() { return new Point(position); }
    public void setPosition(Point position) { this.position = new Point(position); }
    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean contains(Point point) {
        double distance = Math.sqrt(Math.pow(point.x - position.x, 2) +
                Math.pow(point.y - position.y, 2));
        return distance <= radius;
    }
}
