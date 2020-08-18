package com.sundy.lingbao.core.idgenerate;

import com.sundy.lingbao.core.util.LingbaoIdWorker;

public class MachineIdGenerator implements IdGenerator {

	private final LingbaoIdWorker idWorker;
	
	public MachineIdGenerator(String workerId, String datacenterId) {
		idWorker = new LingbaoIdWorker(workerId, datacenterId);
	}
	
	@Override
	public String getId() {
		return idWorker.nextId();
	}
	
}
