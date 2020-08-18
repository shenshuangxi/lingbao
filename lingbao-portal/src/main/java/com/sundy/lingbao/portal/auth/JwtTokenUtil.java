package com.sundy.lingbao.portal.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sundy.lingbao.portal.component.config.PortalConfig;
import com.sundy.lingbao.portal.entity.base.AccountEntity;

@Component
public class JwtTokenUtil {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
	
	private static final String ACCOUNT = "account";
	
	private SecretKey jwtKey;
	
	@Autowired
	private PortalConfig portalConfig;
	
	@PostConstruct
	public void init() {
		byte[] encodedKey = Base64.decodeBase64(portalConfig.getJwtSecretKey());
        jwtKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
	}
	
	private Date generateExpirationDate() {
        long nowMillis = System.currentTimeMillis();
        long expMillis = nowMillis + portalConfig.getJwtTtlMillis();
        Date exp = new Date(expMillis);
        return exp;
    }
	
	private String generateTokenWithClaims(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, jwtKey)
                .compact();
    }
	
	private Claims getClaimsFromToken(String token) {
        try {
        	Claims claims = Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(token).getBody();
        	return claims;
        }catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }
	
	public AppUser getAppUserFromToken(String authToken) {
        long nowMillis = System.currentTimeMillis();
        Claims claims = getClaimsFromToken(authToken);
        if (claims != null) {
        	Object object = claims.get(ACCOUNT);
        	ObjectMapper objectMapper = new ObjectMapper();
        	AccountEntity accountEntity = objectMapper.convertValue(object, AccountEntity.class);
            AppUser appUser = new AppUser(accountEntity, claims.getExpiration().getTime() < nowMillis + portalConfig.getJwtRefreshMillis());
            return appUser;
        }
        return null;
    }
	
	
	public String generateToken(AppUser appUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ACCOUNT, appUser.getAccountEntity());
        return generateTokenWithClaims(claims);
    }

	public String generateToken(AccountEntity accountEntity) {
		Map<String, Object> claims = new HashMap<>();
        claims.put(ACCOUNT, accountEntity);
        return generateTokenWithClaims(claims);
	}
	
}
