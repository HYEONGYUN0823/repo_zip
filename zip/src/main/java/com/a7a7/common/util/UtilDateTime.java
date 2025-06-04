package com.a7a7.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
        // 입력된 date가 null이거나 비어있을 경우를 대비한 방어 코드 추가
        if (date == null || date.trim().isEmpty()) {
            return null;
        }
        return date + " 00:00:00";
    }


    public static String add59TimeString(String date) {
        // 입력된 date가 null이거나 비어있을 경우를 대비한 방어 코드 추가
        if (date == null || date.trim().isEmpty()) {
            return null;
        }
        return date + " 23:59:59";
    }

    /**
     * 문자열을 LocalDateTime 객체로 변환합니다.
     * 기본적으로 "yyyy-MM-dd HH:mm:ss" 형식을 가정합니다.
     * 다른 형식이면 이 메소드를 수정하거나, 포맷을 인자로 받는 메소드를 추가해야 합니다.
     *
     * @param dateTimeString 변환할 날짜시간 문자열 (예: "2023-10-27 15:30:00")
     * @return 변환된 LocalDateTime 객체, 파싱 실패 시 null 반환
     */
    public static LocalDateTime stringToDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            System.err.println("날짜/시간 문자열 파싱 오류 (기본포맷 yyyy-MM-dd HH:mm:ss): " + dateTimeString + " - " + e.getMessage());
            try {
                // ISO_LOCAL_DATE_TIME ("yyyy-MM-ddTHH:mm:ss") 형식 시도
                return LocalDateTime.parse(dateTimeString);
            } catch (DateTimeParseException e2) {
                System.err.println("날짜/시간 문자열 파싱 오류 (ISO 형식): " + dateTimeString + " - " + e2.getMessage());
                return null; // 모든 형식 파싱 실패
            }
        }
    }

    /**
     * 지정된 포맷으로 문자열을 LocalDateTime 객체로 변환합니다.
     *
     * @param dateTimeString 변환할 날짜시간 문자열
     * @param formatPattern  날짜시간 문자열의 포맷 (예: "yyyy/MM/dd HH:mm", "dd.MM.yyyy HH:mm:ss")
     * @return 변환된 LocalDateTime 객체, 파싱 실패 시 null 반환
     */
    public static LocalDateTime stringToDateTime(String dateTimeString, String formatPattern) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty() ||
            formatPattern == null || formatPattern.trim().isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
            return LocalDateTime.parse(dateTimeString, formatter);
        } catch (DateTimeParseException e) {
            System.err.println("날짜/시간 문자열 파싱 오류 (포맷: " + formatPattern + "): " + dateTimeString + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * 문자열을 LocalDate 객체로 변환합니다.
     *
     * @param dateString 변환할 날짜 문자열 (예: "2023-10-27")
     * @param formatPattern 날짜 문자열의 포맷 (예: "yyyy-MM-dd", "yyyy/MM/dd")
     * @return 변환된 LocalDate 객체, 파싱 실패 시 null 반환
     */
    public static LocalDate stringToDate(String dateString, String formatPattern) {
        if (dateString == null || dateString.trim().isEmpty() ||
            formatPattern == null || formatPattern.trim().isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            System.err.println("날짜 문자열 파싱 오류 (포맷: " + formatPattern + "): " + dateString + " - " + e.getMessage());
            return null;
        }
    }

    // --- 여기에 새로운 메소드 추가 ---

    /**
     * 입력된 날짜/시간 문자열을 파싱한 후, 지정된 다른 포맷의 문자열로 변환합니다.
     * 내부적으로 {@link #stringToDateTime(String)}을 호출하여 파싱하며,
     * 이 메소드는 "yyyy-MM-dd HH:mm:ss" 또는 ISO 표준 형식을 시도합니다.
     *
     * @param originalDateTimeString 원본 날짜/시간 문자열 (예: "2023-10-27 15:30:00" 또는 "2023-10-27T15:30:00")
     * @param targetFormatPattern    변환하고자 하는 목표 문자열 포맷 (예: "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm")
     * @return 목표 포맷으로 변환된 날짜/시간 문자열. 파싱 실패, 변환 실패, 또는 입력이 부적절하면 null을 반환합니다.
     */
    public static String reformatDateTimeString(String originalDateTimeString, String targetFormatPattern) {
        if (originalDateTimeString == null || originalDateTimeString.trim().isEmpty() ||
            targetFormatPattern == null || targetFormatPattern.trim().isEmpty()) {
            return null;
        }

        // 1. 기존 메소드를 사용하여 문자열을 LocalDateTime으로 파싱
        LocalDateTime parsedLdt = stringToDateTime(originalDateTimeString);

        if (parsedLdt != null) {
            // 2. 파싱된 LocalDateTime 객체를 목표 포맷의 문자열로 변환
            try {
                DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern(targetFormatPattern);
                return parsedLdt.format(targetFormatter);
            } catch (Exception e) { // DateTimeException 외의 IllegalArgumentException 등도 고려
                System.err.println("LocalDateTime을 목표 포맷 문자열로 변환 중 오류 (포맷: " + targetFormatPattern + "): " + parsedLdt + " - " + e.getMessage());
                return null;
            }
        } else {
            // stringToDateTime 내부에서 이미 에러 메시지가 출력되었을 것이므로 여기서는 null만 반환
            return null;
        }
    }

    /**
     * 입력된 날짜/시간 문자열(특정 입력 포맷 사용)을 파싱한 후, 지정된 다른 포맷의 문자열로 변환합니다.
     * 내부적으로 {@link #stringToDateTime(String, String)}을 호출하여 파싱합니다.
     *
     * @param originalDateTimeString 원본 날짜/시간 문자열
     * @param inputFormatPattern     원본 문자열의 포맷 (예: "dd/MM/yyyy HH.mm.ss")
     * @param targetFormatPattern    변환하고자 하는 목표 문자열 포맷 (예: "yyyy-MM-dd HH:mm:ss")
     * @return 목표 포맷으로 변환된 날짜/시간 문자열. 파싱 실패, 변환 실패, 또는 입력이 부적절하면 null을 반환합니다.
     */
    public static String reformatDateTimeString(String originalDateTimeString, String inputFormatPattern, String targetFormatPattern) {
        if (originalDateTimeString == null || originalDateTimeString.trim().isEmpty() ||
            inputFormatPattern == null || inputFormatPattern.trim().isEmpty() ||
            targetFormatPattern == null || targetFormatPattern.trim().isEmpty()) {
            return null;
        }

        // 1. 특정 입력 포맷으로 문자열을 LocalDateTime으로 파싱
        LocalDateTime parsedLdt = stringToDateTime(originalDateTimeString, inputFormatPattern);

        if (parsedLdt != null) {
            // 2. 파싱된 LocalDateTime 객체를 목표 포맷의 문자열로 변환
            try {
                DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern(targetFormatPattern);
                return parsedLdt.format(targetFormatter);
            } catch (Exception e) {
                System.err.println("LocalDateTime을 목표 포맷 문자열로 변환 중 오류 (포맷: " + targetFormatPattern + "): " + parsedLdt + " - " + e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }
}