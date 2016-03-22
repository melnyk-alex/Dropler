/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net;

/**
 *
 * @author human
 */
public interface ServerListener {

    void serverStarted(Server server);

    void serverStoped(Server server);
}
