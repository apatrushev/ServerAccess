/**
 * 
 */
package ru.naumen.servacc.activechannel;

import java.net.ServerSocket;
import java.net.Socket;

import ru.naumen.servacc.activechannel.i.IActiveChannelThrough;

/**
 * @author vtarasov
 * @since 18.02.16
 */
public class FTPActiveChannel extends SocketActiveChannel
{
    public FTPActiveChannel(IActiveChannelThrough parent, ActiveChannelsRegistry registry, Socket socket, ServerSocket server)
    {
        super(parent, registry, socket, server);
    }
    
    @Override
    public String getId()
    {
        return String.valueOf(getSocket().getPort());
    }
    
    @Override
    public boolean matches(String filter)
    {
        return true;
    }
    
    @Override
    public String getIconName()
    {
        return "/icons/drive-network.png";
    }
    
    @Override
    public String toString()
    {
        return "ports: " + getSocket().getPort() + " to " + getServer().getLocalPort() + " (ftp)";
    }
}