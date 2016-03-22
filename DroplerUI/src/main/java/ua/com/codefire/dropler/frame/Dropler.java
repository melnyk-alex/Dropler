
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.frame;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import ua.com.codefire.dropler.frame.component.ComputerList;
import ua.com.codefire.dropler.frame.component.ComputerListModel;
import ua.com.codefire.dropler.frame.component.Status;
import ua.com.codefire.dropler.net.APIServer;
import ua.com.codefire.dropler.net.EchoScanner;
import ua.com.codefire.dropler.net.EchoScannerListener;
import ua.com.codefire.dropler.net.EchoServer;
import ua.com.codefire.dropler.net.Server;
import ua.com.codefire.dropler.net.ServerListener;
import ua.com.codefire.dropler.net.TransferHandler;
import ua.com.codefire.dropler.net.TransferServer;
import ua.com.codefire.dropler.res.R;
import ua.com.codefire.dropler.ui.Computer;
import ua.com.codefire.dropler.ui.ComputerState;

/**
 *
 * @author human
 */
public final class Dropler extends JFrame implements WindowStateListener, ServerListener, EchoScannerListener {

    private static final Logger LOG = Logger.getLogger(Dropler.class.getName());
//    static {
//        try {
//            LOG.addHandler(new FileHandler(String.format("%s.log", Dropler.class.getSimpleName()), true));
//        } catch (IOException | SecurityException ex) {
//            Logger.getLogger(Dropler.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
//    }
    private Status status;
    private ComputerList computersList;
    private APIServer apiServer;
    private EchoServer echoServer;
    private EchoScanner echoScanner;
    private TransferServer transferServer;
    private TransferHandler transferHandler;
    public int apiPort;
    public int listenPort;
    public int transferPort;
    public String lastPath;

    public Dropler() {
        loadSettings();

        initializeFrame();
        initializeTray();

        initializeService();
    }

    /**
     * Load settings for application.
     */
    private void loadSettings() {
        String osname = System.getProperty("os.name");

        if ("Mac OS X".equals(osname)) {
        }

        Properties app = R.getProperties("app");
        this.lastPath = app.getProperty("app.last_path");
    }

    private void storeSettings() {
        Properties app = R.getProperties("app");
        app.setProperty("app.last_path", lastPath);

        R.setProperties("app", app);
    }

    /**
     * Initialize frame components.
     */
    public void initializeFrame() {
        addWindowStateListener(this);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setName("Computers");
        setResizable(false);

        JScrollPane jspComputerList = new JScrollPane();
        jspComputerList.setBorder(null);
        jspComputerList.setPreferredSize(new Dimension(280, 360));

        computersList = new ComputerList();
        
        ComputerListModel computerListModel = new ComputerListModel();
//        computerListModel.add(0, new Computer("TEST-HOSTNAME", "127.0.0.1", ComputerState.ONLINE));
//        computerListModel.add(0, new Computer("TEST-HOSTNAME", "127.0.0.1", ComputerState.OFFLINE));
//        computerListModel.add(0, new Computer("TEST-HOSTNAME", "127.0.0.1", ComputerState.UNKNOWN));
        computersList.setModel(computerListModel);
        
        jspComputerList.setViewportView(computersList);
        
        status = new Status();
        status.setPreferredSize(new Dimension(280, 26));

        JPanel contentPanel = new JPanel(true);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.add(jspComputerList);
        contentPanel.add(status);
        
        getRootPane().getContentPane().add(contentPanel);

        pack();

        setLocationRelativeTo(null);
    }

    /**
     *
     * @throws HeadlessException
     */
    private void initializeTray() throws HeadlessException {
        if (SystemTray.isSupported()) {
            try {
                Image trayImage = ImageIO.read(R.class.getResource("icon.16.16.png"));

                TrayIcon trayIcon = new TrayIcon(trayImage, "Dropler");
                trayIcon.setImageAutoSize(true);
                trayIcon.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            commandPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "ShowWindow"));
                        }
                    }
                });

                // Set tray icon popup menu
                trayIcon.setPopupMenu(initializeTrayMenu());
                // Add tray icon
                SystemTray.getSystemTray().add(trayIcon);
            } catch (IOException | AWTException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    /**
     *
     * @param e
     */
    public void commandPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "ShowWindow":
                bringToFront();
                break;
            case "HideWindow":
                setVisible(false);
                break;
            case "Quit":
                storeSettings();
                System.exit(0);
                break;
        }
    }

    /**
     *
     * @return @throws HeadlessException
     */
    private PopupMenu initializeTrayMenu() throws HeadlessException {
        PopupMenu popupMenu = new PopupMenu("System");
        MenuItem miComputers = new MenuItem("Show Dropler");
        miComputers.setActionCommand("ShowWindow");
        miComputers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commandPerformed(e);
            }
        });
        popupMenu.add(miComputers);

        MenuItem miQuit = new MenuItem("Quit");
        miQuit.setActionCommand("Quit");
        miQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });
        popupMenu.add(miQuit);

        return popupMenu;
    }

    /**
     *
     */
    private void initializeService() {
        apiServer = new APIServer();
        apiServer.addListener(this);

        echoServer = new EchoServer();
        echoServer.addListener(this);

        transferHandler = new TransferHandler(this);
        transferHandler.setLastPath(lastPath);

        transferServer = new TransferServer();
        transferServer.addListener(this);
        transferServer.addTransferListener(transferHandler);

        try {
            apiServer.start();
            echoServer.start();
            transferServer.start();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        
        echoScanner = new EchoScanner();
        echoScanner.addListener(this);
        echoScanner.start();
    }

    /**
     * Bring frame to front.
     */
    public void bringToFront() {
        setVisible(true);
        setAlwaysOnTop(true);
        requestFocus();
        repaint();
        setAlwaysOnTop(false);
    }

    @Override
    public void connectionListUpdated(Map<InetAddress, Computer> connections) {
        ComputerListModel clm = (ComputerListModel) computersList.getModel();
        
        clm.clear();
        clm.addAll(connections.values());
        clm.trimToSize();
        
        int online = 0;
        Enumeration<Computer> elements = clm.elements();
        while (elements.hasMoreElements()) {
            if (elements.nextElement().getState() == ComputerState.ONLINE) {
                online++;
            }
        }
        
        status.setOnline(online);
        status.setCount(clm.size());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //<editor-fold defaultstate="collapsed" desc="Set Look and Feel (Nimbus)">
        for (UIManager.LookAndFeelInfo lafi : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(lafi.getName())) {
                try {
                    UIManager.setLookAndFeel(lafi.getClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(Dropler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        //</editor-fold>

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Dropler mainFrame = new Dropler();
//                mainFrame.setVisible(true);
            }
        });
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        switch (e.getNewState()) {
        }
    }

    @Override
    public void serverStarted(Server server) {
        LOG.log(Level.INFO, String.format(String.format("%s started at %s", server, server.getListenPort())));
    }

    @Override
    public void serverStoped(Server server) {
        LOG.log(Level.INFO, String.format(String.format("%s stoped!", server)));
    }

    @Override
    public void addressListUpdated(List<InetAddress> addressList) {
        StringBuilder sb  = new StringBuilder("Dropler :: ");
        
        for (InetAddress address : addressList) {
            sb.append(address.getHostAddress()).append("/");
        }
        
        setTitle(sb.deleteCharAt(sb.lastIndexOf("/")).toString());
    }
}
