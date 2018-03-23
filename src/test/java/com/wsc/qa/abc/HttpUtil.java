package com.wsc.qa.abc;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;


public class HttpUtil
{
    public static final String DEFAULT_CHARSET = "UTF-8";
    private static final String METHOD_POST = "POST";

    private HttpUtil() {
        throw new UnsupportedOperationException();
    }

    public static String buildQuery(final Map<String, String> params, final String charset) throws Exception {
        if (params == null || params.isEmpty()) {
            return null;
        }
        final StringBuilder query = new StringBuilder();
        final Set<Map.Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;
        for (final Map.Entry<String, String> entry : entries) {
            final String name = entry.getKey();
            final String value = entry.getValue();
            if (StringUtil.areNotEmpty(name, value)) {
                if (hasParam) {
                    query.append("&");
                }
                else {
                    hasParam = true;
                }
                query.append(name).append("=").append(URLEncoder.encode(value, charset));
            }
        }
        return query.toString();
    }

    public static String doPost(final String url, final Map<String, String> params, final int connectTimeout, final int readTimeout) throws Exception {
        return doPost(url, params, "UTF-8", connectTimeout, readTimeout);
    }

    public static String doPost(final String url, final Map<String, String> params, final String charset, final int connectTimeout, final int readTimeout) throws Exception {
        final String ctype = "application/x-www-form-urlencoded;charset=" + charset;
        final String query = buildQuery(params, charset);
        byte[] content = new byte[0];
        if (query != null) {
            content = query.getBytes(charset);
        }
        return doPost(url, ctype, content, connectTimeout, readTimeout);
    }

    public static String doPost(final String url, final Map<String, String> params, final Map<String, FileItem> fileParams, final int connectTimeout, final int readTimeout) throws Exception {
        if (fileParams == null || fileParams.isEmpty()) {
            return doPost(url, params, "UTF-8", connectTimeout, readTimeout);
        }
        return doPost(url, params, fileParams, "UTF-8", connectTimeout, readTimeout);
    }

