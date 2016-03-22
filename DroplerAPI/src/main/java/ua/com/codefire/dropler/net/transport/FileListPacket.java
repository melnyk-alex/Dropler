/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net.transport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author human
 */
public class FileListPacket extends Packet {

    private List<FileInfo> fileInfoList;

    public FileListPacket(List<FileInfo> fileInfoList) {
        super(PacketType.FILE_LIST);
        this.fileInfoList = fileInfoList;
    }

    public FileListPacket() {
        super(PacketType.FILE_LIST);
        this.fileInfoList = new ArrayList<>();
    }

    public List<FileInfo> getFileInfoList() {
        return fileInfoList;
    }
    
    public FileListPacket addFiles(List<File> files) {
        for (File file : files) {
            fileInfoList.add(new FileInfo(file));
        }
        
        return this;
    }
}
