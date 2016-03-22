/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net.bean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import ua.com.codefire.dropler.net.Server;

/**
 *
 * @author human
 */
public final class DropBeanUtil {

    private static final Logger LOG = Logger.getLogger(DropBeanUtil.class.getName());

    static {
        try {
            LOG.addHandler(new FileHandler(String.format("%s.log", DropBeanUtil.class.getSimpleName()), true));
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(DropBeanUtil.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    private static InetSocketAddress localSocketAddress;

    /**
     * Return Dropler socket address by default API port.
     *
     * @return instance of InetSocketAddress.
     */
    public synchronized static InetSocketAddress getDefaultDroplerAddress() {
        if (localSocketAddress == null) {
            try {
                localSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), Server.API_PORT);
            } catch (UnknownHostException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }

        return localSocketAddress;
    }

    /**
     * Save DropBean object to stream.
     *
     * @param <T>
     * @param stream
     * @param bean
     */
    public static <T extends DropBean> void storeToStream(OutputStream outputStream, T bean) {
        XStream xStream = new XStream(new PureJavaReflectionProvider(), new StaxDriver());
        xStream.toXML(bean, outputStream);
    }

    /**
     * Load DropBean object from stream.
     *
     * @param <T>
     * @param input
     * @return
     */
    public static <T extends DropBean> T loadFromStream(InputStream inputStream) {
        XStream xStream = new XStream(new PureJavaReflectionProvider(), new StaxDriver());
        return (T) xStream.fromXML(inputStream);
    }
}
