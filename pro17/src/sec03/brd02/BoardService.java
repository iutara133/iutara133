package sec03.brd02;

import java.util.List;

//BoardDAO��ü�� ���� ���� selectAllArticles()�޼ҵ带 ȣ����
//DB�� ����� ��� ���� �˻� �� �������� ������ �ϴ� ���� ���� Ŭ���� 
public class BoardService {

	BoardDAO boardDAO;
	
	public BoardService() {//������
		boardDAO = new BoardDAO();//������ ȣ���..BoardDAO��ü ���� 
	}
	
	//BoardController���� ȣ���� �޼ҵ�ν�..
	//�۾��� â���� �Էµ� ������  ArticleVO��ü�� �������� �������� �Ű������� ���� �޾�..
	//�ٽ�~ BoardDAO��ü�� insertNewArticle()�޼ҵ带 ȣ�� �ϸ鼭 �߰��� ��������(ArticleVO)�� ���ڷ� �����Ͽ�
	//DB�� INSERT�۾��� ��� �ϰ� ��.
	public void addArticle(ArticleVO articleVO){
		
		boardDAO.insertNewArticle(articleVO);
		
	}
	
	public List<ArticleVO> listArticles(){
		
		//BoardDAO��ü�� selectAllArticles()�޼ҵ带 ȣ���� ��ü ���� �˻��ؼ� ������
		List<ArticleVO>  articleList = boardDAO.selectAllArticles();
		
		return articleList; //BoardController�� ����
	}	
}
/*
 ���⼭ ���! �����!
 BoardDAOŬ������ �޼ҵ� �̸��� ���� ���޼ҵ���� �����ϴ�SQL���� ���� ���� �˴ϴ�.
 ���� ��� selectAllArticles()�޼ҵ�� ��ü �� ������ ��ȸ�ϴ� SQL���� �����ϹǷ�
 �޼ҵ��̸��� selectAll�� ��� ���ϴ�.
 */


