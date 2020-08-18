package com.sundy.lingbao.common.util;

import com.sundy.lingbao.common.consts.Consts;

public class CommitKeyGenerateUtil {

	public static String generateCommitClusteKey(String appId, String clusterName) {
		return Consts.CLUSTER+"-"+UniqueKeyGenerator.generate(appId,clusterName );
	}
	
}
