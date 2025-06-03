package com.a7a7.modeule.review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
    public Map<String, Object> getReviews(@RequestParam("mealKitSeq") String mealKitSeq) {
        Map<String, Object> returnMap = new HashMap<>();
        try {
            List<ReviewDto> reviews = reviewService.selectReviewsByMealKit(mealKitSeq);
            Double averageRating = reviewService.getAverageRating(mealKitSeq);
            int reviewCount = reviewService.getReviewCount(mealKitSeq);
            returnMap.put("rt", "success");
            returnMap.put("reviews", reviews);
            returnMap.put("averageRating", averageRating);
            returnMap.put("reviewCount", reviewCount);
        } catch (Exception e) {
            returnMap.put("rt", "fail");
            returnMap.put("message", "리뷰 조회 중 오류가 발생했습니다.");
        }
        return returnMap;
    }

    // 리뷰 추가 (AJAX)
    @ResponseBody
    @RequestMapping("/addReview")
    public Map<String, Object> addReview(ReviewDto reviewDto, HttpSession httpSession) {
        Map<String, Object> returnMap = new HashMap<>();
        String userUiSeq = (String) httpSession.getAttribute("sessSeqUsr");
        String userName = (String) httpSession.getAttribute("sessNameUsr");

        if (userUiSeq == null) {
            returnMap.put("rt", "fail");
            returnMap.put("message", "로그인이 필요합니다.");
            return returnMap;
        }

        try {
            reviewDto.setSeq(UUID.randomUUID().toString());
            reviewDto.setUserUi_seq(userUiSeq);
            reviewDto.setUserName(userName);
            reviewService.insertReview(reviewDto);
            returnMap.put("rt", "success");
            returnMap.put("message", "리뷰가 성공적으로 추가되었습니다.");
        } catch (Exception e) {
            returnMap.put("rt", "fail");
            returnMap.put("message", "리뷰 추가 중 오류가 발생했습니다.");
        }
        return returnMap;
    }

    // 리뷰 삭제 (AJAX)
    @ResponseBody
    @RequestMapping("/deleteReview")
    public Map<String, Object> deleteReview(@RequestParam("seq") String seq, HttpSession httpSession) {
        Map<String, Object> returnMap = new HashMap<>();
        String userUiSeq = (String) httpSession.getAttribute("sessSeqUsr");

        if (userUiSeq == null) {
            returnMap.put("rt", "fail");
            returnMap.put("message", "로그인이 필요합니다.");
            return returnMap;
        }

        try {
            int result = reviewService.deleteReview(seq, userUiSeq);
            if (result > 0) {
                returnMap.put("rt", "success");
                returnMap.put("message", "리뷰가 삭제되었습니다.");
            } else {
                returnMap.put("rt", "fail");
                returnMap.put("message", "리뷰 삭제 권한이 없거나 리뷰가 존재하지 않습니다.");
            }
        } catch (Exception e) {
            returnMap.put("rt", "fail");
            returnMap.put("message", "리뷰 삭제 중 오류가 발생했습니다.");
        }
        return returnMap;
    }
}