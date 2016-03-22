/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net.transport;

/**
 *
 * @author human
 */
public class FilePacket extends Packet {

    private String name;
    private long size;
    private byte[] data;
    private boolean directory;

    public FilePacket(String name, long size) {
        super(PacketType.FILE);
        this.name = name;
        this.size = size;
    }

    public FilePacket(String name, byte[] data) {
        super(PacketType.FILE);
        this.name = name;
        this.data = data;
        this.size = data.length;
    }

    public FilePacket(String name, byte[] data, boolean directory) {
        super(PacketType.FILE);
        this.name = name;
        this.data = data;
        this.size = data.length;
        this.directory = directory;
    }

    public boolean isDirectory() {
        return directory;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    public long getSize() {
        return size;
    }

    public boolean haveData() {
        return (data.length > 0);
    }
}
