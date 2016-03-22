/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net.transport;

import java.io.Serializable;

/**
 *
 * @author human
 */
public abstract class Packet implements Serializable {

    private PacketType packetType;

    public Packet(PacketType packetType) {
        this.packetType = packetType;
    }

    public PacketType getPacketType() {
        return packetType;
    }
}
