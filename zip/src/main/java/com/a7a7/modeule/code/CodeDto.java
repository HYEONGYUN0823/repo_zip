package com.a7a7.modeule.code;

import java.util.ArrayList;
import java.util.List;

public class CodeDto {

//	for cache
	public static List<CodeDto> cachedCodeArrayList = new ArrayList<CodeDto>();
	
	private String ifcdSeq;		// 코드 시퀀스
	private String ifcdName;	// 코드 이름
	private String ifcdDelNy;	// 코드 사용여부
	private String ifcdUseNy;
	private String ifcdRegDateTime;
	private String ifcdModDateTime;
	
//	코드그룹
	private String ifcgSeq;		// 코드그룹 시퀀스
	private String ifcgName;	// 코드그룹 이름
	
//	-----
	
	public String getIfcdSeq() {
		return ifcdSeq;
	}
	
	public void setIfcdSeq(String ifcdSeq) {
		this.ifcdSeq = ifcdSeq;
	}
	
	public String getIfcdName() {
		return ifcdName;
	}
	
	public void setIfcdName(String ifcdName) {
		this.ifcdName = ifcdName;
	}
	
	public String getIfcdDelNy() {
		return ifcdDelNy;
	}
	
	public void setIfcdDelNy(String ifcdDelNy) {
		this.ifcdDelNy = ifcdDelNy;
	}
	
	public String getIfcdUseNy() {
		return ifcdUseNy;
	}

	public void setIfcdUseNy(String ifcdUseNy) {
		this.ifcdUseNy = ifcdUseNy;
	}

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

	public String getIfcdRegDateTime() {
		return ifcdRegDateTime;
	}

	public void setIfcdRegDateTime(String ifcdRegDateTime) {
		this.ifcdRegDateTime = ifcdRegDateTime;
	}

	public String getIfcdModDateTime() {
		return ifcdModDateTime;
	}

	public void setIfcdModDateTime(String ifcdModDateTime) {
		this.ifcdModDateTime = ifcdModDateTime;
	}
	
}
