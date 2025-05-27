package com.a7a7.modeule.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.a7a7.common.mail.MailService;
import com.a7a7.common.util.UtilDateTime;
import com.a7a7.modeule.code.CodeService;
import com.a7a7.modeule.order.OrderDto;
import com.a7a7.modeule.order.OrderService;

import jakarta.servlet.http.HttpSession;


@Controller
public class MemberController {
	@Autowired
	MemberService memberService;
	@Autowired
	CodeService codeService;
	@Autowired
	MailService mailService;
	@Autowired
	OrderService orderService;
    
    ////////////////////////////////////////////////////////////////////////////
    ///
	
	public String encodeBcrypt(String planeText, int strength) {
		  return new BCryptPasswordEncoder(strength).encode(planeText);
	}

			
	public boolean matchesBcrypt(String planeText, String hashValue, int strength) {
	  BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(strength);
	  return passwordEncoder.matches(planeText, hashValue);
	}
	
	private void setSearch(MemberVo vo) {
		vo.setShDateStart(vo.getShDateStart() == null || vo.getShDateStart() == "" ? null : UtilDateTime.add00TimeString(vo.getShDateStart()));
		vo.setShDateEnd(vo.getShDateEnd() == null || vo.getShDateEnd() == "" ? null : UtilDateTime.add59TimeString(vo.getShDateEnd()));
	}
	
	
	/************************************************
	 					로그인 관련
	 ************************************************/
	@ResponseBody
	@RequestMapping(value = "/xdm/signin/signinXdmProc")
	public Map<String, Object> signinXdmProc(MemberDto memberDto, HttpSession httpSession) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		MemberDto reMember = memberService.selectOneLogin(memberDto);
		
		if(reMember != null)	{
			returnMap.put("rt", "success");
			
			httpSession.setAttribute("sessSeqXdm", reMember.getSeq());
			httpSession.setAttribute("sessIdXdm", reMember.getiD());
			httpSession.setAttribute("sessPassWordXdm", reMember.getPassWord());
			httpSession.setAttribute("sessNameXdm", reMember.getName());
		} else {
			returnMap.put("rt", "fail");
		}
	    	
		
	    
