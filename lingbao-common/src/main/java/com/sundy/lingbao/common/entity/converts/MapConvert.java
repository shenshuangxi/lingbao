package com.sundy.lingbao.common.entity.converts;

import java.util.Map;
import java.util.Objects;

import javax.persistence.AttributeConverter;

import com.google.gson.Gson;
import com.sundy.lingbao.core.consts.GlobalGsonTypes;
import com.sundy.lingbao.core.util.StringUtils;

public class MapConvert implements AttributeConverter<Map<String, Object>, String> {

	private Gson  gson = new Gson();
	
	@Override
	public String convertToDatabaseColumn(Map<String, Object> attribute) {
		if (Objects.nonNull(attribute)) {
			return gson.toJson(attribute, GlobalGsonTypes.mapType);
		}
		return null;
	}

	@Override
	public Map<String, Object> convertToEntityAttribute(String dbData) {
		if (StringUtils.isNullOrEmpty(dbData)) {
			return null;
		}
		return gson.fromJson(dbData, GlobalGsonTypes.mapType);
	}
}
