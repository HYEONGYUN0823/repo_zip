package com.a7a7.modeule.product;

// import java.util.List; // 평점을 여러 개 선택할 경우 필요

public class ProductVo {

    private String seq = "0";

    // paging
    private int thisPage = 1;
    private int rowNumToShow = 12; // 기본값을 12로 변경 (목록 페이지와 일치시키기 위해)
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

    // ----- AJAX 필터링 및 정렬을 위한 필드 추가 -----
    private Integer minPrice;
    private Integer maxPrice;
    private Integer rating; // 단일 평점 값 (X점 이상)
    // private List<Integer> selectedRatings; // 여러 평점 동시 선택 시 사용
    private String sortOption = "latest"; // 기본 정렬: 최신순

    // -----

    public void setParamsPaging(int totalRows) {
        setTotalRows(totalRows);

        if (getTotalRows() == 0) {
            setTotalPages(0); // 총 페이지도 0으로
            setStartPage(1);
            setEndPage(1); // 또는 0
            setThisPage(1); // 현재 페이지도 1
        } else {
            setTotalPages((getTotalRows() - 1) / getRowNumToShow() + 1); // 올바른 페이지 수 계산
            if (getThisPage() > getTotalPages() && getTotalPages() > 0) { // 총 페이지가 0보다 클때만
                setThisPage(getTotalPages());
            }
            if (getThisPage() < 1) {
                setThisPage(1);
            }
            setStartPage(((getThisPage() - 1) / getPageNumToShow()) * getPageNumToShow() + 1);
            setEndPage(Math.min(getStartPage() + getPageNumToShow() - 1, getTotalPages()));
        }
        setStartRnumForMysql((getRowNumToShow() * (getThisPage() - 1)));
    }

    // Getters and Setters for ALL fields (기존 필드 포함)
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

    // --- 추가된 필드 Getter/Setter ---
    public Integer getMinPrice() { return minPrice; }
    public void setMinPrice(Integer minPrice) { this.minPrice = minPrice; }
    public Integer getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Integer maxPrice) { this.maxPrice = maxPrice; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getSortOption() { return sortOption; }
    public void setSortOption(String sortOption) { this.sortOption = sortOption; }
}