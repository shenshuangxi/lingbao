package com.sundy.lingbao.file.old.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/heartbeat")
public class HeartBeatController {

	@PostMapping
	public ResponseEntity<Object> postHeartBeat() {
		
		return ResponseEntity.ok().build();
	}
	
}
