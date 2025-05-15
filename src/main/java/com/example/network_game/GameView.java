package com.example.network_game;

import javax.swing.*;
import java.awt.*;

public class GameView extends JPanel {
    private GameModel model;
    private Point mousePos;
    private JButton returnToMenuButton;
    private JButton nextStageButton;

    public GameView(GameModel model) {
        this.model = model;
        this.mousePos = new Point();
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);

        
        returnToMenuButton = new JButton("Return to Menu");
        returnToMenuButton.setBounds(350, 400, 150, 30);
        returnToMenuButton.setVisible(false);
        add(returnToMenuButton);

        nextStageButton = new JButton("Next Stage");
        nextStageButton.setBounds(350, 400, 150, 30);
        nextStageButton.setVisible(false);
        add(nextStageButton);
    }

    public void setMousePos(Point mousePos) {
        this.mousePos = mousePos;
        repaint();
    }

    public JButton getReturnToMenuButton() {
        return returnToMenuButton;
    }

    public JButton getNextStageButton() {
        return nextStageButton;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        
        int barX = 10;
        int barY = 10;
        int barWidth = 780;
        int barHeight = 20;
        double totalEffectiveLength = model.getTotalWireLength();
        if (model.getSelectedPort() != null) {
            double tempLength = calculateWireLength(model.getSelectedPort().getPosition(), mousePos);
            totalEffectiveLength += tempLength;
        }
        g2d.setColor(Color.BLACK);
        g2d.drawRect(barX, barY, barWidth, barHeight);
        double remainingWire = Math.max(0, model.getMaxWireLength() - totalEffectiveLength);
        int filledWidth = (int) (remainingWire / model.getMaxWireLength() * barWidth);
        g2d.setColor(model.getSelectedPort() != null && !model.isTemporaryWireValid(mousePos) ? Color.YELLOW : Color.GREEN);
        g2d.fillRect(barX + 1, barY + 1, Math.max(0, filledWidth - 1), barHeight - 1);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString(String.format("Wire: %.0f/%.0f", remainingWire, model.getMaxWireLength()), barX + 5, barY - 5);

        
        int hudX = 10;
        int hudY = 550;
        int hudWidth = 200;
        int hudHeight = 40;
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(hudX, hudY, hudWidth, hudHeight);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString(String.format("Wire: %.0f/%.0f", remainingWire, model.getMaxWireLength()), hudX + 5, hudY + 12);
        g2d.drawString(String.format("Time: %.0f%%", model.getTemporalProgress()), hudX + 5, hudY + 24);
        double packetLoss = model.getTotalPackets() > 0 ? (model.getLostPackets() / (double) model.getTotalPackets()) * 100.0 : 0.0;
        g2d.drawString(String.format("Loss: %.0f%%", packetLoss), hudX + 100, hudY + 12);
        g2d.drawString(String.format("Coins: %d", model.getCoins()), hudX + 100, hudY + 24);

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

        
        if (model.isGameOver()) {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(250, 200, 300, 200);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("Game Over", 350, 250);
            g2d.setFont(new Font("Arial", Font.PLAIN, 16));
            g2d.drawString("Healthy Packets: " + (model.getTotalPackets() - model.getLostPackets()), 300, 300);
            g2d.drawString("Packet Loss: " + String.format("%.0f%%", packetLoss), 300, 320);
            returnToMenuButton.setVisible(true);
        } else if (model.isLevelPassed()) {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(250, 200, 300, 200);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("Level Passed!", 350, 250);
            g2d.setFont(new Font("Arial", Font.PLAIN, 16));
            g2d.drawString("All Packets Returned", 300, 300);
            nextStageButton.setVisible(true);
            returnToMenuButton.setVisible(true);
        } else {
            returnToMenuButton.setVisible(false);
            nextStageButton.setVisible(false);
        }
    }

    private double calculateWireLength(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
    }
}