/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import ua.com.codefire.dropler.ui.Computer;

/**
 *
 * @author human
 */
public interface EchoScannerListener {

    void connectionListUpdated(Map<InetAddress, Computer> connections);
    void addressListUpdated(List<InetAddress> addressList);
}
