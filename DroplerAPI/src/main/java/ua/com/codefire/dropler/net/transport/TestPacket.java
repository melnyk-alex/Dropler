/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net.transport;

import java.util.Objects;

/**
 *
 * @author human
 */
public class TestPacket extends Packet {

    private String cheksumm;

    public TestPacket(String cheksumm) {
        super(PacketType.TEST);
        this.cheksumm = cheksumm;
    }

    public String getCheksumm() {
        return cheksumm;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestPacket other = (TestPacket) obj;
        if (!Objects.equals(this.cheksumm, other.cheksumm)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.cheksumm);
        return hash;
    }
}
