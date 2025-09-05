package com.example.network_game.model;

import java.awt.*;

public class CenterResetEffect {
    Point point;
    long expiryTime;

    public CenterResetEffect(Point point, long expiryTime) {
        this.point = point;
        this.expiryTime = expiryTime;
    }
}