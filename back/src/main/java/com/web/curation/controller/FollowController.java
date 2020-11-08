package com.web.curation.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.curation.dao.AlarmDao;
import com.web.curation.dao.FollowDao;
import com.web.curation.dao.UserDao;
import com.web.curation.model.BasicResponse;
import com.web.curation.model.Follow;
import com.web.curation.model.FollowDTO;

import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = { "*" })
@RestController
public class FollowController {

	@Autowired
	FollowDao followDao;
	@Autowired
	private AlarmDao alarmDao;
	@Autowired 
	private UserDao userDao;

	@GetMapping("/follow/add")
	@ApiOperation(value = "팔로우 추가")
	// following 팔로우 하는 사람  , followed 팔로우 당한 사람
	public Object addFollow(@Valid @RequestParam String followingUser, @Valid @RequestParam String followedUser) {
		Follow follow = new Follow();
		final BasicResponse result = new BasicResponse();
		result.status=true;
		Optional<Follow> optfollow;
		if(followDao.countByFolloweduserAndFollowinguser(followedUser, followingUser)==0) {//없을때
			follow.setFolloweduser(followedUser);
			follow.setFollowinguser(followingUser);
			if (followDao.save(follow) == null) {
				System.out.println("안됨");
				result.data="fail";
			} else {
				alarmDao.addFollowAlarm("2", followedUser, followingUser, 0);
				optfollow = followDao.getFollowByfolloweduserAndFollowinguser(followedUser, followingUser);
				result.object = optfollow.get();
				System.out.println("됨");
				result.data="success";
				
	
			}
		}else {//있을때
			result.data="fail";
			result.object="이미 팔로우한 유저입니다.";
		}
		return result;
	}

	@GetMapping("/follow/forFollowing")
	@ApiOperation(value = "내가 팔로우한 사람들 리스트")
	public Object myFollowList(@Valid @RequestParam String userName) {
		Map<String, Object> resultMap=new HashMap<>();
		List<Follow> result = new ArrayList<>();
		result = followDao.getFollowByFollowinguser(userName);

		if (result.isEmpty()) {
			System.out.println("값 없음");
		} else {
			System.out.println("값 있음");
		} 
		
		List<String> prfimgs = new ArrayList<>();
		for(int i=0;i<result.size();i++) {
			String tmp = userDao.findProfileImgByNickname(result.get(i).getFolloweduser());
			if(tmp==null || tmp.equals("")) {
				tmp="-1";
			}
			prfimgs.add(tmp);
		}
		
		
		resultMap.put("result",result);
		resultMap.put("prfimgs",prfimgs);	
		
		
		return resultMap;
	}

	@GetMapping("/follow/forFollower")
	@ApiOperation(value = "나를 팔로우한 사람들 보여줄 리스트")
	public Object followerList(@Valid @RequestParam String userName) {
		Map<String, Object> resultMap=new HashMap<>();
		List<FollowDTO> result = new ArrayList<>();
		List<Follow> followedList = followDao.getFollowByFolloweduser(userName);
		//나를 팔로우한 사람 목록 점부 가져오기
		if (!followedList.isEmpty()) {
			//나도 팔로우한 사람 목록 가져오기
			List<Follow> followingAndFollowedList = followDao.getFollowByFolloweinguserAndFollweduser(userName);

			for (Iterator<Follow> iter = followedList.iterator(); iter.hasNext();) {
				Follow follow = iter.next();
				FollowDTO followDto = new FollowDTO(follow);
				
				for(Iterator<Follow> iter2 = followingAndFollowedList.iterator(); iter2.hasNext();) {
					Follow follow2 = iter2.next();
					if(follow.getFollowno()== follow2.getFollowno()) {
						followDto.setCheck(true);//팔로우 취소로 해주어야함
					}else {
						followDto.setCheck(false);//팔로우로 버튼을 만들어야함
					}
							
				}
				result.add(followDto);
			}
			
		}
		
		List<String> prfimgs = new ArrayList<>();
		for(int i=0;i<result.size();i++) {
			String tmp = userDao.findProfileImgByNickname(result.get(i).getFollowinguser());
			if(tmp==null || tmp.equals("")) {
				tmp="-1";
			}
			prfimgs.add(tmp);
		}
		
		resultMap.put("result",result);
		resultMap.put("prfimgs",prfimgs);
		
		return resultMap;
	}

	@GetMapping("/follow/delete")
	@ApiOperation(value = "팔로우 삭제")
	public Object deleteFollow(@Valid @RequestParam int followNo) {

		final BasicResponse result = new BasicResponse();
		Follow follow = followDao.findByFollowno(followNo);
		
		
		if (followDao.deleteAllByFollowno(followNo) == 1) {// 성공
			result.status = true;
			result.data = "삭제 성공";
			String type="2";
			alarmDao.deleteByTypeAndRecevierAndFollower(type,follow.getFolloweduser(), follow.getFollowinguser());
			
		} else {// 실패
			result.status = true;
			result.data = "삭제 실패";
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@GetMapping("/isfollowed")
	   @ApiOperation(value = "팔로우 되있는지 확인")
	   public Object isFollow(@Valid @RequestParam String followingUser, @Valid @RequestParam String followedUser) {
	      final BasicResponse result = new BasicResponse();
	      
	      Optional<Follow> optFollow = followDao.getFollowByfolloweduserAndFollowinguser(followedUser, followingUser);
	      
	      if(optFollow.isPresent()) {//있다면
	         result.data = "exist";
	         result.object = optFollow.get();
	         result.status = true;
	      }
	      return new ResponseEntity<>(result, HttpStatus.OK);
	   }

}
