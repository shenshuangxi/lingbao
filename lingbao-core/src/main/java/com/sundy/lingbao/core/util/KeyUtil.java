package com.sundy.lingbao.core.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.sundy.lingbao.core.consts.GloablConst;
import com.sundy.lingbao.core.util.MachineUtil;

public class KeyUtil {

	private static final Joiner STRING_JOINER = Joiner.on(GloablConst.SEPARATOR);
	
	public static String getUUIDKey() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String assembleKey(String appId, String clusterId) {
		List<String> keyParts = Lists.newArrayList(appId, clusterId);
		return STRING_JOINER.join(keyParts);
	}
	
	public static String commitKey(String appId, String clusterName) {
		List<String> keyParts = Lists.newArrayList(appId,clusterName, ""+MachineUtil.getMachineIdentifier(), UUID.randomUUID().toString().replace("-", "").toLowerCase());
		return STRING_JOINER.join(keyParts);
	}

	public static String getFileKey(Map<Integer, String> contents) {
		if (Objects.nonNull(contents)) {
			return MD5Util.encoderByHexMd5(JSON.toJSONString(contents));
		} else {
			return getUUIDKey();
		}
	}
	
	public static String getKey(String frist, String... objects) {
		if (Objects.nonNull(objects)) {
			return Joiner.on("+").join(frist, objects);
		}
		return Joiner.on("+").join(frist, "");
	}
	
	public static String getObjectKey(Object obj) {
		if (Objects.nonNull(obj)) {
			return MD5Util.encoderByHexMd5(JSON.toJSONString(obj));
		} else {
			return getUUIDKey();
		}
	}
	
	
	
}
