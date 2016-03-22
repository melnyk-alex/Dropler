/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net;

import java.net.InetAddress;
import java.util.Set;

/**
 *
 * @author human
 */
public interface NetworkListener {

    void addressUpdated(Set<InetAddress> addresses);
}
