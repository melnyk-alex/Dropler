/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author human
 */
public class Network {

    /**
     * Get available network interfaces.
     * 
     * @return list of available networks.
     */
    public static List<NetworkInterface> getAvailableNetworks() {
        List<NetworkInterface> availableInterfaces = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();

                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual() || ni.isPointToPoint()) {
                    continue;
                }

                availableInterfaces.add(ni);
            }
        } catch (SocketException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        }

        return availableInterfaces;
    }
    
    /**
     * 
     * @return 
     */
    public static List<InetAddress> getAvailableAddresses() {
        List<InetAddress> addresses = new ArrayList<>();

        for (NetworkInterface ni : Network.getAvailableNetworks()) {
            Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress ia = inetAddresses.nextElement();

                if (ia.isLinkLocalAddress() || ia.isLoopbackAddress()) {
                    continue;
                }
                
                addresses.add(ia);
            }
        }
        
        return addresses;
    }
}
