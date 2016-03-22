/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net.bean;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author human
 */
public class FilesDrop extends DropBean {

    private InetAddress toAddress;
    private List<File> files;

    public FilesDrop(InetAddress toAddress, List<File> files) {
        super(DropBeanType.SEND_FILES);
        this.toAddress = toAddress;
        this.files = files;
    }

    public FilesDrop(InetAddress toAddress) {
        super(DropBeanType.SEND_FILES);
        this.toAddress = toAddress;
        this.files = new ArrayList<>();
    }

    public InetAddress getToAddress() {
        return toAddress;
    }

    public List<File> getFiles() {
        return files;
    }
    
    public boolean addFile(File file) {
        return files.add(file);
    }
    
    public boolean addFiles(Collection<File> files) {
        return files.addAll(files);
    }
}
