/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author human
 */
public final class Hasher {

    private static final Logger LOG = Logger.getLogger(Hasher.class.getName());

//    static {
//        try {
//            LOG.addHandler(new FileHandler(String.format("%s.log", Hasher.class.getSimpleName()), true));
//        } catch (IOException | SecurityException ex) {
//            Logger.getLogger(Hasher.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
//        }
//    }

    public static String md5(String target) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(target.getBytes());
            byte[] hash = md.digest();

            StringBuilder sb = new StringBuilder();

            for (byte bt : hash) {
                sb.append(Integer.toString((bt & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return null;
    }
}
