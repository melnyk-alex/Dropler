/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.ui;

import java.util.Objects;

/**
 *
 * @author human
 */
public class Computer  {

    private final String hostName;
    private final String hostAddress;
    private ComputerState state;

    public Computer(String hostname, String hostaddres) {
        this.hostName = hostname;
        this.hostAddress = hostaddres;
        this.state = ComputerState.OFFLINE;
    }

    public Computer(String hostname, String hostaddres, ComputerState state) {
        this.hostName = hostname;
        this.hostAddress = hostaddres;
        this.state = state;
    }

    public String getHostName() {
        return hostName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public ComputerState getState() {
        return state;
    }

    public void setState(ComputerState state) {
        this.state = state;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.hostAddress);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Computer other = (Computer) obj;
        if (!Objects.equals(this.hostAddress, other.hostAddress)) {
            return false;
        }
        return true;
    }
}
