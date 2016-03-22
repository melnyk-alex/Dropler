/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ua.com.codefire.dropler.net.transport.AcceptPacket;
import ua.com.codefire.dropler.net.transport.CommandPacket;
import ua.com.codefire.dropler.net.transport.FileListPacket;
import ua.com.codefire.dropler.net.transport.FilePacket;
import ua.com.codefire.dropler.net.transport.Packet;
import static ua.com.codefire.dropler.net.transport.PacketType.FILE;

/**
 *
 * @author human
 */
public class TransferServer extends Server {

    private static final Logger LOG = Logger.getLogger(TransferServer.class.getName());
//    static {
//        try {
//            LOG.addHandler(new FileHandler(String.format("%s.log", TransferServer.class.getSimpleName()), true));
//        } catch (IOException | SecurityException ex) {
//            Logger.getLogger(TransferServer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
//    }
    private List<TransferListener> listeners;

    public TransferServer() {
        super(Server.TRANSFER_PORT, "Transfer-Server");
        this.listeners = new ArrayList<>();
    }

    public void addTransferListener(TransferListener listener) {
        listeners.add(listener);
    }

    @Override
    public void socketConnected(Socket socket) {
        LOG.log(Level.INFO, String.format("Incoming trasfer from %s", socket.getInetAddress()));

        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            try {
                Object readObject;

                // RECEIVE PACKETS FROM TRANSFER SENDER
                while ((readObject = ois.readObject()) != null) {
                    try {
                        if (readObject instanceof Packet) {
                            Packet packet = (Packet) readObject;

                            switch (packet.getPacketType()) {
                                case FILE_LIST:
                                    LOG.log(Level.INFO, "Recive file list.");
                                    boolean approve = false;

                                    for (TransferListener listener : listeners) {
                                        FileListPacket fileListPacket = (FileListPacket) packet;
                                        approve = listener.approveData(socket.getInetAddress(), fileListPacket);

                                        if (approve) {
                                            break;
                                        }
                                    }

                                    if (approve) {
                                        LOG.log(Level.INFO, "Send accept!");
                                    } else {
                                        LOG.log(Level.INFO, "Send reject!");
                                    }

                                    oos.writeObject(new AcceptPacket(approve));
                                    oos.flush();
                                    break;
                                case FILE:
                                    for (TransferListener listener : listeners) {
                                        listener.receiveData((FilePacket) packet);
                                    }
                                    break;
                                case COMMAND:
                                    CommandPacket commandPacket = (CommandPacket) packet;

                                    if ("END TRANSPORT".equals(commandPacket.getCommand())) {
                                        throw new TransportException("End transport exception!");
                                    }
                                    break;
                            }
                        }
                    } catch (TransportException ex) {
                    }
                }
            } catch (ClassNotFoundException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        } catch (EOFException ex) {
            //LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        try {
            socket.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
