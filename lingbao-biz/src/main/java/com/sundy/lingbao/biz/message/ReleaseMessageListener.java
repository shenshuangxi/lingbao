package com.sundy.lingbao.biz.message;

import com.sundy.lingbao.biz.entity.ReleaseMessageEntity;

public interface ReleaseMessageListener {

	void handlerMessage(ReleaseMessageEntity releaseMessageEntity, String channel);

}
