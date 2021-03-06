/**
 * Copyright (C) 2008 - Abiquo Holdings S.L. All rights reserved.
 *
 * Please see /opt/abiquo/tomcat/webapps/legal/ on Abiquo server
 * or contact contact@abiquo.com for licensing information.
 */
package com.abiquo.aimstub;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.aimstub.Aim.Iface;
import com.abiquo.testng.TestConfig;

/**
 * To run the test : an instance of ''AIM'' agent MUST be running on ''host'':''port'' and assure
 * its configuration have the same values for ''datastore'' and ''repository''.
 */
@Test(groups = TestConfig.ALL_INTEGRATION_TESTS)
public class RimpStubTest
{

    protected static String datastore = "/opt/test/datastore";

    protected static String reposirory = "/opt/test/repository";

    public static final String host = "localhost";

    public static final Integer port = 8889;

    private static String viPath = "some/disk/file space.vhd";

    // 1Gb
    private static Long viSize = 1024 * 1024 * 100l;// 1024 * 1024 * 1024 * 1l;

    private static String vmachineUUID = "XXXX-UUID-XXXX";

    private static String vmachineUUID2 = "XXXX-UUID2-XXXX";

    private static String snapshot = "XXXX-snapshot-XXXX";

    private static String datastoreDefault = "";

    TTransport transport = new TSocket(host, port);

    private Iface aimclient;

    @BeforeMethod
    public void setUp() throws TTransportException
    {
        transport.open();

        TProtocol protocol = new TBinaryProtocol(transport);
        aimclient = new Aim.Client(protocol);

        createFolder(reposirory);
        createVirtualImageFile(reposirory);

        createFolder(datastore);
        createVirtualImageFile(datastore);

    }

    @AfterMethod
    public void tearDown()
    {
        transport.close();
        clearVirtualImageFile(reposirory);
        clearFolder(reposirory);

        clearVirtualImageFile(datastore);
        clearFolder(datastore);
    }

    private void clearVirtualImageFile(final String folder)
    {
        final String absViRepositoryPath = folder + '/' + viPath;
        Boolean deleted = new File(absViRepositoryPath).delete();
        Assert.assertTrue(deleted);
    }

    private void clearFolder(final String folder)
    {
        final String absViRepositoryPath = folder + '/' + viPath;
        Boolean deleted =
            new File(absViRepositoryPath.substring(0, absViRepositoryPath.lastIndexOf('/')))
                .delete();
        Assert.assertTrue(deleted);
    }

    private void createFolder(final String folder)
    {
        final String absViRepositoryPath = folder + '/' + viPath;
        Boolean createdFolder =
            new File(absViRepositoryPath.substring(0, absViRepositoryPath.lastIndexOf('/')))
                .mkdirs();

        Assert.assertTrue(createdFolder);
    }

    private void createVirtualImageFile(final String folder)
    {
        final String absViRepositoryPath = folder + '/' + viPath;
        File diskFile = new File(absViRepositoryPath);
        try
        {
            RandomAccessFile f = new RandomAccessFile(diskFile, "rw");
            f.setLength(viSize);
            f.close();
        }
        catch (IOException e)
        {
            fail();
        }
    }

    @Test
    public void testCopyToDatastoreAndDelete() throws RimpException, TException
    {
        aimclient.copyFromRepositoryToDatastore(viPath, datastoreDefault, vmachineUUID);

        String viDatastorePath = datastore + '/' + vmachineUUID;
        File viDatastore = new File(viDatastorePath);
        assertTrue(viDatastore.exists());
        Long viDatastoreSize = viDatastore.length();
        assertEquals(viSize, viDatastoreSize);

        aimclient.deleteVirtualImageFromDatastore(datastoreDefault, vmachineUUID);
        assertFalse(viDatastore.exists());
    }

    @Test
    public void testDoubleCopyToDatastoreAndDelete() throws RimpException, TException
    {
        aimclient.copyFromRepositoryToDatastore(viPath, datastoreDefault, vmachineUUID);

        String viDatastorePath = datastore + '/' + vmachineUUID;
        File viDatastore = new File(viDatastorePath);
        assertTrue(viDatastore.exists());
        Long viDatastoreSize = viDatastore.length();
        assertEquals(viSize, viDatastoreSize);

        aimclient.copyFromRepositoryToDatastore(viPath, datastoreDefault, vmachineUUID2);

        String viDatastorePath2 = datastore + '/' + vmachineUUID2;
        File viDatastore2 = new File(viDatastorePath2);
        assertTrue(viDatastore2.exists());
        Long viDatastoreSize2 = viDatastore2.length();
        assertEquals(viSize, viDatastoreSize2);

        aimclient.deleteVirtualImageFromDatastore(datastoreDefault, vmachineUUID);
        assertFalse(viDatastore.exists());

        aimclient.deleteVirtualImageFromDatastore(datastoreDefault, vmachineUUID2);
        assertFalse(viDatastore2.exists());
    }

    @Test
    public void testCopyToRepository() throws RimpException, TException
    {
        aimclient.copyFromDatastoreToRepository(viPath, snapshot, "algo", datastore);

        String viRepositoryPath = reposirory + "/algo/" + snapshot;
        File viDatastore = new File(viRepositoryPath);
        assertTrue(viDatastore.exists());
        Long viDatastoreSize = viDatastore.length();
        assertEquals(viSize, viDatastoreSize);

        assertTrue(viDatastore.delete());
    }

    @Test
    public void testGetRimpConf() throws RimpException, TException
    {
        aimclient.checkRimpConfiguration();
    }

    @Test
    public void testGetDatastores() throws RimpException, TException
    {
        List<Datastore> stores = aimclient.getDatastores();

        assertTrue(stores.size() >= 1);
    }

    @Test
    public void testGetNetInterfaces() throws RimpException, TException
    {
        List<NetInterface> netfaces = aimclient.getNetInterfaces();

        assertTrue(netfaces.size() >= 1);
    }
}
