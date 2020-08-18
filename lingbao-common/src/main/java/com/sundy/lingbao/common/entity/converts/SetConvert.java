package com.sundy.lingbao.common.entity.converts;

import java.util.Objects;
import java.util.Set;

import javax.persistence.AttributeConverter;

import com.google.gson.Gson;
import com.sundy.lingbao.core.consts.GlobalGsonTypes;
import com.sundy.lingbao.core.util.StringUtils;

public class SetConvert implements AttributeConverter<Set<Object>, String> {

	private Gson  gson = new Gson();
	
	@Override
	public String convertToDatabaseColumn(Set<Object> attribute) {
		if (Objects.nonNull(attribute)) {
			return gson.toJson(attribute, GlobalGsonTypes.setType);
		}
		return null;
	}

	@Override
	public Set<Object> convertToEntityAttribute(String dbData) {
		if (StringUtils.isNullOrEmpty(dbData)) {
			return null;
		}
		return gson.fromJson(dbData, GlobalGsonTypes.setType);
	}

}
