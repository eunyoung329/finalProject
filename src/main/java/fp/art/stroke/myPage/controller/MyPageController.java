package fp.art.stroke.myPage.controller;

import java.io.Console;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fp.art.stroke.member.model.vo.Member;
import fp.art.stroke.myPage.model.service.MyPageService;
import fp.art.stroke.myPage.model.vo.Addr;
import fp.art.stroke.product.model.vo.WishList;

@Controller
@RequestMapping("/myPage")
@SessionAttributes({ "loginMember" })
public class MyPageController {

	@Autowired
	private MyPageService service;

	private Logger logger = LoggerFactory.getLogger(MyPageController.class);

	@GetMapping("/myPageMain")
	public String myPageMain() {
		return "myPage/myPageMain";
	}

	@GetMapping("/myPageOrderList")
	public String myPageOrderList() {
		return "myPage/myPageOrderList";
	}

	@GetMapping("/myPageCouponList")
	public String myPageCouponList() {
		return "myPage/myPageCouponList";
	}

	@GetMapping("/myPageResentViewList")
	public String myPageResentViewList() {
		return "myPage/myPageResentViewList";
	}

	@GetMapping("/myPageWishList")
	public String myPageWishList(HttpSession session, Model model) {
		// myPageWishList 페이지 들어갈 때 바로 리스트가져오는 컨트롤러
		Member loginMember = (Member) session.getAttribute("loginMember");

		int memberId = loginMember.getMemberId();

		List<WishList> myPageWishList = service.myPageWishList(memberId);
		model.addAttribute("myPageWishList", myPageWishList);

		return "myPage/myPageWishList";
	}

	@GetMapping("/myPageBoardList")
	public String myPageBoardList() {
		return "myPage/myPageBoardList";
	}

	@GetMapping("/myPageModify")
	public String myPageMyReviewList() {
		return "myPage/myPageModify";
	}

	@GetMapping("/myPageAddrList")
	public String myPagemyPageAddrList(HttpSession session, Model model) {
		// 배송 조회 페이지 들어갈때 바로 리스트 가져오는 컨트롤러
		Member loginMember = (Member) session.getAttribute("loginMember");

		int memberId = loginMember.getMemberId();

		List<Addr> addrList = service.selectAddrList(memberId);
		model.addAttribute("addrList", addrList);

		return "myPage/myPageAddrList";
	}

