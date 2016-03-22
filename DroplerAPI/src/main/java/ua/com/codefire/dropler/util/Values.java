/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author human
 */
public class Values {

    /**
     * Convert byte size to string. (1000 to 1kB)
     * @param size
     * @return 
     */
    public static String byteSizeToString(long size) {
        String letter;
        double pow;

        if (size < 1000l) {
            // Byte
            pow = Math.pow(2.0, 0);
            letter = "B";
        } else if (size > 1000l && size <= 1000000l) {
            // Kilobyte
            pow = Math.pow(2.0, 10);
            letter = "kB";
        } else if (size > 1000000l && size <= 1000000000l) {
            // Megabytre
            pow = Math.pow(2.0, 20);
            letter = "MB";
        } else if (size > 1000000000l && size <= 1000000000000l) {
            // Gigabyte
            pow = Math.pow(2.0, 30);
            letter = "GB";
        } else {
            // Terabyte
            pow = Math.pow(2.0, 40);
            letter = "TB";
        }

        String count = new BigDecimal((double) size / pow).setScale(2, RoundingMode.HALF_EVEN).toString();

        return String.format("%s %s", count, letter);
    }
}
