/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.frame.component;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DropMode;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import ua.com.codefire.dropler.frame.dnd.DropResolver;
import ua.com.codefire.dropler.frame.Dropler;
import ua.com.codefire.dropler.ui.Computer;
import ua.com.codefire.dropler.net.Server;
import ua.com.codefire.dropler.net.TransferClient;

/**
 *
 * @author human
 */
public class ComputerList extends JList<Computer> {

    private static final Logger LOG = Logger.getLogger(ComputerList.class.getName());
    private static final String noComputers = "NO CONNECTIONS";

    public ComputerList() {
        setCellRenderer(new ListCellRenderer<Computer>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Computer> list, Computer value, int index, boolean isSelected, boolean cellHasFocus) {
                return new ComputerListItem(value, isSelected);
            }
        });

        setDropMode(DropMode.ON);
        setTransferHandler(new DropResolver());
        setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        setForeground(new Color(155, 155, 155));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        initializePopupMenu();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    rightButtonClick(e);
                }
            }
        });
    }

    private void rightButtonClick(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            ComputerList cList = (ComputerList) e.getSource();
            cList.setSelectedIndex(cList.locationToIndex(e.getPoint()));
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (getModel().getSize() == 0) {
            setBackground(new Color(245, 245, 245));

            Rectangle2D stringBounds = g2d.getFontMetrics().getStringBounds(noComputers, g);

            g2d.drawString(noComputers, (float) (getWidth() / 2 - stringBounds.getCenterX()), (float) (getHeight() / 2 + stringBounds.getCenterY()));
        } else {
            setBackground(Color.WHITE);
        }
    }

    private void initializePopupMenu() {
        JPopupMenu jPopupMenu = new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                ComputerList cList = (ComputerList) invoker;
                int row = cList.locationToIndex(new Point(x, y));
                if (row != -1) {
                    cList.setSelectedIndex(row);
                    super.show(invoker, x, y);
                } else {
                    cList.clearSelection();
                }
            }
        };
        jPopupMenu.add(new JMenuItem("Send file(s)...") {
            @Override
            protected void fireActionPerformed(ActionEvent event) {
                super.fireActionPerformed(event);
                menuActionSendFiles();
            }
        });
        jPopupMenu.add(new JMenuItem("Send screenshot") {
            @Override
            protected void fireActionPerformed(ActionEvent event) {
                super.fireActionPerformed(event);
                menuActionSendScreenshot();
            }
        });
        setComponentPopupMenu(jPopupMenu);
    }

    /**
     *
     */
    private void menuActionSendFiles() {
        Dropler dropler = (Dropler) Window.getWindows()[0];

        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setMultiSelectionEnabled(true);
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        if (jFileChooser.showOpenDialog(dropler) == JFileChooser.APPROVE_OPTION) {
            Computer selectedComputer = getSelectedValue();

            if (selectedComputer != null) {
                TransferClient ts = new TransferClient(selectedComputer.getHostAddress(), Server.TRANSFER_PORT);

                try {
                    ts.addFiles(jFileChooser.getSelectedFiles());
                    ts.send();
                } catch (FileNotFoundException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     *
     */
    private void menuActionSendScreenshot() {
        Dropler dropler = (Dropler) Window.getWindows()[0];

        try {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            Robot robot = new Robot();

            dropler.setVisible(false);
            robot.delay(500);
            BufferedImage screenshot = robot.createScreenCapture(screenRect);
            dropler.setVisible(true);
//            Graphics2D graphics = (Graphics2D) screenshot.getGraphics();
//            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//            graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

            GregorianCalendar gc = new GregorianCalendar();
            String temp = String.format("%s/screenshot-%s%s%s-%s%s%s.jpg", System.getProperty("java.io.tmpdir"),
                    gc.get(GregorianCalendar.YEAR),
                    gc.get(GregorianCalendar.MONTH),
                    gc.get(GregorianCalendar.DAY_OF_MONTH),
                    gc.get(GregorianCalendar.HOUR_OF_DAY),
                    gc.get(GregorianCalendar.MINUTE),
                    gc.get(GregorianCalendar.SECOND));
            File screenshotFile = new File(temp);

            ImageIO.write(screenshot, "jpeg", screenshotFile);

            Computer selectedComputer = getSelectedValue();

            if (selectedComputer != null) {

                TransferClient ts = new TransferClient(selectedComputer.getHostAddress(), Server.TRANSFER_PORT);

                ts.addFiles(new File[]{screenshotFile}).send();
            }
        } catch (AWTException | IOException ex) {
            Logger.getLogger(ComputerList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
