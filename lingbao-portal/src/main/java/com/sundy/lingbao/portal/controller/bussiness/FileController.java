package com.sundy.lingbao.portal.controller.bussiness;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sundy.lingbao.common.dto.FileCommitTreeDto;
import com.sundy.lingbao.common.dto.FileDto;
import com.sundy.lingbao.common.dto.PageDto;
import com.sundy.lingbao.common.util.BeanUtils;
import com.sundy.lingbao.core.util.StringUtils;
import com.sundy.lingbao.portal.controller.base.BaseController;
import com.sundy.lingbao.portal.entity.bussiness.FileCommitTreeEntity;
import com.sundy.lingbao.portal.entity.bussiness.FileEntity;
import com.sundy.lingbao.portal.service.FileService;


@RestController
@RequestMapping("/api/v1/app/{appId}/env/{envId}/cluster/{clusterId}/file")
public class FileController extends BaseController {

	@Autowired
	private FileService fileService;
	
	@GetMapping
	public ResponseEntity<List<FileDto>> findAll(@PathVariable String appId, @PathVariable String envId, @PathVariable String clusterId) {
		List<FileDto> fileDtos = new ArrayList<FileDto>();
		List<FileEntity> fileEntities = fileService.findByAppIdAndEnvIdAndClusterId(appId, envId, clusterId);
		if (Objects.nonNull(fileEntities)) {
			for (FileEntity fileEntity : fileEntities) {
				fileDtos.add(BeanUtils.transfrom(FileDto.class, fileEntity));
			}
		}
		return ResponseEntity.ok(fileDtos);
	}
	
	@GetMapping("/history")
	public ResponseEntity<PageDto<FileCommitTreeDto>> findhistory(@PathVariable String appId, @PathVariable String envId, @PathVariable String clusterId, 
			@RequestParam Integer pageIndex,
			@RequestParam Integer rows,
			@RequestParam(required=false) String sortOrder,
			@RequestParam(required=false) String sort) {
		
		Direction direction = StringUtils.isNullOrEmpty(sortOrder) ? Direction.DESC : Direction.ASC.name().equalsIgnoreCase(sortOrder) ? Direction.ASC : Direction.DESC;
		String[] properties = StringUtils.isNullOrEmpty(sort) ? new String[] {"createDate"}: sort.split(",");
		Page<FileCommitTreeEntity> page = fileService.findHistory(appId, envId, clusterId, PageRequest.of(pageIndex, rows, new Sort(direction, properties)));
		
		PageDto<FileCommitTreeDto> retPage = new PageDto<FileCommitTreeDto>();
		if (Objects.nonNull(page) && page.getContent().size()>0) {
			List<FileCommitTreeDto> fileCommitTreeDtos = new ArrayList<>(page.getContent().size());
			for (FileCommitTreeEntity fileCommitTreeEntity : page.getContent()) {
				fileCommitTreeDtos.add(BeanUtils.transfrom(FileCommitTreeDto.class, fileCommitTreeEntity));
			}
			retPage.setRows(fileCommitTreeDtos);
			retPage.setTotal(page.getTotalElements());
		}
		return ResponseEntity.ok(retPage);
	}
	
	@PutMapping
	public  ResponseEntity<FileDto> create(@PathVariable String appId, @PathVariable String envId, @PathVariable String clusterId, @RequestBody FileDto fileDto) {
		FileEntity fileEntity = fileService.createFile(appId, envId, clusterId, fileDto, getAccount());
		FileDto dto = BeanUtils.transfrom(FileDto.class, fileEntity);
		return ResponseEntity.ok(dto);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<FileDto> update(@PathVariable String appId, @PathVariable String envId, @PathVariable String clusterId, @PathVariable Long id, @RequestBody FileDto fileDto) {
		FileEntity fileEntity = fileService.updateFile(appId, envId, clusterId, id, fileDto, getAccount());
		FileDto newfileDto = BeanUtils.transfrom(FileDto.class, fileEntity);
		return ResponseEntity.ok(newfileDto);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable String appId, @PathVariable String envId, @PathVariable String clusterId, @PathVariable Long id) {
		fileService.deleteFile(appId, envId, clusterId, id);
		return ResponseEntity.ok().build();
	}
	
}
