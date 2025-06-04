package com.a7a7.modeule.review;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    @Autowired
    private ReviewDao reviewDao;

    public List<ReviewDto> selectReviewsByMealKit(String mealKitSeq) {
        return reviewDao.selectReviewsByMealKit(mealKitSeq);
    }

    @Transactional // 데이터 변경 작업이므로 트랜잭션 처리 권장
    public void insertReview(ReviewDto reviewDto) {
        System.out.println("ReviewService - insertReview 호출 - ReviewDto: " + reviewDto);
        try {
            // 여기서 DTO의 String 타입인 mealKit_seq, userUi_seq 등을
            // DAO에 전달하기 전에 Integer로 변환하거나,
            // DAO의 파라미터 타입을 Integer로 하고 여기서 변환해서 넘겨주거나,
            // MyBatis 매퍼에서 jdbcType을 명시하여 자동 변환에 의존할 수 있습니다.
            // 현재 DTO 필드 타입과 DB 컬럼 타입이 다른 경우를 고려해야 합니다.

            // 예를 들어, mealKit_seq와 userUi_seq를 Integer로 변환해야 한다면:
            // (하지만 DTO 필드를 Integer로 하는 것이 더 나을 수 있음)
            // ReviewDao의 insertReview 파라미터가 ReviewDto를 그대로 받는다면,
            // MyBatis 매퍼에서 jdbcType 명시로 처리하는 것이 일반적입니다.

            reviewDao.insertReview(reviewDto);
            System.out.println("리뷰 삽입 성공 (서비스단): mealKit_seq=" + reviewDto.getMealKit_seq() + ", review_uuid=" + reviewDto.getSeq());
        } catch (Exception e) {
            System.err.println("리뷰 삽입 실패 (서비스단): " + e.getMessage());
            e.printStackTrace(); // 서비스단에서도 예외 로깅
            throw e; // 예외를 다시 던져서 컨트롤러에서 처리하거나, 여기서 사용자 정의 예외로 변환
        }
    }

    // ... (deleteReview, getAverageRating, getReviewCount 메서드) ...
    // deleteReview의 seq 파라미터는 이제 UUID(String)를 의미합니다.
    public int deleteReview(String seq_uuid, String userUiSeqFromSession) { // 파라미터명 변경 (uuid 명시)
        // 1. seq_uuid로 리뷰 정보 조회 (userName은 필요 없음, userUi_seq로 권한 확인)
        ReviewDto review = reviewDao.selectReviewBySeq(seq_uuid); // 이 메서드는 UUID로 조회하도록 수정되어야 함
        if (review != null && review.getUserUi_seq().equals(userUiSeqFromSession)) {
            return reviewDao.deleteReview(seq_uuid); // 이 메서드는 UUID로 삭제하도록 수정되어야 함
        }
        return 0; // 삭제 실패 또는 권한 없음
    }

    // getAverageRating, getReviewCount 등도 mealKitSeq (String)를 받지만,
    // 내부적으로 DB의 INT 타입 mealKit_seq와 비교해야 합니다.

    public Double getAverageRating(String mealKitSeq) {
        List<ReviewDto> reviews = reviewDao.selectReviewsByMealKit(mealKitSeq);
        if (reviews.isEmpty()) return 0.0;
        double sum = reviews.stream().mapToInt(ReviewDto::getRating).sum();
        return sum / reviews.size();
    }

    public int getReviewCount(String mealKitSeq) {
        return reviewDao.selectReviewsByMealKit(mealKitSeq).size();
    }
    
    public int getReviewCountByMealKit(String mealKitSeq) {
        // ReviewDao에 해당 _COUNT 쿼리를 실행하는 메서드 추가 필요
        return reviewDao.selectReviewCountByMealKit(mealKitSeq);
    }
}