package com.sundy.lingbao.file.old.component;

import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

import com.sundy.lingbao.common.exception.ServiceException;
import com.sundy.lingbao.core.dto.ServiceDto;
import com.sundy.lingbao.core.tracer.Tracer;

@Component
public class RetryableRestTemplate {

	private Logger logger = LoggerFactory.getLogger(RetryableRestTemplate.class);

	private UriTemplateHandler uriTemplateHandler = new DefaultUriBuilderFactory();

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private FileServiceAddressLocator fileServiceAddressLocator;
	
	
	public <T> T get(String path, Class<T> responseType, Object... urlVariables) throws RestClientException {
	    return execute(HttpMethod.GET, path, null, responseType, urlVariables);
	}

	public <T> ResponseEntity<T> get(String path, ParameterizedTypeReference<T> reference, Object... uriVariables) throws RestClientException {
	    return exchangeGet(path, reference, uriVariables);
	}

	public <T> T post(String path, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
	    return execute(HttpMethod.POST, path, request, responseType, uriVariables);
	}

	public void put(String path, Object request, Object... urlVariables) throws RestClientException {
	    execute(HttpMethod.PUT, path, request, null, urlVariables);
	}

	public void delete(String path, Object... urlVariables) throws RestClientException {
	    execute(HttpMethod.DELETE, path, null, null, urlVariables);
	}

	private <T> T execute(HttpMethod method, String path, Object request, Class<T> responseType, Object... uriVariables) {
	    if (path.startsWith("/")) {
	    	path = path.substring(1, path.length());
	    }
	    String uri = uriTemplateHandler.expand(path, uriVariables).getPath();
      	try {
	        T result = doExecute(method, path, request, responseType, uriVariables);
	        return result;
      	} catch (Throwable t) {
	        logger.error("Http request failed, uri: {}, method: {}", uri, method, t);
	        Tracer.logError(t);
	        if (canRetry(t, method)) {
	        	Tracer.logEvent("retry request", uri);
	        } else {//biz exception rethrow
	        	throw t;
	        }
	    }
	    ServiceException e = new ServiceException(String.format("servers are unresponsive. server address: %s", path));
	    throw e;
	}

	private <T> ResponseEntity<T> exchangeGet(String path, ParameterizedTypeReference<T> reference, Object... uriVariables) {
	    if (path.startsWith("/")) {
	    	path = path.substring(1, path.length());
	    }
	    String uri = uriTemplateHandler.expand(path, uriVariables).getPath();
	    try {
	        ResponseEntity<T> result = restTemplate.exchange(getService(true).getHomepageUrl()+path, HttpMethod.GET, null, reference, uriVariables);
	        return result;
	    } catch (Throwable t) {
	        logger.error("Http request failed, uri: {}, method: {}", uri, HttpMethod.GET, t);
	        Tracer.logError(t);
	        if (canRetry(t, HttpMethod.GET)) {
	        	Tracer.logEvent("retry request", uri);
	        } else {// biz exception rethrow
	        	throw t;
	        }
	    }
	    ServiceException e = new ServiceException(String.format("servers are unresponsive. server address: %s", path));
	    throw e;
	}


	private <T> T doExecute(HttpMethod method, String path, Object request, Class<T> responseType, Object... uriVariables) {
	    T result = null;
	    switch (method) {
	      	case GET:
	      		result = restTemplate.getForObject(getService(true).getHomepageUrl()+path, responseType, uriVariables);
	      		break;
	      	case POST:
	      		result = restTemplate.postForEntity(getService(false).getHomepageUrl()+path, request, responseType, uriVariables).getBody();
	      		break;
	      	case PUT:
	      		restTemplate.put(getService(false).getHomepageUrl()+path, request, uriVariables);
	      		break;
	      	case DELETE:
	      		restTemplate.delete(getService(true).getHomepageUrl()+path, uriVariables);
	      		break;
	      	default:
	      		throw new UnsupportedOperationException(String.format("unsupported http method(method=%s)", method));
	    }
	    return result;
	}
	
	private ServiceDto getService(boolean isRead){
		if (isRead) {
			return fileServiceAddressLocator.getReadSortFileServiceList();
		} else {
			return fileServiceAddressLocator.getWriteSortFileServiceList();
		}
	}
	
	//post,delete,put请求在server处理超时情况下不重试
	private boolean canRetry(Throwable e, HttpMethod method) {
	    Throwable nestedException = e.getCause();
	    if (method == HttpMethod.GET) {
	    	return nestedException instanceof SocketTimeoutException || nestedException instanceof HttpHostConnectException || nestedException instanceof ConnectTimeoutException;
	    } else {
	    	return nestedException instanceof HttpHostConnectException || nestedException instanceof ConnectTimeoutException;
	    }
	}
	
	
	
}
