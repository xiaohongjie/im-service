package com.im.imservice.util;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("all")
public class HttpClientHelper {

	private static final Logger logger = LoggerFactory.getLogger(com.im.imservice.util.HttpClientHelper.class);

	private static final String DEFAULT_CHARSET = "UTF-8";

	private static com.im.imservice.util.HttpClientHelper instance = null;

	private HttpClient client = null;

	/**
	 * 单实例.
	 * 
	 * @return
	 */
	public static com.im.imservice.util.HttpClientHelper getInstance() {

		if (null == instance) {
			synchronized (com.im.imservice.util.HttpClientHelper.class) {
				instance = new com.im.imservice.util.HttpClientHelper();
			}
		}
		return instance;
	}

	/**
	 * http之GET请求调用
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public String doGet(String path) throws Exception {

		URL localURL = new URL(path);

		URLConnection connection = localURL.openConnection();
		HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

		httpURLConnection.setRequestProperty("Accept-Charset", DEFAULT_CHARSET);
		httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		StringBuffer resultBuffer = new StringBuffer();
		String tempLine = null;
		// 响应失败
		if (httpURLConnection.getResponseCode() >= 300) {
			throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
		}

		try {
			inputStream = httpURLConnection.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputStreamReader);

			while ((tempLine = reader.readLine()) != null) {
				resultBuffer.append(tempLine);
			}
		} finally {

			if (reader != null) {
				reader.close();
			}

			if (inputStreamReader != null) {
				inputStreamReader.close();
			}

			if (inputStream != null) {
				inputStream.close();
			}

		}

		return resultBuffer.toString();
	}

	/**
	 * http之POST请求调用
	 * 
	 * @param url
	 * @param parameterMap
	 * @return
	 * @throws Exception
	 */
	public String doPost(String url, Map parameterMap) throws Exception {
		StringBuffer parameterBuffer = new StringBuffer();

		if (parameterMap != null) {
			Iterator iterator = parameterMap.keySet().iterator();
			String key = null;
			String value = null;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				if (parameterMap.get(key) != null) {
					value = (String) parameterMap.get(key);
				} else {
					value = "";
				}

				parameterBuffer.append(key).append("=").append(value);
				if (iterator.hasNext()) {
					parameterBuffer.append("&");
				}
			}
		}

		System.out.println("POST parameter : " + parameterBuffer.toString());

		URL localURL = new URL(url);

		URLConnection connection = localURL.openConnection();
		HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty("Accept-Charset", DEFAULT_CHARSET);
		httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		httpURLConnection.setRequestProperty("Content-Length", String.valueOf(parameterBuffer.length()));

		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		StringBuffer resultBuffer = new StringBuffer();
		String tempLine = null;

		try {
			outputStream = httpURLConnection.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream);

