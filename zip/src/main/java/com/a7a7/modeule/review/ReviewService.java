package com.a7a7.modeule.review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    @Autowired
    private ReviewDao reviewDao;

    // 정렬 기능이 추가된 메서드
    public List<ReviewDto> selectReviewsByMealKitAndSort(String mealKitSeq, String sortBy) {
        List<ReviewDto> reviews;
        if ("highest_rating".equals(sortBy)) {
            reviews = reviewDao.selectReviewsByMealKitOrderByRatingDesc(mealKitSeq);
        } else if ("lowest_rating".equals(sortBy)) {
            reviews = reviewDao.selectReviewsByMealKitOrderByRatingAsc(mealKitSeq);
        } else { // "latest" 또는 기본값
            reviews = reviewDao.selectReviewsByMealKitOrderByRegDateDesc(mealKitSeq);
        }
        return reviews;
    }

    @Transactional
    public void insertReview(ReviewDto reviewDto) {
        try {
            reviewDao.insertReview(reviewDto);
            updateMealKitScore(reviewDto.getMealKit_seq()); // mealKit의 score 업데이트
            System.out.println("리뷰 삽입 및 밀키트 점수 업데이트 성공: mealKit_seq=" + reviewDto.getMealKit_seq() + ", review_uuid=" + reviewDto.getSeq());
        } catch (Exception e) {
            System.err.println("리뷰 삽입 또는 밀키트 점수 업데이트 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

 // ReviewService.java - 해결 방법 1 적용 시
    @Transactional
    public int deleteReview(String reviewUuid, String userUiSeqFromSession) {
        ReviewDto reviewToDelete = reviewDao.selectReviewBySeq(reviewUuid); // selectReviewBySeq는 delNy 포함해서 조회

        if (reviewToDelete == null) {
            System.out.println("삭제할 리뷰를 찾을 수 없습니다. UUID: " + reviewUuid);
            return 0;
        }

        // DTO의 delNy가 Integer이므로 null 체크 가능
        if (reviewToDelete.getDelNy() != null && reviewToDelete.getDelNy() == 1) {
             System.out.println("이미 삭제된 리뷰입니다. UUID: " + reviewUuid);
             return 0; // 이미 삭제됨
        }

        if (!reviewToDelete.getUserUi_seq().equals(userUiSeqFromSession)) {
            System.out.println("리뷰 삭제 권한이 없습니다. 리뷰 작성자: " + reviewToDelete.getUserUi_seq() + ", 요청자: " + userUiSeqFromSession);
            return -1;
        }

        int updateResult = reviewDao.deleteReview(reviewUuid);

        if (updateResult > 0) {
            System.out.println("리뷰가 성공적으로 논리 삭제되었습니다. UUID: " + reviewUuid);
            updateMealKitScore(reviewToDelete.getMealKit_seq());
        } else {
            System.out.println("리뷰 논리 삭제에 실패했습니다. UUID: " + reviewUuid);
        }
        return updateResult;
    }

    // mealKit 테이블의 score를 업데이트하는 메서드
    @Transactional
    public void updateMealKitScore(String mealKitSeq) {
        Double averageRating = getAverageRating(mealKitSeq);
        double scoreToUpdate = (averageRating != null) ? averageRating : 0.0;

        reviewDao.updateMealKitScore(mealKitSeq, scoreToUpdate);
        System.out.println("밀키트 ID " + mealKitSeq + "의 점수가 " + scoreToUpdate + "으로 업데이트되었습니다.");
    }

    // 평균 평점 계산
    public Double getAverageRating(String mealKitSeq) {
        // DAO의 selectReviewsByMealKitOrderByRegDateDesc는 delNy=0 인 것만 가져옴
        List<ReviewDto> reviews = reviewDao.selectReviewsByMealKitOrderByRegDateDesc(mealKitSeq);
        if (reviews == null || reviews.isEmpty()) {
            return null; // 리뷰가 없으면 null 반환 -> updateMealKitScore에서 0.0으로 처리
        }
        double sum = 0;
        for (ReviewDto review : reviews) {
            sum += review.getRating();
        }
        return sum / reviews.size();
    }

    // 리뷰 개수
    public int getReviewCount(String mealKitSeq) {
        return reviewDao.selectReviewCountByMealKit(mealKitSeq); // delNy=0 인 것만 카운트
    }

    // 별점 분포
    public Map<String, Integer> getRatingDistribution(String mealKitSeq) {
        List<Map<String, Object>> rawCounts = reviewDao.getRatingCountsByMealKit(mealKitSeq); // delNy=0 인 것만
        Map<String, Integer> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(String.valueOf(i), 0);
        }
        for (Map<String, Object> row : rawCounts) {
            Object ratingObj = row.get("rating");
            Object countObj = row.get("count");
            if (ratingObj != null && countObj != null) {
                String ratingKey = String.valueOf(((Number) ratingObj).intValue());
                Integer countValue = ((Number) countObj).intValue();
                distribution.put(ratingKey, countValue);
            }
        }
        return distribution;
    }
}