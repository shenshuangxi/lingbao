package com.sundy.lingbao.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceDto {

	private String appName;

	private String instanceId;

	private String homepageUrl;
	
	//0 表示不能写，可以读  效率低  值越小越好
	private Integer readWeight;
	
	private Integer writeWeight;

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ServiceDto{");
		sb.append("appName='").append(appName).append('\'');
		sb.append(", instanceId='").append(instanceId).append('\'');
		sb.append(", homepageUrl='").append(homepageUrl).append('\'');
		sb.append(", readWeight='").append(readWeight).append('\'');
		sb.append(", writeWeight='").append(writeWeight).append('\'');
		sb.append('}');
		return sb.toString();
	}

}
