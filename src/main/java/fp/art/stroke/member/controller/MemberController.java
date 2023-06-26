package fp.art.stroke.member.controller;

import javax.servlet.http.Cookie;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.xml.Log4jEntityResolver;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;

import fp.art.stroke.board.model.vo.BoardDetail;
import fp.art.stroke.member.auth.bo.NaverLoginBO;
import fp.art.stroke.member.model.service.MemberApiService;

//import fp.art.stroke.member.aouth.bo.KakaoLoginBO;
//import fp.art.stroke.member.aouth.bo.NaverLoginBO;
//

import fp.art.stroke.member.model.service.MemberService;
import fp.art.stroke.member.model.vo.Follow;
import fp.art.stroke.member.model.vo.GoogleLoginResponse;
import fp.art.stroke.member.model.vo.GoogleMember;
import fp.art.stroke.member.model.vo.GoogleOAuthRequest;
import fp.art.stroke.member.model.vo.Member;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.net.URI;
import java.util.HashMap;
import java.util.Properties;

@Controller // 생성된 bean이 Controller임을 명시 + bean 등록

@RequestMapping("/member") // localhost:8080/art_stroke/member 이하의 요청을 처리하는 컨트롤러

@SessionAttributes({ "loginMember" }) // Model에 추가된 값의 key와 어노테이션에 작성된 값이 같으면
										// 해당 값을 session scope 이동시키는 역할
@Slf4j
public class MemberController {
	@Value("${google.auth.url}")
    private String googleAuthUrl;

