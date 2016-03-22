/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.res;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author human
 */
public class R {

    private static final Logger LOG = Logger.getLogger(R.class.getName());

    public static Properties getProperties(String name) {
        Properties properties = new Properties();
        try {
            properties.load(R.class.getResourceAsStream(String.format("%s.properties", name)));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return properties;
    }

    public static void setProperties(String name, Properties p) {
        URL url = R.class.getResource(String.format("%s.properties", name));
        try {
            p.store(new FileOutputStream(url.getFile()), new GregorianCalendar().toString());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
