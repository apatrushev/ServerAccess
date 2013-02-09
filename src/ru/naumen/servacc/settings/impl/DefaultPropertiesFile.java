/*
 * Copyright (C) 2005-2012 NAUMEN. All rights reserved.
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 2 as published by the Free Software
 * Foundation and appearing in the file LICENSE.GPL included in the
 * packaging of this file.
 *
 */
package ru.naumen.servacc.settings.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import ru.naumen.servacc.FileResource;

/**
 * @author Andrey Hitrin
 * @since 31.08.12
 */
public class DefaultPropertiesFile implements DefaultFile
{
    @Override
    public void fill(File configFile) throws IOException
    {
        File accountsFile = new File(configFile.getParentFile(), "accounts.xml");
        String path = accountsFile.getCanonicalPath().replaceAll("\\\\", "/");
        PrintStream outputStream = new PrintStream(new FileOutputStream(configFile));
        outputStream.println("# You can define several sources, with different number each");
        outputStream.println("source=" + FileResource.uriPrefix + path);
        outputStream.println("#source1=<some another source file or url here>");
        outputStream.println();
        outputStream.println("# Use 'terminal' variable to set custom terminal launcher.");
        outputStream.println("# Seek for more documentation on http://github.com/apatrushev/ServerAccess");
        outputStream.println("#terminal=xterm  {options}  -e  telnet {host} {port}");
        outputStream.println("#terminal=putty  {options}  -telnet  {host}  -P  {port}");
        outputStream.println("#terminal=open  telnet://{host}:{port}");
        outputStream.close();
    }
}
