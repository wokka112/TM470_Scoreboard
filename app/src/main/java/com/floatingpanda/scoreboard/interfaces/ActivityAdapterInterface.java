package com.floatingpanda.scoreboard.interfaces;

/**
 * Acts as a listener connecting some of the adapters to view activities which provide edit and
 * delete activities.
 */
public interface ActivityAdapterInterface {
    void startEditActivity(Object object);
    void startDeleteActivity(Object object);
}
