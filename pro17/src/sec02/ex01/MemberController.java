package sec02.ex01;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.net.aso.r;

//컨트롤러 역할을 하는 MemberController서블릿 클래스.
//이 컨트롤러에서는 request의  getPathInfo()메소드를 이용해 두단계로 이루어진 요청주소를 가져 옵니다.
//action변수 값에 따라 if문을 분기해서 요청한 작업을 수행합니다.

// listMembers.jsp페이지에서 회원가입 링크 클릭시...
// /member/memberForm.do 주소로 요청이 들어옴 (회원가입 입력 페이지로 이동 시켜줘~) 

// memberForm.jsp페이지에서 DB에 추가할 회원정보 입력후..가입하기 버튼 클릭시...
// /member/addMember.do 주소로 요청이 들어옴(DB에 새로운 회원 추가해줘~)

//@WebServlet("/member/*") //웹브라우저에서 요청시.. 두단계로 요청이 이루어짐
public class MemberController extends HttpServlet {

	MemberDAO memberDAO;

	// init()메소드에서 MemberDAO객체를 생성해서 초기화
	@Override
	public void init() throws ServletException {
		memberDAO = new MemberDAO();
	}
	// alt+shift+s v doGet doPost메소드 오버라이딩
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
					throws ServletException, IOException {
		doHandle(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
					throws ServletException, IOException {
		doHandle(request, response);
	}

	protected void doHandle(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
			//MVC중에 View주소 저장할 용도
			String nextPage = null;
		
			//한글처리
			request.setCharacterEncoding("UTF-8");
			//응답할 데이터 유형 설정
			response.setContentType("text/html;charset=utf-8");
			
			//클라이언트의 2단계의 요청주소값 얻기
			String action = request.getPathInfo();
			// /listMember.do
			// /memberForm.do
			// /addMember.do
			
			System.out.println("action : " + action);
			
			//action변수의 값에따라 if문을 분기해서 요청한 작업을 수행하는데..
			//만약 action변수의 값이 null이거나   /listMembers.do인 경우에 회원검색기능을 수행함.
			if(action == null || action.equals("/listMembers.do")){
				
				//MemberDAO의 listMembers()메소드를 호출하여 회원정보 조회요청에 대해...
				//DB로 부터 검색한 회원정보를 ArrayList로 반환 받습니다.
				ArrayList membersList = memberDAO.listMembers();
				
				//이때 DB로부터 검색한 회원정보들 (ArrayList에 담긴 MemberVO객체들)을 View페이지로
				//전달 하기 위해 임시 저장 공간인 request내장객체 영역에 저장(바인딩)함.
				request.setAttribute("membersList", membersList);
				
				//검색한 회원정보(응답메세지)를 보여줄 View페이지 주소 설정
				//= test02폴더의 listMembers.jsp로 포워딩 하기 위해 주소 저장
				nextPage = "/test02/listMembers.jsp";
			
			
			}else if(action.equals("/memberForm.do")){//회원가입 입력페이지로 이동 시켜줘~
			
				nextPage = "/test02/memberForm.jsp";//회원가입 입력페이지(VIEW)주소 저장
			
			}else if(action.equals("/addMember.do")){//입력한 새로운 회원정보를 DB에 추가 해줘~
				
				//요청값들을(입력한 새로운 회원정보들을) request영역에서 얻기
				String id = request.getParameter("id");
				String pwd = request.getParameter("pwd");
				String name = request.getParameter("name");
				String email = request.getParameter("email");
				
				//MemberVO에 저장
				MemberVO memberVO = new MemberVO(id, pwd, name, email);
				
				//요청된 회원 정보를 DB의 테이블에 insert하기 위해 메소드 호출
				memberDAO.addMember(memberVO);
				
				//회원 등록후 다시 회원목록을 검색해서 보여주는 View페이지로 이동 하기 위해..
				//다시~ MemberController.javat서블릿을 재요청할 주소 저장
				nextPage = "/member/listMembers.do";			
					
			}else{//그외 action변수에 다른 요청 URL이 저장되어 있으면 회원목록을 출력함.
				
				ArrayList membersList = memberDAO.listMembers();
				request.setAttribute("membersList", membersList);
				nextPage = "/test02/listMembers.jsp";
			}
					
			//nextPage변수의 주소를 통해  포워딩 함
			 RequestDispatcher dispatche = 
					 request.getRequestDispatcher(nextPage);
			 dispatche.forward(request, response);
		
//			 <jsp:forward page="/test01/listMembers.jsp">
			 
	}

}




