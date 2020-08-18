package com.sundy.lingbao.portal.controller.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sundy.lingbao.common.dto.PageDto;
import com.sundy.lingbao.common.util.BeanUtils;
import com.sundy.lingbao.core.exception.LingbaoException;
import com.sundy.lingbao.core.util.StringUtils;
import com.sundy.lingbao.portal.dto.Search;
import com.sundy.lingbao.portal.dto.UserDto;
import com.sundy.lingbao.portal.entity.base.AccountEntity;
import com.sundy.lingbao.portal.entity.base.UserEntity;
import com.sundy.lingbao.portal.repository.base.AccountRepository;
import com.sundy.lingbao.portal.repository.base.UserRepository;

@RestController
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> find(String search) {
		search = search==null? "" : search.trim(); 
		List<Map<String, Object>> maps = userRepository.findUserInfo(search);
		return ResponseEntity.ok(maps);
	}
	
	@GetMapping("/appId/{appId}/envId/{envId}/permission-user")
	public ResponseEntity<PageDto<Map<String, Object>>> findPermissionUser(String search, @RequestParam String key, @RequestParam String keyType, @RequestParam Integer authType,
			@RequestParam Integer pageIndex,
			@RequestParam Integer rows,
			@RequestParam(required=false) String sortOrder,
			@RequestParam(required=false) String sort) {
		Direction direction = StringUtils.isNullOrEmpty(sortOrder) ? Direction.DESC : Direction.ASC.name().equalsIgnoreCase(sortOrder) ? Direction.ASC : Direction.DESC;
		String[] properties = StringUtils.isNullOrEmpty(sort) ? new String[] {"createDate"}: sort.split(",");
		search = search==null? "" : search.trim(); 
		Page<Map<String, Object>> page = userRepository.findPermissionUser(search, key, keyType, authType, PageRequest.of(pageIndex, rows, new Sort(direction, properties)));
		
		
		PageDto<Map<String, Object>> retPage = new PageDto<Map<String, Object>>();
		if (Objects.nonNull(page) && page.getContent().size()>0) {
			retPage.setRows(page.getContent());
			retPage.setTotal(page.getTotalElements());
		}
		return ResponseEntity.ok(retPage);
	}
	
	@PatchMapping("/{account}")
	@PreAuthorize("@permission.isAdmin()")
	public ResponseEntity<Object> updateRoels(@PathVariable String account, String roles) {
		if (StringUtils.isNullOrEmpty(account)) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "account can not empty");
		}
		if (account.equals(getAccount())) {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "can not modify self roles");
		}
		AccountEntity accountEntity = accountRepository.findByAccount(account);
		if (Objects.nonNull(accountEntity)) {
			accountEntity.setRoles(roles);
			accountRepository.save(accountEntity);
		} else {
			throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), String.format("account [%s] not found", account));
		}
		return ResponseEntity.ok("success");
	}
	
	
	@GetMapping("/all")
	public ResponseEntity<Page<UserDto>> findAll(Search search) {
		if (search.hasPage()) {
			Page<UserEntity> page = userRepository.findAll((root, query, criteriaBuilder)->{
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (Objects.nonNull(search.getUserName())) {
					predicates.add(criteriaBuilder.like(root.get("name"), "%"+search.getUserName()+"%"));
				}
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}, PageRequest.of(search.getPn(), search.getPs(), new Sort(Direction.DESC, "updateTime")));
			List<UserDto> userDtos = new ArrayList<UserDto>();
			for (UserEntity userEntity : page) {
				userDtos.add(BeanUtils.transfrom(UserDto.class, userEntity));
			}
			Page<UserDto> retPage = new PageImpl<UserDto>(userDtos, page.getPageable(), page.getTotalPages());
			return ResponseEntity.ok(retPage);
		} else {
			List<UserEntity> page = userRepository.findAll((root, query, criteriaBuilder)->{
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(criteriaBuilder.like(root.get("name"), "%"+search.getUserName()+"%"));
				return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
			}, new Sort(Direction.DESC, "updateTime"));
			List<UserDto> userDtos = new ArrayList<UserDto>();
			for (UserEntity userEntity : page) {
				userDtos.add(BeanUtils.transfrom(UserDto.class, userEntity));
			}
			Page<UserDto> retPage = new PageImpl<UserDto>(userDtos);
			return ResponseEntity.ok(retPage);
		}
	}
	
}
