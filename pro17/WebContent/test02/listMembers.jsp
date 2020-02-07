<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 
<%-- JSTL태그 라이브러리 사용을 위한 선언 --%>   
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
    
<%
	//한글처리
	request.setCharacterEncoding("UTF-8");
%> 
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MemberController서블릿에서 request에 바인딩한 ArrayList를 얻어 현재 화면에 출력</title>

	<style type="text/css">
		.cls1 {
			font-size: 40px;
			text-align: center;
		}
	</style>

</head>
<body>
	<p class="cls1">회원정보</p>
	<table align="center" border="1">
		<tr align="center" bgcolor="lightgreen">
			<td width="7%"><b>아이디</b></td>
			<td width="7%"><b>비밀번호</b></td>
			<td width="7%"><b>이름</b></td>
			<td width="7%"><b>이메일</b></td>
			<td width="7%"><b>가입일</b></td>											
		</tr>
<c:choose>
	<c:when test="${requestScope.membersList == null}">
		<tr>
			<td colspan="5">
				<b>등록된 회원이 없습니다.</b>
			</td>
		</tr>
	</c:when>
	<c:when test="${requestScope.membersList != null}">
		<c:forEach var="membervo" items="${requestScope.membersList}">
			<tr align="center">
				<td>${membervo.id}</td>
				<td>${membervo.pwd}</td>
				<td>${membervo.name}</td>
				<td>${membervo.email}</td>
				<td>${membervo.joinDate}</td>
			</tr>
		</c:forEach>
	</c:when>
</c:choose>		

	</table>
	<a href="${pageContext.request.contextPath}/member/memberForm.do">회원가입 하기 </a>

</body>
</html>




