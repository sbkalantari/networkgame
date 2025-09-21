package com.example.network_game.model;

import java.awt.*;

public abstract class Effect {
    public Point point;
    public long expiryTime;

    Effect(Point point, long expiryTime) {
        this.point = point;
        this.expiryTime = expiryTime;
    }
}