			outputStreamWriter.write(parameterBuffer.toString());
			outputStreamWriter.flush();
			// 响应失败
			if (httpURLConnection.getResponseCode() >= 300) {
				throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
			}
			// 通过io流来读取响应数据
			inputStream = httpURLConnection.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputStreamReader);

			while ((tempLine = reader.readLine()) != null) {
				resultBuffer.append(tempLine);
			}

		} finally {

			if (outputStreamWriter != null) {
				outputStreamWriter.close();
			}

			if (outputStream != null) {
				outputStream.close();
			}

			if (reader != null) {
				reader.close();
			}

			if (inputStreamReader != null) {
				inputStreamReader.close();
			}

			if (inputStream != null) {
				inputStream.close();
			}

		}

		return resultBuffer.toString();
	}

	/** =========================================live============================================== */

	/**
	 * No-args constructor
	 */
	private HttpClientHelper() {
		this(256, 25600, 5000, 60000);
	}

	/**
	 * 鏋勯�犲嚱鏁�
	 *
	 * @param maxConnPerHost
	 *            - 杩炴帴姣忓彴涓绘満鏈�澶х殑socket鏁伴噺
	 * @param totalConns
	 *            - 杩炴帴鎵�鏈変富鏈烘渶澶х殑socket鏁伴噺
	 * @param connTimeout
	 *            - socket杩炴帴瓒呮椂璁剧疆
	 * @param soTimeout
	 *            - socket璇诲啓瓒呮椂璁剧疆
	 */
	public HttpClientHelper(int maxConnPerHost, int totalConns, int connTimeout, int soTimeout) {

		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setDefaultMaxConnectionsPerHost(256); // default socket
		// connections: 8
		params.setMaxTotalConnections(5120);
		params.setConnectionTimeout(5000);
		params.setSoTimeout(60000);

		MultiThreadedHttpConnectionManager connManager = new MultiThreadedHttpConnectionManager();
		connManager.setParams(params);

		client = new HttpClient(connManager);
		// client.getHostConfiguration().setProxy("127.0.0.1", 8080);
	}

	/**
	 * 璋冪敤url鍦板潃锛岃繑鍥瀤ml瀛楃涓层��
	 *
	 * @param url
	 * @return
	 */
	public String get(String url) throws Exception {
		return this.get(url, DEFAULT_CHARSET);
	}

	/**
	 * 璋冪敤url鍦板潃锛岃繑鍥瀤ml瀛楃涓层�俒鍙�塢鎸囧畾杩斿洖缂栫爜
	 *
	 * @param url
	 *            - GET璇锋眰URL
	 * @param charSet
	 *            - 璇锋眰瀛楃缂栫爜
	 * @return - 鍝嶅簲Body鍐呭
	 */
	public String get(String url, String charSet) throws Exception {
		return get(url, charSet, null);
	}

	/**
	 * 璋冪敤url鍦板潃锛岃繑鍥瀤ml瀛楃涓层�俒鍙�塢鎸囧畾杩斿洖缂栫爜
	 *
	 * @param url
	 *            - GET璇锋眰URL
	 * @param charSet
	 *            - 璇锋眰瀛楃缂栫爜
	 * @param pairs
	 *            - 璇锋眰澶�
	 * @return - 鍝嶅簲Body鍐呭
	 */
	public String get(String url, String charSet, Map<String, String> pairs) throws Exception {

		GetMethod get = new GetMethod(url);
		get.setRequestHeader("accept", "*/*");
		get.setRequestHeader("Accept-Language", "en-us,en");
		get.setRequestHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1");
		get.setRequestHeader("Accept-Charset", charSet + ",*");
		get.setRequestHeader("Keep-Alive", "60");
		get.setRequestHeader("Connection", "keep-alive");
		get.setRequestHeader("Cache-Control", "max-age=0");
		try {

			if (null != pairs) {
				for (String k : pairs.keySet()) {
					String v = pairs.get(k);
					get.setRequestHeader(k, v);
				}
			}

			int statusCode = client.executeMethod(get);/* 鎵ц鎻愪氦 */
			if (statusCode == HttpStatus.SC_OK) { /* 鎴愬姛鑾峰彇杩斿洖鍐呭 */
				return get.getResponseBodyAsString();
			}
		} catch (Exception ex) {
			logger.error("http client error with url:" + url + ", charSet:" + charSet);
			throw ex;
		} finally {/* 閲婃斁杩炴帴 */
			if (null != get) {
				get.releaseConnection();
			}
		}
		return null;
	}

	/**
	 * http post
	 *
	 * @param url
	 *            - POST璇锋眰URL
	 * @param content
	 *            - POST鍐呭
	 * @return - 鍝嶅簲Body鍐呭
	 */
	public String post(String url, String content) throws Exception {

		UTF8PostMethod post = new UTF8PostMethod(url);
		post.setHttp11(true);
		HttpMethodRetryHandler handler = new DefaultHttpMethodRetryHandler(0, false);// 鏋勫缓涓�涓猺etry handler锛岄噸鍋氭鏁拌缃负0
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, handler);
		try {

			post.setRequestBody(content);

			int statusCode = client.executeMethod(post);/* 鎵ц鎻愪氦 */
			if (statusCode == HttpStatus.SC_OK) {/* 鎴愬姛鑾峰彇杩斿洖鍐呭 */
				return post.getResponseBodyAsString();
			}
		} catch (Exception ex) {
			logger.error("http client error with url:" + url + ", content:" + content);
			throw ex;
		} finally {/* 閲婃斁杩炴帴 */
			if (null != post) {
				post.releaseConnection();
			}
		}
		return null;
	}

	/**
	 * 涓撻棬鐢ㄦ埛灏嗚姹傚彂閫佸埌缃戠姸缃�
	 *
	 * @param url
	 * @param xmlhead
	 * @param xmlbody
	 * @return
	 */
	public String postForBOSS(String url, String xmlhead, String xmlbody) {

		UTF8PostMethod post = new UTF8PostMethod(url);

		try {
			Part[] parts = new Part[2];
			parts[0] = new StringPart("xmlhead", xmlhead);
			parts[1] = new StringPart("xmlbody", xmlbody);

			MultipartRequestEntity entity = new MultipartRequestEntity(parts, new HttpMethodParams());
			post.setRequestEntity(entity);

			post.setRequestHeader("Expect", "100-Continue");
			post.setRequestHeader("Connection", "Keep-Alive");

			int statusCode = client.executeMethod(post);/* 鎵ц鎻愪氦 */

			if (statusCode == HttpStatus.SC_OK) {/* 鎴愬姛鑾峰彇杩斿洖鍐呭 */
				return post.getResponseBodyAsString();
			}
		} catch (Exception ex) {
			logger.error(String.format("Http client post boss error with url:%s, head:%s, body:%s", url, xmlhead, xmlbody));
		} finally {/* 閲婃斁杩炴帴 */
			if (null != post) {
				post.releaseConnection();
			}
		}
		return null;
	}

	/**
	 * http post
	 *
	 * @param url
	 *            - POST璇锋眰URL
	 * @param content
	 *            - POST鍐呭
	 * @return - 鍝嶅簲Body鍐呭
	 */
	public String post(String url, NameValuePair... content) {

		UTF8PostMethod post = new UTF8PostMethod(url);
		post.setHttp11(true);
		post.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setRequestHeader("Accept-Charset", "UTF-8,*");
		post.setRequestHeader("Pragma", "no-cache");
		post.setRequestHeader("Cache-Control", "no-cache");
		post.setRequestHeader("Comp-Control", "dsmp/sms-mt");

		try {

			post.setRequestBody(content);

			int statusCode = client.executeMethod(post);/* 鎵ц鎻愪氦 */
			if (statusCode == HttpStatus.SC_OK) {/* 鎴愬姛鑾峰彇杩斿洖鍐呭 */
				return post.getResponseBodyAsString();
			}
		} catch (Exception ex) {
			logger.error("http client error with url:" + url + ", content:" + content);
		} finally {/* 閲婃斁杩炴帴 */
			if (null != post) {
				post.releaseConnection();
			}
		}
		return null;
	}

	/**
	 * http post XML 澶囨敞: http澶�:
	 *
	 * @param url
	 *            - POST璇锋眰URL
	 * @param xml
	 *            - xml瀛楃涓�
	 * @return - 鍝嶅簲Body鍐呭
	 */
	public String postXml(String url, String xml) {
		return postXml(url, xml, null);
	}

	public String postXml(String url, String xml, Map<String, String> pairs) {

		UTF8PostMethod post = new UTF8PostMethod(url);
		post.setHttp11(true);
		post.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setRequestHeader("Content-Type", "text/xml; charset=utf-8");
		post.setRequestHeader("Accept-Charset", "UTF-8,*");
		post.setRequestHeader("Pragma", "no-cache");
		post.setRequestHeader("Cache-Control", "no-cache");
		post.setRequestHeader("Comp-Control", "dsmp/sms-mt");

		try {

			if (null != pairs) {
				for (String k : pairs.keySet()) {
					String v = pairs.get(k);
					post.setRequestHeader(k, v);
				}
			}

			post.setRequestBody(xml);

			int statusCode = client.executeMethod(post);/* 鎵ц鎻愪氦 */
			if (statusCode == HttpStatus.SC_OK) {/* 鎴愬姛鑾峰彇杩斿洖鍐呭 */
				return post.getResponseBodyAsString();
			}
		} catch (Exception ex) {
			logger.error("http client error with url:" + url + ", xml:" + xml);
		} finally {/* 閲婃斁杩炴帴 */
			if (null != post) {
				post.releaseConnection();
			}
		}
		return null;
	}

	public String postJson(String url, String json) {
		return postJson(url, json, null);
	}

	public String postJson(String url, String json, Map<String, String> pairs) {

		UTF8PostMethod post = new UTF8PostMethod(url);
		post.setHttp11(true);
		post.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setRequestHeader("Content-Type", "application/json; charset=utf-8");
		post.setRequestHeader("Accept-Charset", "UTF-8,*");
		post.setRequestHeader("Pragma", "no-cache");
		post.setRequestHeader("Cache-Control", "no-cache");
		post.setRequestHeader("Comp-Control", "dsmp/sms-mt");

		try {

			if (null != pairs) {
				for (String k : pairs.keySet()) {
					String v = pairs.get(k);
					post.setRequestHeader(k, v);
				}
			}

			post.setRequestBody(json);

			int statusCode = client.executeMethod(post);/* 鎵ц鎻愪氦 */
			if (statusCode == HttpStatus.SC_OK) {/* 鎴愬姛鑾峰彇杩斿洖鍐呭 */
				return post.getResponseBodyAsString();
			} else {
				logger.info("HttpClient response error, code=[" + statusCode + "]");
			}
		} catch (Exception ex) {
			logger.error("http client error with url:" + url + ", json:" + json);
		} finally {/* 閲婃斁杩炴帴 */
			if (null != post) {
				post.releaseConnection();
			}
		}
		return null;
	}

	/**
	 * 涓撻棬鐢ㄤ簬鍚慤MC(鐢ㄦ埛绠＄悊涓績)鍙戦�佹姤鏂囩殑鏂规硶锛屽鍔犻噸璇曟満鍒�
	 *
	 * @param url
	 *            - POST璇锋眰URL
	 * @param xml
	 *            - xml瀛楃涓�
	 * @return - 鍝嶅簲Body鍐呭
	 */
	public String postUmc(String url, String xml) {
		// 榛樿璁剧疆3娆�
		int count = Integer.getInteger("post.umc.retry.times", 3) + 1;
		for (int i = 1; i < count; i++) {
			UTF8PostMethod post = new UTF8PostMethod(url);
			post.getParams().setVersion(HttpVersion.HTTP_1_1);
			post.setRequestHeader("Host", String.valueOf(xml.length()));
			post.setRequestHeader("Content-Type", "text/xml");
			post.setRequestHeader("Content-Length", String.valueOf(xml.length()));
			post.setRequestHeader("Cache-Control", "no-cache, no-store");
			post.setRequestHeader("Pragma", "no-cache");
			post.setRequestHeader("SOAPAction", "http://www.oasis-open.org/committees/security");

			try {
				StringRequestEntity requestEntity = new StringRequestEntity(xml, "text/xml", "UTF-8");
				post.setRequestEntity(requestEntity);

				int statusCode = client.executeMethod(post);/* 鎵ц鎻愪氦 */
				if (statusCode == HttpStatus.SC_OK) {/* 鎴愬姛鑾峰彇杩斿洖鍐呭 */
					return post.getResponseBodyAsString();
				}
			} catch (SocketException e) {
				logger.error("postUmc retry url=" + url + ", count=" + i);
				if (i == count - 1) {
					logger.error("postUmc fail  url=" + url + ", count=" + i);
				}
				continue;
			} catch (Exception ex) {
				logger.error("http client error with url:" + url + ", xml:" + xml);
			} finally {/* 閲婃斁杩炴帴 */
				if (null != post) {
					post.releaseConnection();
				}
			}
		}
		return null;
	}

	/**
	 * 鑾峰彇apache httpclient瀵硅薄 澶囨敞: 鑷畾涔夋帶鍒秇ttp璇锋眰
	 *
	 * @return - httpclient瀵硅薄
	 */
	public HttpClient getHttpClient() {
		return this.client;
	}

	/**
	 * Inner class [PostMethod] for UTF-8 support
	 *
	 * @author www.aspirehld.com
	 * @creator yuyoo(zhandulin@aspirehld.com)
	 * @date 2011-6-15 涓嬪崍04:34:03
	 */
	private static class UTF8PostMethod extends PostMethod {

		public UTF8PostMethod(String url) {
			super(url);
		}

		@Override
		public String getRequestCharSet() {
			return "UTF-8";
		}
	}


	/**
	 * 向指定 URL 发送POST方法的请求
	 *
	 * @param url
	 * 发送请求的 URL
	 * @param param
	 * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public  String sendPost(String url, String param) {
		String result = null;
		CloseableHttpClient client = HttpClients.createDefault();
		URIBuilder builder = new URIBuilder();

		try {

			HttpPost post = new HttpPost(url);
			// 设置请求头
			post.setHeader("Content-Type", "application/json"); // 设置请求体
			post.setEntity(new StringEntity(param.toString(), Charset.forName("UTF-8")));
			// 获取返回信息
			HttpResponse response = client.execute(post);
			result = response.toString();
		} catch (Exception e) {
			logger.info("接口请求失败" + e.getStackTrace());
		}
		return result;
	}
}
