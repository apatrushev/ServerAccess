/*
 * Copyright (C) 2005-2012 NAUMEN. All rights reserved.
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 2 as published by the Free Software
 * Foundation and appearing in the file LICENSE.GPL included in the
 * packaging of this file.
 *
 */
package ru.naumen.servacc;

import ru.naumen.servacc.util.Util;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class HTTPResource implements AutoCloseable
{
    private static final int TIMEOUT = 0;

    private URL url;
    private String password;
    private String login;

    private HttpURLConnection connection;

    public class NotAuthenticatedError extends Exception
    {
        private static final long serialVersionUID = -3931469290753527645L;

        public NotAuthenticatedError(String message)
        {
            super(message);
        }
    }

    public HTTPResource(String url) throws MalformedURLException
    {
        this.url = new URL(url);
    }

    @Override
    public void close()
    {
        if (connection != null)
        {
            connection.disconnect();
            connection = null;
        }
    }

    public void setAuthentication(String login, String password)
    {
        this.login = login;
        this.password = password;
    }

    public InputStream getInputStream() throws Exception
    {
        close();
        connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(TIMEOUT);
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        if ("https".equals(url.getProtocol()) && connection instanceof HttpsURLConnection)
        {
            ((HttpsURLConnection) connection).setSSLSocketFactory(getSSLSocketFactory());
        }
        if (!Util.isEmptyOrNull(login))
        {
            String passwordToEncode = this.password != null ? this.password : "";
            String auth = "Basic " + Base64.getEncoder().encodeToString((login + ":" + passwordToEncode).getBytes());
            connection.setRequestProperty("Authorization", auth);
        }
        connection.connect();
        try
        {
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                String encoding = connection.getContentEncoding();

                if (encoding != null && encoding.equalsIgnoreCase("gzip"))
                {
                    return new GZIPInputStream(connection.getInputStream());
                }
                else if (encoding != null && encoding.equalsIgnoreCase("deflate"))
                {
                    return new InflaterInputStream(connection.getInputStream(), new Inflater(true));
                }
                else
                {
                    return connection.getInputStream();
                }
            }
            else if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
            {
                throw new NotAuthenticatedError(connection.getResponseMessage());
            }
            else
            {
                String description = String.valueOf(connection.getResponseCode());
                if (!Util.isEmptyOrNull(connection.getResponseMessage()))
                {
                    description += ", " + connection.getResponseMessage();
                }
                throw new IOException("Unexpected response code: " + description);
            }
        }
        catch (Exception e)
        {
            close();
            throw e;
        }
    }

    private static TrustManager[] getConfidingTrustManager()
    {
        return new TrustManager[]{ new X509TrustManager()
        {
            public java.security.cert.X509Certificate[] getAcceptedIssuers()
            {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
            {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
            {
            }
        } };
    }

    private static SSLSocketFactory getSSLSocketFactory() throws Exception
    {
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, getConfidingTrustManager(), new java.security.SecureRandom());
        return context.getSocketFactory();
    }
}
