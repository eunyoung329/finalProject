<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="${contextPath}/resources/css/member/searchIdPw-style.css">
<link rel="stylesheet" href="${contextPath}/resources/css/style.css">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Gothic+A1:wght@300;400;500;600&family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet">
</head>
<body>
  <header class="header-style">
       <jsp:include page ="/WEB-INF/views/common/header.jsp"/>
  </header>

  <main class="searchIdPw-main-style">

      <!-- 여기부터 추가 -->
      <section class="searchIdPw-contents-wrap">
       
        <tab>
          <ul class="tabnav">
            <li><a href="#tab01">아이디찾기</a></li>
            <li><a href="#tab02">비밀번호찾기</a></li>
          </ul>
          <div class="tabcontent">
           
            <div id="tab01">
              
              <h3>*가입하셨던 방법으로 이메일 찾기가 가능합니다.</h3>
              <div class="selectWay_area">
                가입방법 : Email<input type="radio" id="FindEmail_Email" name="searchType">
                Tel<input type="radio" id="FindEmail_Tel" name="searchType"></p>
              </div>
                <form action="searchId" method="post" validation="" name="searchId">
                  <div class="searchEmail_area">
                 <table>
                    
                     <tbody>
                     
                     <tr>
                         <th>이름</th>
                         <td><input type="text" id="memberName" name="memberName" placeholder="이름을 입력하세요"></td>
                     </tr>
                     <tr>
                       <th>전화번호</th>
                       <td><select id="mobile1" name="memberTel" class="memberTel">
                        <option value="010">010</option>
                        <option value="011">011</option>
                        <option value="016">016</option>
                        <option value="017">017</option>
                        <option value="018">018</option>
                        <option value="019">019</option>
                        </select>-<input type="text" class="memberTel" name="memberTel"  maxlength="4">-<input type="text"  class="memberTel" name="memberTel"  maxlength="4"></td>
                     </tr>
                    </tbody>
                 </table>
                </div>
                 <div class="searchEmailBtn_area">
                 <button id="searchEmailBtn">이메일찾기</button>
                </div>
             </form>


            </div>
            <div id="tab02">
              <h3>*가입방법으로 비밀번호 찾기가 가능합니다.</h3>
              <div class="selectWay_area">
                가입방법 : Email<input type="radio" id="FindEmail_Email" name="searchType">
                Tel<input type="radio" id="FindEmail_Tel" name="searchType"></p>
              </div>
                <form action="searchPw" method="post" validation="">
                  <div class="searchPw_area">
                 <table>
                     <tr>
                         <th>이름</th>
                         <td><input type="text" id="memberName" name="memberName" placeholder="이름을 입력하세요"></td>
                     </tr>
                     <tbody>
                     <tr>
                       <th>이메일</th>
                       <td><input type="text" id="memberEmail" name="memberEmail" placeholder="이메일을 입력하세요"></td>
                     </tr>
                    </tbody>
                
                 </table>
                </div>
                 <div class="searchEmailBtn_area">
                 <button id="searchPwBtn">비밀번호찾기</button>
                 </div>
             </form>
           
          </div>
        </tab><!--tab-->
       
         </section>
    </main>
    
    <footer class="footer-style">
       <jsp:include page ="/WEB-INF/views/common/footer.jsp"/>
    </footer>
    	<!-- jQuery 라이브러리 추가 -->
	<script src="https://code.jquery.com/jquery-3.6.0.min.js"
  integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4="
  crossorigin="anonymous"></script>
    <script src="${contextPath}/resources/js/member/searchIdPw.js"></script>
    </body>
    </html>
    