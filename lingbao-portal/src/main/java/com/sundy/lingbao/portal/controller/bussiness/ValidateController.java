package com.sundy.lingbao.portal.controller.bussiness;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sundy.lingbao.portal.controller.base.BaseController;

@RestController
@RequestMapping("/api/v1/validate")
public class ValidateController extends BaseController {

	@GetMapping
	public ResponseEntity<Object> validate() {
		return ResponseEntity.ok().build();
	}
	
}
