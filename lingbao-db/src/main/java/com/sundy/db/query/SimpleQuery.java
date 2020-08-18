package com.sundy.db.query;

import java.util.List;
import java.util.Map;

public interface SimpleQuery<T> {

	SimpleQuery<T> criteraMap(Map<String, Object> queryObject);
	
	SimpleQuery<T> critera(String name, Object value);
	
	SimpleQuery<T> query(String queryStatement);

	SimpleQuery<T> withConverter(Object converter);
	
	Long executeCount();
	
	List<T> executeList();
	
	T executeOne();
	
	
}
