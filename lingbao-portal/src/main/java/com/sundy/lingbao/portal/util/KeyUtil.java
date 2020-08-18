package com.sundy.lingbao.portal.util;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.http.HttpStatus;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.sundy.lingbao.common.exception.LingbaoException;
import com.sundy.lingbao.portal.entity.bussiness.AppEntity;
import com.sundy.lingbao.portal.entity.bussiness.ClusterEntity;

public class KeyUtil {

	public static String getUUIDKey() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String getAppClusterKey(String appId, String clusterId) {
		if (Objects.isNull(appId)) {
			throw new LingbaoException(HttpStatus.SERVICE_UNAVAILABLE.value(), "appId is null");
		}
		return Joiner.on("+").join(appId, clusterId==null?"":clusterId);
	}
	
	public static String getKey(String first, String second, Object... objects) {
		return Joiner.on("+").join(first, second, objects)+"+";
	}
	
	public static String getKey(String frist, String second) {
		return Joiner.on("+").join(frist, second)+"+";
	}
	
	public static String getKey(String frist) {
		return frist+"+";
	}
	
	public static void main(String[] args) {
		String key = getKey("1");
		System.out.println(key);
		key = getKey("1", "2");
		System.out.println(key);
		key = getKey("1", "2", "3");
		System.out.println(key);
	}
	
	public static String getFileKey(Map<Integer, String> contents) {
		if (Objects.nonNull(contents)) {
			return MD5Util.encoderByHexMd5(JSON.toJSONString(contents));
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

	public static String getClusterKey(ClusterEntity clusterEntity) {
		if (Objects.nonNull(clusterEntity)) {
			return MD5Util.encoderByHexMd5(JSON.toJSONString(clusterEntity));
		} else {
			return getUUIDKey();
		}
	}
	
}
