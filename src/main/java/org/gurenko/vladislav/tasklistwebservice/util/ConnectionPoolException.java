package org.gurenko.vladislav.tasklistwebservice.util;

public class ConnectionPoolException extends Exception{
    ConnectionPoolException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
