/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net.transport;

/**
 *
 * @author human
 */
public class CommandPacket extends Packet {

    private String command;

    public CommandPacket(String command) {
        super(PacketType.COMMAND);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
