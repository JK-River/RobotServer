
package com.krobot.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    private static Registry<CookieSpecProvider> cookieSpecRegistry = null;

    private static RequestConfig requestConfig = null;

    static {
        cookieSpecRegistry = RegistryBuilder.<CookieSpecProvider> create().register("easy", new CookieSpecProvider() {
            public CookieSpec create(HttpContext context) {
                return new BrowserCompatSpec() {
                    @Override
                    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
                    }
                };
            }
        }).build();

        requestConfig = RequestConfig.custom().setCookieSpec("easy").setConnectionRequestTimeout(2000)
                .setSocketTimeout(10000).setConnectTimeout(2000).build();
    }

    public String post(String postUrl, Map<String, String> paramMap) {
        String result = null;
        try {
            result = httpPost(postUrl, paramMap);
        } catch (ClientProtocolException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } 
        return result;
    }

    private String httpPost(String postUrl, Map<String, String> paramMap) throws ClientProtocolException, IOException {
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(postUrl);
            HttpEntity entity = new UrlEncodedFormEntity(getNameValuePairs(paramMap), "UTF-8");
            post.setEntity(entity);
            response = getHttpClient().execute(post);
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } finally {
            if (null != response) {
                response.close();
            }
        }
    }

    private CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setDefaultCookieSpecRegistry(cookieSpecRegistry).setDefaultRequestConfig(requestConfig).build();
    }

    private List<NameValuePair> getNameValuePairs(Map<String, String> paramMap) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (Entry<String, String> entry : paramMap.entrySet()) {
            final Entry<String, String> fEntry = entry;
            nameValuePairs.add(new NameValuePair() {
                @Override
                public String getName() {
                    return fEntry.getKey();
                }

                @Override
                public String getValue() {
                    return fEntry.getValue();
                }
            });
        }
        return nameValuePairs;
    }

    public String post(String postUrl, String xmlData) {
        String result = null;
        try {
            result = httpPost(postUrl, xmlData);
        } catch (ClientProtocolException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } 
        return result;
    }

    private String httpPost(String postUrl, String xmlData) throws ClientProtocolException, IOException {
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(postUrl);
            StringEntity entity = new StringEntity(xmlData, "UTF-8");
            entity.setContentType("text/xml");
            post.setEntity(entity);
            response = getHttpClient().execute(post);
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } finally {
            if (null != response) {
                response.close();
            }
        }
    }
}
