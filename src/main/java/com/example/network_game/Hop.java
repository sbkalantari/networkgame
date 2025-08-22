package com.example.network_game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Hop implements ValidDevice {
    ArrayList<Port> ports = new ArrayList<>();
    Queue<Pocket> pockets = new LinkedList<>();
    private int x, y, width, height;

    @Override
    public boolean IsValid() {
        for (Port p : ports)
            if (!p.IsValid()) return false;
        return true;
    }

    public ArrayList<Port> getPorts() {
        return ports;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}