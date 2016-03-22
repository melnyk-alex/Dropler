/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.frame.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;
import javax.swing.TransferHandler;
import ua.com.codefire.dropler.frame.component.ComputerList;
import ua.com.codefire.dropler.ui.Computer;
import ua.com.codefire.dropler.net.Server;
import ua.com.codefire.dropler.net.TransferClient;

/**
 *
 * @author human
 */
public class DropResolver extends TransferHandler {

    private static final Logger LOG = Logger.getLogger(DropResolver.class.getName());

    @Override
    public boolean canImport(TransferSupport support) {
        if (validateDataFlavour(support)) {
            ComputerList computerList = (ComputerList) support.getComponent();

            JList.DropLocation loc = (JList.DropLocation) support.getDropLocation();
            int index = loc.getIndex();

            if (index >= 0) {
                computerList.setSelectedIndex(index);
            } else {
                computerList.clearSelection();
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean importData(TransferSupport support) {
        try {
            Transferable transferable = support.getTransferable();
            List<File> dropFileList = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
            
            if (!dropFileList.isEmpty()) {
                Computer selectedComputer = getSelectedItem(support);

                if (selectedComputer != null) {
                    TransferClient ts = new TransferClient(selectedComputer.getHostAddress(), Server.TRANSFER_PORT);

                    try {
                        ts.addFiles(dropFileList).send();
                    } catch (IOException ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return true;
    }

    /**
     *
     * @param support
     * @return
     */
    private boolean validateDataFlavour(TransferSupport support) {
        for (DataFlavor df : support.getDataFlavors()) {
            if (!DataFlavor.javaFileListFlavor.equals(df)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 
     * @param support
     * @return 
     */
    private Computer getSelectedItem(TransferSupport support) {
        ComputerList computerList = (ComputerList) support.getComponent();
        return computerList.getSelectedValue();
    }
}
