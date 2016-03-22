/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net.transport;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author human
 */
public class FileInfo implements Serializable {

    private String name;
    private long size;

    public FileInfo(File file) {
        this.name = file.getName();
        this.size = file.length();
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }
}
