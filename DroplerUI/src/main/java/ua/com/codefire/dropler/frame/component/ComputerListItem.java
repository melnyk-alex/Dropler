/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.frame.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.util.logging.Logger;
import ua.com.codefire.dropler.ui.Computer;

/**
 *
 * @author human
 */
public class ComputerListItem extends Component {

    private static final Logger LOG = Logger.getLogger(ComputerListItem.class.getName());
    private Computer computer;
    private boolean isSelected;
    private Font titleFont;
    private Font descriptionFont;
    private Color titleColor;
    private Color descriptionColor;
    private Color selectColor;
    private PopupMenu popupMenu;

    public ComputerListItem(Computer computer, boolean isSelected) {
        this.computer = computer;
        this.isSelected = isSelected;
        this.titleFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        this.descriptionFont = new Font(Font.MONOSPACED, Font.PLAIN, 10);
        this.titleColor = new Color(25, 25, 25);
        this.descriptionColor = new Color(65, 65, 65);
        this.selectColor = new Color(222, 223, 224);
    }

    @Override
    public void addNotify() {
        setSize(new Dimension(getParent().getParent().getWidth(), 40));
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Color statusColor, statusBorderColor;

        switch (computer.getState()) {
            case ONLINE:
                statusBorderColor = new Color(55, 185, 35);
                statusColor = new Color(105, 235, 85);
                break;
            case OFFLINE:
                statusBorderColor = new Color(235, 85, 105);
                statusColor = new Color(255, 255, 255);
                break;
            default:
                statusBorderColor = new Color(225, 225, 225);
                statusColor = new Color(245, 245, 245);
                break;
        }

        if (isSelected) {
            g2d.setBackground(selectColor);
        }

        g2d.clearRect(0, 0, getWidth(), getHeight());

        int statusSize = 10, statusMargin = 15;
        Point statusPoint = new Point(statusMargin, (getHeight() - statusSize) / 2);

        g2d.setColor(statusBorderColor);
        g2d.drawOval(statusPoint.x, statusPoint.y, statusSize, statusSize);

        g2d.setColor(statusColor);
        g2d.fillOval(statusPoint.x + 1, statusPoint.y + 1, statusSize - 1, statusSize - 1);

        int textMarginLeft = statusSize + statusMargin * 2, textDistance = 2;

        String title = computer.getHostName();
        String description = computer.getHostAddress();

        int titleH = (int) g.getFontMetrics(titleFont).getStringBounds(title, g).getHeight();
        int descriptionH = (int) g.getFontMetrics(descriptionFont).getStringBounds(description, g).getHeight();

        int titleY = (getHeight() - (titleH + textDistance + descriptionH)) / 2 + titleH;
        int descriptionY = titleY + textDistance + descriptionH;

        g2d.setFont(titleFont);
        g2d.setColor(titleColor);
        g2d.drawString(title, textMarginLeft, titleY);

        g2d.setFont(descriptionFont);
        g2d.setColor(descriptionColor);
        g2d.drawString(description, textMarginLeft, descriptionY);
    }
}
