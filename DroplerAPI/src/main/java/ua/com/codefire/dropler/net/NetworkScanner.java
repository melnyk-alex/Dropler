/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import ua.com.codefire.dropler.util.Network;

/**
 *
 * @author human
 */
public class NetworkScanner extends Thread {

    private static final Logger LOG = Logger.getLogger(NetworkScanner.class.getName());

    private Set<InetAddress> available;
    private List<NetworkListener> listeners;

    public NetworkScanner() {
        available = new HashSet<>();
        listeners = new ArrayList<>();
    }

    public Set<InetAddress> getAvailable() {
        return available;
    }

    public List<NetworkListener> getListeners() {
        return listeners;
    }

    public void addListener(NetworkListener networkListener) {
        listeners.add(networkListener);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000);

                if (refreshList()) {
                    for (NetworkListener listener : listeners) {
                        listener.addressUpdated(available);
                    }
                }
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    /**
     * Refresh available addresses list.
     *
     * @return value indicates whether list updated.
     */
    public boolean refreshList() {
        List<InetAddress> availableAddresses = Network.getAvailableAddresses();

        if (!available.containsAll(availableAddresses) || availableAddresses.size() != available.size()) {
            available.clear();
            available.addAll(availableAddresses);
            return true;
        }

        return false;
    }

}
