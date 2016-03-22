/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import ua.com.codefire.dropler.frame.Dropler;
import ua.com.codefire.dropler.net.transport.FileInfo;
import ua.com.codefire.dropler.net.transport.FileListPacket;
import ua.com.codefire.dropler.net.transport.FilePacket;
import ua.com.codefire.dropler.util.Archiver;
import ua.com.codefire.dropler.util.Values;

/**
 *
 * @author human
 */
public class TransferHandler implements TransferListener {

    private static final Logger LOG = Logger.getLogger(TransferHandler.class.getName());
//    static {
//        try {
//            LOG.addHandler(new FileHandler(String.format("%s.log", TransferHandler.class.getSimpleName()), true));
//        } catch (IOException | SecurityException ex) {
//            Logger.getLogger(TransferHandler.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
//    }
    private final Dropler droplerFrame;
    private String lastPath;

    public TransferHandler(Dropler jFrame) {
        this.droplerFrame = jFrame;
    }

    public String getLastPath() {
        return lastPath;
    }

    public void setLastPath(String lastPath) {
        this.lastPath = lastPath;
    }

    @Override
    public boolean approveData(InetAddress fromAddress, FileListPacket fileListPacket) {
        StringBuilder sb = new StringBuilder(String.format("Receive file(s) from client %s?\n", fromAddress.getHostAddress()));

        for (FileInfo fileInfo : fileListPacket.getFileInfoList()) {
            String dataSize = Values.byteSizeToString(fileInfo.getSize());

            sb.append(fileInfo.getName()).append(" [").append(dataSize).append("]\n");
        }

        boolean visibleBeforeState = droplerFrame.isVisible();

        droplerFrame.bringToFront();

        int confirmResult = JOptionPane.showConfirmDialog(droplerFrame, sb.toString(), "Dropler :: Save files", JOptionPane.YES_NO_OPTION);

        if (confirmResult == JOptionPane.YES_OPTION) {
            if (lastPath == null || lastPath.isEmpty()) {
                lastPath = System.getProperty("user.home");
            }

            JFileChooser fileChooser = new JFileChooser(lastPath);
            fileChooser.setDialogTitle("Choose directory for save files");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (fileChooser.showDialog(droplerFrame, "Select directory") == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                lastPath = selectedFile.getAbsolutePath();
                return true;
            }
        } else {
            droplerFrame.setVisible(visibleBeforeState);
        }

        return false;
    }

    @Override
    public void receiveData(FilePacket filePacket) {
        String dataSize = Values.byteSizeToString(filePacket.getData().length);
        LOG.log(Level.INFO, String.format("Receive: %s [%s]", filePacket.getName(), dataSize));

        File directory = new File(lastPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            String format = filePacket.isDirectory() ? "%s/%s.zip" : "%s/%s";

            File receivedFile = new File(String.format(format, directory, filePacket.getName()));

            try (FileOutputStream fos = new FileOutputStream(receivedFile)) {
                fos.write(filePacket.getData());
            }

            if (filePacket.isDirectory()) {
                Archiver.unpackArchive(receivedFile);
            }
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
