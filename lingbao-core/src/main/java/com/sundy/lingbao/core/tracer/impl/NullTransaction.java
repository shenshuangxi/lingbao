package com.sundy.lingbao.core.tracer.impl;

import com.sundy.lingbao.core.tracer.spi.Transaction;

public class NullTransaction implements Transaction {

	@Override
	public void setStatus(String status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatus(Throwable e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addData(String key, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void complete() {
		// TODO Auto-generated method stub

	}

}