    @Value("${google.login.url}")
    private String googleLoginUrl;

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.redirect.url}")
    private String googleRedirectUrl;

    @Value("${google.secret}")
    private String googleClientSecret;
	
	
	
	
	private Logger logger = LoggerFactory.getLogger(MemberController.class);

	@Autowired
	private MemberService service;
	
	@Autowired
    private MemberApiService apiService;
    
	/* GoogleLogin */
	@Autowired
	private GoogleConnectionFactory googleConnectionFactory;
	@Autowired
	private OAuth2Parameters googleOAuth2Parameters;
	
	
	/*네이버 로그인*/
	/*NaverLoginBO*/
	private NaverLoginBO naverLoginBO;
	private String apiResult=null;
	
	
	

	
	
	
	
    

    
    

	@GetMapping("/login") // Get방식 : /comm/member/login 요청
	public String login(Model model,HttpSession session) {
		
		/*네이버아이디로 인증 URL을 생성하기 위하여 naverLoginBO클래스의 getAuthorizationURL메소드 호출*/
		String naverAuthUrl=naverLoginBO.getAuthorizationUrl(session);
		/*인증요청문 확인 */
		System.out.println("네이버:"+naverAuthUrl);
		/*객체바인딩 */
		model.addAttribute("urlNaver",naverAuthUrl);
		
		/*생성한 인증 URL을 view로 전달 */
		
		return "member/login";
	}

	@PostMapping("/login")
	public String login(@ModelAttribute Member inputMember, Model model, RedirectAttributes ra,
			HttpServletResponse resp, HttpServletRequest req,
			@RequestParam(value = "saveId", required = false) String saveId) {

		// @ModelAttribute 생략 가능
		// -> 커맨드 객체 (@ModelAttribute가 생략된 상태에서 파라미터가 필드에 세팅된 객체)

		logger.info("로그인 기능 수행됨");

		// 아이디, 비밀번호가 일치하는 회원 정보를 조회하는 Service 호출 후 결과 반환 받기
		Member loginMember = service.login(inputMember);

		/*
		 * Model : 데이터를 맵 형식(K:V) 형태로 담아 전달하는 용도의 객체 -> request, session을 대체하는 객체
		 * 
		 * - 기본 scope : request - session scope로 변환하고 싶은 경우 클래스 레벨로 @SessionAttributes를
		 * 작성되면 된다.
		 */

		// @SessionAttributes 미작성 -> request scope

		if (loginMember != null) { // 로그인 성공 시
			model.addAttribute("loginMember", loginMember); // == req.setAttribute("loginMember", loginMember);

			System.out.println("로그인됨");

			// 로그인 성공 시 무조건 쿠키 생성
			// 단, 아이디 저장 체크 여부에 따라서 쿠기의 유지 시간을 조정
			Cookie cookie = new Cookie("saveId", loginMember.getMemberEmail());

			if (saveId != null) { // 아이디 저장이 체크 되었을 때

				cookie.setMaxAge(60 * 60 * 24 * 365); // 초 단위로 지정 (1년)

			} else { // 체크 되지 않았을 때
				cookie.setMaxAge(0); // 0초 -> 생성 되자마자 사라짐 == 쿠키 삭제
			}

			// 쿠키가 적용될 범위(경로) 지정
			cookie.setPath(req.getContextPath());

			// 쿠키를 응답 시 클라이언트에게 전달
			resp.addCookie(cookie);

			return "redirect:/";

		} else {
			// model.addAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");

			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
			System.out.println("로그인안됨");
			// -> redirect 시 잠깐 Session scope로 이동후
			// redirect가 완료되면 다시 Request scope로 이동

			// redirect 시에도 request scope로 세팅된 데이터가 유지될 수 있도록 하는 방법을
			// Spring에서 제공해줌
			// -> RedirectAttributes 객체 (컨트롤러 매개변수에 작성하면 사용 가능)

			return "redirect:/member/login";
		}

		// session.setAttribute("loginMember", loginMember);

	}

	// 로그아웃
	@GetMapping("/logout")
	public String logout( /* HttpSession session, */
			SessionStatus status) {

		// 로그아웃 == 세션을 없애는 것

		// * @SessionAttributes을 이용해서 session scope에 배치된 데이터는
		// SessionStatus라는 별도 객체를 이용해야만 없앨 수 있다.
		logger.info("로그아웃 수행됨");

		// session.invalidate(); // 기존 세션 무효화 방식으로는 안된다!

		status.setComplete(); // 세센이 할 일이 완료됨 -> 없앰

		return "redirect:/"; // 메인페이지 리다이렉트

	}

	@GetMapping("/signUp")
	public String signUp(@ModelAttribute Member member,
			@RequestParam(name = "emailOptIn", defaultValue = "N") String emailOptIn, Model model,
			HttpServletRequest request) {
		if (emailOptIn != null) {
			model.addAttribute("emailOptIn", emailOptIn);
		}

		String contextPath = request.getContextPath();
		model.addAttribute("contextPath", contextPath);

		return "member/signUp";
	}

	// 0619 ey 쿠폰 추가
	@PostMapping("/signUp")
	public String signUp(Member inputMember, String[] memberAddr,
			@RequestParam(name = "emailOptIn", defaultValue = "N") String emailOptIn, HttpServletRequest request,
			RedirectAttributes ra) {

		// 이메일 수신 여부 설정
		inputMember.setEmailOptIn(emailOptIn);

		// 커맨드 객체를 이용해서 입력된 회원 정보를 잘 받아옴
		// 단, 같은 name을 가진 주소가 하나의 문자열로 합쳐서 세팅되어있음.
		// -> 도로명 주소에 " , " 기호가 포함되는 경우가 있어 이를 구분자로 사용할 수 없음.

		// String[] memberAddr :
		// name이 memberAddr인 파라미터의 값을 모두 배열에 담아서 반환

		inputMember.setMemberAddr(String.join(",,", memberAddr));
		// String.join("구분자", 배열)
		// 배열을 하나의 문자열로 합치는 메서드
		// 중간에 들어가 구분자를 지정할 수 있다.
		// [a, b, c] - join 진행 -> "a,,b,,c"

		if (inputMember.getMemberAddr().equals(",,,,")) { // 주소가 입력되지 않은 경우
			inputMember.setMemberAddr(null); // null로 변환
		}

		int result = service.signUp(inputMember);

		String message = ""; // 빈 문자열로 초기화
		String path = ""; // 빈 문자열로 초기화
		String confirmScript = "";
		// 회원 가입 서비스 호출

		if (result > 0) { // 회원 가입 성공

			message = "회원가입이 성공하였습니다.";
			path = "redirect:/"; // 메인페이지
		} else { // 실패
			message = "회원가입이 실패하였습니다.";
			path = "redirect:/member/signUp"; // 회원 가입 페이지
		}

		ra.addFlashAttribute("message", message);
		ra.addFlashAttribute("confirmScript", confirmScript);
		return path;
	}

	// 이메일 중복 검사
	@ResponseBody // ajax 응답 시 사용!
	@GetMapping("/emailDupCheck")
	public int emailDupCheck(String memberEmail) {
		int result = service.emailDupCheck(memberEmail);

		// 컨트롤러에서 반환되는 값은 forward 또는 redirect를 위한 경로인 경우가 일반적
		// -> 반환되는 값은 경로로 인식됨

		// -> 이를 해결하기위한 어노테이션 @ResponseBody 가 존재함

		// @ResponseBody : 반환되는 값을 응답의 몸통(body)에 추가하여
		// 이전 요청 주소로 돌아감
		// -> 컨트롤러에서 반환되는 값이 경로가 아닌 "값 자체"로 인식됨.

		return result;

	}

	// 닉네임 중복 검사
	@ResponseBody
	@GetMapping("/nicknameDupCheck")
	public int nicknameDupCheck(String memberNick) {
		int result = service.nicknameDupCheck(memberNick);

		return result;

	}

	// 06/12 ey
	// 이메일 인증번호 보내기
	@ResponseBody // ajax 응답 시 사용!
	@GetMapping("/sendEmail")
	public int sendEmail(@RequestParam("inputEmail") String inputEmail) {

		String subject = "[artStroke] 회원 가입 이메일 인증번호"; // 제목
		String fromEmail = "art_s@artstroke.co.kr"; // 보내는 사람으로 표시될 이메일
		String fromUsername = "관리자"; // 보내는 사람 이름
		String toEmail = inputEmail; // 받는사람, 콤마(,)로 여러개 나열 가능

		final String smtpEmail = "unn3290@gmail.com"; // 이메일
		final String password = ""; // 발급 받은 비밀번호

		// 메일 옵션 설정
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587"); // 465, 587
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		try {
			// 메일 세션 생성
			Session session = Session.getInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpEmail, password);
				}
			});

			// 메일 송/수신 옵션 설정(1명 보내기)
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromEmail, fromUsername)); // 송신자(보내는 사람) 지정
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail)); // 수신자(받는사람) 지정
			message.setSubject(subject); // 이메일 제목 지정

			// 인증번호 6자리 생성코드(영어 대/소문 + 숫자)
			String cNumber = "";
			for (int i = 0; i < 6; i++) {
				int sel1 = (int) (Math.random() * 3); // 0:숫자 / 1,2:영어
				if (sel1 == 0) {
					int num = (int) (Math.random() * 10); // 0~9
					cNumber += num;
				} else {
					char ch = (char) (Math.random() * 26 + 65); // A~Z
					int sel2 = (int) (Math.random() * 2); // 0:소문자 / 1:대문자
					if (sel2 == 0) {
						ch = (char) (ch + ('a' - 'A')); // 대문자로 변경
					}
					cNumber += ch;
				}
			}

			// 메일에 출력할 텍스트
			// 메일에 출력할 텍스트
			String mailContent = "<h3>[artStroke] 회원 가입 인증 번호입니다.</h3>\n" + "<h3>인증 번호 : <span style='color:red'>"
					+ cNumber + "</span></h3>\n";

			// 메일 콘텐츠 설정
			message.setContent(mailContent, "text/html; charset=utf-8");

			// 메일 발송
			Transport.send(message);

			// 인증번호를 받은 이메일, 인증번호, 인증번호 발급 시간 -> DB 삽입
			int result = service.insertCertification(inputEmail, cNumber);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;

		}
	}

	@ResponseBody // ajax 응답 시 사용!
	@GetMapping("/checkNumber")
	public int checkNumber(@RequestParam("cNumber") String cNumber, @RequestParam("inputEmail") String inputEmail) {

		int result = service.checkNumber(inputEmail, cNumber);

		return result;
	}

