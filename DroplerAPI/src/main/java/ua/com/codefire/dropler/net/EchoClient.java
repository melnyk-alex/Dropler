/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import ua.com.codefire.dropler.util.Hasher;

/**
 *
 * @author human
 */
public class EchoClient extends Client<EchoClient> {

    private static final Logger LOG = Logger.getLogger(EchoClient.class.getName());
//    static {
//        try {
//            LOG.addHandler(new FileHandler(String.format("%s.log", APIServer.class.getSimpleName()), true));
//        } catch (IOException | SecurityException ex) {
//            Logger.getLogger(APIServer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
//    }
    private Boolean reachable = false;

    public EchoClient(String host, int port, int timeout) {
        super(host, port, timeout);
    }

    public Boolean isReachable() {
        return reachable;
    }

    @Override
    public void connected(Socket socket) {
        String connectTime = new GregorianCalendar().toString();
        String hashsum = Hasher.md5(connectTime);

        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(connectTime);
            dos.flush();

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String checkHashsum = dis.readUTF();

            if (hashsum.equals(checkHashsum)) {
                reachable = true;
            } else {
                LOG.log(Level.INFO, "HASH IS NOT VALID!");
            }
            
            socket.close();
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
