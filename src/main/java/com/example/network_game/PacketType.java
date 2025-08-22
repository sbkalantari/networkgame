package com.example.network_game;

public enum PacketType {
    MESSENGER("Messenger", "Standard communication packet"),
    CONFIDENTIAL("Confidential", "Secure packet destroyed by spy systems"),
    TROJAN("Trojan", "Malicious packet created by sabotage systems"),
    PROTECTED("Protected", "VPN-protected packet immune to interference"),
    LARGE("Large", "Large packet requiring splitting/merging");

    private final String displayName;
    private final String description;

    PacketType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }

    /**
     * Get color for packet type visualization
     */
    public java.awt.Color getColor() {
        switch (this) {
            case MESSENGER:
                return new java.awt.Color(100, 200, 255); 
            case CONFIDENTIAL:
                return new java.awt.Color(255, 100, 100); 
            case TROJAN:
                return new java.awt.Color(150, 50, 150);  
            case PROTECTED:
                return new java.awt.Color(100, 255, 100); 
            case LARGE:
                return new java.awt.Color(255, 200, 100); 
            default:
                return new java.awt.Color(200, 200, 200); 
        }
    }
}