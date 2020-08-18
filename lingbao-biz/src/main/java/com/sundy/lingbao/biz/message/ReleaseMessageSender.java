package com.sundy.lingbao.biz.message;

import com.sundy.lingbao.biz.entity.ReleaseMessageEntity;

public interface ReleaseMessageSender {

	void sendMessage(ReleaseMessageEntity releaseMessageEntity, String channel);
	
}
