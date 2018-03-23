package com.wsc.qa.abc;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class DefaultTrustManager implements X509TrustManager
{
    @Override
	public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    @Override
	public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
    }

    @Override
	public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
    }
}