package com.indigententerprises.services;

/**
 *
 * @author jonniesavell
 */
public interface NotificationService<T> {

    public void notify(T t) throws NotificationException;
}
