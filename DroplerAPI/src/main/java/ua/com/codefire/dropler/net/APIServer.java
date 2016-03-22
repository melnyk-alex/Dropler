/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import ua.com.codefire.dropler.net.bean.DropBean;
import ua.com.codefire.dropler.net.bean.DropBeanUtil;
import ua.com.codefire.dropler.net.bean.FilesDrop;

/**
 *
 * @author human
 */
public class APIServer extends Server {

    private static final Logger LOG = Logger.getLogger(APIServer.class.getName());
//    static {
//        try {
//            LOG.addHandler(new FileHandler(String.format("%s.log", APIServer.class.getSimpleName()), true));
//        } catch (IOException | SecurityException ex) {
//            Logger.getLogger(APIServer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
//    }

    public APIServer() {
        super(Server.API_PORT, "API-Server");
    }

    @Override
    protected void socketConnected(Socket socket) {
        InetAddress inetAddress = socket.getInetAddress();
        
        String xmlFilePath = null;

        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            xmlFilePath = dis.readUTF();
            socket.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        if (xmlFilePath != null && !xmlFilePath.isEmpty()) {
            try {
                DropBean dropBean = DropBeanUtil.loadFromStream(new FileInputStream(xmlFilePath));

                switch (dropBean.getBeanType()) {
                    case SEND_FILES:
                        FilesDrop filesDrop = (FilesDrop) dropBean;

                        LOG.log(Level.INFO, String.format("SEND FILES FROM %s TO %s", inetAddress, filesDrop.getToAddress()));
                        TransferClient tc = new TransferClient(filesDrop.getToAddress().getHostAddress(), Server.TRANSFER_PORT);
                        try {
                            tc.addFiles(filesDrop.getFiles());
                        } catch (IOException ex) {
                            LOG.log(Level.SEVERE, ex.getMessage(), ex);
                        }
                        tc.send();
                        break;
                }
            } catch (FileNotFoundException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
}
