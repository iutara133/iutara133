package sec03.brd01;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//현재 서블릿은  /board/listArticles.do주소로 요청이 들어오면
//DB에 저장된 글모두를 검색 하는 작업을 수행 하는 서블릿
//@WebServlet("/board/*")
public class BoardController extends HttpServlet{
	
	BoardService boardService;
	
	//서블릿 초기화시 BoardService객체를 생성 함.
	@Override
	public void init() throws ServletException {
		boardService = new BoardService();
	}
	
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
	
		//뷰페이지 주소를 저장할 변수 
		String nextPage = "";
		
		//한글처리
		request.setCharacterEncoding("UTF-8");
		//클라이언트의 웹브라우저로 응답할 데이터유형 설정
		response.setContentType("text/html; charset=utf-8");
		//요청 URL을 얻자
		String action = request.getPathInfo();
		System.out.println("action : " + action);
		
		try{
			List<ArticleVO> articlesList;
			
			if(action == null){//요청 URL이 없을떄..
				
				articlesList = boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage = "/board01/listArticles.jsp";
				
			}else if(action.equals("/listArticles.do")){//요청URL이 
														//DB에 저장된 모든 글을 검색하는 요청을 했을떄
				articlesList = boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage = "/board01/listArticles.jsp";
			}
			//디스패치 방식으로 뷰페이지 재요청 (포워딩)
			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}	
	
}







