package com.example.network_game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Pocket {
    private ShapeType shape;
    private double size;
    private double speed;
    private int coinReward;
    private Hop currentHop;
    private Wire currentWire;
    private double progress;
    private boolean lost;
    private Point shapeOffset;
    //TODO fix dynamic health
    private int health;
    private Pocket lastHit;

    public Pocket() {
        this.progress = 0;
        this.lost = false;
        this.size = 2;
        this.speed = 100;
        this.coinReward = 1;
        this.shapeOffset = new Point(0, 0);
        this.health = 2;
    }

    public void setShape(ShapeType shape) {
        this.shape = shape;
        if (shape == ShapeType.Square) {
            this.size = 2;
            this.coinReward = 1;
            this.speed = 100;
        } else {
            this.size = 3;
            this.coinReward = 2;
            this.speed = 120;
        }
    }

    public boolean gotHit(Pocket pocket){
        this.health--;
        this.lastHit = pocket;
        if (health == 0) {
            this.lost = true;
            return true;
        }
        return false;
    }

    public void updateProgress(double deltaTime) {
        if (currentWire == null || lost) return;
        Port destPort = currentWire.getDestination();
        double effectiveSpeed = speed;
        if (destPort.getShape() != shape) {
            if (shape == ShapeType.Square) {
                effectiveSpeed *= 0.5;
            } else {
                effectiveSpeed *= 1.5;
            }
        }
        double distance = Math.sqrt(
                Math.pow(currentWire.getDestination().getX() - currentWire.getSource().getX(), 2) +
                        Math.pow(currentWire.getDestination().getY() - currentWire.getSource().getY(), 2)
        );
        progress += (effectiveSpeed * deltaTime) / distance;
        if (progress > 1) progress = 1;
    }

    public Shape getShape(Point center) {
        int x = center.x + shapeOffset.x;
        int y = center.y + shapeOffset.y;
        if (shape == ShapeType.Square) {
            return new Rectangle2D.Double(x - 8, y - 8, 16, 16); 
        } else {
            return new Polygon(
                    new int[]{x, x - 10, x + 10}, 
                    new int[]{y - 10, y + 10, y + 10}, 
                    3
            );
        }
    }

    public void setLost(boolean lost) {
        this.lost = lost;
    }

    public boolean isLost() {
        return lost;
    }

    public Point getCenterOfMass(Point src, Point dest) {
        if (currentWire == null) return null;
        double t = progress;
        int x = (int)(src.x * (1 - t) + dest.x * t);
        int y = (int)(src.y * (1 - t) + dest.y * t);
        return new Point(x, y);
    }

    public void applyImpactDisplacement(Point collisionPoint, Point src, Point dest) {
        if (currentWire == null || lost) return;
        Point center = getCenterOfMass(src, dest);
        double distance = Math.sqrt(Math.pow(center.x - collisionPoint.x, 2) + Math.pow(center.y - collisionPoint.y, 2));
        double radius = 100.0;
        if (distance < radius && distance > 0) {
            double strength = (radius - distance) / radius;
            double maxDisplacement = 5.0;
            
            double wireDx = dest.x - src.x;
            double wireDy = dest.y - src.y;
            double wireLength = Math.sqrt(wireDx * wireDx + wireDy * wireDy);
            if (wireLength == 0) return;
            
            double perpDx = -wireDy / wireLength;
            double perpDy = wireDx / wireLength;
            
            double dx = center.x - collisionPoint.x;
            double dy = center.y - collisionPoint.y;
            
            double dot = dx * perpDx + dy * perpDy;
            double displacement = strength * maxDisplacement * Math.signum(dot);
            shapeOffset.x += (int)(perpDx * displacement);
            shapeOffset.y += (int)(perpDy * displacement);
        }
    }

    public boolean isCenterOfMassOutsideShape() {
        if (currentWire == null) return false;
        Point center = getCenterOfMass(currentWire.getSource().getPosition(), currentWire.getDestination().getPosition());
        Shape shape = getShape(center);
        return !shape.contains(center.x, center.y);
    }

    public ShapeType getShapeType() {
        return shape;
    }

    public double getSize() {
        return size;
    }

    public int getCoinReward() {
        return coinReward;
    }

    public Hop getCurrentHop() {
        return currentHop;
    }

    public void setCurrentHop(Hop hop) {
        this.currentHop = hop;
    }

    public Wire getCurrentWire() {
        return currentWire;
    }

    public void setCurrentWire(Wire wire) {
        this.currentWire = wire;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public Pocket getLastHit() {
        return lastHit;
    }

    public void setLastHit(Pocket lastHit) {
        this.lastHit = lastHit;
    }
}