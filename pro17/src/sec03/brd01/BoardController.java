package sec03.brd01;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//���� ������  /board/listArticles.do�ּҷ� ��û�� ������
//DB�� ����� �۸�θ� �˻� �ϴ� �۾��� ���� �ϴ� ����
//@WebServlet("/board/*")
public class BoardController extends HttpServlet{
	
	BoardService boardService;
	
	//���� �ʱ�ȭ�� BoardService��ü�� ���� ��.
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
	
		//�������� �ּҸ� ������ ���� 
		String nextPage = "";
		
		//�ѱ�ó��
		request.setCharacterEncoding("UTF-8");
		//Ŭ���̾�Ʈ�� ���������� ������ ���������� ����
		response.setContentType("text/html; charset=utf-8");
		//��û URL�� ����
		String action = request.getPathInfo();
		System.out.println("action : " + action);
		
		try{
			List<ArticleVO> articlesList;
			
			if(action == null){//��û URL�� ������..
				
				articlesList = boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage = "/board01/listArticles.jsp";
				
			}else if(action.equals("/listArticles.do")){//��ûURL�� 
														//DB�� ����� ��� ���� �˻��ϴ� ��û�� ������
				articlesList = boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage = "/board01/listArticles.jsp";
			}
			//����ġ ������� �������� ���û (������)
			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}	
	
}







