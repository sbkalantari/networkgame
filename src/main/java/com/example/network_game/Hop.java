package com.example.network_game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Hop implements ValidDevice {
    ArrayList<Port> ports = new ArrayList<Port>();
    Queue<Pocket> pockets = new LinkedList<Pocket>();

    @Override
    public boolean IsValid() {
        for (Port p : ports)
            if (!p.IsValid()) return false;
        return true;
    }
}
