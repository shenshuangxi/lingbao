package com.sundy.lingbao.portal.auth.token;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sundy.lingbao.common.exception.LingbaoException;
import com.sundy.lingbao.portal.component.config.PortalConfig;
import com.sundy.lingbao.portal.util.Base64Util;
import com.sundy.lingbao.portal.util.CrypteUtil;

@Component
public class TokenUtil {

	@Autowired
	private PortalConfig portalConfig;
	
	public Token deSign(String tokenStr) {
		try {
			String dencryptToken = CrypteUtil.base64Decrypt(tokenStr, portalConfig.getTokenKey("1234567890"));
			Token token =  (Token) Base64Util.decodeObject(dencryptToken);
			if(token==null){
				throw new Exception();
			}
			return token;
		} catch (Exception e) {
			throw new LingbaoException(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.name());
		}
	}
	
	
	public String sign(Token token) {
		try {
			String tokenStr = Base64Util.encodeObject(token);
			if(StringUtils.isEmpty(token)){
				throw new Exception();
			}
			String encryptToken = CrypteUtil.base64Encrypt(tokenStr, portalConfig.getTokenKey("1234567890"));
			return encryptToken;
		} catch (Exception e) {
			throw new LingbaoException(HttpStatus.SERVICE_UNAVAILABLE.value(), HttpStatus.SERVICE_UNAVAILABLE.name());
		}
	}
	
	/*public static void main(String[] args) throws Exception {
		TokenUtil tokenUtil = new TokenUtil();
		tokenUtil.portalConfig = new PortalConfig();
		UserToken userToken = new UserToken();
		String token = tokenUtil.sign(userToken);
		Long currentTime = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			Token ueToken = tokenUtil.deSign(token);
			System.out.println(ueToken);
		}
		System.out.println("耗时---"+(System.currentTimeMillis()-currentTime));
	}*/
	
}
