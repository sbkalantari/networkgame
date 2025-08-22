package com.example.network_game;

public enum PacketType {
    MESSAGE,        // Regular message packets
    CONFIDENTIAL,   // Destroyed by spy systems
    PROTECTED,      // Protected by VPN, immune to spy/sabotage
    TROJAN,         // Malicious packets that can be converted
    LARGE           // Large packets that need distribution/merging
}