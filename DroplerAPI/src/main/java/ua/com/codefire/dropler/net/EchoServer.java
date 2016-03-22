/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ua.com.codefire.dropler.util.Hasher;

/**
 *
 * @author human
 */
public final class EchoServer extends Server {

    private static final Logger LOG = Logger.getLogger(EchoServer.class.getName());
//    static {
//        try {
//            LOG.addHandler(new FileHandler(String.format("%s.log", EchoServer.class.getSimpleName()), true));
//        } catch (IOException | SecurityException ex) {
//            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
//    }

    /**
     *
     */
    public EchoServer() {
        super(Server.LISTEN_PORT, "Echo-Server");
    }

    @Override
    public void start() throws IOException {
        super.start();
    }

    @Override
    protected void socketConnected(Socket socket) {
        new Thread(new Response(socket)).start();
    }

    /**
     *
     */
    private class Response implements Runnable {

        private Socket socket;

        public Response(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String inputData = dis.readUTF();

                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                if ("RESOLVE IP".equalsIgnoreCase(inputData)) {
                    StringBuilder sb = new StringBuilder();

                    sb.append("Server:").append("\n");
                    sb.append(serverSocket.getInetAddress().toString()).append("\n");
                    sb.append(serverSocket.getLocalSocketAddress().toString()).append("\n");
                    sb.append("Client:").append("\n");
                    sb.append(socket.getInetAddress().toString()).append("\n");
                    sb.append(socket.getLocalAddress().toString()).append("\n");
                    sb.append(socket.getLocalSocketAddress().toString()).append("\n");
                    sb.append(socket.getRemoteSocketAddress().toString());

                    dos.writeUTF(sb.toString());
                    dos.flush();
                } else {
                    String hashsum = Hasher.md5(inputData);

                    dos.writeUTF(hashsum);
                    dos.flush();
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
}
