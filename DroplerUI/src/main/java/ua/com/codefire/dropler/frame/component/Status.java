/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.frame.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;

/**
 *
 * @author human
 */
public class Status extends JComponent {
    
    private int count;
    private int online;
    private Font statusFont;
    private Color countColor;
    private Color onlineColor;

    public Status() {
        this.statusFont = new Font(Font.MONOSPACED, Font.PLAIN, 11);
        this.countColor = new Color(65, 65, 65);
        this.onlineColor = new Color(105, 235, 85);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        repaint();
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setBackground(new Color(240, 240, 240));
        g2d.clearRect(0, 0, getWidth(), getHeight());
        g2d.setColor(new Color(225, 225, 225));
        g2d.drawLine(0, 0, getWidth(), 0);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g2d.setFont(statusFont);
        
        String part_one = String.format("On-line: %d", online);
        String part_two = String.format("/%d", count);
        
        Rectangle2D messageBounds = g2d.getFontMetrics(statusFont).getStringBounds(part_one, g);
        
        int lineX = (int)(getHeight() / 2 - messageBounds.getCenterY());
        
        g2d.setColor(countColor);
        g2d.drawString(part_one, 12, lineX);
        g2d.setColor(countColor);
        g2d.drawString(part_two, (int)(12 + messageBounds.getMaxX()), lineX);
    }

}
