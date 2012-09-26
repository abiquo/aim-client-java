/**
 * Copyright (C) 2008 - Abiquo Holdings S.L. All rights reserved.
 *
 * Please see /opt/abiquo/tomcat/webapps/legal/ on Abiquo server
 * or contact contact@abiquo.com for licensing information.
 */
package com.abiquo.aimstub;

public class AimStubException extends Exception
{
    private static final long serialVersionUID = -8574961294833045095L;

    public AimStubException(String message)
    {
        super(message);
    }

    public AimStubException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
