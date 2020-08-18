package com.sundy.lingbao.core.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient {

	private Logger logger = LoggerFactory.getLogger(HttpClient.class);

	private final PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
	private final RequestConfig requestConfig;

	public HttpClient(int maxTimeout, int maxTotal, int maxPerRoute) {
		// 设置连接池大小
		this.connMgr.setMaxTotal(maxTotal);
		this.connMgr.setDefaultMaxPerRoute(maxPerRoute);

		RequestConfig.Builder configBuilder = RequestConfig.custom();
		// 设置连接超时
		configBuilder.setConnectTimeout(maxTimeout);
		// 设置读取超时
		configBuilder.setSocketTimeout(maxTimeout);
		// 设置从连接池获取连接实例的超时
		configBuilder.setConnectionRequestTimeout(maxTimeout);
		// 在提交请求之前 测试连接是否可用
		// configBuilder.setStaleConnectionCheckEnabled(true);
		this.requestConfig = configBuilder.build();
	}
	
	public HttpClient() {
		// 设置连接池大小
		this.connMgr.setMaxTotal(100);
		this.connMgr.setDefaultMaxPerRoute(100);

		RequestConfig.Builder configBuilder = RequestConfig.custom();
		// 设置连接超时
		configBuilder.setConnectTimeout(60*1000);
		// 设置读取超时
		configBuilder.setSocketTimeout(60*1000);
		// 设置从连接池获取连接实例的超时
		configBuilder.setConnectionRequestTimeout(60*1000);
		// 在提交请求之前 测试连接是否可用
		// configBuilder.setStaleConnectionCheckEnabled(true);
		this.requestConfig = configBuilder.build();
	}

	public static String map2Str(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue().toString() + "&");
		}
		if (params.size() > 0) {
			sb.delete(sb.length() - 1, sb.length());
		}
		return sb.toString();
	}

	// get
	public String doGet(String path, Map<String, String> headers, Map<String, Object> params) {
		try {
			long start = System.currentTimeMillis();
			if (params != null && !params.isEmpty()) {
				path = path + "?" + map2Str(params);
			}
			HttpClientBuilder httpBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(connMgr);
			CloseableHttpClient httpClient = httpBuilder.build();// 创建默认的httpClient实例
			HttpGet httpGet = new HttpGet(path);
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpGet.setHeader(entry.getKey(), entry.getValue());
				}
			}
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity!=null) {
				String reseContent = EntityUtils.toString(httpEntity);
				EntityUtils.consume(httpEntity);
				logger.error("HTTP应答状态:" + httpResponse.getStatusLine() + " 耗时: " + (System.currentTimeMillis() - start));
				logger.error("返回消息: " + reseContent);
				return reseContent;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// post
	public String doFormPost(String path, Map<String, String> headers, Map<String, Object> formData) {
		try {
			long start = System.currentTimeMillis();
			HttpClientBuilder httpBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(connMgr);
			CloseableHttpClient httpClient = httpBuilder.build();// 创建默认的httpClient实例
			HttpPost httpPost = new HttpPost(path);
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpPost.setHeader(entry.getKey(), entry.getValue());
				}
			}
			if (formData!=null && !formData.isEmpty()) {
				List<NameValuePair> parameters = new ArrayList<NameValuePair>();
				for (Entry<String, Object> entry : formData.entrySet()) {
					parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			}
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			String reseContent = EntityUtils.toString(httpResponse.getEntity());
			EntityUtils.consume(httpResponse.getEntity());
			logger.error("HTTP应答状态:" + httpResponse.getStatusLine() + " 耗时: " + (System.currentTimeMillis() - start));
			logger.error("返回消息: " + reseContent);
			return reseContent;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String doStringPost(String path, Map<String, String> headers, Map<String, Object> params) {
		try {
			long start = System.currentTimeMillis();
			HttpClientBuilder httpBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(connMgr);
			CloseableHttpClient httpClient = httpBuilder.build();// 创建默认的httpClient实例
			HttpPost httpPost = new HttpPost(path);
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpPost.setHeader(entry.getKey(), entry.getValue());
				}
			}
			if (params!=null && !params.isEmpty()) {
				httpPost.setEntity(new StringEntity(map2Str(params), StandardCharsets.UTF_8));
			}
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			String reseContent = EntityUtils.toString(httpResponse.getEntity());
			EntityUtils.consume(httpResponse.getEntity());
			logger.error("HTTP应答状态:" + httpResponse.getStatusLine() + " 耗时: " + (System.currentTimeMillis() - start));
			logger.error("返回消息: " + reseContent);
			return reseContent;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// patch
	public String patch(String path, Map<String, String> headers, Map<String, Object> params) {
		try {
			long start = System.currentTimeMillis();
			HttpClientBuilder httpBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(connMgr);
			CloseableHttpClient httpClient = httpBuilder.build();// 创建默认的httpClient实例
			HttpPatch httpPatch = new HttpPatch(path);
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpPatch.setHeader(entry.getKey(), entry.getValue());
				}
			}
			if (params!=null && !params.isEmpty()) {
				httpPatch.setEntity(new StringEntity(map2Str(params), StandardCharsets.UTF_8));
			}
			CloseableHttpResponse httpResponse = httpClient.execute(httpPatch);
			String reseContent = EntityUtils.toString(httpResponse.getEntity());
			EntityUtils.consume(httpResponse.getEntity());
			logger.error("HTTP应答状态:" + httpResponse.getStatusLine() + " 耗时: " + (System.currentTimeMillis() - start));
			logger.error("返回消息: " + reseContent);
			return reseContent;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// put
	public String put(String path, Map<String, String> headers, Map<String, Object> params) {
		try {
			long start = System.currentTimeMillis();
			HttpClientBuilder httpBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(connMgr);
			CloseableHttpClient httpClient = httpBuilder.build();// 创建默认的httpClient实例
			HttpPut httpPut = new HttpPut(path);
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpPut.setHeader(entry.getKey(), entry.getValue());
				}
			}
			if (params!=null && !params.isEmpty()) {
				httpPut.setEntity(new StringEntity(map2Str(params), StandardCharsets.UTF_8));
			}
			CloseableHttpResponse httpResponse = httpClient.execute(httpPut);
			String reseContent = EntityUtils.toString(httpResponse.getEntity());
			EntityUtils.consume(httpResponse.getEntity());
			logger.error("HTTP应答状态:" + httpResponse.getStatusLine() + " 耗时: " + (System.currentTimeMillis() - start));
			logger.error("返回消息: " + reseContent);
			return reseContent;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// delete
	public byte[] delete(String path, Map<String, String> headers, Map<String, Object> params) {
		try {
			long start = System.currentTimeMillis();
			if (params != null && !params.isEmpty()) {
				path = path + "?" + map2Str(params);
			}
			HttpClientBuilder httpBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(connMgr);
			CloseableHttpClient httpClient = httpBuilder.build();// 创建默认的httpClient实例
			HttpDelete httpDelete = new HttpDelete(path);
			if (headers != null && !headers.isEmpty()) {
				for (Entry<String, String> entry : headers.entrySet()) {
					httpDelete.setHeader(entry.getKey(), entry.getValue());
				}
			}
			CloseableHttpResponse httpResponse = httpClient.execute(httpDelete);
			byte[] reseContent = EntityUtils.toByteArray(httpResponse.getEntity());
			EntityUtils.consume(httpResponse.getEntity());
			logger.error("HTTP应答状态:" + httpResponse.getStatusLine() + " 耗时: " + (System.currentTimeMillis() - start));
			logger.error("返回消息: " + reseContent);
			return reseContent;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
