/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ua.com.codefire.dropler.net.transport.AcceptPacket;
import ua.com.codefire.dropler.net.transport.CommandPacket;
import ua.com.codefire.dropler.net.transport.FileListPacket;
import ua.com.codefire.dropler.net.transport.FilePacket;
import ua.com.codefire.dropler.util.Archiver;

/**
 *
 * @author human
 */
public class TransferClient implements Runnable {

    private static final Logger LOG = Logger.getLogger(TransferClient.class.getName());
//    static {
//        try {
//            LOG.addHandler(new FileHandler(String.format("%s.log", TransferClient.class.getSimpleName()), true));
//        } catch (IOException | SecurityException ex) {
//            Logger.getLogger(TransferClient.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
//    }
    private List<File> files;
    private String address;
    private int port;
    private int connectionTimeout = 3000;

    /**
     *
     * @param address
     * @param port
     */
    public TransferClient(String address, int port) {
        this.address = address;
        this.port = port;
        this.files = new ArrayList<>();
    }

    /**
     *
     * @param address
     * @param port
     * @param files
     */
    public TransferClient(String address, int port, List<File> files) {
        this.address = address;
        this.port = port;
        this.files = files;
    }

    /**
     *
     * @param files
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public TransferClient addFiles(Collection<File> files) throws FileNotFoundException, IOException {
        for (File file : files) {
            addFile(file);
        }

        return this;
    }

    /**
     *
     * @param files
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public TransferClient addFiles(File[] files) throws FileNotFoundException, IOException {
        for (File file : files) {
            addFile(file);
        }

        return this;
    }

    /**
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean addFile(File file) throws FileNotFoundException, IOException {
        if (file.exists()) {
            files.add(file);
        }

        return false;
    }

    /**
     *
     */
    public void send() {
        Thread thread = new Thread(this);
        thread.setName("Send-Data-Thread");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    @Override
    public void run() {
        Socket socket = new Socket();

        int tryCount = 3;
        for (int trying = tryCount; trying > 0; trying--) {
            InetSocketAddress socketAddress = new InetSocketAddress(address, port);

            try {
                LOG.log(Level.INFO, String.format("Connect to %s [#%d]", socketAddress, tryCount - trying));
                socket.connect(socketAddress, connectionTimeout);
                LOG.log(Level.INFO, "Connection established.");
                trying = 0;

                LOG.log(Level.INFO, "Prepare list files...");
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(new FileListPacket().addFiles(files));
                LOG.log(Level.INFO, "Send list files...");
                oos.flush();

                LOG.log(Level.INFO, "Waiting for accept...");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Object readObject = ois.readObject();

                boolean acceptSend = false;

                if (readObject instanceof AcceptPacket) {
                    AcceptPacket acceptPacket = (AcceptPacket) readObject;
                    acceptSend = acceptPacket.isAccept();
                    LOG.log(Level.INFO, acceptSend ? "Data accepted!" : "Data rejected!");
                }

                if (acceptSend) {
                    LOG.log(Level.INFO, "Data accepted!\nPrepare data...");
                    List<FilePacket> filePackets = prepareData();

                    LOG.log(Level.INFO, "Send data:");
                    sendFilePackets(filePackets, oos);

                    // Send close command packet.
                    oos.writeObject(new CommandPacket("END TRASPORT"));
                    oos.flush();
                }

                LOG.log(Level.INFO, "Trasfer end!");
                break;
            } catch (SocketException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            } catch (IOException | ClassNotFoundException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        try {
            socket.close();
            LOG.log(Level.INFO, "Disconected!");
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     *
     * @return @throws IOException
     */
    private List<FilePacket> prepareData() throws IOException {
        List<FilePacket> packets = new ArrayList<>();

        for (File file : files) {
            File inputFile = file;

            if (file.isDirectory()) {
                inputFile = Archiver.packIntoArchive(file.getAbsoluteFile());
            }

            try (FileInputStream fis = new FileInputStream(inputFile)) {
                byte[] data = new byte[fis.available()];
                fis.read(data);

                packets.add(new FilePacket(file.getName(), data, file.isDirectory()));
            }
        }

        return packets;
    }

    /**
     *
     * @param dataPackets
     * @param oos
     * @throws IOException
     */
    private void sendFilePackets(List<FilePacket> dataPackets, ObjectOutputStream oos) throws IOException {
        for (FilePacket filePacket : dataPackets) {
            String sendMessage;

            if (filePacket.isDirectory()) {
                sendMessage = "  Directory '%s'.";
            } else {
                sendMessage = "  File '%s' [ %d bytes ]";
            }

            LOG.log(Level.INFO, String.format(sendMessage, filePacket.getName(), filePacket.getSize()));
            oos.writeObject(filePacket);
            oos.flush();
        }
    }
}
