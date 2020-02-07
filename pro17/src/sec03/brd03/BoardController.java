package sec03.brd03;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

//��Ʈ�ѷ� ����ϴ� BoardController���� Ŭ���� �Դϴ�.
//�ֿ俪��:
//action������ ���� /articleForm.do�̸� �۾���â�� ���������� �̿��� ��û �ϰ�,
//action������ ���� /addArticle.do�̸� ���� �������� �Է��� �۾������� DB�� �߰��ϴ� �۾��� ��.
//�Ʒ��� �츮 �����ڰ� ���� ���� upload()�޼ҵ带 ȣ���� �۾���â���� ���۵� �۰���������
//Map��  key/value ������ ���� �մϴ�.
//������ ÷���� ��� ���� ���� �̸��� Map�� ������ �� ÷���� ������ ����ҿ� ���ε� �մϴ�.
//upload()�޼ҵ带 ȣ�����Ŀ��� ��ȯ�� Map���� ���������� ���� �ɴϴ�.
//�׷����� serviceŬ������ addArticle()�޼ҵ��� ���ڷ� ���� ������ �����ϸ� ������ ��ϵ�.

//@WebServlet("/board/*") // /board/articleForm.do 
public class BoardController extends HttpServlet{
	
	//�۾��� ȭ�鿡�� �۳����� �ۼ��ҋ� ÷���� ������ ���ε� ��� ��ġ�� ����� ����
	private static String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	
	
	BoardService boardService;
	ArticleVO articleVO;
	
	
	
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
		String action = request.getPathInfo();// /articleForm.do
											  // /addArticle.do
		
		System.out.println("action : " + action);
		
