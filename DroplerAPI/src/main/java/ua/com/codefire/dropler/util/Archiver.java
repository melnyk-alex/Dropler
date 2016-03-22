/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author human
 */
public class Archiver {

    private static final Logger LOG = Logger.getLogger(Archiver.class.getName());

//    static {
//        try {
//            LOG.addHandler(new FileHandler(String.format("%s.log", Archiver.class.getSimpleName()), true));
//        } catch (IOException | SecurityException ex) {
//            Logger.getLogger(Archiver.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
//    }

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static File packIntoArchive(File file) throws IOException {
        File archiveFile = new File(String.format("%s/../%s.zip", System.getProperty("java.io.tmpdir"), file.getName()));
        URI baseURI = file.getParentFile().toURI();

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(archiveFile))) {
            if (file.isDirectory()) {
                for (File fileEntry : file.listFiles()) {
                    listDirectory(zos, baseURI, fileEntry);
                }
            } else {
                zos.putNextEntry(new ZipEntry(file.getName()));
                zos.write(readFile(file));
                zos.closeEntry();
            }
        }

        return archiveFile;
    }

    /**
     *
     * @param archiveFile
     * @throws IOException
     */
    public static void unpackArchive(File archiveFile) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archiveFile))) {
            ZipEntry nextEntry;

            while ((nextEntry = zis.getNextEntry()) != null) {
                File archFile = new File(String.format("%s/%s", archiveFile.getParent(), nextEntry.getName()));

                if (nextEntry.isDirectory()) {
                    archFile.mkdir();
                } else {
                    File parent = new File(archFile.getParent());

                    if (!parent.exists()) {
                        parent.mkdirs();
                    }

                    try (FileOutputStream fos = new FileOutputStream(archFile)) {
                        int tmp;

                        while ((tmp = zis.read()) > -1) {
                            fos.write(tmp);
                        }
                    }
                }

                zis.closeEntry();
            }
        }

        archiveFile.delete();
    }

    /**
     *
     * @param zos
     * @param baseURI
     * @param fileEntry
     * @throws IOException
     */
    private static void listDirectory(ZipOutputStream zos, URI baseURI, File fileEntry) throws IOException {
        URI entryPath = baseURI.relativize(fileEntry.toURI());

        if (fileEntry.isFile()) {
            zos.putNextEntry(new ZipEntry(entryPath.getPath()));
            zos.write(readFile(fileEntry));
            zos.closeEntry();
        } else {
            String path = entryPath.getPath();
            zos.putNextEntry(new ZipEntry(path.endsWith("/") ? path : path + "/"));

            for (File dirEntry : fileEntry.listFiles()) {
                listDirectory(zos, baseURI, dirEntry);
            }

            zos.closeEntry();
        }
    }

    /**
     *
     * @param fileEntry
     * @return
     * @throws IOException
     */
    private static byte[] readFile(File fileEntry) throws IOException {
        byte[] data;
        try (FileInputStream fis = new FileInputStream(fileEntry)) {
            data = new byte[fis.available()];
            fis.read(data);
        }
        return data;
    }
}
