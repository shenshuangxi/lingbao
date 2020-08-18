package com.sundy.lingbao.client.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse<T> {

	private Integer statusCode;
	
	private T body;
	
}
