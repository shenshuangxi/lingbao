package com.sundy.lingbao.common.controller;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sundy.lingbao.common.exception.AbstractLingbaoHttpException;
import com.sundy.lingbao.core.tracer.Tracer;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);
	
	private Gson gson = new Gson();
	@SuppressWarnings("serial")
	private static Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
	
	@ExceptionHandler(Throwable.class)
	public ResponseEntity<Map<String, Object>> exception(HttpServletRequest request, Throwable ex) {
		return handleError(request, HttpStatus.INTERNAL_SERVER_ERROR, ex);
	}
	
	@ExceptionHandler({HttpRequestMethodNotSupportedException.class, HttpMediaTypeException.class})
	public ResponseEntity<Map<String, Object>> restTemplateException(HttpServletRequest request, ServletException ex) {
		return handleError(request, HttpStatus.BAD_REQUEST, ex);
	}
	
	@ExceptionHandler(HttpStatusCodeException.class)
	public ResponseEntity<Map<String, Object>> restTemplateException(HttpServletRequest request, HttpStatusCodeException ex) {
	    return handleError(request, ex.getStatusCode(), ex);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, Object>> accessDeny(HttpServletRequest request, AccessDeniedException ex) {
	    return handleError(request, HttpStatus.FORBIDDEN, ex);
	}
	
	@ExceptionHandler({AbstractLingbaoHttpException.class})
	public ResponseEntity<Map<String, Object>> badRequest(HttpServletRequest request, AbstractLingbaoHttpException ex) {
	    return handleError(request, HttpStatus.valueOf(ex.getStatusCode()), ex);
	}
	
	private ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request, HttpStatus status, Throwable ex) {
		String message = ex.getMessage();
		logger.error(message, ex);
		Tracer.logError(ex);
		Map<String, Object> errorAttributes = new HashMap<>();
		boolean errorHandled = false;
		if (ex instanceof HttpStatusCodeException) {
			try {
				//try to extract the original error info if it is thrown from apollo programs, e.g. admin service
				errorAttributes = gson.fromJson(((HttpStatusCodeException) ex).getResponseBodyAsString(), mapType);
				status = ((HttpStatusCodeException) ex).getStatusCode();
				errorHandled = true;
			} catch (Throwable th) {
				//ignore
			}
		}
		if (!errorHandled) {
			errorAttributes.put("status", status.value());
			errorAttributes.put("message", message);
			errorAttributes.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			errorAttributes.put("exception", ex.getClass().getName());
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<>(errorAttributes, headers, status);
	}
	
}