//0614 ey
	// 문자를 보낼때 맵핑되는 메소드
	@ResponseBody
	@GetMapping("/sendSms")
	public int sendSms(HttpServletRequest request, @RequestParam("inputTel") String inputTel) throws Exception {

		String api_key = ""; // 위에서 받은 api key를 추가
		String api_secret = ""; // 위에서 받은 api secret를 추가

		sms.art.stroke.Coolsms coolsms = new sms.art.stroke.Coolsms(api_key, api_secret);
		// 이 부분은 홈페이지에서 받은 자바파일을 추가한다음 그 클래스를 import해야 쓸 수 있는 클래스이다.

		String smsCNumber = "";
		for (int i = 0; i < 4; i++) {
			int num = (int) (Math.random() * 10); // 0~9
			smsCNumber += num;
		}

		HashMap<String, String> set = new HashMap<String, String>();
		set.put("to", inputTel); // 수신번호
		set.put("from", ""); // 발신번호(문자를 보낼 사람)
		// set.put("from", (String)request.getParameter("번호")); // 발신번호, jsp에서 전송한 발신번호를
		// 받아 map에 저장한다.
		set.put("text", "[artStroke]인증번호" + smsCNumber + "를 입력해주세요"); // 문자내용 // 문자내용, jsp에서 전송한 문자내용을 받아 map에 저장한다.
		set.put("type", "sms"); // 문자 타입

		System.out.println(set);

		coolsms.send(set); // 보내기&전송결과받기

		int result = service.telInsertCertification(inputTel, smsCNumber);

		return result; // 문자 메시지 발송 성공했을때
	}

	@ResponseBody // ajax 응답 시 사용!
	@GetMapping("/checkSmsNumber")
	public int checkSmsNumber(@RequestParam("smsCNumber") String smsCNumber,
			@RequestParam("inputTel") String inputTel) {

		int result = service.checkSmsNumber(inputTel, smsCNumber);
		logger.info(String.valueOf(result));

		return result;
	}

	// id/비밀번호 화면전환
	@GetMapping("/searchIdPw") // Get방식 : /stoke/member/signUp 요청
	public String searchIdPw() {
		return "member/searchIdPw";
	}

	@GetMapping("/terms") // Get방식 : /stoke/member/terms 요청
	public String terms() {
		return "member/terms";
	}

	
	//0620 ey
	@RequestMapping(value = "/insertCoupon", method = RequestMethod.GET)
	public String insertCoupon(HttpSession session, RedirectAttributes ra,
			@RequestParam(name = "couponOptIn", defaultValue = "N") String couponOptIn) {
		Member loginMember = (Member) session.getAttribute("loginMember");

		if (loginMember == null) {
			// 로그인되어 있지 않은 경우
			ra.addFlashAttribute("message", "로그인을 먼저 해주세요.");
			return "redirect:/member/login"; // 로그인 페이지로 리다이렉트
		}

		if (loginMember.getCouponOptIn().equals("Y")) {

			ra.addFlashAttribute("message", "이미 받으신 쿠폰이 존재합니다.");

		} else {
			int memberId = loginMember.getMemberId();

			String couponId1 = "";
			for (int i = 0; i < 8; i++) {
				int num = (int) (Math.random() * 10); // 0~9
				couponId1 += num;
			}
			String couponId2 = "";
			for (int i = 0; i < 8; i++) {
				int num = (int) (Math.random() * 10); // 0~9
				couponId2 += num;
			}

			int couponCategory = 1;
			String couponInfo = "가입축하쿠폰";
			String couponName1 = "10% 할인쿠폰";
			double discountAmount1 = 0.1;
			String couponName2 = "배송비무료쿠폰";
			double discountAmount2 = 2500;

			int result1 = service.addCouponDiscount(memberId, couponId1, couponCategory, couponName1, couponInfo,
					discountAmount1);
			int result2 = service.addCouponFreeShipping(memberId, couponId2, couponCategory, couponName2, couponInfo,
					discountAmount2);

			if (result1 > 0 && result2 > 0) {
				// couponOptIn 값을 "Y"로 업데이트합니다.
				couponOptIn = "Y";
				int updateResult = service.updateCouponOptIn(memberId, couponOptIn);
				if (updateResult > 0) {

					ra.addFlashAttribute("message", "쿠폰이 지급되었습니다.");
					loginMember.setCouponOptIn("Y");
					session.setAttribute("loginMember", loginMember);

				} else {
					ra.addFlashAttribute("message", "쿠폰 수신동의 업데이트에 실패하였습니다.");
				}
			} else {
				ra.addFlashAttribute("message", "쿠폰 지급에 실패하였습니다.");
			}
		}

		return "redirect:/"; // 처리 후 메인 페이지로 리다이렉트
	}

	@ResponseBody
	@PostMapping("/insertFollow")
	public int insertFollow(Follow follow) {
		int result = service.insertFollow(follow);
		return result;
	}

	@ResponseBody
	@PostMapping("/deleteFollow")
	public int deleteFollow(Follow follow) {
		int result = service.deleteFollow(follow);
		return result;
	}
	
	
