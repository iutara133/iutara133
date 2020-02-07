package sec03.brd01;

import java.util.List;

//BoardDAO객체를 생성 한후 selectAllArticles()메소드를 호출해
//DB에 저장된 모든 글을 검색 해 가져오는 역할을 하는 서비스 관련 클래스 
public class BoardService {

	BoardDAO boardDAO;
	
	public BoardService() {//생성자
		boardDAO = new BoardDAO();//생성자 호출시..BoardDAO객체 생성 
	}
	
	public List<ArticleVO> listArticles(){
		
		//BoardDAO객체의 selectAllArticles()메소드를 호출해 전체 글을 검색해서 가져옴
		List<ArticleVO>  articleList = boardDAO.selectAllArticles();
		
		return articleList; //BoardController로 리턴
	}	
}
/*
 여기서 잠깐! 쉬어가기!
 BoardDAO클래스의 메소드 이름은 보통 각메소드들이 실행하는SQL문에 의해 결정 됩니다.
 예를 들어 selectAllArticles()메소드는 전체 글 정보를 조회하는 SQL문을 실행하므로
 메소드이름에 selectAll이 들어 갑니다.
 */


