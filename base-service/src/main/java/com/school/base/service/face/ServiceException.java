package com.school.base.service.face;

/**
 * @author work
 */
public class ServiceException extends RuntimeException {

    public ServiceException() {
    }

    public ServiceException(String string) {
        super(string);
    }

    public ServiceException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public ServiceException(Throwable thrwbl) {
        super(thrwbl);
    }
}
