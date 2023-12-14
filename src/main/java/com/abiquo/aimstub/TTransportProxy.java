/**
 * Copyright (C) 2008 - Abiquo Holdings S.L. All rights reserved.
 *
 * Please see /opt/abiquo/tomcat/webapps/legal/ on Abiquo server
 * or contact contact@abiquo.com for licensing information.
 */
package com.abiquo.aimstub;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.abiquo.aimstub.Aim.Client;
import com.abiquo.aimstub.Aim.Iface;

public class TTransportProxy implements InvocationHandler
{

    protected TTransport transport;

    protected TProtocol protocol;

    protected Client client;

    protected String url;

    protected int port;

    @SuppressWarnings("rawtypes")
    public static Iface getInstance(final String url, final int port)
    {
        return (Iface) java.lang.reflect.Proxy.newProxyInstance(
            Thread.currentThread().getContextClassLoader(), new Class[] {Iface.class},
            new TTransportProxy(url, port));
    }

    protected TTransportProxy(final String url, final int port)
    {
        super();

        this.url = url;
        this.port = port;

        this.transport = new TSocket(this.url, this.port);
        this.protocol = new TBinaryProtocol(this.transport);
        this.client = new Client(this.protocol);
    }

    /**
     * Intercepts all calls to business logic and checks user session.
     * 
     * @param target The target object to invoke.
     * @param method The target method to invoke.
     * @param args The arguments of the method.
     * @throws Throwable If an exception is thrown on the target method.
     * @see BasicCommand#execute(UserSession, String[], Object[], Class)
     */
    @Override
    public Object invoke(final Object target, final Method method, final Object[] args)
        throws Throwable
    {

        Object result;

        try
        {
            transport.open();

            result = method.invoke(client, args);
        }
        catch (InvocationTargetException ex)
        {
            // Re-throw the exception thrown by the target method
            throw ex.getTargetException();
        }
        finally
        {
            if (transport.isOpen())
            {
                transport.close();
            }
        }

        return result;
    }

}
