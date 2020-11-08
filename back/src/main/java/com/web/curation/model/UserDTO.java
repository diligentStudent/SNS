package com.web.curation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserDTO {
	private String nickname;
	

	private String password;
	private String email;
	private LocalDateTime create_Date;
	private LocalDate birth;
	private String gender;
	private String selfintroduce;
	private String profile_img;

	public UserDTO() {
	}

	public UserDTO(User user) {
		this.birth = user.getBirth();
		this.create_Date = user.getCreate_Date();
		this.email = user.getEmail();
		this.gender = user.getGender();
		this.nickname = user.getNickname();
		this.password = user.getPassword();
		this.selfintroduce = user.getSelfintroduce();
		this.profile_img = user.getProfile_img();
	}

	public String getProfile_img() {
		return profile_img;
	}

	public void setProfile_img(String profile_img) {
		this.profile_img = profile_img;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDateTime getCreate_Date() {
		return create_Date;
	}

	public void setCreate_Date(LocalDateTime create_Date) {
		this.create_Date = create_Date;
	}

	public LocalDate getBirth() {
		return birth;
	}

	public void setBirth(LocalDate birth) {
		this.birth = birth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getSelfintroduce() {
		return selfintroduce;
	}

	public void setSelfintroduce(String selfintroduce) {
		this.selfintroduce = selfintroduce;
	}

}
