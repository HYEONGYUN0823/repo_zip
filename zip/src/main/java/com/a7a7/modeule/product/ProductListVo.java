package com.a7a7.modeule.product;

public class ProductListVo {
	
	private String seq = "0";
	
//	paging
	private int thisPage = 1;									// 현재 페이지
	private int rowNumToShow = 20;								// 화면에 보여줄 데이터 줄 갯수
	private int pageNumToShow = 5;								// 화면에 보여줄 페이징 번호 갯수

	private int totalRows;										// 전체 데이터 갯수
	private int totalPages;										// 전체 페이지 번호
	private int startPage;										// 시작 페이지 번호
	private int endPage;										// 마지막 페이지 번호

	private int startRnumForMysql = 0;							// 쿼리 시작 row
	
	private String ifbnSeq;
	

//	search
	private Integer shUseNy = 1; 								/* null 값을 받아야 되는 경우가 있어서 int 대신 Integer 사용 */
	private Integer shDelNy = 0; 								/* null 값을 받아야 되는 경우가 있어서 int 대신 Integer 사용 */
	private Integer shOptionDate = 0;							/* null 값을 받아야 되는 경우가 있어서 int 대신 Integer 사용 */
	private String shDateStart;
	private String shDateEnd;
	private Integer shOption;									/* null 값을 받아야 되는 경우가 있어서 int 대신 Integer 사용 */
	private Integer shOption1;
	private String shValue;
	
//	-----

	public void setParamsPaging(int totalRows) {
//		setThisPage(3);
		
		setTotalRows(totalRows);
		
		if (getTotalRows() == 0) {
			setTotalPages(1);
		} else {
			setTotalPages(getTotalRows() / getRowNumToShow());
		}
		
		if (getTotalRows() % getRowNumToShow() > 0) {
			setTotalPages(getTotalPages() + 1);
		}
		
		if (getTotalPages() < getThisPage()) {
			setThisPage(getTotalPages());
		}
		
		setStartPage(((getThisPage() - 1) / getPageNumToShow()) * getPageNumToShow() + 1);
		
		setEndPage(getStartPage() + getPageNumToShow() - 1);
		
		if (getEndPage() > getTotalPages()) {
			setEndPage(getTotalPages());
		}
		
		if (thisPage == 1) {
			setStartRnumForMysql(0);
		} else {
			setStartRnumForMysql((getRowNumToShow() * (getThisPage()-1)));
		}
		
//		System.out.println("getThisPage():" + getThisPage());
//		System.out.println("getTotalRows():" + getTotalRows());
//		System.out.println("getRowNumToShow():" + getRowNumToShow());
//		System.out.println("getTotalPages():" + getTotalPages());
//		System.out.println("getStartPage():" + getStartPage());
//		System.out.println("getEndPage():" + getEndPage());
//		System.out.println("getStartRnumForMysql(): " + getStartRnumForMysql());
		
	}
	
	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public int getThisPage() {
		return thisPage;
	}

	public void setThisPage(int thisPage) {
		this.thisPage = thisPage;
	}

	public int getRowNumToShow() {
		return rowNumToShow;
	}

	public void setRowNumToShow(int rowNumToShow) {
		this.rowNumToShow = rowNumToShow;
	}

	public int getPageNumToShow() {
		return pageNumToShow;
	}

	public void setPageNumToShow(int pageNumToShow) {
		this.pageNumToShow = pageNumToShow;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public int getStartRnumForMysql() {
		return startRnumForMysql;
	}

	public void setStartRnumForMysql(int startRnumForMysql) {
		this.startRnumForMysql = startRnumForMysql;
	}


	public String getIfbnSeq() {
		return ifbnSeq;
	}


	public void setIfbnSeq(String ifbnSeq) {
		this.ifbnSeq = ifbnSeq;
	}


	public Integer getShUseNy() {
		return shUseNy;
	}


	public void setShUseNy(Integer shUseNy) {
		this.shUseNy = shUseNy;
	}


	public Integer getShDelNy() {
		return shDelNy;
	}


	public void setShDelNy(Integer shDelNy) {
		this.shDelNy = shDelNy;
	}


	public Integer getShOptionDate() {
		return shOptionDate;
	}


	public void setShOptionDate(Integer shOptionDate) {
		this.shOptionDate = shOptionDate;
	}


	public String getShDateStart() {
		return shDateStart;
	}


	public void setShDateStart(String shDateStart) {
		this.shDateStart = shDateStart;
	}


	public String getShDateEnd() {
		return shDateEnd;
	}


	public void setShDateEnd(String shDateEnd) {
		this.shDateEnd = shDateEnd;
	}


	public Integer getShOption() {
		return shOption;
	}


	public void setShOption(Integer shOption) {
		this.shOption = shOption;
	}


	public String getShValue() {
		return shValue;
	}


	public void setShValue(String shValue) {
		this.shValue = shValue;
	}

	public Integer getShOption1() {
		return shOption1;
	}

	public void setShOption1(Integer shOption1) {
		this.shOption1 = shOption1;
	}
	
	
}
