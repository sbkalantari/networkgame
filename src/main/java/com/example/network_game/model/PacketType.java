package com.example.network_game.model;

public enum PacketType {
    MESSENGER(1, 1),
    CONFIDENTIAL(1, 1),
    TROJAN(1, 1),
    PROTECTED(2, 5),


    TEXT_INCOMPATIBLE(2, 2),
    VIDEO_ACCELERATING(3, 3),
    AUDIO_DECELERATING(1, 1),


    LARGE_SMALL(4, 3),
    LARGE_MEDIUM(6, 4),
    LARGE_BIG(8, 8),
    LARGE_HUGE(10, 10);

    private final int size;
    private final int coinReward;

    PacketType(int size, int coinReward) {
        this.size = size;
        this.coinReward = coinReward;
    }

    public int getSize() {
        return size;
    }

    public int getCoinReward() {
        return coinReward;
    }

    public boolean isLargePacket() {
        return this == LARGE_SMALL || this == LARGE_MEDIUM || this == LARGE_BIG || this == LARGE_HUGE;
    }

    public boolean isMessengerType() {
        return this == MESSENGER || this == TEXT_INCOMPATIBLE || this == VIDEO_ACCELERATING || this == AUDIO_DECELERATING;
    }

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
            case TEXT_INCOMPATIBLE:
                return new java.awt.Color(255, 255, 100);
            case VIDEO_ACCELERATING:
                return new java.awt.Color(255, 150, 50);
            case AUDIO_DECELERATING:
                return new java.awt.Color(150, 255, 150);
            case LARGE_SMALL:
            case LARGE_MEDIUM:
            case LARGE_BIG:
            case LARGE_HUGE:
                return new java.awt.Color(255, 200, 100);
            default:
                return new java.awt.Color(200, 200, 200);
        }
    }
}