package com.a7a7.modeule.review;

import java.util.Date;

public class ReviewDto {
    private String seq;
    private String reviewTitle;
    private String reviewContent; // reviewContent 필드
    private int rating;
    private String mealKit_seq;
    private String userUi_seq;
    private String userName;
    private Date regDate;
    private Date modDate;
    
//  -----
  
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getReviewTitle() {
		return reviewTitle;
	}
	public void setReviewTitle(String reviewTitle) {
		this.reviewTitle = reviewTitle;
	}
	public String getReviewContent() {
		return reviewContent;
	}
	public void setReviewContent(String reviewContent) {
		this.reviewContent = reviewContent;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getMealKit_seq() {
		return mealKit_seq;
	}
	public void setMealKit_seq(String mealKit_seq) {
		this.mealKit_seq = mealKit_seq;
	}
	public String getUserUi_seq() {
		return userUi_seq;
	}
	public void setUserUi_seq(String userUi_seq) {
		this.userUi_seq = userUi_seq;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getRegDate() {
		return regDate;
	}
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
	public Date getModDate() {
		return modDate;
	}
	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}
    
}