//	
	
//	//구글 로그인
//	@ResponseBody
//	@RequestMapping(value = "/loginGoogle", method = RequestMethod.POST)
//	public void loginGooglePOST(Member member, HttpSession session, RedirectAttributes ra, Member mvo) {
//	    Member returnVO = service.loginMemberByGoogle(member);
//	    String mvo_ajaxEmail = mvo.getMemberEmail(); 
//	    System.out.println("C: 구글아이디 포스트 db에서 가져온 member " + member);
//	    System.out.println("C: 구글아이디 포스트 ajax에서 가져온 Email " + mvo_ajaxEmail);
//	    
//	    if (returnVO == null) { // 아이디가 DB에 존재하지 않는 경우
//	        // 구글 회원가입
//	        service.joinMemberByGoogle(member);    
//
//	        // 구글 로그인
//	        returnVO = service.loginMemberByGoogle(member);
//	        session.setAttribute("memberEmail", returnVO.getMemberEmail());            
//	        ra.addFlashAttribute("mvo", returnVO);
//	    }
//
//	    if (mvo_ajaxEmail.equals(returnVO.getMemberEmail())) { // 이메일이 DB에 존재하는 경우
//	        // 구글 로그인
//	        session.setAttribute("memberEmail", returnVO.getMemberEmail());            
//	        ra.addFlashAttribute("mvo", returnVO);
//	    } else { // 이메일이 DB에 존재하지 않는 경우
//	        // 구글 회원가입
//	        service.joinMemberByGoogle(member);    
//
//	        // 구글 로그인
//	        returnVO = service.loginMemberByGoogle(member);
//	        session.setAttribute("memberEmail", returnVO.getMemberEmail());            
//	        ra.addFlashAttribute("mvo", returnVO);
//	    }
//	}
	
	
	
