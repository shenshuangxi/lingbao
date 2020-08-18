package com.sundy.db.entity;

import java.io.Serializable;
import java.util.Date;

public interface PersistEntity extends Serializable {

	String getId();
	
	void setId(String id);
	
	void setCreateTime(Date date);
	
	Date getCreateTime();
	
	void setUpdateTime(Date date);
	
	Date getUpdateTime();
	
	void setCreateBy(String opearator);
	
	String getCreateBy();
	
	void setUpdateBy(String opearator);
	
	String getUpdateBy();
	
	void setStatus(Integer status);
	
	Integer getStatus();
	
	String getRemark();
	
	void setRemark(String remark);
	
}
