package sec03.brd02;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDAO { //DB작업 비즈니스로직 처리
	
	private DataSource dataFactory;
			Connection conn;
			PreparedStatement pstmt;
	
	public BoardDAO() {//객체 생성시 생성자가 호출되면서 커넥션풀 생성
		try{
			Context ctx = new InitialContext();
			Context envContext = (Context)ctx.lookup("java:/comp/env");
			dataFactory = (DataSource)envContext.lookup("jdbc/oracle");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	//DB에 새글을 추가 하기 위해 DB에 존재 하는 가장 최신 글번호를 검색해서 제공 하는 메소드
	private int getNewArticleNO(){
		try {
			conn = dataFactory.getConnection();
			//글번호중 가장 큰 글번호를 조회 하는 SQL문
			String query = "SELECT max(articleNO) from t_board";
			
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()){//최신글번호가 검색되었다면
				return (rs.getInt(1) + 1);//가장 큰 글번호에 1을 더한 번호를 반환함 : 이유-> 새글글번호로 지정 하기 위함
			}
			//자원해제
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	//DB에 새글정보를 추가하는 메소드
	//insertNewArticle메소드의 SQL문(insert문)을 실행하기 전에..
	//바로위의 getNewArticleNO()메소드를 호출해.. DB에 추가할 새글번호(최신글번호  + 1)을 얻어 온다.
	public void insertNewArticle(ArticleVO article){
		try {
			conn = dataFactory.getConnection();
			int articleNO = getNewArticleNO(); //DB에 추가할 새글번호를 얻어옴
			int parentNO = article.getParentNO();
			String title = article.getTitle();
			String content = article.getContent();
			String id = article.getId();
			String imageFileName = article.getImageFileName();
			
			String query = "INSERT INTO t_board (articleNO, parentNO, title, "
					     + "content, imageFileName, id)"
						 + " VALUES(?,?,?,?,?,?)";
		
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, articleNO);
			pstmt.setInt(2, parentNO);
			pstmt.setString(3, title);
			pstmt.setString(4, content);
			pstmt.setString(5, imageFileName);
			pstmt.setString(6, id);
			
			pstmt.executeUpdate();
			
			pstmt.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	//BoardService클래스에서 BoardDAO의  selectAllArticles()메소드를 호출하면
	//계층형 SQL문을 이용해 계층형 구조로 전체글을 조회한 후 반환합니다.
	public List<ArticleVO> selectAllArticles(){
		
		List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
		
		try{
			//커넥션풀로부터 커넥션객체 얻기
			conn = dataFactory.getConnection();
			
			//계층형 구조로 전체 글을 조회하는 오라클의 계층형SQL문
			String query = "SELECT LEVEL,articleNO,parentNO,title,content,id,writeDate"
					     + " from t_board"
					     + " START WITH parentNO=0"
					     + " CONNECT BY PRIOR articleNO=parentNO"
					     + " ORDER SIBLINGS BY articleNO DESC";
			/*
			 위 SELECT구문 참고 설명
			 
			 1. 먼저 START WITH parentNO=0
			    -> parentNO의 값이 0인 글이 최상위 계층이다라는 의미입니다.
			    -> parentNo가 0이면 그글은 최상위 부모글이 되는 것입니다.
			 
			 2. CONNECT BY PRIOR articleNO=parentNO
			  	-> 각 글이 어떤 부모글과 연결되는지를 나타냅니다.
			 
			 3. ORDER SIBLINGS BY articleNO DESC
			   -> 계층 구조로 조회된 글을 articleNo순으로 내림차순으로 정렬 하여 다시 검색
			 
			 4. select문의 LEVEL가상컬럼은  계층형 SQL문 실행시,
			    CONNECT BY PRIOR articleNO=parentNO로 글이 나열되면서 각 글의 깊이를 나타냅니다.
			       오라클에서 알아서 부모글에 대해서 깊이를 계산해서 LEVEL로 반환 합니다.   
			 
			 5. 따라서 계층형 SQL문을 실행하면서 오라클이 전체 글에 대해서 내부적으로 모든 글의 articleNo에대해,
			    다른글들의 parentNo와 비교해서 같으면 그 글들의 parentNo의 글 아래 정렬한후,
			  articleNo의 내림 차순으로 정렬 하는 과정을 거칩니다.   
			     
			 */
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int level = rs.getInt("level");//각 글의 깊이(계층)를 level변수에 저장
				int articleNO = rs.getInt("articleNO");//글번호는 숫자형이므로 getInt()로 값을 가져옴
				int parentNO = rs.getInt("parentNO");//부모글번호
				String title = rs.getString("title");
				String content = rs.getString("content");
				String id = rs.getString("id");
				Date writeDate = rs.getDate("writeDate");
				
				//검색한 하나의 글정보를 ArticleVO객체의 각변수에 저장함.
				ArticleVO article = new ArticleVO();
				article.setLevel(level);
				article.setArticleNO(articleNO);
				article.setParentNO(parentNO);
				article.setTitle(title);
				article.setContent(content);
				article.setId(id);
				article.setWriteDate(writeDate);
				
				//ArrayList에 ArticleVO객체 추가
				articlesList.add(article);
			}//while문
			//자원해제
			rs.close();
			pstmt.close();
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		//ArryList반환
		return articlesList;
		
	}//selectAllArticles()
	
}//BoardDAO







