package com.frigobrain.iot;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * 开发环境信任所有证书的 TrustManager
 * 生产环境应使用华为云 IoTDA 的 CA 证书进行验证
 */
public class TrustAllManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        // Trust all in dev
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
        // Trust all in dev
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
