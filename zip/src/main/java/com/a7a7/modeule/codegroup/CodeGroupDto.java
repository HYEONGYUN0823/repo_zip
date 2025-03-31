package com.a7a7.modeule.codegroup;

public class CodeGroupDto {
	
	private String ifcgSeq;			// 시퀀스
	private String ifcgName;		// 코드그룹 이름
	private String ifcgDelNy;		// 사용여부
	private String ifcgUseNy;
	private String ifcgRegDateTime;
	private String ifcgModDateTime;

	private String xifcdSeqCount;	// 갯수
	
//	-----
	
	public String getIfcgSeq() {
		return ifcgSeq;
	}
	
	public void setIfcgSeq(String ifcgSeq) {
		this.ifcgSeq = ifcgSeq;
	}
	
	public String getIfcgName() {
		return ifcgName;
	}
	
	public void setIfcgName(String ifcgName) {
		this.ifcgName = ifcgName;
	}
	
	public String getIfcgDelNy() {
		return ifcgDelNy;
	}
	
	public String getIfcgUseNy() {
		return ifcgUseNy;
	}

	public void setIfcgUseNy(String ifcgUseNy) {
		this.ifcgUseNy = ifcgUseNy;
	}

	public void setIfcgDelNy(String ifcgDelNy) {
		this.ifcgDelNy = ifcgDelNy;
	}
	
	public String getXifcdSeqCount() {
		return xifcdSeqCount;
	}
	
	public void setXifcdSeqCount(String xifcdSeqCount) {
		this.xifcdSeqCount = xifcdSeqCount;
	}

	public String getIfcgRegDateTime() {
		return ifcgRegDateTime;
	}

	public void setIfcgRegDateTime(String ifcgRegDateTime) {
		this.ifcgRegDateTime = ifcgRegDateTime;
	}

	public String getIfcgModDateTime() {
		return ifcgModDateTime;
	}

	public void setIfcgModDateTime(String ifcgModDateTime) {
		this.ifcgModDateTime = ifcgModDateTime;
	}
	
	
	
}
