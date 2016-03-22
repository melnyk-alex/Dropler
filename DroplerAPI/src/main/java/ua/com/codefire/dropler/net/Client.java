/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net;

import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author human
 */
public abstract class Client<T> implements Callable<T> {

    private static final Logger LOG = Logger.getLogger(Client.class.getName());
//    static {
//        try {
//            LOG.addHandler(new FileHandler(String.format("%s.log", Client.class.getSimpleName()), true));
//        } catch (IOException | SecurityException ex) {
//            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
//    }
    private Socket socket;
    private String host;
    private int port;
    private int timeout;
    private InetSocketAddress socketAddress;

    public Client(String host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    @Override
    public T call() {
        socketAddress = new InetSocketAddress(host, port);

        socket = new Socket();

        try {
            socket.connect(socketAddress, timeout);

            connected(socket);
        } catch (ConnectException | SocketTimeoutException | UnknownHostException | BindException ex) {
            //LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return (T) this;
    }

    public abstract void connected(Socket socket);
}
