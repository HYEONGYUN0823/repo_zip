package com.a7a7.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UtilDateTime {
	
//	public static void setSearch(BaseVo vo) {
//		vo.setShDateStart(vo.getShDateStart() == null || vo.getShDateStart() == "" ? null : UtilDateTime.add00TimeString(vo.getShDateStart()));
//		vo.setShDateEnd(vo.getShDateEnd() == null || vo.getShDateEnd() == "" ? null : UtilDateTime.add59TimeString(vo.getShDateEnd()));
//	}
	
	public static String addNowTimeString(String date) {
		LocalDateTime localDateTime = LocalDateTime.now();
		String localDateTimeString = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		
		return date + " " + localDateTimeString;
	}
	
	public static String nowString() {
		LocalDate today = LocalDate.now();
		
		return today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
	
	public static String add00TimeString(String date) {
		return date + " 00:00:00";
	}

	
	public static String add59TimeString(String date) {
		return date + " 23:59:59";
	}
	
}
