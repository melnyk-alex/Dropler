/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net.transport;

/**
 *
 * @author human
 */
public class AcceptPacket extends Packet {

    private boolean accept;

    public AcceptPacket(boolean accept) {
        super(PacketType.ACCEPT);
        this.accept = accept;
    }

    public boolean isAccept() {
        return accept;
    }
}
