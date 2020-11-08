package com.web.curation.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.mail.internet.MimeMessage;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.web.curation.dao.UserDao;
import com.web.curation.model.BasicResponse;
import com.web.curation.model.User;
import com.web.curation.model.UserDTO;
import com.web.curation.request.SignupRequest;
import com.web.curation.service.user.JwtService;
import com.web.curation.service.user.UserLoginService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javassist.NotFoundException;

@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
		@ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
		@ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
		@ApiResponse(code = 500, message = "Failure", response = BasicResponse.class) })

@CrossOrigin(origins = { "*" })
@RestController
public class AccountController {

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	UserDao userDao;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserLoginService userService;

	@GetMapping("/account/login")
	@ApiOperation(value = "로그인")
	public Object login(@RequestParam(required = true) final String email,
			@RequestParam(required = true) final String password) {

//      Optional<User> userOpt = userDao.findUserByEmailAndPassword(email, password);

		ResponseEntity response = null;

		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		try {
			UserDTO loginUser = userService.login(email, password);
			System.out.println(loginUser.getEmail());
			System.out.println(loginUser.getPassword());

			// 아이디가 존재하지 않으면 result -1
			if (loginUser.getEmail() == null || !loginUser.getEmail().equals(email)) {
				resultMap.put("result", -1);
			}

			// 비밀번호가 틀리다면 res 2
			else if (!loginUser.getPassword().equals(password)) {
				resultMap.put("result", 2);
			}
			// 로그인 성공 했다면 토큰 생성 후 result 1
			// 토큰 정보는 request의 헤더로 보내고 나머지는 Map에 답는다.
			else {
				String token = jwtService.create(loginUser);
				System.out.println(token);
				resultMap.put("auth_token", token);
				resultMap.put("result", 1);
				resultMap.put("data", loginUser);
			}

			// res.setHeader("jwt-auth-token",token);
		} catch (NotFoundException e) {
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
			e.printStackTrace();
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}

	@PostMapping("/account/signup")
	@ApiOperation(value = "회원가입")
	public Object signup(@Valid @RequestBody SignupRequest request) {
		Map<String,Object> resultMap=new HashMap<>();
		// 이메일, 닉네임 중복처리 필수
		// 회원가입단을 생성해 보세요.
		final BasicResponse result = new BasicResponse();
		// 이메일, 닉네임 중복처리 필수

		// 저장
		User user = new User();
		StringTokenizer st = new StringTokenizer(request.getBirth());
		LocalDate currentDate = LocalDate.of(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()),
				Integer.parseInt(st.nextToken()));

		user.setNickname(request.getNickname());
		user.setEmail(request.getEmail());
		user.setBirth(currentDate);
		user.setGender(request.getGender());
		user.setPassword(request.getPassword());
		if(request.getSelfintroduce()!=null) user.setSelfintroduce(request.getSelfintroduce());
		user.setProfile_img(request.getProfile_img());
		if (userDao.findUserByNickname(user.getNickname()).isPresent()
				|| userDao.findUserByEmail(user.getEmail()).isPresent()) {
			result.status = true;
			result.data = "fail";
		} else {
			if (userDao.save(user) == null) {
				result.status = true;
				result.data = "fail";

			} else {
				result.status = true;
				result.data = "success";
				resultMap.put("result",result);
				String token = jwtService.create(new UserDTO(user));
				resultMap.put("auth_token",token);
				resultMap.put("data",user);
				
			}
		}
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@GetMapping("/account/findPassword")
	@ApiOperation(value = "비밀번호 찾기")
	public Map<String, Object> findPassword(@Valid @RequestParam String email, @Valid @RequestParam String pTime) {
		Map<String, Object> result = new HashMap<>();
		LocalDate time = LocalDate.of(Integer.parseInt(pTime.substring(0, 4)), Integer.parseInt(pTime.substring(4, 6)),
				Integer.parseInt(pTime.substring(6, 8)));
		Optional<User> optUser = userDao.findUserByEmailAndBirth(email, time);
		if (!optUser.isPresent()) {
		} else {
			Properties props = new Properties();
			
			String host="smtp.gmail.com";
			String port="587";
			String user="ouosssssssa@gmail.com";
			String password="zzxx1122";
			
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.ssl.trust", host);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.host", host);
			
			if (port != null)
			{
				props.put("mail.smtp.port", port);
				props.put("mail.smtp.socketFactory.port", port);
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			}
			
			UserDTO userDto = new UserDTO(optUser.get());

			String to = userDto.getEmail();
			String subject = "핏온유 비밀번호 인증입니다 확인해 주세요";
			int randomCode = new Random().nextInt(9000) + 1000;
			String certificationNum = Integer.toString(randomCode);
			StringBuilder text = new StringBuilder();
			text.append(userDto.getNickname());
			text.append(" 님의 비밀번호를 위한 비밀번호 인증 절차입니다 하단의 번호를 핏온유 화면에 입력해 주세요\n");
			text.append("인증번호:" + certificationNum + '\n');
			text.append("인증번호를 다른사람이 보지 않게 주의해 주세요.\n");
//			text.append("핏온유 인증 페이지로 이동하기");
//			text.append("http://localhost:8081/");

			MimeMessage message = emailSender.createMimeMessage();
			try {
				MimeMessageHelper helper = new MimeMessageHelper(message, true);
				helper.setFrom("ouosssssssa@gmail.com");
				helper.setTo(to);
				helper.setSubject(subject);
				helper.setText(text.toString());
				emailSender.send(message);
				result.put("userInfo", userDto);
				result.put("certifNum", certificationNum);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@PostMapping(value = "/account/addProfileImg")
   @ApiOperation(value = "가입하기")

   public Object addProfileImg(@RequestParam("profile-img-edit") MultipartFile img, @RequestParam String nickname) {
      Map<String,Object> resultMap=new HashMap<>();
      final BasicResponse result = new BasicResponse();
      // 이 path는 로컬에선 일단 각자 경로로 테스트
      String path ="/var/www/html/dist/images/profile/";
      String savedName =nickname+"_"+img.getOriginalFilename();
      File file = new File(path + savedName);
      try {
         img.transferTo(file);
         String storePath="../images/profile/"+savedName;
         if(userDao.updateProfileImg(storePath, nickname)==1) {
            result.data="success";
            UserDTO userDTO = new UserDTO(userDao.findUserByNickname(nickname).get());
            String Token = jwtService.create(userDTO);
            resultMap.put("auth_token",Token);
            resultMap.put("profileurl",storePath);
            
         }
         else {
            result.data="fail";
         }
         System.out.println(storePath);
      } catch (Exception e) {
         e.printStackTrace();
      }
      
      resultMap.put("result",result);
      System.out.println(img);
      return resultMap;
   }

	@GetMapping("/account/checkDoubleEmail")
	@ApiOperation(value = "이메일 중복검사")
	public Object findEmail(@Valid @RequestParam String email) {
		final BasicResponse result = new BasicResponse();

		Optional<User> optUser = userDao.getUserByEmail(email);
		if (!optUser.isPresent()) {// 없는 경우
			result.status = true;
			result.data = "non exist";
		} else {// 있는 경우
			result.status = true;
			result.data = "exist";
			result.object = optUser.get();
		}
		return result;
	}

	@GetMapping("/account/checkNickname")
	@ApiOperation(value = "닉네임 중복검사")
	public Object findNick(@Valid @RequestParam String nickname) {
		
		final BasicResponse result = new BasicResponse();

		Optional<User> optUser = userDao.findUserByNickname(nickname);
		if (!optUser.isPresent()) {// 없는 경우
			result.status = true;
			result.data = "non exist";
		} else {// 있는 경우
			result.status = true;
			result.data = "exist"; 
			result.object = optUser.get(); 
		}
		return result;
	}
	
	@PostMapping("/account/changePassword")
	@ApiOperation(value = "새 비밀번호 설정")
	public Object changePwd(@Valid @RequestParam("email") String email,@Valid @RequestParam("password") String password) {
		Map<String, Object> resultMap = new HashMap<>();
		System.out.println(email+ " "+password);
		final BasicResponse result = new BasicResponse();
		try{
			userDao.updatePassword(password, email);
			result.status=true;
			result.data="success";
			UserDTO userDTO = new UserDTO(userDao.findUserByEmail(email).get());
			String Token = jwtService.create(userDTO);
			resultMap.put("auth_token",Token);
			
		}
		catch (Exception e){
			result.status=true;
			result.data="fail";
			resultMap.put("result",result);
		}
		
		return resultMap;
	}
	@GetMapping("/account/token")
	public Map<String, Object> getUserByToken(@RequestParam String jwt){
		Map<String, Object> resultMap = new HashMap<>();
		try {
			jwtService.checkValid(jwt); // 토큰이 유효한지 검사

			resultMap.put("userInfo",jwtService.get(jwt)); // 토큰에 담긴 정보 담기
			resultMap.put("result",1);
			
		}catch(Exception e){
			resultMap.put("result",0);
		}
		return resultMap;
	}
	
	
	@PostMapping("/account/nickchange")
	@ApiOperation(value = "닉네임 변경")
	public Object changeNick(@Valid @RequestParam("prev") String prev,@Valid @RequestParam("cur") String cur) {
		Map<String,Object> resultMap=new HashMap<>();
		System.out.println(prev+ " "+cur);
		final BasicResponse result = new BasicResponse();
		try {
			if(userDao.updateNickname(prev, cur)==1) {
				result.data="success";
				result.object=cur;
				UserDTO userDTO = new UserDTO(userDao.findUserByNickname(cur).get());
				String Token = jwtService.create(userDTO);
				resultMap.put("auth_token",Token);
			}else {
				result.data="fail";
			}
		}
		catch(Exception e) {
			result.data="fail";
		}
		result.status=true;
		resultMap.put("result",result);
		
		return resultMap;
	}
	@PostMapping("/account/social/{type}")
	@ApiOperation(value="소셜 로그인시 회원가입 처리")
	public Object socialjoin(@RequestBody User user, @PathVariable int type) {
		
		System.out.println(user.getNickname()+" "+user.getEmail()+" "+type);
		final BasicResponse result = new BasicResponse();
		Map<String,Object> resultMap=new HashMap<>();
		
		if(type==0) {  // 소셜 로그인시 
			Optional<User> u=userDao.findUserByEmail(user.getEmail());
			if(u.isPresent()) {   
				
				result.data="1";  // 이미 존재하면 1
				UserDTO userDTO = new UserDTO(u.get());
				resultMap.put("userinfo",userDTO);
				String Token = jwtService.create(userDTO);
				resultMap.put("auth_token",Token);
				
			}
			else {
				result.data="0";  // 존재하지 않는경우 0
			}
			
		}		
		if(type==1) {   // 약관 페이지에서 넘어온 경우
			
			result.data="success";
			String uuid;
			do {
				uuid = new String(UUID.randomUUID().toString().substring(0,8));
			}while(userDao.findById(uuid).isPresent());
			user.setNickname(uuid);
			
			userDao.save(user);
			
			UserDTO userDTO = new UserDTO(user);
			String Token = jwtService.create(userDTO);
			resultMap.put("auth_token",Token);
			
		}
		result.status=true; 
		resultMap.put("result", result);
		
		return resultMap;
	}
	
	
	@PutMapping("/account/selfintro")
	@ApiOperation(value="한줄 자기소개 수정")
	public Object selfintro(@RequestParam String nickname, @RequestParam String selfintroduce) {
		Map<String,Object> resultMap = new HashMap<>();
		final BasicResponse result = new BasicResponse();
		if(userDao.updateSelfintro(selfintroduce, nickname)==1) {
			result.data="success";
			result.object=selfintroduce;
			UserDTO userDTO = new UserDTO(userDao.findUserByNickname(nickname).get());
			String Token = jwtService.create(userDTO);
			resultMap.put("auth_token",Token);
		}
		else {
			result.data="false";
		}
		result.status=true;
		resultMap.put("result",result);
		
		return resultMap;
	}
	
	@DeleteMapping("/account/delete")
	@ApiOperation(value="회원 탈퇴")
	public Object delUser(@RequestParam String nickname) {
		final BasicResponse result = new BasicResponse();
		if(userDao.deleteUser(nickname)==1) {
			result.data="success";
		}
		else {
			result.data="fail";
		}
		result.status=true;
		
		return result;
	}
	@GetMapping("/account/confirmemail")
	   @ApiOperation(value = "이메일 인증")
	   public Map<String, Object> confrimEmail(@Valid @RequestParam String email) {
	      Map<String, Object> result = new HashMap<>();
	      Properties props = new Properties();
	      
	      String host="smtp.gmail.com";
	  String port="587";
	  String user="ouosssssssa@gmail.com";
	  String password="zzxx1122";
	  
	  props.put("mail.smtp.starttls.enable", "true");
	  props.put("mail.smtp.ssl.trust", host);
	  props.put("mail.smtp.auth", "true");
	  props.put("mail.smtp.host", host);
	  
	  if (port != null)
	  {
	     props.put("mail.smtp.port", port);
	     props.put("mail.smtp.socketFactory.port", port);
	     props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	  }
	  
	
	  String to = email;
	  String subject = "핏온유 회원가입 인증번호입니다. 확인해 주세요";
	  int randomCode = new Random().nextInt(9000) + 1000;
	  String certificationNum = Integer.toString(randomCode);
	  StringBuilder text = new StringBuilder();
	  text.append("핏온유 회원가입을 위한 인증 절차입니다. 하단의 번호를 핏온유 화면에 입력해 주세요\n");
	  text.append("인증번호:" + certificationNum + '\n');
	  text.append("인증번호를 다른사람이 보지 않게 주의해 주세요.\n");
	
	  MimeMessage message = emailSender.createMimeMessage();
	  try {
	     System.out.println(4);
	     MimeMessageHelper helper = new MimeMessageHelper(message, true);
	     helper.setFrom("ouosssssssa@gmail.com");
	     helper.setTo(to);
	     helper.setSubject(subject);
	     helper.setText(text.toString());
	     emailSender.send(message);
	     result.put("certifNum", certificationNum);
	
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      return result;
	   }
	
	
}