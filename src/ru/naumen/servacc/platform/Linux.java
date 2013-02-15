/*
 * Copyright (C) 2005-2012 NAUMEN. All rights reserved.
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 2 as published by the Free Software
 * Foundation and appearing in the file LICENSE.GPL included in the
 * packaging of this file.
 */
package ru.naumen.servacc.platform;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Andrey Hitrin
 */
public class Linux implements Platform
{
    @Override
    public void openFTPBrowser(int localPort) throws IOException
    {
        new ProcessBuilder("gftp", MessageFormat.format("ftp://anonymous@127.0.0.1:{0,number,#}", localPort)).start();
    }

    @Override
    public File getConfigDirectory() throws IOException
    {
        final String userHome = System.getProperty("user.home");
        return new File(userHome, ".serveraccess");
    }

    @Override
    public boolean isTraySupported()
    {
        return true;
    }

    @Override
    public boolean isAppMenuSupported()
    {
        return false;
    }

    @Override
    public boolean useSystemSearchWidget()
    {
        return false;
    }

    @Override
    public Command defaultBrowser()
    {
        return new Command("xdg-open  {url}");
    }

    @Override
    public Command defaultTerminal()
    {
        return new Command("xterm  -e  telnet {host} {port}");
    }
}
