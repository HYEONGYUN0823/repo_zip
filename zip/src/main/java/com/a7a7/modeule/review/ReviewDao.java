package com.a7a7.modeule.review;

import java.util.List;

public interface ReviewDao {
    List<ReviewDto> selectReviewsByMealKit(String mealKitSeq);
    ReviewDto selectReviewBySeq(String seq);
    int insertReview(ReviewDto reviewDto);
    int deleteReview(String seq);
}