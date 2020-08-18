package com.sundy.lingbao.portal.util;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class HttpClient {

	public static final MediaType JSON = MediaType.APPLICATION_JSON_UTF8;
	
	private static UriTemplateHandler uriTemplateHandler = new DefaultUriBuilderFactory();
	
	private static RestTemplate restTemplate;
	
	private static final String host = "http://localhost:8087";
	
	static {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
		fastJsonHttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
		restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(fastJsonHttpMessageConverter);
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		requestFactory.setConnectionRequestTimeout(600000);
		requestFactory.setConnectTimeout(600000);
		requestFactory.setBufferRequestBody(false);
		requestFactory.setReadTimeout(600000);
		restTemplate.setRequestFactory(requestFactory);
	}
	
	

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T, R> T get(String url, R request, Map<String, String> headerParam, Class<T> responseType, Object... urlVariables) {
        return exchange(url, HttpMethod.GET, request, headerParam, responseType, urlVariables);
    }

    public static <T, R> T put(String url, R request, Map<String, String> headerParam, Class<T> responseType, Object... urlVariables) {
        return exchange(url, HttpMethod.PUT, request, headerParam, responseType, urlVariables);
    }

    public static <T, R> T post(String url, R request, Map<String, String> headerParam, Class<T> responseType, Object... urlVariables) {
        return exchange(url, HttpMethod.POST, request, headerParam, responseType, urlVariables);
    }

    public static <T, R> T patch(String url, R request, Map<String, String> headerParam, Class<T> responseType, Object... urlVariables) {
        return exchange(url, HttpMethod.PATCH, request, headerParam, responseType, urlVariables);
    }

    public static <T, R> T delete(String path, R request, Map<String, String> headerParam, Class<T> responseType, Object... urlVariables) {
        return exchange(path, HttpMethod.DELETE, request, headerParam, responseType, urlVariables); 
    }

    @SuppressWarnings("unchecked")
	public static <T, R> T exchange(String path, HttpMethod method, R request, Map<String, String> headerParam, Class<T> responseType, Object... urlVariables) {
    	if (path.startsWith("/")) {
	    	path = path.substring(1, path.length());
	    }
    	
    	if (Objects.nonNull(urlVariables)) {
    		path = uriTemplateHandler.expand(path, urlVariables).getPath();
    	}
    	
    	HttpEntity<R> httpEntity = null;
	    if (Objects.nonNull(headerParam)) {
	    	HttpHeaders httpHeaders = new HttpHeaders();
	    	for (Entry<String, String> entry : headerParam.entrySet()) {
	    		httpHeaders.add(entry.getKey(), entry.getValue());
			}
	    	if (Objects.nonNull(request)) {
	    		httpEntity = new HttpEntity<R>(request, httpHeaders);
	    	} else {
	    		httpEntity = new HttpEntity<R>(httpHeaders);
	    	}
	    	
	    } else if (Objects.nonNull(request)) {
	    	httpEntity = new HttpEntity<R>(request);
	    }
    	
    	try {
    		if (Objects.isNull(responseType)) {
    			ResponseEntity<Object> responseEntity = restTemplate.exchange(host+path, method, httpEntity, Object.class);
    			return (T) responseEntity.getBody();
    		}  else {
    			ResponseEntity<T> result = restTemplate.exchange(host+path, method, httpEntity, responseType);
    			return result.getBody();
    		}
	    } catch (Throwable e) {
	    	e.printStackTrace();
	    }
    	return null;
    }

}
