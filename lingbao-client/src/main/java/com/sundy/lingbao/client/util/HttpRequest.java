package com.sundy.lingbao.client.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HttpRequest {

	private String url;
	
	private long connectTimeout;
	
	private long readTimeout;
	
}