		try{
			List<ArticleVO> articlesList;
			
			if(action == null){//��û URL�� ������..
				
				articlesList = boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage = "/board02/listArticles.jsp";
				
			}else if(action.equals("/listArticles.do")){//��ûURL�� 
														//DB�� ����� ��� ���� �˻��ϴ� ��û�� ������
				articlesList = boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage = "/board02/listArticles.jsp";
			
			//listArticles.jsp���� �۾��� ��ũ�� Ŭ�� ������..
			//���� ���� �ִ� ȭ������ �̵� ������~ ��� ��û�� ��� ������..
			}else if(action.equals("/articleForm.do")){
				
				nextPage = "/board02/articleForm.jsp";//�۾��� ȭ��
				
			//articleForm.jsp���� �߰��� ���۳����� �Է��ϰ�  �۾��� ��ư�� ��������..
			//���߰� ��û�� ��� ������..
			}else if(action.equals("/addArticle.do")){
		
				Map<String, String> articleMap = upload(request, response);	
			
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
		
				articleVO = new ArticleVO();
				articleVO.setParentNO(0);//�߰��� ������ �θ�۹�ȣ�� 0���� ����
				articleVO.setId("hong");//�߰��� ���� �ۼ��� ID��  hong���� ����
				articleVO.setTitle(title);//�߰��� ���� ���� ����
				articleVO.setContent(content);//�Է��Ͽ� �߰��� ���� ���� ����
				articleVO.setImageFileName(imageFileName);//���ε��� ���ϸ��� ����
				
				//t_board���̺� ���ο� ���� �߰����� �߰��� ���ۿ� ���� �۹�ȣ�� ��ȯ�޴´�.
				int articleNO = boardService.addArticle(articleVO);
				
				//������ ÷���� ��쿡�� ���� �մϴ�.
				if(imageFileName != null && imageFileName.length() != 0){
					
					//temp������ �ӽ÷� ���ε�� ���� ��ü�� ������.
					 File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					 
					 
					 File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					 //C:\\board\\article_image�� ��� ������ �۹�ȣ�� Ǯ���� ���� �մϴ�.
					 destDir.mkdirs();
					 
					 //temp������ ������ �۹�ȣ�� �̸����� �ϴ� ������ �̵���ŵ�ϴ�.
					 FileUtils.moveFileToDirectory(srcFile, destDir, true);	
				}
				
				PrintWriter pw = response.getWriter();
				pw.print("<script>" 
				         +"  alert('������ �߰��߽��ϴ�.');" 
						 +" location.href='"+request.getContextPath()+"/board/listArticles.do';"
				         +"</script>");

				return;
				
			}				
			//����ġ ������� �������� ���û (������)
			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);			
		}catch(Exception e){
			e.printStackTrace();
		}	
	}//doHandle�޼ҵ� ���κ�	
	
	//���� ���ε� ó���� ���� upload�޼ҵ�
	private Map<String, String> upload(HttpServletRequest request,
									   HttpServletResponse response) 
									   throws ServletException,IOException{
		
		Map<String, String> articleMap = new HashMap<String,String>();
		
		String encoding = "utf-8";
		
		//�۾��⸦ �Ҷ� ÷���� �̹����� ������ ���� ��ο� ���� ���� �ϱ� ���� File��ü ����
		File currentDirPath = new File(ARTICLE_IMAGE_REPO);
		
		//���ε��� ���� �����͸� �ӽ÷� ������ ��ü �޸� ����
		DiskFileItemFactory factory = new DiskFileItemFactory();
		
		//���� ���ε��.. ����� �ӽ� �޸� �ִ� ũ�� 1�ް� ����Ʈ�� ����
		factory.setSizeThreshold(1024*1024*1);
		
		//�ӽø޸𸮿� ���Ͼ��ε��.. ������ 1�ް� ����Ʈ ũ�⸦ �ѱ� ��� ���� ���ε�� ���� ��θ� ������
		factory.setRepository(currentDirPath);
		
		//����
		//DiskFileitemFactoryŬ������ ���ε� ������ ũ�Ⱑ ������ ũ�⸦ �ѱ� ��������
		//���ε��� ���� �����͸� �ӽ� �޸���? DiskFileItemFactory��ü �޸�(�ӽø޸�)�� �����ϰ�
		//������ ũ�⸦ �ѱ� ��� ���ε�� ��ο� ���Ϸ� ���ε�(����) �Ѵ�.
		
		//���Ͼ��ε��� �޸𸮸� ������ �U���� ���� �޾� ������!! ���Ͼ��ε带 ó���� ��ü ����
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		try {
			//���ε��� ���Ͽ� ���� ��û������ ����Ǿ� �ִ� request��ü��?
			//ServletFileUpload��ü�� parseRequest�޼ҵ� ȣ���... ���ڷ� ���� �ϸ�
			//request��ü�� ����Ǿ� �ִ� ������ �Ľ��ؼ�...
			//DiskFileItem��ü�� ������ �� ~ 
			//DiskFileItem��ü���� ArrayList�� �߰� �մϴ�.
			//����~ ArrayList�� ��ȯ��.
			List items = upload.parseRequest(request);
			
			//ArrayListũ�� ��ŭ(DiskFileItem��ü�� ������ŭ)�ݺ�
			for(int i=0; i<items.size(); i++){
				
				//ArrayList�������� �迭����.. DiskFileItem��ü�� ����(������ �ϳ��� ����)�� ��´�.
				FileItem fileItem = (FileItem)items.get(i);
				
				//DiskFileItem��ü�� ������  ���� �������� �ƴҰ�� 
				if(fileItem.isFormField()){
					
					System.out.println(fileItem.getFieldName() + "=" + fileItem.getString(encoding));
					
					//articleForm.jsp���������� �Է��� ������, �۳��븸 ����~ HsahMap��  key,value�������� ������
					//HashMap�� ����� �������� �� -> {title=�Է��ѱ�����, content=�Է��ѱ۳���}
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
					
				}else{//DiskFileItem��ü�� ������ ���� �������� ���!!
					
					System.out.println("�Ķ���͸� : " + fileItem.getFieldName());
					System.out.println("���ε��� ���ϸ� : " + fileItem.getName());
					System.out.println("���ε��� ���� ũ�� : " + fileItem.getSize() + "bytes");
					
					//articleForm.jsp���� �Է��� ������, �۳���, ��û�� ���ε� ���� ������...
					//��� ������? HashMap��  key,value �������� ������.
					//{imageFileName=���ε��������̸�}
					articleMap.put(fileItem.getFieldName(), fileItem.getName());
					
					//��ü : ���ε��� ������ ���� �� ���  ���ε��� ������ ���� �̸��� �̿��� ����ҿ� ���ε���
					//���ε��� ���� ũ�Ⱑ 0���� ũ�ٸ�?(���ε��� ������ ���� �Ѵٸ�?)
					if(fileItem.getSize() > 0){
						
						//���ε��� ���ϸ��� ��� �ڿ������� \\���ڿ��� ��� �ִ��� �� �ε��� ��ġ�� �˷��ִµ�
						int idx = fileItem.getName().lastIndexOf("\\");
						
						if(idx == -1){
							idx = fileItem.getName().lastIndexOf("/"); // -1��� 
						}	
						//���ε��� ���ϸ� ���
						String fileName = fileItem.getName().substring(idx + 1);
						
						//���ε��� ���� ��� + ���ϸ� �� �̿��Ͽ� ���ε��� ���Ͽ� ���� ��ü ����
						File uploadFile = new File(currentDirPath + "\\temp\\" + fileName);
						
						//���� ���� ���ε�
						fileItem.write(uploadFile);
					}//end if
				}//end if
			}//end for
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articleMap;//�ؽ��� ����
	}//upload�޼ҵ� ��
	
}//BoardController�� �� 







