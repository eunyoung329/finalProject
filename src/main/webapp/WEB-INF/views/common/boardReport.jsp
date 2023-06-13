<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel = "stylesheet" href="${contextPath}/resources/css/board/report-style.css">
    <script src="https://kit.fontawesome.com/d89904c156.js" crossorigin="anonymous"></script>
    <title>artstroke_report</title>
</head>
<body>
    <header class="header-style">
        <section class="contents-wrap">
            <div class = "boardReport_title">
                <div class = "full flex-row">
                    <div class = "boardReport-title-element-1 full flex-row"><button class="fa-solid fa-arrow-left arrow-style" style="cursor:pointer;"></button></div>
                    <div class = "boardReport-title-element-2 full flex-row"><h3>신고하기</h3></div>
                    <div class = "boardReport-title-element-3 full flex-row flex-row"></div>
                </div>
            </div>
        </section>
    </header>
    <form action = "*" type="post">
    <main class="main-style">
        <!-- 여기부터 추가 -->
        
        <section class="contents-wrap">
             <div class = "boardReport_content">
                <div>
                    <div class = "report-title-Write">
                        <h3>신고하기</h3>
                    </div>
                    <table class = "report-table">
                        <tr>
                            <td class = "report-td1">신고대상</td>
                            <td><!--jstl로 넣을 예정.--></td>
                        </tr>
                        <tr>
                            <td class = "report-td2">신고 게시글(댓글)</td>
                            <td><!--여기에는 jstl로 넣음.--></td>
                        </tr>
                    </table>
                    <div class = "report-reason-field"><h3>신고 사유</h3></div>
                    <textarea class = "boardReport-textarea"></textarea>
                </div>
                
            </div>
            
            
            
        </section>
    </main>
    <div class = "report-btn"><button class = "report-btn-atr">제출하기</button></div>
    </form>
    <footer class="footer-style">
        
    </footer>
    
</body>
</html>