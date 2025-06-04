package com.a7a7.modeule.review;

import java.util.Date;

public class ReviewDto {
    private String seq; // 애플리케이션에서 사용할 UUID (review_uuid 컬럼에 저장)
    private String reviewTitle;
    private String reviewContent;
    private int reviewCount;
    private int rating;
    private String mealKit_seq;
    private String userUi_seq;
    private String userName;
    private Date regDate;
    private Date modDate;
    private Integer delNy;

    // private Integer reviewCount;    // 밀키트 리뷰에서는 사용 안 함
    // private Integer recipePost_seq; // 밀키트 리뷰에서는 사용 안 함

    // Getters and Setters (reviewCount, recipePost_seq 제외 또는 포함)
    public String getSeq() { return seq; }
    public void setSeq(String seq) { this.seq = seq; }
    public String getReviewTitle() { return reviewTitle; }
    public void setReviewTitle(String reviewTitle) { this.reviewTitle = reviewTitle; }
    public String getReviewContent() { return reviewContent; }
    public void setReviewContent(String reviewContent) { this.reviewContent = reviewContent; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getMealKit_seq() { return mealKit_seq; }
    public void setMealKit_seq(String mealKit_seq) { this.mealKit_seq = mealKit_seq; }
    public String getUserUi_seq() { return userUi_seq; }
    public void setUserUi_seq(String userUi_seq) { this.userUi_seq = userUi_seq; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Date getRegDate() { return regDate; }
    public void setRegDate(Date regDate) { this.regDate = regDate; }
    public Date getModDate() { return modDate; }
    public void setModDate(Date modDate) { this.modDate = modDate; }
    public int getReviewCount() { return reviewCount; }
	public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
	public Integer getDelNy() { return delNy; }
	public void setDelNy(Integer delNy) { this.delNy = delNy; }
	@Override
    public String toString() {
        return "ReviewDto{" +
                "seq='" + seq + '\'' +
                ", reviewTitle='" + reviewTitle + '\'' +
                ", reviewContent='" + reviewContent + '\'' +
                ", rating=" + rating +
                ", mealKit_seq='" + mealKit_seq + '\'' +
                ", userUi_seq='" + userUi_seq + '\'' +
                ", userName='" + userName + '\'' +
                // ", reviewCount=" + reviewCount +       // 제외
                // ", recipePost_seq=" + recipePost_seq + // 제외
                '}';
    }
}