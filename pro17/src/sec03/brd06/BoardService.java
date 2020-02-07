package sec03.brd06;

import java.util.List;

//BoardDAO��ü�� ���� ���� selectAllArticles()�޼ҵ带 ȣ����
//DB�� ����� ��� ���� �˻� �� �������� ������ �ϴ� ���� ���� Ŭ���� 
public class BoardService {

	BoardDAO boardDAO;
	
	public BoardService() {//������
		boardDAO = new BoardDAO();//������ ȣ���..BoardDAO��ü ���� 
	}
	
	//��Ʈ�ѷ����� modArticle�޼ҵ带 ȣ���ϸ鼭
	//�ٽ� BoardDAO�� updateArticle()�޼ҵ带 ȣ���� ����(UPDATE)�� ������(ArticleVO��ü)����
	public void modArticle(ArticleVO article){
		
		boardDAO.updateArticle(article);
		
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

	//BoardController���� removeArticle()�޼ҵ� ȣ��� ..
	//�Ű������� articleNO�� �۹�ȣ�� ���� �޾�... BoardDAO�� selectRemoveArticles()�޼ҵ带 
	//���� ȣ����  �۹�ȣ�� ���� �۰� �� �ڽı� �۹�ȣ�� ���޹޾� ArrayList��  �����մϴ�.
	//�׷� ����  deleteArticle()�޼ҵ带 ȣ���� �۹�ȣ�� ���� �۰� �ڽı��� �����ϰ� �۹�ȣ�� ��ȯ�մϴ�.
	public List<Integer> removeArticle(int articleNO) {
		//���� �����ϱ� �� �۹�ȣ���� ArrayList��ü�� �����մϴ�.
		List<Integer> articleNOList = boardDAO.selectRemoveArticles(articleNO);
		
		//������ �۹�ȣ�� ������ ���� ���� 
		boardDAO.deleteArticle(articleNO);
		
		//������ �۹�ȣ ����� ��Ʈ�ѷ��� ��ȯ��
		return articleNOList;
	}	
	
	
}//BoardServiceŬ���� ��
/*
 ���⼭ ���! �����!
 BoardDAOŬ������ �޼ҵ� �̸��� ���� ���޼ҵ���� �����ϴ�SQL���� ���� ���� �˴ϴ�.
 ���� ��� selectAllArticles()�޼ҵ�� ��ü �� ������ ��ȸ�ϴ� SQL���� �����ϹǷ�
 �޼ҵ��̸��� selectAll�� ��� ���ϴ�.
 */


