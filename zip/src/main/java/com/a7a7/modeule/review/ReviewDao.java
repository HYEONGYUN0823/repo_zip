package com.a7a7.modeule.review;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface ReviewDao {
    // 정렬 및 기본 조회
    List<ReviewDto> selectReviewsByMealKitOrderByRegDateDesc(@Param("mealKitSeq") String mealKitSeq);
    List<ReviewDto> selectReviewsByMealKitOrderByRatingDesc(@Param("mealKitSeq") String mealKitSeq);
    List<ReviewDto> selectReviewsByMealKitOrderByRatingAsc(@Param("mealKitSeq") String mealKitSeq);

    ReviewDto selectReviewBySeq(@Param("reviewUuid") String reviewUuid); // UUID로 조회
    int insertReview(ReviewDto reviewDto);
    int deleteReview(@Param("reviewUuid") String reviewUuid); // UUID로 논리적 삭제 (delNy=1 업데이트)

    // 통계
    int selectReviewCountByMealKit(@Param("mealKitSeq") String mealKitSeq); // 총 리뷰 수 (delNy=0)
    List<Map<String, Object>> getRatingCountsByMealKit(@Param("mealKitSeq") String mealKitSeq); // 별점별 리뷰 수 (delNy=0)

    // mealKit 테이블 score 업데이트
    int updateMealKitScore(@Param("mealKitSeq") String mealKitSeq, @Param("score") Double score);
}