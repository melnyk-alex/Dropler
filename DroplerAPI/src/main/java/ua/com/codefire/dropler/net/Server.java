/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author human
 */
public abstract class Server implements Runnable {

    private static final Logger LOG = Logger.getLogger(Server.class.getName());
//    static {
//        try {
//            LOG.addHandler(new FileHandler(String.format("%s.log", Server.class.getSimpleName()), true));
//        } catch (IOException | SecurityException ex) {
//            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
//    }
    //<editor-fold defaultstate="collapsed" desc="STANDART PORTS">
    public static final int API_PORT = 23380;
    public static final int LISTEN_PORT = 23381;
    public static final int TRANSFER_PORT = 23382;
    //</editor-fold>
    private int listenPort;
    private String serverName = "Server";
    private int listenTimeout = 3333;
    protected ServerSocket serverSocket;
    private ServerState serverState;
    private List<ServerListener> serverListeners;

    public Server(int listenPort) {
        this.listenPort = listenPort;
        this.serverListeners = new ArrayList<>();
    }

    public Server(int listenPort, String serverName) {
        this(listenPort);
        this.serverName = serverName;
    }

    public int getListenPort() {
        return listenPort;
    }

    public String getServerName() {
        return serverName;
    }

    public ServerState getServerState() {
        return serverState;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     *
     * @param serverListener
     */
    public void addListener(ServerListener serverListener) {
        serverListeners.add(serverListener);
    }

    /**
     * Register server socket to anyone listed port.
     *
     * @throws java.io.IOException by ServerSocket.
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(listenPort);
        serverSocket.setSoTimeout(listenTimeout);

        serverState = ServerState.STARTED;

        raiseEvent();

        // Start listen...
        String threadName = String.format("%s-Thread", serverName.replaceAll(" ", "-"));
        Thread serverThread = new Thread(this, threadName);
        serverThread.start();
    }

    @Override
    public void run() {
        serverState = ServerState.EXECUTE;

        while (serverState == ServerState.EXECUTE) {
            try {
                Socket acceptSocket = serverSocket.accept();
                socketConnected(acceptSocket);
            } catch (SocketTimeoutException ex) {
                //LOG.log(Level.SEVERE, ex.getMessage(), ex);
            } catch (ConnectException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }

//            Thread.yield();
        }
    }

    protected abstract void socketConnected(Socket socket);

    /**
     * Stop thread and server socket listen.
     *
     * @return true if server stop and false if not started.
     */
    public void stop() {
        serverState = ServerState.STOPED;

        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
                serverSocket = null;

                raiseEvent();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param serverState
     */
    private void raiseEvent() {
        for (ServerListener sl : serverListeners) {
            switch (serverState) {
                case STARTED:
                    sl.serverStarted(this);
                    break;
                case STOPED:
                    sl.serverStoped(this);
                    break;
            }
        }
    }

    @Override
    public String toString() {
        return serverName;
    }
}