    public static String doPost(final String url, final Map<String, String> params, final Map<String, FileItem> fileParams, final String charset, final int connectTimeout, final int readTimeout) throws IOException, JdException {
        final String boundary = System.currentTimeMillis() + "";
        HttpURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;
        try {
            try {
                final String ctype = "multipart/form-data;charset=" + charset + ";boundary=" + boundary;
                conn = getConnection(new URL(url), "POST", ctype);
                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);
            }
            catch (IOException e) {
                throw new JdException(e);
            }
            try {
                out = conn.getOutputStream();
                final byte[] entryBoundaryBytes = ("\r\n--" + boundary + "\r\n").getBytes(charset);
                final Set<Map.Entry<String, String>> textEntrySet = params.entrySet();
                for (final Map.Entry<String, String> textEntry : textEntrySet) {
                    final byte[] textBytes = getTextEntry(textEntry.getKey(), textEntry.getValue(), charset);
                    out.write(entryBoundaryBytes);
                    out.write(textBytes);
                }
                final Set<Map.Entry<String, FileItem>> fileEntrySet = fileParams.entrySet();
                for (final Map.Entry<String, FileItem> fileEntry : fileEntrySet) {
                    final FileItem fileItem = fileEntry.getValue();
                    final byte[] fileBytes = getFileEntry(fileEntry.getKey(), fileItem.getFileName(), fileItem.getMimeType(), charset);
                    out.write(entryBoundaryBytes);
                    out.write(fileBytes);
                    byte[] content = fileItem.getContent();
                    content = ((content == null) ? new byte[0] : content);
                    out.write(content);
                }
                final byte[] endBoundaryBytes = ("\r\n--" + boundary + "--\r\n").getBytes(charset);
                out.write(endBoundaryBytes);
                rsp = getResponseAsString(conn);
            }
            catch (IOException e) {
                throw new JdException(e);
            }
        }
        finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rsp;
    }

    public static String doGet(final String url,  final String charset, final int connectTimeout, final int readTimeout) throws IOException, JdException {
    	final String boundary = System.currentTimeMillis() + "";
    	HttpURLConnection conn = null;
    	OutputStream out = null;
    	String rsp = null;
    	conn = getConnection(new URL(url), "GET", charset);
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		rsp = getResponseAsString(conn);
    	return rsp;
    }

    public static String doPost(final String url, final String ctype, final byte[] content, final int connectTimeout, final int readTimeout) throws IOException {
        HttpURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;
        try {
            conn = getConnection(new URL(url), "POST", ctype);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            out = conn.getOutputStream();
            out.write(content);
            rsp = getResponseAsString(conn);
        }
        finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rsp;
    }

    private static HttpURLConnection getConnection(final URL url, final String method, final String ctype) throws IOException {
        HttpURLConnection conn = null;
        if ("https".equals(url.getProtocol())) {
            SSLContext ctx = null;
            try {
                ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0], new DefaultTrustManager[] { new DefaultTrustManager() }, new SecureRandom());
            }
            catch (Exception e) {
                throw new IOException(e);
            }
            final HttpsURLConnection connHttps = (HttpsURLConnection)url.openConnection();
            connHttps.setSSLSocketFactory(ctx.getSocketFactory());
            connHttps.setHostnameVerifier(new HostnameVerifier() {
                @Override
				public boolean verify(final String hostname, final SSLSession session) {
                    return true;
                }
            });
            conn = connHttps;
        }
        else {
            conn = (HttpURLConnection)url.openConnection();
        }
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
        conn.setRequestProperty("User-Agent", "360buy-sdk-java");
        conn.setRequestProperty("Content-Type", ctype);
        return conn;
    }

    protected static String getResponseAsString(final HttpURLConnection conn) throws IOException {
        final String charset = getResponseCharset(conn.getContentType());
        final InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), charset);
        }
        final String msg = getStreamAsString(es, charset);
        if (StringUtil.isEmpty(msg)) {
            throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
        }
        throw new IOException(msg);
    }

    private static String getStreamAsString(final InputStream stream, final String charset) throws IOException {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
            final StringWriter writer = new StringWriter();
            final char[] chars = new char[256];
            int count = 0;
            while ((count = reader.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }
            return writer.toString();
        }
        finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private static String getResponseCharset(final String ctype) {
        String charset = "UTF-8";
        if (!StringUtil.isEmpty(ctype)) {
            final String[] arr$;
            final String[] params = arr$ = ctype.split(";");
            final int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                String param = arr$[i$];
                param = param.trim();
                if (param.startsWith("charset")) {
                    final String[] pair = param.split("=", 2);
                    if (pair.length == 2 && !StringUtil.isEmpty(pair[1])) {
                        charset = pair[1].trim();
                        break;
                    }
                    break;
                }
                else {
                    ++i$;
                }
            }
        }
        return charset;
    }

    private static byte[] getTextEntry(final String fieldName, final String fieldValue, final String charset) throws IOException {
        final StringBuilder entry = new StringBuilder();
        entry.append("Content-Disposition:form-data;name=\"");
        entry.append(fieldName);
        entry.append("\"\r\nContent-Type:text/plain\r\n\r\n");
        entry.append(fieldValue);
        return entry.toString().getBytes(charset);
    }

    private static byte[] getFileEntry(final String fieldName, final String fileName, final String mimeType, final String charset) throws IOException {
        final StringBuilder entry = new StringBuilder();
        entry.append("Content-Disposition:form-data;name=\"");
        entry.append(fieldName);
        entry.append("\";filename=\"");
        entry.append(fileName);
        entry.append("\"\r\nContent-Type:");
        entry.append(mimeType);
        entry.append("\r\n\r\n");
        return entry.toString().getBytes(charset);
    }

    public static void main(String[] args) throws Exception {

    	HashMap<String, String> temp= new HashMap<>();
    	temp.put("a", "2");
    	temp.put("b", "1");
    	String var = doGet("https://www.baidu.com?1111", "utf-8", 3000,3000);
    	System.out.println(var);
	}


}
