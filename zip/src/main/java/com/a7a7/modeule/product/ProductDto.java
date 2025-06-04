package com.a7a7.modeule.product;

import com.a7a7.modeule.upload.UploadDto;

public class ProductDto extends UploadDto {

    private String seq;
    private int brandName; // DB에서 가져온 브랜드 코드 (숫자)
    private String mealKitName;
    private Integer stock;
    private Integer price;
    private double score;
    private String ingredient;
    private String mealDelNy; // String으로 유지 (DB가 숫자여도 MyBatis가 처리)
    private String mealUseNy; // String으로 유지
    private String userUi_seq;
    private String mealRegDateTime; // String으로 유지
    private String mealModDateTime; // String으로 유지
    private Integer reviewCount; // MyBatis에서 이미 조회

    // -----
    private String path; // 이미지 (MyBatis에서 이미 조회)
    // -----
    private int uploadImg1Type = 1001;
    private int uploadImg1MaxNumber = 1001;

    // --- 추가된 필드 ---
    private String brandNameAsString; // 실제 브랜드 이름 문자열 (서비스에서 채워줄 필드)

    // -----
    // Getters and Setters for ALL fields
    public String getSeq() { return seq; }
    public void setSeq(String seq) { this.seq = seq; }
    public int getBrandName() { return brandName; }
    public void setBrandName(int brandName) { this.brandName = brandName; }
    public String getMealKitName() { return mealKitName; }
    public void setMealKitName(String mealKitName) { this.mealKitName = mealKitName; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public String getIngredient() { return ingredient; }
    public void setIngredient(String ingredient) { this.ingredient = ingredient; }
    public String getMealDelNy() { return mealDelNy; }
    public void setMealDelNy(String mealDelNy) { this.mealDelNy = mealDelNy; }
    public String getMealUseNy() { return mealUseNy; }
    public void setMealUseNy(String mealUseNy) { this.mealUseNy = mealUseNy; }
    public String getUserUi_seq() { return userUi_seq; }
    public void setUserUi_seq(String userUi_seq) { this.userUi_seq = userUi_seq; }
    public String getMealRegDateTime() { return mealRegDateTime; }
    public void setMealRegDateTime(String mealRegDateTime) { this.mealRegDateTime = mealRegDateTime; }
    public String getMealModDateTime() { return mealModDateTime; }
    public void setMealModDateTime(String mealModDateTime) { this.mealModDateTime = mealModDateTime; }
    public int getUploadImg1Type() { return uploadImg1Type; }
    public void setUploadImg1Type(int uploadImg1Type) { this.uploadImg1Type = uploadImg1Type; }
    public int getUploadImg1MaxNumber() { return uploadImg1MaxNumber; }
    public void setUploadImg1MaxNumber(int uploadImg1MaxNumber) { this.uploadImg1MaxNumber = uploadImg1MaxNumber; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    // --- 추가된 필드 Getter/Setter ---
    public String getBrandNameAsString() { return brandNameAsString; }
    public void setBrandNameAsString(String brandNameAsString) { this.brandNameAsString = brandNameAsString; }
}