		return returnMap;
	}
	
	@ResponseBody
	@RequestMapping(value = "/xdm/signin/signoutXdmProc")
	public Map<String, Object> signoutXdmProc(MemberDto memberDto, HttpSession httpSession) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		httpSession.setAttribute("sessSeqXdm", null);
		httpSession.setAttribute("sessIdXdm", null);
		httpSession.setAttribute("sessPasswordXdm", null);
		returnMap.put("rt", "success");
		return returnMap;
	}
	
	@RequestMapping(value = "/xdm/signin/signinXdm")
	public String memberXdmList(HttpSession httpSession, MemberDto memberDto) throws Exception {
		
		return "xdm/signin/SigninXdm";
	}

	
	@ResponseBody
	@RequestMapping("/usr/userUi/checkId")
	public Map<String, Object> checkId(MemberDto memberDto) {
		int count = memberService.checkId(memberDto); // memberDto에 iD가 담겨 있음

		Map<String, Object> result = new HashMap<>();
		result.put("exists", count > 0); // 이미 존재하면 true, 아니면 false

		return result;
	}
	
	@RequestMapping(value = "/usr/signin/signinUsr")
	public String memberUsrList(HttpSession httpSession, MemberDto memberDto) throws Exception {
		
		return "usr/signin/SigninUsr";
	}
	
	@ResponseBody
	@RequestMapping(value = "/usr/signin/signinUsrProc")
	public Map<String, Object> signinUsrProc(MemberDto memberDto, HttpSession httpSession) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		MemberDto reMember = memberService.selectOneLogin(memberDto);
		
		if (reMember == null) {
		    returnMap.put("rt", "fail");
		    // returnMap.put("msg", "아이디 또는 비밀번호가 일치하지 않습니다.");
		    return returnMap;
		}
		
		if ("1".equals(reMember.getDelNy())) {
		    returnMap.put("rt", "fail");
//		    returnMap.put("msg", "탈퇴한 계정입니다.");
		    return returnMap;
		}
		
		System.out.println("delNy 값: " + reMember.getDelNy());
		
		boolean check = matchesBcrypt(memberDto.getPassWord(), reMember.getPassWord(), 10);
		
		if(reMember != null && check)	{
			returnMap.put("rt", "success");
			
			httpSession.setAttribute("sessSeqUsr", reMember.getSeq());
			httpSession.setAttribute("sessIdUsr", reMember.getiD());
			httpSession.setAttribute("sessPassWordUsr", reMember.getPassWord());
			httpSession.setAttribute("sessNameUsr", reMember.getName());
			httpSession.setAttribute("sessEmailUsr", reMember.getEmail());
		    httpSession.setAttribute("sessPhoneUsr", reMember.getPhoneNumber());
		} else {
			returnMap.put("rt", "fail");
		}
		
		return returnMap;
	}
	
	@ResponseBody
	@RequestMapping(value = "/usr/signin/signoutUsrProc")
	public Map<String, Object> signoutUsrProc(MemberDto memberDto, HttpSession httpSession) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		httpSession.setAttribute("sessSeqUsr", null);
		httpSession.setAttribute("sessIdUsr", null);
		httpSession.setAttribute("sessPasswordUsr", null);
		returnMap.put("rt", "success");
		return returnMap;
	}
	
	/************************************************
						회원 관련
	 ************************************************/
	@RequestMapping(value = "/xdm/member/MemberXdmList")
	public String memberXdmList(@ModelAttribute("vo") MemberVo vo, Model model, HttpSession httpSession, MemberDto memberDto) throws Exception {
		
		setSearch(vo);
	    vo.setParamsPaging(memberService.selectOneCount(vo));

	    if (vo.getTotalRows() > 0) {
	        model.addAttribute("list", memberService.selectList(vo));
	        model.addAttribute("memberDto", memberDto); // MemberDto 값도 유지
	    }
	    System.out.println("shAdmSignin: " + vo.getShAdmSignin());
	    
//	    model.addAttribute("vo", vo); // MemberVo 값 유지
	    
		return "xdm/member/MemberXdmList";
	}
	
	@RequestMapping(value = "/xdm/member/MemberXdmForm")
	public String memberXdmForm(@ModelAttribute("vo") MemberVo vo, MemberDto memberDto, Model model) throws Exception {
		
		if (vo.getSeq().equals("0") || vo.getSeq().equals("")) {
//			insert mode
		} else {
//			update mode
			model.addAttribute("item", memberService.selectOne(memberDto));
		}
		System.out.println("vo.getSeq() = " + vo.getSeq());
		return "xdm/member/MemberXdmForm";
	}
	
	@RequestMapping(value = "/xdm/member/MemberXdmInst")
	public String memberXdmInst(MemberDto memberDto) {
		memberService.insert(memberDto);
		
		return "redirect:/xdm/member/MemberXdmList";
	}
	
	@RequestMapping(value = "/xdm/member/MemberXdmUpdt")
	public String memberXdmUpdt(MemberDto memberDto) {
		memberService.update(memberDto);
		
		return "redirect:/xdm/member/MemberXdmList";
	}
	
	@RequestMapping(value = "/xdm/member/MemberXdmUele")
	public String memberXdmUele(MemberDto memberDto) {
		memberService.uelete(memberDto);
		
		return "redirect:/xdm/member/MemberXdmList";
	}
	
	@RequestMapping(value = "/usr/userUi/MemberUsrForm")
	public String userUiSignup(@ModelAttribute("vo") MemberVo vo, MemberDto memberDto, Model model, HttpSession httpSession) throws Exception {
	    if(vo.getSeq() == null) {
	        vo.setSeq("0");
	    }

	    if (vo.getSeq().equals("0") || vo.getSeq().equals("")) {
	        // insert mode
	    } else {
	        // update mode
	        model.addAttribute("item", memberService.selectOne(memberDto));

	        // 세션에 사용자 정보 반영
	        String sessSeqUsr = (String) httpSession.getAttribute("sessSeqUsr");
	        if (sessSeqUsr != null) {
	            // 이미 세션에 값이 존재한다면, 해당 정보를 가져와서 세션에 반영
	            MemberDto user = memberService.selectOne(memberDto);
	            httpSession.setAttribute("sessNameUsr", user.getName());
	            httpSession.setAttribute("sessIdUsr", user.getiD());
	            httpSession.setAttribute("sessPassWordUsr", user.getPassWord());
	        }
	    }
	    System.out.println("vo.getSeq() = " + vo.getSeq());
	    return "usr/userUi/MemberUsrForm";
	}
	
	@RequestMapping(value = "/usr/userUi/MemberUsrInst")
	public String memberUsrInst(MemberDto memberDto) throws Exception {
		memberDto.setPassWord(encodeBcrypt(memberDto.getPassWord(), 10));
		
		int count = memberService.insert(memberDto);
		
		if (count == 1) {
			new Thread() {
				public void run() {
					try {
						mailService.sendMailWelcome(memberDto);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
		
		return "redirect:/usr/signin/signinUsr";
	}
	
    @RequestMapping(value = "/usr/setting/AccountOrdersUsrList")
    public String accountOrdersUsrList(HttpSession session, Model model) {

        String userUiSeq = (String) session.getAttribute("sessSeqUsr"); // 인터셉터가 처리했다면 null이 아니어야 함
        System.out.println("### MemberController - 조회할 사용자 userUiSeq: " + userUiSeq);

        if (userUiSeq == null) { // 만약을 위한 방어 코드 (인터셉터가 확실하다면 생략 가능)
            System.err.println("### MemberController - userUiSeq가 null입니다. 로그인 상태를 확인해주세요.");
            // return "redirect:/usr/member/login"; // 로그인 페이지로 리다이렉트
            // 또는 빈 리스트를 전달하여 "주문 내역 없음"을 표시하도록 할 수도 있습니다.
            model.addAttribute("orderList", new ArrayList<OrderDto>());
            return "usr/setting/AccountOrders";
        }

        List<OrderDto> orderList = orderService.getOrdersByUserSeq(userUiSeq);
        model.addAttribute("orderList", orderList); // 모델에 항상 추가 (null이거나 비어있을 수 있음)

        if (orderList == null) {
            System.out.println("### MemberController - orderService.getOrdersByUserSeq()가 null을 반환했습니다.");
        } else {
            System.out.println("### MemberController - 조회된 주문 목록 개수: " + orderList.size());
            if (orderList.isEmpty()) {
                System.out.println("### MemberController - 해당 사용자의 주문 내역이 없습니다.");
            } else {
                System.out.println("### MemberController - 조회된 주문 상세 내역:");
                int itemCount = 0;
                for (OrderDto order : orderList) {
                    itemCount++;
                    System.out.println("  --- 주문 아이템 " + itemCount + " ---");
                    System.out.println("    OrderDto.seq (주문 PK 또는 주문아이템 PK?): " + order.getSeq()); // XML의 order_seq_alias
                    System.out.println("    OrderDto.productName: " + order.getProductName());             // XML의 product_name_alias
                    System.out.println("    OrderDto.orderDate: " + order.getOrderDate());                 // XML의 order_date_alias
                    System.out.println("    OrderDto.quantity: " + order.getQuantity());                   // XML의 item_quantity_alias
                    System.out.println("    OrderDto.price (주문아이템 단가): " + order.getPrice());           // XML의 item_price_alias
                    System.out.println("    OrderDto.mealKitSeq (상품 PK): " + order.getMealKitSeq());       // XML의 meal_kit_seq_alias
                    System.out.println("    OrderDto.orderId (Toss 주문 ID): " + order.getOrderId());     // XML의 toss_order_id_alias
                    System.out.println("    OrderDto.total_price (주문 총액): " + order.getTotal_price()); // XML의 total_price_alias
                    System.out.println("    OrderDto.status (주문 상태): " + order.getStatus());             // XML의 status_alias
                    // HTML에서 사용하려는 추가 필드가 있다면 여기서 로깅
                    // System.out.println("    OrderDto.productImageUrl: " + order.getProductImageUrl());
                    // System.out.println("    OrderDto.productSpec: " + order.getProductSpec());
                }
            }
        }
        return "usr/setting/AccountOrders";
    }
	
	
	@RequestMapping(value = "/usr/setting/userUiUsrSettings")
	public String memberUsrSettings() {
		return "usr/setting/userUiSettings";
	}
	
	@RequestMapping(value = "/usr/setting/ChangeNewPasswordUsrForm")
	public String changeNewPasswordUsrForm() {
		return "usr/setting/ChangeNewPasswordUsrForm";
	}
	
	@RequestMapping(value = "/usr/setting/ChangePasswordUsrForm")
	public String changePasswordUsrForm(HttpSession httpSession) {
		Boolean verified = (Boolean) httpSession.getAttribute("passwordVerified");

		if (verified == null || !verified) {
		    return "redirect:/usr/setting/ChangeNewPasswordUsrForm";
		}

		httpSession.removeAttribute("passwordVerified"); // ✅ 비밀번호 확인 재사용 방지

	    return "usr/setting/ChangePasswordUsrForm";
	}
	
	@RequestMapping(value = "/usr/setting/AcountUsrDelete")
	public String acountDelete() {
		return "usr/setting/AcountDelete";
	}
	
	@RequestMapping(value = "/usr/setting/AcoutUele")
	public String acountUele(MemberDto memberDto, HttpSession httpSession) {

	    // 세션에서 사용자 seq 가져오기
	    String sessSeqUsr = (String) httpSession.getAttribute("sessSeqUsr");

	    // 로그인한 사용자의 seq 설정
	    memberDto.setSeq(sessSeqUsr);

		memberService.uelete(memberDto);
		
		return "redirect:/usr/signin/SigninUsr";
	}
	
	@RequestMapping(value = "/usr/member/MemberUsrUpdt")
	public String memberUsrUpdt(MemberDto memberDto, HttpSession httpSession) {

	    // 세션에서 사용자 seq 가져오기
	    String sessSeqUsr = (String) httpSession.getAttribute("sessSeqUsr");

	    // 로그인한 사용자의 seq 설정
	    memberDto.setSeq(sessSeqUsr);

	    // 정보 수정
	    memberService.userUpdate(memberDto);

	    // 이름 바뀐 경우 세션에도 반영
	    httpSession.setAttribute("sessNameUsr", memberDto.getName());

		
		return "redirect:/usr/setting/userUiUsrSettings";
	}
	
	@RequestMapping(value = "/usr/member/MemberUsrPasswordChange")
	public String memberUsrPasswordChange(MemberDto memberDto, HttpSession httpSession) {
		
		// 세션에서 사용자 seq 가져오기
		String sessSeqUsr = (String) httpSession.getAttribute("sessSeqUsr");
		
		// 로그인한 사용자의 seq 설정
		memberDto.setSeq(sessSeqUsr);
		
		
		memberDto.setPassWord(encodeBcrypt(memberDto.getPassWord(), 10));
		
		// 정보 수정
		memberService.passwordChange(memberDto);
		
	    // 비밀번호 변경 후 세션에 반영
	    httpSession.setAttribute("sessPassWordUsr", memberDto.getPassWord());
		
		return "redirect:/usr/setting/ChangePasswordUsrForm";
	}
	
//	@ResponseBody
//	@RequestMapping("/usr/userUi/checkPassword")
//	public Map<String, Object> checkPassword(MemberDto memberDto, HttpSession httpSession) {
//	    Map<String, Object> result = new HashMap<>();
//
//	    // 세션에서 사용자 seq 가져오기
//	    String sessSeqUsr = (String) httpSession.getAttribute("sessSeqUsr");
//
//	    if (sessSeqUsr == null) {
//	        result.put("exists", false);
//	        return result;
//	    }
//
//	    // DB에서 해당 사용자 정보 조회
//	    memberDto.setSeq(sessSeqUsr);
//	    MemberDto dbMember = memberService.selectOne(memberDto);
//	    
//	    boolean check = matchesBcrypt(memberDto.getPassWord(), dbMember.getPassWord(), 10);
//	    if (dbMember != null && check) {
//	        result.put("exists", false);
//	        return result;
//	    }
//
//
//	    result.put("exists", check); // 일치하면 true, 아니면 false
//	    return result;
//	}
	
	@ResponseBody
	@RequestMapping(value = "/usr/userUi/checkPassword")
	public Map<String, Object> checkPassword(MemberDto memberDto, HttpSession httpSession) throws Exception {
	    Map<String, Object> returnMap = new HashMap<>();

	    String sessIdUsr = (String) httpSession.getAttribute("sessIdUsr");

	    if (sessIdUsr == null) {
	        returnMap.put("rt", "fail");
	        return returnMap;
	    }

	    MemberDto param = new MemberDto();
	    param.setiD(sessIdUsr);
	    MemberDto reMember = memberService.checkPassword(param);

	    boolean check = matchesBcrypt(memberDto.getPassWord(), reMember.getPassWord(), 10);

	    if (reMember != null && check) {
	        httpSession.setAttribute("passwordVerified", true);
	        returnMap.put("rt", "success");
	    } else {
	        returnMap.put("rt", "fail");
	    }

	    return returnMap;
	}
}
