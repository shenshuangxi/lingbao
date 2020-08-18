package com.sundy.lingbao.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sundy.lingbao.core.exception.LingbaoException;

public class HttpUtil {

	private final static Logger loggger = LoggerFactory.getLogger(HttpUtil.class);
	
	public static String streamToString(InputStream stream) throws IOException{
		InputStreamReader isr = null;
		try {
			StringBuilder sb = new StringBuilder();
			isr = new InputStreamReader(stream, StandardCharsets.UTF_8);
			CharBuffer buffer = CharBuffer.allocate(1024);
			while(isr.read(buffer)!=-1) {
				buffer.flip();
				sb.append(buffer);
				buffer.clear();
			}
			return sb.toString();
		} finally {
			if (Objects.nonNull(isr)) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static <T> HttpResponse<T> doGet(HttpRequest httpRequest, Class<T> responseType) {
		Function<String, T> convert = new Function<String, T>() {
			@Override
			public T apply(String t) {
				return JSON.parseObject(t, responseType);
			}
		};
		return doGet(httpRequest, convert);
	}
	
	public static <T> HttpResponse<T> doGet(HttpRequest httpRequest, TypeReference<T> reference) {
		Function<String, T> convert = new Function<String, T>() {
			@Override
			public T apply(String t) {
				return JSON.parseObject(t, reference);
			}
		};
		return doGet(httpRequest, convert);
	}
	
	
	private static <T> HttpResponse<T> doGet(HttpRequest httpRequest, Function<String, T> function) {
		HttpURLConnection httpURLConnection = null;
		Integer statusCode = 0;
		try {
			httpURLConnection = (HttpURLConnection) new URL(httpRequest.getUrl()).openConnection();
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setConnectTimeout(new Long(httpRequest.getConnectTimeout()).intValue());
			httpURLConnection.setReadTimeout(new Long(httpRequest.getReadTimeout()).intValue());
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(false);
			httpURLConnection.connect();
			statusCode = httpURLConnection.getResponseCode();
			String response = null;
			try {
				response = streamToString(httpURLConnection.getInputStream());
			} catch (IOException e) {
				try {
					loggger.error(streamToString(httpURLConnection.getErrorStream()), e);
				} catch (IOException e1) {}
				throw e;
			}
			if (statusCode==200) {
				return new HttpResponse<T>(statusCode, function.apply(response));
			} else if (statusCode==304) {
				return new HttpResponse<T>(statusCode, null);
			}
		} catch (IOException e) {
			loggger.error(e.getMessage(), e);
			throw new LingbaoException(statusCode, "Could not complete get operation");
		}
		throw new LingbaoException(statusCode, String.format("Get operation failed for %s", httpRequest.getUrl()));
	}
	
}
