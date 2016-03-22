/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.codefire.dropler.net.bean;

import java.io.Serializable;

/**
 *
 * @author human
 */
public abstract class DropBean implements Serializable {

    private DropBeanType beanType;

    public DropBean(DropBeanType beanType) {
        this.beanType = beanType;
    }

    public DropBeanType getBeanType() {
        return beanType;
    }
}
