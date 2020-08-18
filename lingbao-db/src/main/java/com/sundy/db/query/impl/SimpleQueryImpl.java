package com.sundy.db.query.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sundy.db.DbException;
import com.sundy.db.query.SimpleQuery;
import com.sundy.db.session.Session;


public class SimpleQueryImpl<T> implements SimpleQuery<T>{

	private Map<String, Object> parameterMap = new HashMap<String, Object>();
	private String queryStatement;
	private Object converter;
	
	private Session session; 
	
	public SimpleQueryImpl(Session session) {
		this.session = session;
	}

	@Override
	public SimpleQuery<T> criteraMap(Map<String, Object> queryObject) {
		this.parameterMap = queryObject;
		return this;
	}

	@Override
	public SimpleQuery<T> critera(String name, Object value) {
		this.parameterMap.put(name, value);
		return this;
	}

	@Override
	public SimpleQuery<T> query(String queryStatement) {
		this.queryStatement = queryStatement;
		return this;
	}

	@Override
	public SimpleQuery<T> withConverter(Object converter) {
		this.converter = converter;
		return this;
	}

	@Override
	public Long executeCount() {
		return (Long) session.selectOne(queryStatement+"Count", this);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<T> executeList() {
		List results = session.selectList(queryStatement, this);
		return convertResults(results);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T executeOne() {
		return (T) session.selectOne(queryStatement, this);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<T> convertResults(List result) {
		if (converter == null) {
			return result;
		}
		for (Object o : result) {
			if (o instanceof Map) {
				handleMap((Map<String, Object>) o);
			}
		}
		return result;
	}

	private void handleMap(Map<String, Object> o) {
		Map<String,Object> handleResult = new HashMap<String, Object>();
		Set<String> keySet = o.keySet();
		for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
			String k = it.next();
			String[] keys = k.split("_");
			if (keys.length == 2) {
				try {
					Method method = getConverterMethod(converter.getClass(),keys[1]);
					if (method.getParameterTypes().length != 1) {
						continue;
					}
					Object value = null;
					if (o.get(k) == null) {
						// do nothing
						value = null;
					}
					value = method.invoke(converter, o.get(k));
					it.remove();
					o.remove(k);
					handleResult.put(keys[0], value);
				} catch (NoSuchMethodException e) {
					continue;
				} catch (InvocationTargetException | IllegalAccessException e) {
					throw new DbException("convert property [key: " + k + ", value: " + o.get(k) + "] error with report service", e);
				}
			}
		}
		 o.putAll(handleResult);
	}
	
	private Method getConverterMethod(Class<?> clazz , String methodName) throws NoSuchMethodException {
		Method[] methods = clazz.getMethods();
		for(Method method : methods) {
			if(methodName.equals(method.getName())) {
				return method;
			}
		}
		throw new NoSuchMethodException();
	}
	
	
	public Map<String, Object> getQo() {
		return parameterMap;
	}

}
