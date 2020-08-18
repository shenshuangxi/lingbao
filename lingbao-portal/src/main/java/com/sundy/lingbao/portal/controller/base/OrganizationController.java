package com.sundy.lingbao.portal.controller.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sundy.lingbao.common.exception.LingbaoException;
import com.sundy.lingbao.common.util.BeanUtils;
import com.sundy.lingbao.portal.dto.OrganzationDto;
import com.sundy.lingbao.portal.entity.base.OrganzationEntity;
import com.sundy.lingbao.portal.repository.base.OrganzationRepositroy;


@RestController
@RequestMapping("/org")
public class OrganizationController extends BaseController {

	@Autowired
	private OrganzationRepositroy organzationRepositroy;
	
	@GetMapping("/orgId/{orgId}")
	public ResponseEntity<OrganzationDto> findOrganzation(@PathVariable Long orgId) {
		Optional<OrganzationEntity> optional = organzationRepositroy.findById(orgId);
		if (optional.isPresent()) {
			OrganzationDto organzationDto = BeanUtils.transfrom(OrganzationDto.class, optional.get());
			return ResponseEntity.ok(organzationDto);
		}
		throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "not found organzation");
	}
	
	
	@GetMapping("/orgId/{orgId}/children")
	public ResponseEntity<List<OrganzationDto>> findOrganzationChildren(@PathVariable Long orgId) {
		List<OrganzationEntity> organzationEntities = organzationRepositroy.findByParentId(orgId);
		if (Objects.nonNull(organzationEntities)) {
			List<OrganzationDto> organzationDtos = new ArrayList<OrganzationDto>();
			for (OrganzationEntity organzationEntity : organzationEntities) {
				OrganzationDto organzationDto = BeanUtils.transfrom(OrganzationDto.class, organzationEntity);
				organzationDtos.add(organzationDto);
			}
			return ResponseEntity.ok(organzationDtos);
		}
		return ResponseEntity.ok(new ArrayList<OrganzationDto>());
	}
	
	
	
	@PutMapping("/parent/{parentId}/create")
	public ResponseEntity<OrganzationDto> createOrganzation(@PathVariable Long parentId, String orgName) {
		
		
		Optional<OrganzationEntity> optionalParent = organzationRepositroy.findById(parentId);
		
		OrganzationEntity organzationEntity = new OrganzationEntity();
		organzationEntity.setOrgName(orgName);
		organzationEntity.setParentId(parentId);
		organzationEntity.setCreateBy(SecurityContextHolder.getContext().getAuthentication().getName());
		organzationEntity.setUpdateBy(organzationEntity.getCreateBy());
		organzationEntity = organzationRepositroy.save(organzationEntity);
		
		if  (optionalParent.isPresent()) {
			organzationEntity.setHierarchy(optionalParent.get().getHierarchy()+"."+organzationEntity.getId()+".");
		} else {
			organzationEntity.setHierarchy("."+organzationEntity.getId()+".");
		}
		
		organzationRepositroy.save(organzationEntity);
		
		
		return ResponseEntity.ok(BeanUtils.transfrom(OrganzationDto.class, organzationEntity));
	}
	
	@PatchMapping("/orgId/{orgId}")
	public ResponseEntity<OrganzationDto> updateOrganzation(@PathVariable Long orgId, String orgName, Long parentId) {
		Optional<OrganzationEntity> optionalChild = organzationRepositroy.findById(orgId);
		if (optionalChild.isPresent()) {
			OrganzationEntity currentOrganzationEntity = optionalChild.get();
			currentOrganzationEntity.setOrgName(orgName);
			if (!optionalChild.get().getParentId().equals(parentId)) {
				Optional<OrganzationEntity> optionalParent = organzationRepositroy.findById(orgId);
				if (optionalParent.isPresent()) {
					currentOrganzationEntity.setParentId(parentId);
					String newHierarchy = optionalParent.get().getHierarchy()+"."+currentOrganzationEntity.getId();
					List<OrganzationEntity> currentChildOrganzationEntities = organzationRepositroy.findByHierarchyStartingWith(currentOrganzationEntity.getHierarchy());
					if (Objects.nonNull(currentChildOrganzationEntities)) {
						for (OrganzationEntity organzationEntity : currentChildOrganzationEntities) {
							String childHierarchy = organzationEntity.getHierarchy().replace(currentOrganzationEntity.getHierarchy(), newHierarchy);
							organzationEntity.setHierarchy(childHierarchy);
						}
					}
					organzationRepositroy.saveAll(currentChildOrganzationEntities);
					currentOrganzationEntity.setHierarchy(newHierarchy);
				} else {
					throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "not found parent organzation");
				}
			}
			organzationRepositroy.save(currentOrganzationEntity);
			OrganzationDto organzationDto = BeanUtils.transfrom(OrganzationDto.class, currentOrganzationEntity);
			return ResponseEntity.ok(organzationDto);
		} 
		throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "not found organzation");
	}
	
	@DeleteMapping("orgId/{orgId}")
	public ResponseEntity<Object> deleteOrganzation(@PathVariable Long orgId) {
		Optional<OrganzationEntity> optionalChild = organzationRepositroy.findById(orgId);
		if (optionalChild.isPresent()) {
			OrganzationEntity currentOrganzationEntity = optionalChild.get();
			List<OrganzationEntity> currentChildOrganzationEntities = organzationRepositroy.findByHierarchyStartingWith(currentOrganzationEntity.getHierarchy());
			if (Objects.nonNull(currentChildOrganzationEntities)) {
				for (OrganzationEntity organzationEntity : currentChildOrganzationEntities) {
					String childHierarchy = organzationEntity.getHierarchy().replace(currentOrganzationEntity.getHierarchy(), ".");
					organzationEntity.setHierarchy(childHierarchy);
				}
			}
			organzationRepositroy.saveAll(currentChildOrganzationEntities);
			organzationRepositroy.delete(currentOrganzationEntity);
			return ResponseEntity.ok().build();
		} 
		throw new LingbaoException(HttpStatus.BAD_REQUEST.value(), "not found organzation");
	}
	
}
