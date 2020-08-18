package com.sundy.lingbao.core.tracer.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.sundy.lingbao.core.tracer.spi.Transaction;

public class LoggerTransaction implements Transaction {

	private final Logger logger = LoggerFactory.getLogger(LoggerTransaction.class);
	
	private String status;
	private Throwable throwable;
	private Map<String, Object> datas;
	
	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public void setStatus(Throwable e) {
		this.throwable = e;
	}

	@Override
	public void addData(String key, Object value) {
		if (Objects.isNull(datas)) {
			datas = new HashMap<String, Object>();
		}
		this.datas.put(key, value);
	}

	@Override
	public void complete() {
		ToStringHelper toStringHelper = MoreObjects.toStringHelper(this).omitNullValues()
		.add("status", status);
		if (Objects.nonNull(datas)) {
			for (Entry<String, Object> entry : datas.entrySet()) {
				toStringHelper.add(entry.getKey(), entry.getValue());
			}
		}
		if (Objects.nonNull(throwable)) {
			logger.error(toStringHelper.toString(), throwable);
		} else {
			logger.info(toStringHelper.toString());
		}
	}

}
