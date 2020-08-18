package com.sundy.lingbao.biz.util;

import java.util.Date;
import java.util.List;



import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.sundy.lingbao.biz.entity.AppEntity;
import com.sundy.lingbao.biz.entity.ClusterEntity;
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
	
	public static String getClusterKey(ClusterEntity clusterEntity) {
		if (Objects.nonNull(clusterEntity)) {
			return MD5Util.encoderByHexMd5(JSON.toJSONString(clusterEntity));
		} else {
			return getUUIDKey();
		}
	}
	
	public static String getAppKey(AppEntity appEntity) {
		if (Objects.nonNull(appEntity)) {
			return MD5Util.encoderByHexMd5(JSON.toJSONString(appEntity));
		} else {
			return getUUIDKey();
		}
	}
	
	public static void main(String[] args) {
		AppEntity appEntity = new AppEntity();
		appEntity.setAppId(KeyUtil.getUUIDKey());
		appEntity.setCreateBy("123");
		appEntity.setCreateDate(new Date());
		appEntity.setName("qwer");
		appEntity.setOrgName("sdfaw");
		appEntity.setOwnerEmail("aswe");
		appEntity.setOwnerName("asdcaw");
		appEntity.setUpdateBy("uj");
		appEntity.setUpdateDate(new Date());
		System.out.println(getAppKey(appEntity));
		System.out.println(getAppKey(appEntity));
	}
	
	
}
