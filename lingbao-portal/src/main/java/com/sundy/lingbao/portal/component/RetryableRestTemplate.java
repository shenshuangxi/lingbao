package com.sundy.lingbao.portal.component;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
	private AdminServiceAddressLocator adminServiceAddressLocator;
	
	public <T> T healthGet(String envId, String path,  Class<T> responseType, Object... urlVariables) throws RestClientException {
	    return execute(envId, HttpMethod.GET, path, null, responseType, urlVariables);
	}
	
	public <T> T get(String envId, String path, Object request, Map<String, String> header, Class<T> responseType, Object... urlVariables) throws RestClientException {
	    return exchange(envId, HttpMethod.GET, path, request, header, responseType, urlVariables);
	}

	public <T> T post(String envId, String path, Object request, Map<String, String> header, Class<T> responseType, Object... uriVariables) throws RestClientException {
	    return exchange(envId, HttpMethod.POST, path, request, header, responseType, uriVariables);
	}
	
	public <T> T patch(String envId, String path, Object request, Map<String, String> header, Class<T> responseType, Object... urlVariables) throws RestClientException {
	    return exchange(envId, HttpMethod.PATCH, path, request, header, responseType, urlVariables);
	}

	public <T> T put(String envId, String path, Object request, Map<String, String> header, Class<T> responseType, Object... urlVariables) throws RestClientException {
		return exchange(envId, HttpMethod.PUT, path, request, header, responseType, urlVariables);
	}
	
	public <T> T delete(String envId, String path, Object request, Map<String, String> header, Class<T> responseType, Object... urlVariables) throws RestClientException {
		return exchange(envId, HttpMethod.DELETE, path, null, header, null, urlVariables);
	}
	
	@SuppressWarnings("unchecked")
	public <T, R> T exchange(String envId, HttpMethod method, String path, R request, Map<String, String> headerParam, Class<T> responseType, Object... urlVariables) {
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
	    
	    List<ServiceDto> serviceDtos = getService(envId);
	    for (ServiceDto serviceDto : serviceDtos) {
	    	try {
	    		if (Objects.isNull(responseType)) {
	    			ResponseEntity<Object> responseEntity = restTemplate.exchange(parseHost(serviceDto)+path, method, httpEntity, Object.class);
	    			return (T) responseEntity.getBody();
	    		}  else {
	    			ResponseEntity<T> result = restTemplate.exchange(parseHost(serviceDto)+path, method, new HttpEntity<R>(request), responseType, urlVariables);
	    			return result.getBody();
	    		}
		    } catch (Throwable t) {
		        logger.error("Http request failed, uri: {}, method: {}", path, HttpMethod.GET, t);
		        Tracer.logError(t);
		        if (canRetry(t, method)) {
		        	Tracer.logEvent("retry request", path);
		        } else {// biz exception rethrow
		        	throw t;
		        }
		    }
		}
	    ServiceException e = new ServiceException(String.format("servers are unresponsive. server address: %s", path));
	    throw e;
	}

	public <T> ResponseEntity<T> exchangeGet(String envId, String path, ParameterizedTypeReference<T> reference, Object... uriVariables) {
	    if (path.startsWith("/")) {
	    	path = path.substring(1, path.length());
	    }
	    String uri = uriTemplateHandler.expand(path, uriVariables).getPath();
	    
	    List<ServiceDto> serviceDtos = getService(envId);
	    for (ServiceDto serviceDto : serviceDtos) {
	    	try {
		        ResponseEntity<T> result = restTemplate.exchange(parseHost(serviceDto)+path, HttpMethod.GET, null, reference, uriVariables);
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
		}
	    ServiceException e = new ServiceException(String.format("servers are unresponsive. server address: %s", path));
	    throw e;
	}
	
	public <T> T execute(String envId, HttpMethod method, String path, Object request, Class<T> responseType, Object... uriVariables) {
	    if (path.startsWith("/")) {
	    	path = path.substring(1, path.length());
	    }
	    String uri = uriTemplateHandler.expand(path, uriVariables).getPath();
	    
	    List<ServiceDto> serviceDtos = getService(envId);
	    for (ServiceDto serviceDto : serviceDtos) {
	    	try {
		        T result = doExecute(method, serviceDto, path, request, responseType, uriVariables);
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
		}
	    ServiceException e = new ServiceException(String.format("servers are unresponsive. server address: %s", path));
	    throw e;
	}
	
	public <T> T doExecute(HttpMethod method, ServiceDto serviceDto, String path, Object request, Class<T> responseType, Object... uriVariables) {
	    T result = null;
	    switch (method) {
	      	case GET:
	      		result = restTemplate.getForObject(parseHost(serviceDto)+path, responseType, uriVariables);
	      		break;
	      	case POST:
	      		result = restTemplate.postForEntity(parseHost(serviceDto)+path, request, responseType, uriVariables).getBody();
	      		break;
	      	case PATCH:
	      		result = restTemplate.patchForObject(parseHost(serviceDto)+path, request, responseType, uriVariables);
	      		break;
	      	case PUT:
	      		restTemplate.put(parseHost(serviceDto)+path, request, uriVariables);
	      		break;
	      	case DELETE:
	      		restTemplate.delete(parseHost(serviceDto)+path, uriVariables);
	      		break;
	      	default:
	      		throw new UnsupportedOperationException(String.format("unsupported http method(method=%s)", method));
	    }
	    return result;
	}
	
	private List<ServiceDto> getService(String envId){
		return adminServiceAddressLocator.getAdminServiceList(envId);
	}
	
	private String parseHost(ServiceDto serviceDto) {
	    return serviceDto.getHomepageUrl() + "/";
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
