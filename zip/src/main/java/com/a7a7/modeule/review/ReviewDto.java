package com.a7a7.modeule.review;

import java.util.Date; // 또는 LocalDateTime

public class ReviewDto {

    private String seq; // 리뷰 ID
    private String reviewTitle; // 리뷰 제목
    private String reviewContent; // 리뷰 내용 (DB 컬럼 'review'에 해당)
    private Integer rating; // 사용자가 매긴 별점 (1~5)

    private String mealKit_seq; // 리뷰 대상 밀키트 ID
    private String userUi_seq; // 작성자 ID (user_ui 테이블의 seq)
    private String recipePost_seq; // (밀키트 리뷰에서는 보통 사용 안 함)

    // 작성자 정보 (user_ui 테이블과 조인해서 가져올 정보)
    private String userName; // 작성자 이름
    private String userProfileImageUrl; // 작성자 프로필 이미지 경로

    private Date regDate; // 리뷰 등록일 (또는 LocalDateTime)
    private Date modDate; // 리뷰 수정일 (선택적)

    // 기본 생성자
    public ReviewDto() {}

    // Getter 및 Setter 메서드
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
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

    public String getRecipePost_seq() {
        return recipePost_seq;
    }

    public void setRecipePost_seq(String recipePost_seq) {
        this.recipePost_seq = recipePost_seq;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
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