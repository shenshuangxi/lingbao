package com.sundy.lingbao.portal.controller.base;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sundy.lingbao.common.exception.LingbaoException;
import com.sundy.lingbao.core.util.StringUtils;
import com.sundy.lingbao.portal.auth.JwtTokenUtil;
import com.sundy.lingbao.portal.dto.UserDto;
import com.sundy.lingbao.portal.dto.UserToken;
import com.sundy.lingbao.portal.entity.base.AccountEntity;
import com.sundy.lingbao.portal.entity.base.UserEntity;
import com.sundy.lingbao.portal.repository.base.AccountRepository;
import com.sundy.lingbao.portal.repository.base.UserRepository;
import com.sundy.lingbao.portal.util.MD5Util;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@PostMapping("/login")
	public ResponseEntity<UserToken> login(String account, String password) {
		AccountEntity accountEntity = accountRepository.findByAccountAndPassword(account, MD5Util.encoderByHexMd5(password));
		if (Objects.isNull(accountEntity)) {
			throw new LingbaoException(HttpStatus.UNAUTHORIZED.value(), "Wrong account or password.");
		}
		String token = jwtTokenUtil.generateToken(accountEntity);
		Optional<UserEntity> optional = userRepository.findById(accountEntity.getUserId());
		if (optional.isPresent()) {
			UserEntity userEntity = optional.get();
			UserToken userToken = new UserToken();
			userToken.setEmail(userEntity.getEmail());
			userToken.setName(userEntity.getName());
			userToken.setNickName(accountEntity.getNickName());
			userToken.setPhone(userEntity.getPhone());
			userToken.setToken(token);
			return ResponseEntity.ok(userToken);
		}
		throw new LingbaoException(HttpStatus.UNAUTHORIZED.value(), "no user info found");
	}
	
	@Transactional
	@PutMapping("/register")
	public ResponseEntity<Object> register(@RequestBody UserDto userDto) {
		
		if (Objects.isNull(userDto.getEmail())) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "email can not be emtpy");
		}
		
		if (!userDto.getEmail().matches("^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format(" %s error email format", userDto.getEmail()));
		}
		
		if (StringUtils.isNullOrEmpty(userDto.getNickName())) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "nick name can not be emtpy");
		}
		
		if (StringUtils.isNullOrEmpty(userDto.getPassword())) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "password can not be emtpy");
		}
		
		AccountEntity accountEntity = accountRepository.findByAccount(userDto.getEmail());
		if (Objects.nonNull(accountEntity)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "email is exists");
		}
		
		UserEntity userEntity = new UserEntity();
		userEntity.setEmail(userDto.getEmail());
		userEntity.setName(userDto.getName());
		userEntity.setPhone(userDto.getPhone());
		userRepository.save(userEntity);
		
		accountEntity = new AccountEntity();
		accountEntity.setAccount(userDto.getEmail());
		accountEntity.setNickName(userDto.getNickName());
		accountEntity.setPassword(MD5Util.encoderByHexMd5(userDto.getPassword()));
		accountEntity.setUserId(userEntity.getId());
//		accountEntity.setRoles(RoleType.Admin.getName());
		
		accountRepository.save(accountEntity);
		
		return ResponseEntity.ok().build();
	}
	
}
