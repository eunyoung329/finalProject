package fp.art.stroke.member.model.service;


import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import fp.art.stroke.board.model.vo.BoardDetail;
import fp.art.stroke.member.model.dao.MemberDAO;
import fp.art.stroke.member.model.vo.Follow;
import fp.art.stroke.member.model.vo.Member;
//import net.nurigo.sdk.message.model.Message;


@Service
public class MemberServiceImpl implements MemberService {
	
	@Autowired //bean으로 등록된 객체중 같은 타입이 있으면 의존성 주입(DI)
	private MemberDAO dao;

	// 암호화를 위한 bcrypt 객체 의존성 주입(DI)
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	private Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);
	
	
	
	// * Connection을 Service에서 얻어왔던 이유 *
	// - Service의 메서드 하나는 요청을 처리하는 업무 단위
	//  -> 해당 업무가 끝난 후 트랜잭션을 한번에 처리하기 위해서
	//     어쩔 수 없이 Connection을 Service에서 얻어올 수 밖에 없었다.
	
	// 로그인 서비스 구현
		@Override
		public Member login(Member inputMember) {
			
			// 전달 받은 비밀번호를 암호화하여
			// DB에서 조회한 비밀번호와 비교 (DB에서 비교 X)
			
			// 왜? Bcrypt 암호화를 진행하기 위해서 어쩔 수 없이 Service에서 비교해야함
			
			// sha 방식 암호화 
			// A 회원 / 비밀번호 1234  -> 암호화 : abcd
			// B 회원 / 비밀번호 1234  -> 암호화 : abcd  (암호화 시 변경된 내용이 같음)
			
			
			// Bcrypt 암호화 : 암호화 하기 전에 salt를 추가하여 변형된 상태로 암호화를 진행
			// A 회원 / 비밀번호 1234  -> 암호화 : abcd
			// B 회원 / 비밀번호 1234  -> 암호화 : @azd  
			
			// * 매번 암호화되는 비밀번호가 달라져서 DB에서 직접 비교 불가능
			// 대신 Bcrypt 암호화를 지원하는 객체가
			// 이를 비교하는 기능(메서드) 가지고있어 이를 활용하면 된다.
			
			
			// ** Bcrypt 암호화를 사용하기 위해 이를 지원하는 Spring-security 모듈 추가 **
			
							// 원래 비밀번호(평문)      /       암호화된 비밀번호
			logger.debug( inputMember.getMemberPw() + " / " +  bcrypt.encode(inputMember.getMemberPw()) );
//			logger.debug( inputMember.getMemberPw() + " / " +  bcrypt.encode(inputMember.getMemberPw()) );
//			logger.debug( inputMember.getMemberPw() + " / " +  bcrypt.encode(inputMember.getMemberPw()) );
//			logger.debug( inputMember.getMemberPw() + " / " +  bcrypt.encode(inputMember.getMemberPw()) );
//			logger.debug( inputMember.getMemberPw() + " / " +  bcrypt.encode(inputMember.getMemberPw()) );
			
			String encryptedPassword = bcrypt.encode(inputMember.getMemberPw());
	        logger.info(encryptedPassword);
	        
			Member loginMember = dao.login(inputMember);
			
			
			// loginMember == null  : 일치하는 이메일 없음
			
			if(loginMember != null) { // 일치하는 이메일을 가진 회원 정보가 있을 경우
				
				// 입력된 비밀번호(평문) , 조회된 비밀번호(암호화) 비교 (같으면 true)
								 		//평문                  ,   암호화
				if( bcrypt.matches(  inputMember.getMemberPw()   ,  loginMember.getMemberPw() ) ) {
					// 비밀번호가 일치할 경우
					
					loginMember.setMemberPw(null); // 비교 끝났으면 비밀번호 지우기
					
				
					
				} else { // 비밀번호가 일치하지 않은 경우
					loginMember = null; // 로그인을 실패한 형태로 바꿔줌
					
				}
			}
			
			return loginMember;
			
			// Connection을 얻어오거나/반환하거나
			// 트랜잭션 처리를하는 구문을 적지 않아도
			// Spring에서 제어를 하기 때문에 Service 구문이 간단해진다.
		}



		@Override
		public int emailDupCheck(String memberEmail) {
			return dao.emailDupCheck(memberEmail);
		}



		@Override
		public int nicknameDupCheck(String memberNick) {
			return dao.nicknameDupCheck(memberNick);
		}



		@Override
		public int signUp(Member inputMember) {
			// 비밀번호 암호화(bcrypt)
			String encPw = bcrypt.encode( inputMember.getMemberPw() );
			
			// 암호화된 비밀번호로 다시 세팅
			inputMember.setMemberPw(encPw);

			// DAO 호출
			int result = dao.signUp(inputMember);
			
			// 트랜잭션 제어 처리를 하는 이유
			// -> 1개의 서비스에서 n개의 dao를 호출
			//    -> dao 하나라도 예외 발생 시 전체 rollback
			
			return result;
		}


//06/12 ey
		//이메일 인증번호 보내기
		@Override
		public int insertCertification(String inputEmail, String cNumber) {
		      int result = 0;
		      
		        try {
		            result = dao.updateCertification(inputEmail, cNumber);
		            if (result == 0) {
		                result = dao.insertCertification(inputEmail, cNumber);
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		        return result;
		    }



		@Override
		public int checkNumber(String inputEmail, String cNumber) {
		
			return dao.checkNumber(inputEmail,cNumber);
		}

		

		@Override
		public int telInsertCertification(String inputTel, String smsCNumber) {
			int result = 0;
		      
	        try {
	            result = dao.telUpdateCertification(inputTel, smsCNumber);
	            if (result == 0) {
	                result = dao.telInsertCertification(inputTel, smsCNumber);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return result;
			
		}


		@Override
		public int checkSmsNumber(String inputTel, String smsCNumber) {
		
			return dao.checkSmsNumber(inputTel,smsCNumber);
		}



		@Override
		public int insertFollow(Follow follow) {
			// TODO Auto-generated method stub
			return dao.insertFollow(follow);
		}
		public int addCouponDiscount(int memberId, String couponId, int couponCategory, String couponName1,
				String couponInfo, double discountAmount1) {
			
			
			return dao.addCouponDiscount(memberId,couponId,couponCategory,couponName1,
				couponInfo,discountAmount1);
		}



		@Override
		public int deleteFollow(Follow follow) {
			// TODO Auto-generated method stub
			return dao.deleteFollow(follow);
		}
		public int addCouponFreeShipping(int memberId, String couponId, int couponCategory, String couponName2,
				String couponInfo, double discountAmount2) {
			
			
			return dao.addCouponFreeShipping(memberId,couponId,couponCategory,couponName2,
				couponInfo,discountAmount2);

		}






			
			
			
			
		

	

}
