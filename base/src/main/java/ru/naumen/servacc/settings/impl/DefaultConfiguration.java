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
import java.io.IOException;

import ru.naumen.servacc.exception.ServerAccessException;
import ru.naumen.servacc.settings.ApplicationProperties;
import ru.naumen.servacc.settings.ListProvider;

/**
 * @author Andrey Hitrin
 * @since 07.09.12
 */
public class DefaultConfiguration
{
    private ApplicationProperties properties;
    private ApplicationProperties windowProperties;

    public DefaultConfiguration(ApplicationProperties properties, ApplicationProperties windowProperties)
    {
        this.properties = properties;
        this.windowProperties = windowProperties;
    }

    public ListProvider filterProperties(String regex)
    {
        return new PropertiesFilter(properties.getAppProperties(), regex);
    }

    public ApplicationProperties getWindowProperties()
    {
        return windowProperties;
    }

    public static DefaultConfiguration create(File configDirectory)
    {
        try
        {
            configDirectory.mkdirs();
            file(configDirectory, "accounts.xml", new FileCopy("/defaults/accounts.xml"));
            return new DefaultConfiguration(
                file(configDirectory, "serveraccess.properties", new DefaultPropertiesFile()),
                file(configDirectory, "window.properties", new FileCopy("/defaults/window.properties")));
        }
        catch (IOException e)
        {
            throw new ServerAccessException(e);
        }
    }

    private static ApplicationProperties file(File rootDir, String name, DefaultFile defaultContent) throws IOException
    {
        File propertiesFile = new File(rootDir, name);
        if (propertiesFile.createNewFile())
        {
            defaultContent.fill(propertiesFile);
        }
        return new ApplicationProperties(propertiesFile);
    }
}
