package com.sundy.lingbao.core.idgenerate;

import com.sundy.lingbao.core.foundation.Foundation;
import com.sundy.lingbao.core.util.ServiceBootstrap;

public class IdGenerateFactory {

	private static IdGenerator idGenerator;
	
	static {
		try {
			if (idGenerator==null) {
				idGenerator = ServiceBootstrap.loadFirst(IdGenerator.class);
			}
		} catch (Throwable e) {
		}
		if (idGenerator==null) {
			idGenerator = new MachineIdGenerator(Foundation.app().getWorkerId(), Foundation.app().getDataCenterId());
		}
	}
	
	public static IdGenerator getIdGenerator() {
		return idGenerator;
	}
	
}
