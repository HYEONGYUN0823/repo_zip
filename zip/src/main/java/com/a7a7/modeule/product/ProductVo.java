package com.a7a7.modeule.product;

import java.util.List; // List 임포트

public class ProductVo {

    private String seq = "0";

    // paging
    private int thisPage = 1;
    private int rowNumToShow = 12;
    private int pageNumToShow = 5;
    private int totalRows;
    private int totalPages;
    private int startPage;
    private int endPage;
    private int startRnumForMysql = 0;

    private String ifbnSeq;

    // search
    private Integer shUseNy = 1;
    private Integer shDelNy = 0;
    private Integer shOptionDate = 0;
    private String shDateStart;
    private String shDateEnd;
    private Integer shOption;
    private Integer shOption1; // 브랜드 코드
    private String shValue; // 검색어

    // ----- AJAX 필터링 및 정렬을 위한 필드 -----
    private Integer minPrice;
    private Integer maxPrice;
    private List<Integer> ratings; // 여러 평점 선택을 위해 List<Integer>로 변경
    private String sortOption = "latest";

    public void setParamsPaging(int totalRows) {
        this.setTotalRows(totalRows);
        if (this.getTotalRows() == 0) {
            this.setTotalPages(0);
            this.setStartPage(1);
            this.setEndPage(1);
            this.setThisPage(1);
        } else {
            this.setTotalPages((this.getTotalRows() - 1) / this.getRowNumToShow() + 1);
            if (this.getThisPage() > this.getTotalPages() && this.getTotalPages() > 0) {
                this.setThisPage(this.getTotalPages());
            }
            if (this.getThisPage() < 1) {
                this.setThisPage(1);
            }
            this.setStartPage(((this.getThisPage() - 1) / this.getPageNumToShow()) * this.getPageNumToShow() + 1);
            this.setEndPage(Math.min(this.getStartPage() + this.getPageNumToShow() - 1, this.getTotalPages()));
        }
        this.setStartRnumForMysql((this.getThisPage() - 1) * this.getRowNumToShow());
    }

    // Getters and Setters
    public String getSeq() { return seq; }
    public void setSeq(String seq) { this.seq = seq; }
    public int getThisPage() { return thisPage; }
    public void setThisPage(int thisPage) { this.thisPage = thisPage; }
    public int getRowNumToShow() { return rowNumToShow; }
    public void setRowNumToShow(int rowNumToShow) { this.rowNumToShow = rowNumToShow; }
    public int getPageNumToShow() { return pageNumToShow; }
    public void setPageNumToShow(int pageNumToShow) { this.pageNumToShow = pageNumToShow; }
    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public int getStartPage() { return startPage; }
    public void setStartPage(int startPage) { this.startPage = startPage; }
    public int getEndPage() { return endPage; }
    public void setEndPage(int endPage) { this.endPage = endPage; }
    public int getStartRnumForMysql() { return startRnumForMysql; }
    public void setStartRnumForMysql(int startRnumForMysql) { this.startRnumForMysql = startRnumForMysql; }
    public String getIfbnSeq() { return ifbnSeq; }
    public void setIfbnSeq(String ifbnSeq) { this.ifbnSeq = ifbnSeq; }
    public Integer getShUseNy() { return shUseNy; }
    public void setShUseNy(Integer shUseNy) { this.shUseNy = shUseNy; }
    public Integer getShDelNy() { return shDelNy; }
    public void setShDelNy(Integer shDelNy) { this.shDelNy = shDelNy; }
    public Integer getShOptionDate() { return shOptionDate; }
    public void setShOptionDate(Integer shOptionDate) { this.shOptionDate = shOptionDate; }
    public String getShDateStart() { return shDateStart; }
    public void setShDateStart(String shDateStart) { this.shDateStart = shDateStart; }
    public String getShDateEnd() { return shDateEnd; }
    public void setShDateEnd(String shDateEnd) { this.shDateEnd = shDateEnd; }
    public Integer getShOption() { return shOption; }
    public void setShOption(Integer shOption) { this.shOption = shOption; }
    public Integer getShOption1() { return shOption1; }
    public void setShOption1(Integer shOption1) { this.shOption1 = shOption1; }
    public String getShValue() { return shValue; }
    public void setShValue(String shValue) { this.shValue = shValue; }
    public Integer getMinPrice() { return minPrice; }
    public void setMinPrice(Integer minPrice) { this.minPrice = minPrice; }
    public Integer getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Integer maxPrice) { this.maxPrice = maxPrice; }
    public List<Integer> getRatings() { return ratings; } // 변경됨
    public void setRatings(List<Integer> ratings) { this.ratings = ratings; } // 변경됨
    public String getSortOption() { return sortOption; }
    public void setSortOption(String sortOption) { this.sortOption = sortOption; }
}