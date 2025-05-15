package com.example.network_game;

import javax.swing.*;
import java.awt.*;

public class GameView extends JPanel {
    private GameModel model;
    private Point mousePos;

    public GameView(GameModel model) {
        this.model = model;
        this.mousePos = new Point();
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);
    }

    public void setMousePos(Point mousePos) {
        this.mousePos = mousePos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < model.getSystems().size(); i++) {
            Hop system = model.getSystems().get(i);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(system.getX(), system.getY(), system.getWidth(), system.getHeight());
            g2d.setColor(Color.BLACK);
            g2d.drawRect(system.getX(), system.getY(), system.getWidth(), system.getHeight());

            if (i == 0) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.drawString("S", system.getX() + 5, system.getY() + 15);
            }

            int ledX = system.getX() + system.getWidth() / 2 - 5;
            int ledY = system.getY() + 5;
            if (system.IsValid()) {
                g2d.setColor(Color.BLUE);
            } else {
                g2d.setColor(new Color(128, 128, 128, 100));
            }
            g2d.fillOval(ledX, ledY, 10, 10);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawOval(ledX, ledY, 10, 10);

            for (Port port : system.ports) {
                g2d.setColor(port == model.getSelectedPort() ? Color.RED : Color.BLUE);
                g2d.fillRect(port.getX() - 5, port.getY() - 5, 10, 10);
            }
        }

        for (Wire wire : model.getWires()) {
            Point src = wire.getSource().getPosition();
            Point dest = wire.getDestination().getPosition();
            if (wire.IsValid())
                g2d.setColor(Color.BLACK);
            else
                g2d.setColor(Color.RED);
            g2d.drawLine(src.x, src.y, dest.x, dest.y);
        }

        for (Pocket pocket : model.getPockets()) {
            if (pocket.isLost() || pocket.getCurrentWire() == null) continue;
            Point src = pocket.getCurrentWire().getSource().getPosition();
            Point dest = pocket.getCurrentWire().getDestination().getPosition();
            Point center = pocket.getCenterOfMass(src, dest);
            if (center == null) continue;

            
            Shape shape = pocket.getShape(center);
            g2d.setColor(pocket.getShapeType() == ShapeType.Square ? Color.GREEN : Color.YELLOW);
            g2d.fill(shape);

            
            g2d.setColor(Color.BLACK);
            g2d.fillOval(center.x - 1, center.y - 1, 2, 2);
        }

        if (model.getSelectedPort() != null) {
            g2d.setColor(model.isTemporaryWireValid(mousePos) ? Color.BLUE : Color.RED);
            g2d.drawLine(model.getSelectedPort().getX(), model.getSelectedPort().getY(), mousePos.x, mousePos.y);
        }
    }
}