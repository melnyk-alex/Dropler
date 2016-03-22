/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net;

import java.net.InetAddress;
import ua.com.codefire.dropler.net.transport.FileListPacket;
import ua.com.codefire.dropler.net.transport.FilePacket;

/**
 *
 * @author human
 */
public interface TransferListener {

    void receiveData(FilePacket filePacket);

    boolean approveData(InetAddress fromAddress, FileListPacket fileListPacket);
}
