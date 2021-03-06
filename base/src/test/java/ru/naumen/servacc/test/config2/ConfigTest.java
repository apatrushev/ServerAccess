/*
 * Copyright (C) 2005-2012 NAUMEN. All rights reserved.
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 2 as published by the Free Software
 * Foundation and appearing in the file LICENSE.GPL included in the
 * packaging of this file.
 *
 */
package ru.naumen.servacc.test.config2;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;
import ru.naumen.servacc.config2.Account;
import ru.naumen.servacc.config2.Config;
import ru.naumen.servacc.config2.Group;
import ru.naumen.servacc.config2.HTTPAccount;
import ru.naumen.servacc.config2.SSHAccount;
import ru.naumen.servacc.config2.SSHKey;

public class ConfigTest
{
    static Config createConfigSafe(InputStream stream)
    {
        try
        {
            return new Config(stream);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testConfigurationParser()
    {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("test-config.xml");
        Config config = createConfigSafe(stream);
        Assert.assertEquals("There must be one group", config.getChildren().size(), 1);
        Group group = (Group)config.getChildren().get(0);
        Assert.assertTrue("There must be child group", group.getChildren().get(0) instanceof Group);
        group = (Group) group.getChildren().get(0);
        Assert.assertEquals("There must be 3 accounts", group.getChildren().size(), 3);

        Assert.assertTrue("First one is SSHAccount", group.getChildren().get(0) instanceof SSHAccount);
        SSHAccount sshAccount = (SSHAccount)group.getChildren().get(0);
        String NONAME = "*** empty ***";
        Assert.assertFalse("Account must have a name", NONAME.equals(sshAccount.toString()));
        Assert.assertEquals("SSHAccount: attribute host", "127.0.0.1", sshAccount.getHost());
        Assert.assertEquals("SSHAccount: attribute port", Integer.valueOf(23), sshAccount.getPort());
        Assert.assertEquals("SSHAccount: attribute login", "foo", sshAccount.getLogin());
        Assert.assertEquals("SSHAccount: attribute password", "bar", sshAccount.getPassword());
        Assert.assertEquals("SSHAccount: attribute through", null, sshAccount.getParams().get("through"));
        Assert.assertEquals("SSHAccount: attribute comment", "example server", sshAccount.getComment());
        assertThat(sshAccount.getSecureKey(), is(nullValue()));

        Assert.assertTrue("Second one is Account", group.getChildren().get(1) instanceof Account);
        Account account = (Account)group.getChildren().get(1);
        Assert.assertEquals("Account: attribute login", "foo1", account.getParams().get("login"));
        Assert.assertEquals("Account: attribute password", "bar1", account.getParams().get("password"));
        Assert.assertEquals("SSHAccount: attribute through", "1", account.getParams().get("through"));

        Assert.assertTrue("Third one is HTTPAccount", group.getChildren().get(2) instanceof HTTPAccount);
        HTTPAccount httpAccount = (HTTPAccount)group.getChildren().get(2);
        Assert.assertEquals("HTTPAccount: attribute url", "http://127.0.0.1:8080/main", httpAccount.getURL());
        Assert.assertEquals("HTTPAccount: attribute login", "foo2", httpAccount.getLogin());
        Assert.assertEquals("HTTPAccount: attribute password", "bar2", httpAccount.getPassword());
        Assert.assertEquals("HTTPAccount: attribute through", "1", httpAccount.getParams().get("through"));
    }

    @Test
    public void allowRSAKeyInSSHAccountConfig() throws Exception
    {
        String accountWithRSAKey = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>" +
                                   "<Accounts version=\"2\">" +
                                   "<Account id=\"1\" type=\"ssh\" comment=\"example server\">" +
                                       "<Param name=\"login\" value=\"honey\" />" +
                                       "<Param name=\"rsaKey\" value=\"id_rsa.pub\" />" +
                                       "<Param name=\"rsaPassword\" value=\"princess\" />" +
                                       "<Param name=\"address\" value=\"192.168.1.1\" />" +
                                   "</Account>" +
                                   "</Accounts>";
        Config config = new Config(new ByteArrayInputStream(accountWithRSAKey.getBytes(StandardCharsets.UTF_8)));
        SSHAccount account = (SSHAccount) config.getChildren().get(0);
        SSHKey key = account.getSecureKey();

        assertThat(account.getLogin(), is("honey"));
        assertThat(account.getPassword(), is(nullValue()));
        assertThat(key.getProtocolType(), is("ssh-rsa"));
        assertThat(key.getPath(), is("id_rsa.pub"));
        assertThat(key.getPassword(), is("princess"));
    }

    @Test
    public void allowDSAKeyInSSHAccountConfig() throws Exception
    {
        String accountWithRSAKey = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>" +
            "<Accounts version=\"2\">" +
            "<Account id=\"1\" type=\"ssh\" comment=\"example server\">" +
            "<Param name=\"login\" value=\"honey\" />" +
            "<Param name=\"dsaKey\" value=\"id_dsa.pub\" />" +
            "<Param name=\"dsaPassword\" value=\"ebola\" />" +
            "<Param name=\"address\" value=\"192.168.1.1\" />" +
            "</Account>" +
            "</Accounts>";
        Config config = new Config(new ByteArrayInputStream(accountWithRSAKey.getBytes(StandardCharsets.UTF_8)));
        SSHAccount account = (SSHAccount) config.getChildren().get(0);
        SSHKey key = account.getSecureKey();

        assertThat(account.getLogin(), is("honey"));
        assertThat(account.getPassword(), is(nullValue()));
        assertThat(key.getProtocolType(), is("ssh-dss"));
        assertThat(key.getPath(), is("id_dsa.pub"));
        assertThat(key.getPassword(), is("ebola"));
    }
}
