/*
 * Copyright (C) 2005-2012 NAUMEN. All rights reserved.
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 2 as published by the Free Software
 * Foundation and appearing in the file LICENSE.GPL included in the
 * packaging of this file.
 *
 */
package ru.naumen.servacc.config2.i;

public interface IConfigItem
{
    boolean matches(String filter);

    default boolean isAutoExpanded()
    {
        return true;
    }

    default boolean isAccount() { return false; }

    default boolean isConnectable() { return false; }

    default boolean isPortForwarder() { return false; }

    default boolean isFtpBrowseable() { return false; }

    String getIconName();
}
