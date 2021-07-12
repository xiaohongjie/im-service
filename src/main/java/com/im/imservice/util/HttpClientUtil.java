package com.im.imservice.util;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP/HTTPS 请求工具类
 *
 * @author :
 * @version : 2.0.0
 * @date : 2017/4/27
 */

public class HttpClientUtil {
	private static final Logger logger = LoggerFactory.getLogger(com.im.imservice.util.HttpClientUtil.class);

	private static PoolingHttpClientConnectionManager poolManager;
	private static RequestConfig requestConfig;
	private static SocketConfig socketConfig;

	private static HttpRequestRetryHandler httpRequestRetryHandler;

	// 最大连接数
	private static int _MAX_TOTAL = 200;
	// 每个目标服务器最大连接数(缺省)
	private static int _DEFAULT_MAX_PER_ROUTE = 50;

	private static int _DEFAULT_CONN_TIMEOUT = 28000;
	private static int _DEFAULT_CONN_REQUEST_TIMEOUT = 8000;
	private static int _DEFAULT_CONN_SOCKET_TIMEOUT = 8000;

	private static int _DEFAULT_SO_TIMEOUT = 5000;
	private static int _DEFAULT_SO_LINGER = 10;

	static {
		poolManager = new PoolingHttpClientConnectionManager();
		poolManager.setMaxTotal(_MAX_TOTAL);
		poolManager.setDefaultMaxPerRoute(_DEFAULT_MAX_PER_ROUTE);

		requestConfig = RequestConfig.custom().setConnectTimeout(_DEFAULT_CONN_TIMEOUT).setConnectionRequestTimeout(_DEFAULT_CONN_REQUEST_TIMEOUT)
				.setSocketTimeout(_DEFAULT_CONN_SOCKET_TIMEOUT).build();

		socketConfig = SocketConfig.custom().setSoKeepAlive(true).setSoTimeout(_DEFAULT_SO_TIMEOUT).setSoLinger(_DEFAULT_SO_LINGER).build();

		// 请求重试处理
		httpRequestRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				if (executionCount > 2) {// 如果已经重试了2次(不包括第一次)，就放弃
					return false;
				}
				if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
					return true;
				}
				if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
					return false;
				}
				if (exception instanceof SocketTimeoutException) {// 超时
					return true;
				}
				if (exception instanceof UnknownHostException) {// 目标服务器不可达
					return false;
				}
				if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
					return true;
				}
				if (exception instanceof SSLException) {// SSL握手异常
					return false;
				}

				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				// 如果请求是幂等的，就再次尝试
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};
	}

	private static @Bean
    HttpClientBuilder httpClientBuilder(boolean retryHandler) {
		RequestConfig config = RequestConfig.custom().setRedirectsEnabled(false).build();// 不允许重定向
		HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultRequestConfig(config).disableAutomaticRetries()
				.setRedirectStrategy(new LaxRedirectStrategy());// 利用LaxRedirectStrategy处理POST重定向问题;
		// HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpClientBuilder.setDefaultSocketConfig(socketConfig);
		httpClientBuilder.setDefaultRequestConfig(requestConfig);
		httpClientBuilder.setConnectionManager(poolManager);
		if (retryHandler)
			httpClientBuilder.setRetryHandler(httpRequestRetryHandler);
		return httpClientBuilder;
	}

	private static @Bean
    HttpClientBuilder httpsClientBuilder(boolean retryHandler) throws Exception {
		RequestConfig config = RequestConfig.custom().setRedirectsEnabled(false).build();// 不允许重定向
		HttpClientBuilder httpsClientBuilder = HttpClients.custom().setDefaultRequestConfig(config).disableAutomaticRetries()
				.setRedirectStrategy(new LaxRedirectStrategy());// 利用LaxRedirectStrategy处理POST重定向问题;
		// HttpClientBuilder httpsClientBuilder = HttpClients.custom();
		httpsClientBuilder.setSSLSocketFactory(createSSLConnSocketFactory());
		httpsClientBuilder.setDefaultRequestConfig(requestConfig);
		httpsClientBuilder.setConnectionManager(poolManager);
		if (retryHandler)
			httpsClientBuilder.setRetryHandler(httpRequestRetryHandler);
		return httpsClientBuilder;
	}

	/**
	 * 发送 GET 请求，不带参数
	 *
	 * @param url
	 * @return
	 */
	public static String doGet(String url) throws Exception {
		return doGet(url, new HashMap<String, Object>());
	}

	/**
	 * 发送 GET 请求，参数为 K-V形式
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	public static String doGet(String url, Map<String, Object> params) throws Exception {
		String apiUrl = url;
		StringBuffer param = new StringBuffer();
		int i = 0;
		for (String key : params.keySet()) {
			if (i == 0)
				param.append("?");
			else
				param.append("&");
			param.append(key).append("=").append(params.get(key));
			i++;
		}
		apiUrl += param;

		CloseableHttpClient httpClient = getHttpClient(apiUrl, false);
		CloseableHttpResponse response = null;
		HttpGet httpGet = new HttpGet(apiUrl);
		try {
			httpGet.addHeader("Content-Type", "application/json");
			response = httpClient.execute(httpGet);
			return httpResponse(response);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			if (null != response)
				response.close();
			if (null != httpGet)
				httpGet.releaseConnection();
		}
	}

	/**
	 * 发送 POST 请求（HTTP），不带输入数据
	 *
	 * @param apiUrl
	 * @return
	 */
	public static String doPost(String apiUrl) throws Exception {
		return doPost(apiUrl, new HashMap<String, Object>());
	}

	public static String doPostRetryHandler(String apiUrl) throws Exception {
		return doPostRetryHandler(apiUrl, new HashMap<String, Object>());
	}

	/**
	 * 发送 POST 请求，K-V形式
	 *
	 * @param apiUrl
	 *            API接口URL
	 * @param params
	 *            参数map
	 * @return
	 */
	public static String doPost(String apiUrl, Map<String, Object> params) throws Exception {
		return doPostByParams(apiUrl, params, false);
	}

	public static String doPostRetryHandler(String apiUrl, Map<String, Object> params) throws Exception {
		return doPostByParams(apiUrl, params, true);
	}

	private static String doPostByParams(String apiUrl, Map<String, Object> params, boolean retryHandler) throws Exception {
		CloseableHttpClient httpClient = getHttpClient(apiUrl, retryHandler);
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(apiUrl);
		try {
			List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
				pairList.add(pair);
			}
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
			response = httpClient.execute(httpPost);
			// 解决重定向问题
			response = handleRedirect(httpClient, response);

			return httpResponse(response);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (null != response)
				response.close();
			if (null != httpPost)
				httpPost.releaseConnection();
		}
	}
	
	/**
	 * 发送 POST 请求，K-V形式
	 *
	 * @param apiUrl
	 *            API接口URL
	 * @param params
	 *            参数map
	 * @return
	 */
	public static String doPostForm(String apiUrl, Map<String, Object> params) throws Exception {
		return doPostFormByParams(apiUrl, params, false);
	}
	
	private static String doPostFormByParams(String apiUrl, Map<String, Object> params, boolean retryHandler) throws Exception {
		CloseableHttpClient httpClient = getHttpClient(apiUrl, retryHandler);
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(apiUrl);
		try {
			List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
				pairList.add(pair);
			}
			httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
			httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
			response = httpClient.execute(httpPost);
			// 解决重定向问题
			response = handleRedirect(httpClient, response);

			return httpResponse(response);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (null != response)
				response.close();
			if (null != httpPost)
				httpPost.releaseConnection();
		}
	}



	/**
	 * 发送 POST 请求，JSON形式
	 *
	 * @param apiUrl
	 * @param json
	 *            json对象
	 * @return
	 */
	public static String doPost(String apiUrl, String json) throws Exception {
		return doPostByJson(apiUrl, json, false);
	}

	public static String doPostRetryHandler(String apiUrl, String json) throws Exception {
		return doPostByJson(apiUrl, json, true);
	}

	private static String doPostByJson(String apiUrl, String json, boolean retryHandler) throws Exception {
		CloseableHttpClient httpClient = getHttpClient(apiUrl, retryHandler);
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(apiUrl);
		try {
			httpPost.setEntity(new StringEntity(json.toString(), "UTF-8"));
			httpPost.addHeader("Content-Type", "application/json");
			response = httpClient.execute(httpPost);
			// 解决重定向问题
			response = handleRedirect(httpClient, response);

			return httpResponse(response);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (null != response)
				response.close();
			if (null != httpPost)
				httpPost.releaseConnection();
		}
	}

	private static CloseableHttpResponse handleRedirect(CloseableHttpClient httpClient, CloseableHttpResponse response)
			throws IOException, ClientProtocolException {
		// 解决重定向问题
		int statusCode = response.getStatusLine().getStatusCode();
		logger.info("statusCode==" + statusCode); // 返回码
		if (200 < statusCode && statusCode < 400) {
			Header header = response.getFirstHeader("Location");

			// 重定向地址
			String location = header.getValue();
			logger.info("重定向地址:" + location);

			// 然后再对新的location发起请求即可
			HttpGet httpGet = new HttpGet(location);
			response = httpClient.execute(httpGet);
			logger.info("返回报文:" + EntityUtils.toString(response.getEntity(), "UTF-8"));
		}
		return response;
	}

	/**
	 * 创建SSL安全连接
	 */
	private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
		SSLConnectionSocketFactory sslsf = null;
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}

				@Override
				public void verify(String host, SSLSocket ssl) throws IOException {
				}

				@Override
				public void verify(String host, X509Certificate cert) throws SSLException {
				}

				@Override
				public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
				}
			});
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		return sslsf;
	}

	/**
	 * 判断HTTP/HTTPS
	 *
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private static CloseableHttpClient getHttpClient(String url, boolean retryHandler) throws Exception {
		CloseableHttpClient httpClient = null;
		if (url.startsWith("https://")) {
			httpClient = httpsClientBuilder(retryHandler).build();
		} else {
			httpClient = httpClientBuilder(retryHandler).build();
		}
		return httpClient;
	}

	/**
	 * 处理 response
	 *
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private static String httpResponse(CloseableHttpResponse response) throws Exception {
		StringBuilder sb = new StringBuilder();
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception("HttpUtil response error, code=[" + statusCode + "]");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		String output;
		while ((output = br.readLine()) != null) {
			sb.append(output);
		}
		return sb.toString();
	}

}
