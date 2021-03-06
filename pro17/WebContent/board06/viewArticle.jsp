<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isELIgnored="false" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
  request.setCharacterEncoding("UTF-8");
%> 
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />
<head>
   <meta charset="UTF-8">
   <title>글보기</title>
   <style>
     #tr_btn_modify{
       display:none;
     }
   
   </style>
   <script  src="http://code.jquery.com/jquery-latest.min.js"></script> 
   <script type="text/javascript" >
   
    //답글 쓰기 버튼을 클릭 했을때 호출 되는 함수 
   	function fn_reply_form(url, parentNO) {
	
    	var form = document.createElement("form");
    	form.setAttribute("method", "post");
    	//매개변수로 전달 받은  요청주소를 <form>태그의 action속성에 주소값으로 설정함.
    	form.setAttribute("action", url);
    	//함수 호출시 전달된 articleNO값을 <input>태그를 이용해 컨트롤러에 전달.
    	var parentNOInput = document.createElement("input");
    	parentNOInput.setAttribute("type", "hidden");
    	parentNOInput.setAttribute("name", "parentNO");
    	parentNOInput.setAttribute("value", parentNO);
    	
    	form.appendChild(parentNOInput);
    	document.body.appendChild(form);
    	form.submit();
	}
   
   
   
     function backToList(obj){
	    obj.action="${contextPath}/board/listArticles.do";
	    obj.submit();
     }
 	
	 function fn_enable(obj){
		 //수정하기 버튼 클릭시...수정하기 위한 텍스트 박스를 활성화 시킴
		 document.getElementById("i_title").disabled=false;
		 document.getElementById("i_content").disabled=false;
		 document.getElementById("i_imageFileName").disabled=false;
		 //수정반영하기 버튼과 취소버튼이 있는 tr태그영역을 보이게 만들기 
		 document.getElementById("tr_btn_modify").style.display="block";
		 //수정하기버튼, 삭제하기버튼, 리스트로돌아가기버튼, 답글쓰기버튼이 있는 tr태그영역을 숨김
		 document.getElementById("tr_btn").style.display="none";
	 }
	 
	 function fn_modify_article(obj){
		 obj.action="${contextPath}/board/modArticle.do";
		 obj.submit();
	 }
	 //삭제하기 버튼을 클릭하면 자바스크립트의 함수를 호출해  삭제할 글번호인? articleNO를 
	 //BoardController로 전송함.
	 function fn_remove_article(url,articleNO){
		 var form = document.createElement("form");
		 form.setAttribute("method", "post");
		 form.setAttribute("action", url);		   
//  	 <form action='${contextPath}/board/removeArticle.do' method="post">
		 	
// 		 </form>
	     var articleNOInput = document.createElement("input");
	     articleNOInput.setAttribute("type","hidden");
	     articleNOInput.setAttribute("name","articleNO");
	     articleNOInput.setAttribute("value", articleNO);
// 	     <input type="hidden" name="articleNO" value="2">
	      
	     form.appendChild(articleNOInput);
//  	 <form action='${contextPath}/board/removeArticle.do' method="post">
// 	     	<input type="hidden" name="articleNO" value="2">		 	
// 		 </form>	 

	     document.body.appendChild(form);
	//     <body>
	//  	 <form action='${contextPath}/board/removeArticle.do' method="post">
	//	     	<input type="hidden" name="articleNO" value="2">		 	
	//		 </form>	     	
	//     </body>
	
		 form.submit();
	 
	 }
	 function readURL(input) {
	     if (input.files && input.files[0]) {
	         var reader = new FileReader();
	         reader.onload = function (e) {
	             $('#preview').attr('src', e.target.result);
	         }
	         reader.readAsDataURL(input.files[0]);
	     }
	 }  
 </script>
</head>
<body>

<!-- 
	수정하기 버튼을 클릭해 fn_enable()함수를 호출하여
	비활성화된 텍스트 박스를 수정할 수 있도록 활성화 시킵니다.
	또한 글 정보와 이미지를 수정 한 후 수정반영하기 버튼을 클릭하면
	fn_modify_article()함수를 호출하여 BoardController로 수정 요청시 수정할 데이터를 전달함.
 -->

  <form name="frmArticle" method="post" action="${contextPath}"  enctype="multipart/form-data">
	  <table  border="0"  align="center">
		  <tr>
			  <td width="150" align="center" bgcolor="#FF9933">글번호</td>
			  <td >
				  <input type="text"  value="${article.articleNO }"  disabled />
				  <!-- 글 수정시 글번호를 컨트롤러로 전송하기 위해 미리 <hidden>태그를 이용해 글번호를 request에 저장함. -->
				  <input type="hidden" name="articleNO" value="${article.articleNO}"  />
			  </td>
		  </tr>
		  <tr>
			  <td width="150" align="center" bgcolor="#FF9933"> 작성자 아이디</td>
			  <td>
			  	<input type=text value="${article.id }" name="writer"  disabled />
			  </td>
		  </tr>
		  <tr>
			  <td width="150" align="center" bgcolor="#FF9933">제목</td>
			  <td><input type=text value="${article.title }"  name="title"  id="i_title" disabled /></td>   
		  </tr>
		  <tr>
			   <td width="150" align="center" bgcolor="#FF9933">내용</td>
			   <td>
			   		<textarea rows="20" cols="60"  name="content"  id="i_content"  disabled />${article.content }</textarea>
			   </td>  
		  </tr>
	 
	<c:if test="${not empty article.imageFileName && article.imageFileName!='null' }">  
		<tr>
		   <td width="150" align="center" bgcolor="#FF9933" rowspan="2">이미지</td>
		   <td>
		   	 <!-- 이미지 수정에 대비해 미리 원래 이미지 파일이름을 <hidden>태그로 저장 -->
		     <input  type= "hidden"   name="originalFileName" value="${article.imageFileName }" />
		     <img src="${contextPath}/download.do?articleNO=${article.articleNO}&imageFileName=${article.imageFileName}" id="preview"/><br>   
		   </td>   
		</tr>  
		<tr>
		    <td>
		    	<!-- 수정된 이미지 파일 이름을 request에 저장하여 컨트롤러로 전송! -->
		       <input  type="file"  name="imageFileName" id="i_imageFileName"   disabled   onchange="readURL(this);"   />
		    </td>
		</tr>
	 </c:if>
		  <tr>
			   <td width=20% align=center bgcolor=#FF9933> 등록일자</td>
			   <td>
			    <input type=text value="<fmt:formatDate value="${article.writeDate}" />" disabled />
			   </td>   
		  </tr>
		  <tr id="tr_btn_modify"  >
			   <td colspan="2"   align="center" >
			     <input type=button value="수정반영하기"   onClick="fn_modify_article(frmArticle)">
		         <input type=button value="취소"  onClick="backToList(frmArticle)">
			   </td>   
		  </tr>  
		  <tr  id="tr_btn">
		   <td colspan=2 align=center>
			    <input type=button value="수정하기" onClick="fn_enable(this.form)">
			    <input type=button value="삭제하기" onClick="fn_remove_article('${contextPath}/board/removeArticle.do', ${article.articleNO})">
			    <input type=button value="리스트로 돌아가기"  onClick="backToList(this.form)">
			    <!-- 답글쓰기를 클릭하면 fn_reply_form()함술를 호출 하면서 
			                글번호와 답글 요청 주소를 함께전달함 -->
   				<input type=button value="답글쓰기" onClick="fn_reply_form('${contextPath}/board/replyForm.do', ${article.articleNO})">
		   </td>
		  </tr>
	 </table>
 </form>
</body>
</html>






