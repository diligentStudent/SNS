package com.web.curation.model;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@Table(name = "recellboard")
public class Recell {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int recellNo;

	@NotNull
	private String recellUser;
	@NotNull
	private String recellPrice;
	private String recellImage;
	private String recellContent;
	@Column(insertable = false, updatable = false) // 일기전용시 false
	private LocalDateTime recellDate;
	private String recellSize;
	private String roomname;
	private int salecheck;
	private String place;
	private String category;

	public int getRecellNo() {
		return recellNo;
	}

	public void setRecellNo(int recellNo) {
		this.recellNo = recellNo;
	}

	public String getRecellUser() {
		return recellUser;
	}

	public void setRecellUser(String recellUser) {
		this.recellUser = recellUser;
	}

	public String getRecellPrice() {
		return recellPrice;
	}

	public void setRecellPrice(String recellPrice) {
		this.recellPrice = recellPrice;
	}

	public String getRecellImage() {
		return recellImage;
	}

	public void setRecellImage(String recellImage) {
		this.recellImage = recellImage;
	}

	public String getRecellContent() {
		return recellContent;
	}

	public void setRecellContent(String recellContent) {
		this.recellContent = recellContent;
	}

	public String getRecellSize() {
		return recellSize;
	}

	public void setRecellSize(String recellSize) {
		this.recellSize = recellSize;
	}

	public LocalDateTime getRecellDate() {
		return recellDate;
	}

	public void setRecellDate(LocalDateTime recellDate) {
		this.recellDate = recellDate;
	}

	public String getRoomname() {
		return roomname;
	}

	public void setRoomname(String roomname) {
		this.roomname = roomname;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place=place;
	}

}
