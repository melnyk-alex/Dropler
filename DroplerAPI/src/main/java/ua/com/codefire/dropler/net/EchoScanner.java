/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ua.com.codefire.dropler.ui.Computer;
import ua.com.codefire.dropler.ui.ComputerState;

/**
 *
 * @author human
 */
public class EchoScanner implements Runnable, NetworkListener {

    private static final Logger LOG = Logger.getLogger(EchoScanner.class.getName());
//    static {
//        try {
//            LOG.addHandler(new FileHandler(String.format("%s.log", EchoScanner.class.getSimpleName()), true));
//        } catch (IOException | SecurityException ex) {
//            Logger.getLogger(EchoScanner.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
//    }
    /**
     * Scan interval in seconds.
     */
    private int scanInterval = 30;
    /**
     * Socket connection timeout.
     */
    private int connectionTimeout = 3000;
    /**
     * Thread pool size.
     */
    private int threadPoolSize = 256;
    /**
     * Current state.
     */
    private boolean scanLaunched;
    /**
     * Mask for generating IP addresses.
     */
    private List<String> networkMasks;
    /**
     * Connection history.
     */
    private Map<InetAddress, Computer> connections;
    private List<EchoScannerListener> listeners;
    
    /**
     * 
     */
    private List<String> excludeAddresses;
    /**
     * 
     */
    private NetworkScanner networkScanner;

    /**
     * Create object for scan listen port.
     */
    public EchoScanner() {
        this.networkMasks = new ArrayList<>();
        this.excludeAddresses = new ArrayList<>();
        this.connections = Collections.synchronizedMap(new HashMap<InetAddress, Computer>());
        this.listeners = new ArrayList<>();
    }

    /**
     * Returns current connection set.
     * @return connection set.
     */
    public Set<InetAddress> getConnections() {
        return connections.keySet();
    }

    /**
     *
     * @param listener
     */
    public void addListener(EchoScannerListener listener) {
        listeners.add(listener);
    }

    /**
     * Start scanner into new thread.
     */
    public void start() {
        this.networkScanner = new NetworkScanner();
        this.networkScanner.addListener(this);
        this.networkScanner.start();
        
        if (!scanLaunched) {
            scanLaunched = true;

            Thread networkScanerThread = new Thread(this, "Network-Scanner-Thread");
            networkScanerThread.setPriority(Thread.MIN_PRIORITY);
            networkScanerThread.setDaemon(true);
            networkScanerThread.start();
        }
    }

    @Override
    public void run() {
        while (scanLaunched) {
            Set<Callable<EchoClient>> clients = new HashSet<>();

            for (String networkMask : networkMasks) {
                for (int a = 0; a < 256; a++) {

                    String address = String.format("%s.%d", networkMask, a);
                    
                    if (excludeAddresses.contains(address)) {
                        continue;
                    }

                    clients.add(new EchoClient(address, Server.LISTEN_PORT, 5000));
                }
            }

            executeScan(clients);

            try {
                Thread.sleep(scanInterval * 1000);
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    /**
     * 
     * @param clients 
     */
    private void executeScan(Set<Callable<EchoClient>> clients) {
        try {
            for (Future<EchoClient> future : executePool(clients)) {
                try {
                    EchoClient echoClient = future.get();

                    InetSocketAddress socketAddress = echoClient.getSocketAddress();

                    if (echoClient.isReachable()) {
                        addOnLine(socketAddress);
                    } else {
                        setOffLine(socketAddress.getAddress());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(EchoScanner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     * @param calls
     * @return
     * @throws InterruptedException 
     */
    private List<Future<EchoClient>> executePool(Set<Callable<EchoClient>> calls) throws InterruptedException {
        ExecutorService esftp = Executors.newFixedThreadPool(threadPoolSize);
        List<Future<EchoClient>> futures = esftp.invokeAll(calls);
        esftp.shutdown();

        return futures;
    }

    /**
     * Stop scanner thread.
     */
    public void stop() {
        scanLaunched = false;
    }

    /**
     *
     * @param socketAddress
     */
    public void addOnLine(InetSocketAddress socketAddress) {
        InetAddress inetAddress = socketAddress.getAddress();

        if (!connections.containsKey(inetAddress)) {
            Computer computer = new Computer(socketAddress.getHostName(), inetAddress.getHostAddress());
            connections.put(inetAddress, computer);
        }

        setOnLine(inetAddress);
    }

    /**
     * 
     * @param address 
     */
    private void setOnLine(InetAddress address) {
        Computer computer = connections.get(address);

        if (computer.getState() != ComputerState.ONLINE) {
            computer.setState(ComputerState.ONLINE);
            LOG.log(Level.INFO, String.format("ON-LINE: %s", address));
            raiseEvent();
        }
    }

    /**
     *
     * @param socketAddress
     */
    public void addOffLine(InetSocketAddress socketAddress) {
        InetAddress inetAddress = socketAddress.getAddress();

        if (!connections.containsKey(inetAddress)) {
            Computer computer = new Computer(socketAddress.getHostName(), inetAddress.getHostAddress());
            computer.setState(ComputerState.ONLINE);
            connections.put(socketAddress.getAddress(), computer);
        }

        setOffLine(inetAddress);
    }

    /**
     *
     * @param address
     */
    public void setOffLine(InetAddress address) {
        if (connections.containsKey(address)) {
            Computer computer = connections.get(address);

            if (computer.getState() != ComputerState.OFFLINE) {
                computer.setState(ComputerState.OFFLINE);
                LOG.log(Level.INFO, String.format("OFF-LINE: %s", address));
                raiseEvent();
            }
        }
    }

    /**
     *
     */
    private void raiseEvent() {
        for (EchoScannerListener listener : listeners) {
            listener.connectionListUpdated(connections);
        }
    }

    @Override
    public void addressUpdated(Set<InetAddress> addresses) {
        Pattern pattern = Pattern.compile("(\\d{1,3}\\.){3}(\\d{1,3})?");
        
        for (InetAddress inetAddress : addresses) {
            Matcher matcher = pattern.matcher(inetAddress.getHostAddress());
            while (matcher.find()) {
                String address = matcher.group();
                String addressMask = address.substring(0, address.lastIndexOf("."));
                networkMasks.add(addressMask);
                break;
            }
        }
        
        for (EchoScannerListener listener : listeners) {
            listener.addressListUpdated(new ArrayList<>(addresses));
        }
    }
}
