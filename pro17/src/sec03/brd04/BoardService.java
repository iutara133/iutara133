package sec03.brd04;

import java.util.List;

//BoardDAO��ü�� ���� ���� selectAllArticles()�޼ҵ带 ȣ����
//DB�� ����� ��� ���� �˻� �� �������� ������ �ϴ� ���� ���� Ŭ���� 
public class BoardService {

	BoardDAO boardDAO;
	
	public BoardService() {//������
		boardDAO = new BoardDAO();//������ ȣ���..BoardDAO��ü ���� 
	}
	
	
	//���ο���� DB�� �߰���Ű�� ���� �޼ҵ� ȣ��!
	//���߰��� �����ϸ� �۹�ȣ�� ��ȯ�ؼ� ��Ʋ�η��� ������.
	public int addArticle(ArticleVO articleVO){
		
		return  boardDAO.insertNewArticle(articleVO);
		
	}
	
	public List<ArticleVO> listArticles(){
		
		//BoardDAO��ü�� selectAllArticles()�޼ҵ带 ȣ���� ��ü ���� �˻��ؼ� ������
		List<ArticleVO>  articleList = boardDAO.selectAllArticles();
		
		return articleList; //BoardController�� ����
	}


	//BoardController���� ȣ���� �޼ҵ�� �󼼺� �۹�ȣ�� �Ű������� ���� �޾�....
	//BoardDAO�� selectArticle()�޼ҵ� ȣ��� ���ڷ� �����Ͽ� �ϳ��� ������ �˻� ��û�� ��.
	//�˻��� ����� ArticleVO��ü�� ��ȯ �޴´�.
	//���� BoardController�� �ٽ�~~ ArticleVO�� ���� �Ѵ�.
	public ArticleVO viewArticle(int articleNO) {
		
		//BoardDAO�� selectArticle()�޼ҵ� ȣ��� ���ڷ� �����Ͽ� �ϳ��� ������ �˻� ��û�� ��.
		//�˻��� ����� ArticleVO��ü�� ��ȯ �޴´�.
		ArticleVO article = boardDAO.selectArticles(articleNO);
		
		return article;//���� BoardController�� �ٽ�~~ ArticleVO�� ���� �Ѵ�.
	}	
	
	
}//BoardServiceŬ���� ��
/*
 ���⼭ ���! �����!
 BoardDAOŬ������ �޼ҵ� �̸��� ���� ���޼ҵ���� �����ϴ�SQL���� ���� ���� �˴ϴ�.
 ���� ��� selectAllArticles()�޼ҵ�� ��ü �� ������ ��ȸ�ϴ� SQL���� �����ϹǷ�
 �޼ҵ��̸��� selectAll�� ��� ���ϴ�.
 */


