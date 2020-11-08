package com.web.curation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.curation.dao.AlarmDao;
import com.web.curation.model.Alarm;
import com.web.curation.model.BasicResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
		@ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
		@ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
		@ApiResponse(code = 500, message = "Failure", response = BasicResponse.class) })

@CrossOrigin(origins = { "*" })
@RestController
@RequestMapping("/alarm")
public class AlarmController {
	/*
	 * 댓글  type 1
	팔로우  type 2
	좋아요  type 3
	 */
	
	@Autowired
	private AlarmDao alarmDao;
	
	@GetMapping
	public Map<String,Object> alarmList(@Valid @RequestParam String nickname){
		Map<String,Object> resultMap= new HashMap<>();
		
		List<Alarm> list=alarmDao.findByRecevierAndIsReadOrderByAlramNoDesc(nickname);
	
		int cnt=list.size();
		if(cnt!=0) {
			resultMap.put("result",1);
			resultMap.put("alist",list);
			resultMap.put("acnt",cnt);
		}
		else {
			resultMap.put("result",0);
		}
		return resultMap;
	}
	@PostMapping
	public Object isRead(@RequestParam List<Integer> alramNo) {
		final BasicResponse result = new BasicResponse();
		System.out.println(alramNo.size());
		
		alarmDao.isRead(alramNo);
		result.status=true;
		result.data="success";

		
		return result;
	}
	
	@PostMapping("/del")
	public Object allDelete(@Valid @RequestParam String recevier) {
		final BasicResponse result = new BasicResponse();
		System.out.println(recevier);
		alarmDao.allDel(recevier);
		
		result.status=true;
		result.data="success";
		
		return result;
	}
	@GetMapping("/check")
	public Object checkAlarm(@RequestParam String recevier) {
		final BasicResponse result = new BasicResponse();
		result.status=true;
		if(alarmDao.checkAlarm(recevier)>0) {		
			result.data="1"; 
		}
		else {		
			result.data="0";
		}
		
		return result;
	}
	
	
}
