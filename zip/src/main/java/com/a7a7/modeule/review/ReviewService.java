package com.a7a7.modeule.review;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    private ReviewDao reviewDao;

    public List<ReviewDto> selectReviewsByMealKit(String mealKitSeq) {
        return reviewDao.selectReviewsByMealKit(mealKitSeq);
    }

    public int insertReview(ReviewDto reviewDto) {
        return reviewDao.insertReview(reviewDto);
    }

    public int deleteReview(String seq, String userUiSeq) {
        ReviewDto review = reviewDao.selectReviewBySeq(seq);
        if (review != null && review.getUserUi_seq().equals(userUiSeq)) {
            return reviewDao.deleteReview(seq);
        }
        return 0;
    }

    public Double getAverageRating(String mealKitSeq) {
        List<ReviewDto> reviews = reviewDao.selectReviewsByMealKit(mealKitSeq);
        if (reviews.isEmpty()) return 0.0;
        double sum = reviews.stream().mapToInt(ReviewDto::getRating).sum();
        return sum / reviews.size();
    }

    public int getReviewCount(String mealKitSeq) {
        return reviewDao.selectReviewsByMealKit(mealKitSeq).size();
    }
}