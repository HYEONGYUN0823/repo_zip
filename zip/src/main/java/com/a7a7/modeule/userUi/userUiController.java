package com.a7a7.modeule.userUi;

import com.a7a7.modeule.product.ProductDto;
import com.a7a7.modeule.product.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping; // GetMapping 사용 권장

import java.util.ArrayList;
import java.util.List;

@Controller
public class userUiController {
    
    @Autowired
    private ProductService productService;

    // @Autowired
    // private CartService cartService; // 장바구니 개수 표시용

    @GetMapping({"/", "/usr/index/Index"}) // @RequestMapping(value=...) 대신 @GetMapping 사용 권장
    public String userIndex(Model model, HttpSession session) {
        
        try {
            List<ProductDto> popularProducts = productService.getPopularProducts();
            model.addAttribute("popularProducts", popularProducts != null ? popularProducts : new ArrayList<>());
            System.out.println("### userUiController.userIndex: 인기 상품 " + (popularProducts != null ? popularProducts.size() : 0) + "개 조회됨.");
        } catch (Exception e) {
            System.err.println("### userUiController.userIndex: 인기 상품 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("popularProducts", new ArrayList<>());
        }

        // 장바구니 개수 로직 (필요시 CartService 주입 후 사용)
        // long cartItemCount = 0;
        // String userUiSeq = (String) session.getAttribute("sessSeqUsr");
        // if (userUiSeq != null && cartService != null) {
        //     cartItemCount = cartService.getCartItemCount(session);
        // }
        // model.addAttribute("cartItemCount", cartItemCount);
        
        return "usr/index/Index";
    }
}