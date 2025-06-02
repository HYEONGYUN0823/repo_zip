package com.a7a7.modeule.review;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReviewDao {
    int insertReview(ReviewDto dto);
    List<ReviewDto> findReviewsByMealKitSeq(Map<String, Object> params);
    long countReviewsByMealKitSeq(String mealKitSeq);
    Double getAverageRatingForMealKit(String mealKitSeq);
    List<Map<String, Object>> getRatingCountsForMealKit(String mealKitSeq);
    // 필요한 경우 리뷰 수정, 삭제 메서드 시그니처 추가
}