//	// 로그인 첫 화면 요청 메소드
//		@RequestMapping(value = "/googleLogin", method = { RequestMethod.GET, RequestMethod.POST })
//		public String googleLogin(Model model, HttpSession session) {
//
//			/* 구글code 발행 */
//			OAuth2Operations oauthOperations = googleConnectionFactory.getOAuthOperations();
//			String url = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, googleOAuth2Parameters);
//
//			System.out.println("구글:" + url);
//
//			model.addAttribute("google_url", url);
//
//			/* 생성한 인증 URL을 View로 전달 */
//			return "redirect:/";
//		}
	

    @GetMapping("/getGoogleAuthUrl")
    public ResponseEntity<?> getGoogleAuthUrl(HttpServletRequest request) throws Exception {

        String reqUrl = googleLoginUrl + "/o/oauth2/v2/auth?client_id=" + googleClientId + "&redirect_uri=" + googleRedirectUrl
                + "&response_type=code&scope=email%20profile%20openid&access_type=offline";

        logger.info("myLog-LoginUrl : {}",googleLoginUrl);
        logger.info("myLog-ClientId : {}",googleClientId);
        logger.info("myLog-RedirectUrl : {}",googleRedirectUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(reqUrl));

        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }
    @GetMapping("/oauth_google_check")
    public String oauth_google_check(HttpServletRequest request,
                                     @RequestParam(value = "code") String authCode,
                                     Model model) throws Exception {
        int isLogin = 0;
        String googleUid = null;

        GoogleOAuthRequest googleOAuthRequest = GoogleOAuthRequest
                .builder()
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .code(authCode)
                .redirectUri(googleRedirectUrl)
                .grantType("authorization_code")
                .build();

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<GoogleLoginResponse> apiResponse = restTemplate.postForEntity(googleAuthUrl + "/token", googleOAuthRequest, GoogleLoginResponse.class);
        GoogleLoginResponse googleLoginResponse = apiResponse.getBody();

        logger.info("responseBody {}", googleLoginResponse.toString());

        String googleToken = googleLoginResponse.getId_token();

        String requestUrl = UriComponentsBuilder.fromHttpUrl(googleAuthUrl + "/tokeninfo").queryParam("id_token", googleToken).toUriString();

        String resultJson = restTemplate.getForObject(requestUrl, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        GoogleMember googleMember = objectMapper.readValue(resultJson, GoogleMember.class);
        if (googleMember != null) {
            isLogin = apiService.selectApiMemberCount(googleMember.getEmail(), "google");
        } else {
        	
        }
        if (isLogin > 0) {
            // 로그인 유저가 있으면 로그인을 진행.
            Member loginMember = apiService.selectApiMember(googleMember.getEmail(), "google");
            model.addAttribute("loginMember", loginMember);
            return "redirect:/"; // 내 페이지로의 뷰 이름으로 수정하세요.
        } else {
            model.addAttribute("googleMember", googleMember);
            // int signUp = service.insertApiMember(googleMember);
            return "member/sns_signUp";
        }
    }
    @PostMapping("/sns_signUp")
    public String snsSignUp(@RequestParam("")
    						Member member,
    						Model model) {
    	return "redirect:/";
    }
    
	
	//네이버
	@Autowired
	private void setNaverLoginBO(NaverLoginBO naverLoginBO) {
		this.naverLoginBO=naverLoginBO;
		
	}
	

	@RequestMapping(value="/callBackNaver",method= {RequestMethod.GET,RequestMethod.POST})
	public String callbackNaver(Model model,@RequestParam String code,@RequestParam String state,HttpSession session)throws Exception {
		System.out.println("로그인성공 callbackNaver");
		OAuth2AccessToken oauthToken;
		oauthToken=naverLoginBO.getAccessToken(session, code, state);
		//로그인사용자정보를 읽어온다
		apiResult=naverLoginBO.getUserProfile(oauthToken);
		
		JSONParser jsonParser=new JSONParser();
		JSONObject jsonObj;
		
		jsonObj=(JSONObject) jsonParser.parse(apiResult);
		JSONObject response_obj=(JSONObject)jsonObj.get("response");
		
		//프로필 조회
		String email=(String)response_obj.get("email");
		String name=(String)response_obj.get("name");
		
		//세션에 사용자 정보 등록
		//session.setAttribute("isLogin_r","Y");
		session.setAttribute("signIn", apiResult);
		session.setAttribute("email", email);
		session.setAttribute("name", name);
		
		/*네이버로그인 성공 페이지 View 호출 */
		
		return "redirect:/loginSuccess";
		
	}
	
	//소셜 로그인 성공페이지
	@RequestMapping("/loginSuceess")
	public String loginSuccess() {
		return "loginSucess";
	}
	
}
