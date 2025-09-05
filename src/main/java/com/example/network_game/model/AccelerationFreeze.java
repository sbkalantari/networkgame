package com.example.network_game.model;

import java.awt.*;

public class AccelerationFreeze {
    public Point point;
    public long expiryTime;

    public AccelerationFreeze(Point point, long expiryTime) {
        this.point = point;
        this.expiryTime = expiryTime;
    }
}