	/**
	 * 배송지 등록, 수정!
	 * 
	 * @param addrName
	 * @param receiverName
	 * @param postcode
	 * @param roadAddress
	 * @param detailAddress
	 * @param addrTel
	 * @param addrIdString
	 * @param session
	 * @param ra
	 * @return
	 */
	@PostMapping("/addrReg")
	public String addrReg(@RequestParam("addrName") String addrName, @RequestParam("receiverName") String receiverName,
			@RequestParam("postcode") String postcode, @RequestParam("roadAddress") String roadAddress,
			@RequestParam("detailAddress") String detailAddress, @RequestParam("addrTel") String addrTel,
			@RequestParam(value = "addrId", required = false) String addrIdString, HttpSession session,
			RedirectAttributes ra) {

		Member loginMember = (Member) session.getAttribute("loginMember");

		int memberId = loginMember.getMemberId();
		System.out.println(memberId + "현재 멤버아이디!!");

		int addrId = 0; // 기본값 설정
		if (addrIdString != null && !addrIdString.isEmpty()) {
			try {
				addrId = Integer.parseInt(addrIdString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}

		int result = service.addrReg(addrName, receiverName, postcode, roadAddress, detailAddress, addrTel, memberId,
				addrId);

		String message = "";

		if (result >= 1) {
			message = "배송지가 등록되었습니다.";
		} else {
			message = "배송지 등록이 실패하였습니다.";
		}
		ra.addFlashAttribute("message", message);

		return "redirect:/myPage/myPageAddrList";
	}

	/**
	 * 배송지 삭제 컨트롤러
	 * 
	 * @param addrId
	 * @return
	 */
	@ResponseBody
	@GetMapping("/deleteAddress")
	public int deleteAddress(@RequestParam("addrId") int addrId) {

		int result = service.deleteAddr(addrId);

		return result;
	}

	/**
	 * 장바구니에 담기 컨트롤러
	 * 
	 * @param session
	 * @param productId
	 * @param selectedOption
	 * @param productPrice
	 * @return
	 */
	@ResponseBody
	@GetMapping("/cartInsert")
	public int cartInsert(HttpSession session, @RequestParam("productId") int productId,
			@RequestParam("selectedOption") String selectedOption, @RequestParam("productPrice") int productPrice) {
		Member loginMember = (Member) session.getAttribute("loginMember");

		int memberId = loginMember.getMemberId();

		int result = service.cartInsert(productId, memberId, selectedOption, productPrice);

		return result;
	}

	/**
	 * 위시리스트 삭제 컨트롤러
	 * 
	 * @param productId
	 * @param session
	 * @return
	 */
	@ResponseBody
	@GetMapping("/deleteWishlist")
	public int deleteWishlist(@RequestParam("productId") int productId, HttpSession session) {
		Member loginMember = (Member) session.getAttribute("loginMember");

		int memberId = loginMember.getMemberId();

		int result = service.deleteWishlist(productId, memberId);

		return result;
	}

	/**
	 * 위시리스트 선택 삭제 컨트롤러
	 * 
	 * @param productIds
	 * @param session
	 * @return
	 */
	@ResponseBody
	@GetMapping("/deleteSelectedWishlist")
	public int deleteSelectedWishlist(@RequestParam("productIds") List<Integer> productIds, HttpSession session) {
		Member loginMember = (Member) session.getAttribute("loginMember");

		int memberId = loginMember.getMemberId();
		int result = service.deleteSelectedWishlist(productIds, memberId);

		return result;
	}

	/**
	 * 회원탈퇴 컨트롤러
	 * 
	 * @param session
	 * @return
	 */
	@GetMapping("/secession")
	public String secession() {
		return "myPage/myPageModify";
	}

	@PostMapping("/secession")
	public String secession(HttpSession session, SessionStatus status, HttpServletRequest req, HttpServletResponse resp,
			RedirectAttributes ra) {
		Member loginMember = (Member) session.getAttribute("loginMember");

		int memberId = loginMember.getMemberId();

		int result = service.secession(memberId);
		String message = null;
		String path = null;

		if (result > 0) {
			message = "탈퇴 되었습니다.";
			path = "/";

			// 세션 없애기
			status.setComplete();

			// 쿠키 없애기
			Cookie cookie = new Cookie("saveId", "");
			cookie.setMaxAge(0);
			cookie.setPath(req.getContextPath());
			resp.addCookie(cookie);

		} else {
			message = "회원탈퇴에 실패하였습니다.";
			path = "/myPage/myPageModify";
		}

		ra.addFlashAttribute("message", message);

		return "redirect:" + path;
	}

	@GetMapping("/myPageReviewList")
	public String myPageReviewList() {
		return "myPage/myPageReviewList";
	}

	@GetMapping("/myPageMessage")
	public String myPageMessage() {
		return "myPage/myPageMessage";
	}

	@GetMapping("/profile")
	public String profile() {
		return "myPage/myPageMain";
	}
	
	@ResponseBody
	@GetMapping("/nicknameDupCheck")
	public int nicknameDupCheck(@RequestParam("memberNick") String memberNick, HttpSession session) {
		Member loginMember = (Member) session.getAttribute("loginMember");

		int memberId = loginMember.getMemberId();

		int result = service.nicknameDupCheck(memberNick, memberId);
		
		return result;
	}
	/**
	 * 프로필수정
	 * 
	 * @param loginMember
	 * @param uploadImage
	 * @param map
	 * @param req
	 * @param ra
	 * @param session
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/profile")
	public String updateProfile(@ModelAttribute("loginMember") Member loginMember,
			@RequestParam(value = "uploadImage", required = false) MultipartFile uploadImage /* 업로드 파일 */
			, @RequestParam Map<String, Object> map /* delete 담겨있음 */
			, HttpServletRequest req /* 파일 저장 경로 탐색용 */
			, RedirectAttributes ra, HttpSession session) throws IOException {

		// 1) 웹 접근 경로 (/comm/resources/images/memberProfile/)
		String webPath = "/resources/img/memberProfile/";

		// 2) 서버 저장 폴더 경로
		String folderPath = req.getSession().getServletContext().getRealPath(webPath);
		// C:\workspace\7_Framework\comm\src\main\webapp\resources\images\memberProfile

		// map에 경로 2개, 이미지, delete, 회원번호 담기
		map.put("webPath", webPath);
		map.put("folderPath", folderPath);
		map.put("uploadImage", uploadImage);
		map.put("memberId", loginMember.getMemberId());

		int result = service.updateProfile(map);

		String message = null;

		if (result > 0) {
			message = "프로필 이미지가 변경되었습니다.";

			// DB-세션 동기화

			// 서비스 호출 시 전달된 map은 실제로는 주소만 전달(얕은복사)
			// -> 서비스에서 추가된 "profileImage"는 원본 map에 그대로 남아있음~!

			loginMember.setProfileImage((String) map.get("profileImage"));

		} else {
			message = "프로필 이미지 변경 실패...";
		}

		ra.addFlashAttribute("message", message);
		return "redirect:profile";
	}

	/**
	 * 개인정보 수정 컨트롤러
	 */
	@PostMapping("/modify")
	public String updateInfo( @ModelAttribute("loginMember") Member loginMember,
			@RequestParam Map<String, Object> paramMap,  // 요청 시 전달된 파라미터를 구분하지 않고 모두 Map에 담아서 얻어옴           
			String[] updateAddress,
			RedirectAttributes ra) {
		
		String memberAddress = String.join(",,", updateAddress);
		if(memberAddress.equals(",,,,"))	memberAddress = null;
		
		paramMap.put("memberId", loginMember.getMemberId());
		paramMap.put("memberAddr", memberAddress);
		
		int result = service.updateInfo(paramMap);
		
		String message = null;
		
		if(result > 0) {
			message = "회원 정보가 수정되었습니다.";
			
			// DB - Session의 회원정보 동기화(얕은 복사 활용)
			loginMember.setMemberNick(   (String)paramMap.get("memberNickname")    );
			loginMember.setMemberSns(   (String)paramMap.get("memberSNS")    );
			loginMember.setMemberAddr(   (String)paramMap.get("memberAddr")    );
		}else {
			message = "회원 정보 수정 실패";
		}
		ra.addFlashAttribute("message", message);
		
		return "redirect:myPageModify";
	}
}
