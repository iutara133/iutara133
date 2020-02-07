package sec03.brd01;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDAO { //DB�۾� ����Ͻ����� ó��
	
	private DataSource dataFactory;
			Connection conn;
			PreparedStatement pstmt;
	
	public BoardDAO() {//��ü ������ �����ڰ� ȣ��Ǹ鼭 Ŀ�ؼ�Ǯ ����
		try{
			Context ctx = new InitialContext();
			Context envContext = (Context)ctx.lookup("java:/comp/env");
			dataFactory = (DataSource)envContext.lookup("jdbc/oracle");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//BoardServiceŬ�������� BoardDAO��  selectAllArticles()�޼ҵ带 ȣ���ϸ�
	//������ SQL���� �̿��� ������ ������ ��ü���� ��ȸ�� �� ��ȯ�մϴ�.
	public List<ArticleVO> selectAllArticles(){
		
		List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
		
		try{
			//Ŀ�ؼ�Ǯ�κ��� Ŀ�ؼǰ�ü ���
			conn = dataFactory.getConnection();
			
			//������ ������ ��ü ���� ��ȸ�ϴ� ����Ŭ�� ������SQL��
			String query = "SELECT LEVEL,articleNO,parentNO,title,content,id,writeDate"
					     + " from t_board"
					     + " START WITH parentNO=0"
					     + " CONNECT BY PRIOR articleNO=parentNO"
					     + " ORDER SIBLINGS BY articleNO DESC";
			/*
			 �� SELECT���� ���� ����
			 
			 1. ���� START WITH parentNO=0
			    -> parentNO�� ���� 0�� ���� �ֻ��� �����̴ٶ�� �ǹ��Դϴ�.
			    -> parentNo�� 0�̸� �ױ��� �ֻ��� �θ���� �Ǵ� ���Դϴ�.
			 
			 2. CONNECT BY PRIOR articleNO=parentNO
			  	-> �� ���� � �θ�۰� ����Ǵ����� ��Ÿ���ϴ�.
			 
			 3. ORDER SIBLINGS BY articleNO DESC
			   -> ���� ������ ��ȸ�� ���� articleNo������ ������������ ���� �Ͽ� �ٽ� �˻�
			 
			 4. select���� LEVEL�����÷���  ������ SQL�� �����,
			    CONNECT BY PRIOR articleNO=parentNO�� ���� �����Ǹ鼭 �� ���� ���̸� ��Ÿ���ϴ�.
			       ����Ŭ���� �˾Ƽ� �θ�ۿ� ���ؼ� ���̸� ����ؼ� LEVEL�� ��ȯ �մϴ�.   
			 
			 5. ���� ������ SQL���� �����ϸ鼭 ����Ŭ�� ��ü �ۿ� ���ؼ� ���������� ��� ���� articleNo������,
			    �ٸ��۵��� parentNo�� ���ؼ� ������ �� �۵��� parentNo�� �� �Ʒ� ��������,
			  articleNo�� ���� �������� ���� �ϴ� ������ ��Ĩ�ϴ�.   
			     
			 */
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int level = rs.getInt("level");//�� ���� ����(����)�� level������ ����
				int articleNO = rs.getInt("articleNO");//�۹�ȣ�� �������̹Ƿ� getInt()�� ���� ������
				int parentNO = rs.getInt("parentNO");//�θ�۹�ȣ
				String title = rs.getString("title");
				String content = rs.getString("content");
				String id = rs.getString("id");
				Date writeDate = rs.getDate("writeDate");
				
				//�˻��� �ϳ��� �������� ArticleVO��ü�� �������� ������.
				ArticleVO article = new ArticleVO();
				article.setLevel(level);
				article.setArticleNO(articleNO);
				article.setParentNO(parentNO);
				article.setTitle(title);
				article.setContent(content);
				article.setId(id);
				article.setWriteDate(writeDate);
				
				//ArrayList�� ArticleVO��ü �߰�
				articlesList.add(article);
			}//while��
			//�ڿ�����
			rs.close();
			pstmt.close();
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		//ArryList��ȯ
		return articlesList;
		
	}//selectAllArticles()
	
}//BoardDAO







