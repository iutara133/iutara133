package sec02.ex02;

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

// 클라이언트가 웹브라우저를 이용하여 컨트롤러에 요처하면 request객체의 getPathInfo()메소드를 이용해
// 수정 요청 주소인?  /member/modMemberForm.do 와  /member/modeMember.do 를 가져온후 
// if문으로 분기하여 작업을 수행 하도록 MemberController클래스를 작성하자.

// listMembers.jsp페이지에서 회원삭제를 위해 삭제링크를 클릭 했을떄...
// 삭제 요청 주소인? /member/delMember.do 를 가져온 후 
// if문으로 분기하여 작업을 수행 하도록  MemberController클래스를 작성 하자.

@WebServlet("/member/*") //웹브라우저에서 요청시.. 두단계로 요청이 이루어짐
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
			// /modMemberForm.do
			// /modMember.do
			// /delMember.do
			
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
//				request.setAttribute("msg", "deleted");
//				request.setAttribute("msg", "modified");
				//검색한 회원정보(응답메세지)를 보여줄 View페이지 주소 설정
				//= test02폴더의 listMembers.jsp로 포워딩 하기 위해 주소 저장
				nextPage = "/test03/listMembers.jsp";
			
			
			}else if(action.equals("/memberForm.do")){//회원가입 입력페이지로 이동 시켜줘~
			
				nextPage = "/test03/memberForm.jsp";//회원가입 입력페이지(VIEW)주소 저장
			
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
				//새로운 회원정보를 DB의 테이블에 insert에 성공하면 뷰페이지에 보여줄 메세지 저장
				request.setAttribute("msg", "addMember");
				//회원 등록후 다시 회원목록을 검색해서 보여주는 View페이지로 이동 하기 위해..
				//다시~ MemberController.javat서블릿을 재요청할 주소 저장
				nextPage = "/member/listMembers.do";			
					
			//listMembers.jsp페이지에서 수정링크를 클릭 했을때..
			//컨트롤러에 회원정보 수정창 요청시 ID로 회원정보를 조회한후 수정창으로 포워딩 함.
			}else if(action.equals("/modMemberForm.do")){
				
				//수정하기전에 수정할 회원 ID를 전달 받아.. 검색 하기 위함.
				String id = request.getParameter("id");
				
				//수정할 회원 ID에 해당하는 회원 정보 검색
				MemberVO memberInfo = memberDAO.findMember(id);
				
				//수정전 회원 한명의 정보를 검색해서 가져와서 View페이지(수정창)으로 전달 하기 위해
				//request내장객체에 저장
				request.setAttribute("memInfo", memberInfo);
				
				//회원정보 수정창 View페이지로 포워딩 하기 위한 주소 저장
				nextPage = "/test03/modMemberForm.jsp";							
			
			//회원정보 수정창(modMemberForm.jsp)에서 회원수정정보를 입력후 수정하기 버튼 클릭한후..
			// /member/modMember.do주소로  DB테이블에 저장된 회원데이터 수정 요청이 들어오면
			}else if(action.equals("/modMember.do")){
				//요청값얻기(회원수정정보)
				String id = request.getParameter("id");
				String pwd = request.getParameter("pwd");
				String name = request.getParameter("name");
				String email = request.getParameter("email");
				//MemberVO의 각변수에 저장
				MemberVO memberVO = new MemberVO(id, pwd, name, email);
				//DB 회원 테이블의 데이터 수정 요청
				memberDAO.modMember(memberVO);//UPDATE
			    //수정UPDATE에 성공하면 listMembers.jsp에 수정작업완료 메세지를 전달 하기 위해..
				//request에 메세지 저장
				request.setAttribute("msg", "modified");
				//수정후  모든 회원을 검색하여 보여주기 위해 
				//MemberController.java서블릿으로 요청할 주소 저장
				nextPage = "/member/listMembers.do";
				
			}else if(action.equals("/delMember.do")){//삭제 요청이 들어 왔을때
				//삭제할 회원의 ID를 받아옵니다.
				String id = request.getParameter("id");
				
				//삭제할 회원 ID를 이용하여 DB저장된 회원삭제 작업 명령~
				memberDAO.delMember(id);
				
				//삭제에 성공 하면 회원목록창(listMembers.jsp) View페이지에 
				//삭제 완료 메세지를 출력 하기 위해  메세지를 request내장객체 영역에 저장
				request.setAttribute("msg", "deleted");
				
				//삭제 성공후 다시 모든 회원을 검색 하기 위한 요청 주소를 저장
				nextPage = "/member/listMembers.do";
				
			}else{//그외 action변수에 다른 요청 URL이 저장되어 있으면 회원목록을 출력함.
				
				ArrayList membersList = memberDAO.listMembers();
				request.setAttribute("membersList", membersList);
				nextPage = "/test03/listMembers.jsp";
			}
					
			//nextPage변수의 주소를 통해  포워딩 함
			 RequestDispatcher dispatche = 
					 request.getRequestDispatcher(nextPage);
			 dispatche.forward(request, response);
		
//			 <jsp:forward page="/test01/listMembers.jsp">
			 
	}

}




