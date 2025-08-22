package com.example.network_game;

import java.awt.*;

public class AccelerationFreeze {
    Point point;
    long expiryTime;
    public AccelerationFreeze(Point point, long expiryTime) {
        this.point = point;
        this.expiryTime = expiryTime;
    }
}