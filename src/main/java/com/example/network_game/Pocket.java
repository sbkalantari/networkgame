package com.example.network_game;

public class Pocket {
    private ShapeType shape;
    private double size;
    private double speed;
    private int coinReward;
    private Hop currentHop;
    private Wire currentWire;
    private double progress;

    public Pocket() {
        this.progress = 0;
        this.size = 2;
        this.speed = 100;
        this.coinReward = 1;
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

    public void updateProgress(double deltaTime) {
        if (currentWire == null) return;
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

    public ShapeType getShape() {
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
}