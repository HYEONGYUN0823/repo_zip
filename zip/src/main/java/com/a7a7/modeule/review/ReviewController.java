package com.a7a7.modeule.review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usr/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // 리뷰 목록 조회 (AJAX)
    @ResponseBody
    @RequestMapping("/getReviews")
    public Map<String, Object> getReviews(@RequestParam("mealKitSeq") String mealKitSeq,
                                          @RequestParam(value = "sortBy", defaultValue = "latest") String sortBy) {
        Map<String, Object> returnMap = new HashMap<>();
        try {
            List<ReviewDto> reviews = reviewService.selectReviewsByMealKitAndSort(mealKitSeq, sortBy);
            Double averageRating = reviewService.getAverageRating(mealKitSeq); // 서비스에서 null 또는 값 반환
            int reviewCount = reviewService.getReviewCount(mealKitSeq);
            Map<String, Integer> ratingDistribution = reviewService.getRatingDistribution(mealKitSeq);

            returnMap.put("rt", "success");
            returnMap.put("reviews", reviews);
            returnMap.put("averageRating", averageRating == null ? 0.0 : averageRating); // null이면 0.0으로
            returnMap.put("reviewCount", reviewCount);
            returnMap.put("ratingDistribution", ratingDistribution);
        } catch (Exception e) {
            e.printStackTrace();
            returnMap.put("rt", "fail");
            returnMap.put("message", "리뷰 조회 중 오류가 발생했습니다.");
        }
        return returnMap;
    }

    // 리뷰 추가 (AJAX)
    @ResponseBody
    @RequestMapping("/addReview")
    public Map<String, Object> addReview(@ModelAttribute ReviewDto reviewDto, HttpSession httpSession) {
        System.out.println("===================================================");
        System.out.println("ReviewController - addReview 메서드 진입");
        System.out.println("요청으로 받은 ReviewDto (ModelAttribute 바인딩 후): " + reviewDto);
        System.out.println("세션 ID: " + httpSession.getId());
        System.out.println("세션 sessSeqUsr: " + httpSession.getAttribute("sessSeqUsr"));
        System.out.println("세션 sessNameUsr: " + httpSession.getAttribute("sessNameUsr"));
        System.out.println("===================================================");

        Map<String, Object> returnMap = new HashMap<>();
        String userUiSeqFromSession = (String) httpSession.getAttribute("sessSeqUsr");
        String userNameFromSession = (String) httpSession.getAttribute("sessNameUsr");

        if (userUiSeqFromSession == null) {
            returnMap.put("rt", "fail");
            returnMap.put("message", "로그인이 필요합니다. 세션이 만료되었거나 로그인하지 않았습니다.");
            System.out.println("리뷰 등록 실패: 로그인 필요 (userUiSeqFromSession is null)");
            return returnMap;
        }

        try {
            reviewDto.setSeq(UUID.randomUUID().toString());
            reviewDto.setUserUi_seq(userUiSeqFromSession);
            reviewDto.setUserName(userNameFromSession);

            if (reviewDto.getMealKit_seq() == null || reviewDto.getMealKit_seq().isEmpty()) {
                System.out.println("리뷰 등록 실패: mealKit_seq 누락");
                returnMap.put("rt", "fail");
                returnMap.put("message", "필수 정보(상품 ID)가 누락되었습니다.");
                return returnMap;
            }
            if (reviewDto.getReviewTitle() == null || reviewDto.getReviewTitle().trim().isEmpty()) {
                 System.out.println("리뷰 등록 실패: reviewTitle 누락");
                 returnMap.put("rt", "fail");
                 returnMap.put("message", "리뷰 제목을 입력해주세요.");
                 return returnMap;
            }
            if (reviewDto.getReviewContent() == null || reviewDto.getReviewContent().trim().isEmpty()) {
                 System.out.println("리뷰 등록 실패: reviewContent 누락");
                 returnMap.put("rt", "fail");
                 returnMap.put("message", "리뷰 내용을 입력해주세요.");
                 return returnMap;
            }

            System.out.println("DB 삽입 전 최종 ReviewDto: " + reviewDto);
            reviewService.insertReview(reviewDto);

            returnMap.put("rt", "success");
            returnMap.put("message", "리뷰가 성공적으로 추가되었습니다.");
            System.out.println("리뷰 등록 성공, UUID: " + reviewDto.getSeq());

        } catch (Exception e) {
            e.printStackTrace();
            returnMap.put("rt", "fail");
            returnMap.put("message", "리뷰 추가 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            System.out.println("리뷰 등록 실패 (Exception): " + e.getMessage());
        }
        return returnMap;
    }

    // 리뷰 삭제 (AJAX)
    @ResponseBody
    @RequestMapping("/deleteReview")
    public Map<String, Object> deleteReview(@RequestParam("seq") String reviewUuid, HttpSession httpSession) { // 파라미터명 명확히
        Map<String, Object> returnMap = new HashMap<>();
        String userUiSeq = (String) httpSession.getAttribute("sessSeqUsr");

        if (userUiSeq == null) {
            returnMap.put("rt", "fail");
            returnMap.put("message", "로그인이 필요합니다.");
            return returnMap;
        }

        try {
            int result = reviewService.deleteReview(reviewUuid, userUiSeq);
            if (result > 0) {
                returnMap.put("rt", "success");
                returnMap.put("message", "리뷰가 삭제되었습니다.");
            } else if (result == -1) {
                returnMap.put("rt", "fail");
                returnMap.put("message", "리뷰 삭제 권한이 없습니다.");
            } else {
                returnMap.put("rt", "fail");
                returnMap.put("message", "삭제할 리뷰를 찾을 수 없거나 이미 삭제된 리뷰입니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnMap.put("rt", "fail");
            returnMap.put("message", "리뷰 삭제 중 오류가 발생했습니다.");
        }
        return returnMap;
    }

    @ResponseBody
    @RequestMapping("/checkSession")
    public Map<String, Object> checkSession(HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        map.put("sessSeqUsr", session.getAttribute("sessSeqUsr"));
        map.put("sessNameUsr", session.getAttribute("sessNameUsr"));
        return map;
    }
}