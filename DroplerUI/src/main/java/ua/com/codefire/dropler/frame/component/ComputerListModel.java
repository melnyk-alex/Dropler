/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.frame.component;

import java.util.Collection;
import javax.swing.DefaultListModel;
import ua.com.codefire.dropler.ui.Computer;

/**
 *
 * @author human
 */
public class ComputerListModel extends DefaultListModel<Computer> {

    /**
     * 
     * 
     * @param computers 
     */
    public void addAll(Collection<Computer> computers) {
        for (Computer computer : computers) {
            addElement(computer);
        }
    }
}
