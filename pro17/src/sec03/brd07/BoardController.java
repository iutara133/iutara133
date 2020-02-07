package sec03.brd07;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

//��۵� ���� �̹Ƿ� ��� ��ɵ� ���� ���� ��ɰ� ����մϴ�.
//�ٸ����� ���â ��û(/replyForm.do)��  �̸� �θ�� ��ȣ�� parentNO�Ӽ�����
//session���� ������ ���� �� ����,
//����� �ۼ����� ��� ��� ��û(/addRply.do)�ϸ�  session��������
//parentNo�� ������  t_board���̺� �߰��Ѵٴ� �� �Դϴ�.
//@WebServlet("/board/*")
public class BoardController extends HttpServlet {
	private static String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	BoardService boardService;
	ArticleVO articleVO;
	HttpSession session;//��ۿ� ���� �θ� �۹�ȣ�� �����ϱ� ���� ������ �����.

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		boardService = new BoardService();
		articleVO = new ArticleVO();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}
									//board/removeArticle.do
	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nextPage = "";
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		String action = request.getPathInfo(); // /replyForm.do
		System.out.println("action:" + action);
		try {
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>();
			if (action == null) {
				articlesList = boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage = "/board06/listArticles.jsp";
			} else if (action.equals("/listArticles.do")) {
				articlesList = boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage = "/board06/listArticles.jsp";
			} else if (action.equals("/articleForm.do")) {
				nextPage = "/board06/articleForm.jsp";
			} else if (action.equals("/addArticle.do")) {
				int articleNO = 0;
				Map<String, String> articleMap = upload(request, response);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");

				articleVO.setParentNO(0);
				articleVO.setId("hong");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				articleNO = boardService.addArticle(articleVO);
				if (imageFileName != null && imageFileName.length() != 0) {
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
					srcFile.delete();
				}
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('������ �߰��߽��ϴ�.');" + " location.href='" + request.getContextPath()
						+ "/board/listArticles.do';" + "</script>");

				return;
			} else if (action.equals("/viewArticle.do")) {
				String articleNO = request.getParameter("articleNO");
				articleVO = boardService.viewArticle(Integer.parseInt(articleNO));
				request.setAttribute("article", articleVO);
				nextPage = "/board06/viewArticle.jsp";
			} else if (action.equals("/modArticle.do")) {
				Map<String, String> articleMap = upload(request, response);
				int articleNO = Integer.parseInt(articleMap.get("articleNO"));
				articleVO.setArticleNO(articleNO);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				articleVO.setParentNO(0);
				articleVO.setId("hong");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				boardService.modArticle(articleVO);
				if (imageFileName != null && imageFileName.length() != 0) {
					String originalFileName = articleMap.get("originalFileName");
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
					;
					File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + originalFileName);
					oldFile.delete();
				}
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('���� �����߽��ϴ�.');" + " location.href='" + request.getContextPath()
						+ "/board/viewArticle.do?articleNO=" + articleNO + "';" + "</script>");
				return;
			
			//���� ��û ��� ������..
			}else if(action.equals("/removeArticle.do")){
				//������ �۹�ȣ�� request���� �������� 
				int articleNO = Integer.parseInt(request.getParameter("articleNO"));
				
				//������ �۹�ȣ(articleNO)�� ���� ���� �������� ������ �θ�۰� �ڽı��� 
				//�۹�ȣ����(articleNO����) list�� ��Ƽ� ���� �޴´�.
				//���� �޴� ����? DB�� ����� ���� ������ ��  �ױ� ��ȣ�� �̷���� �̹��� ������������
				//��� ���� �ϱ� ����.
				List<Integer> articleNOList = boardService.removeArticle(articleNO);
				
				for(int _articleNO :  articleNOList){
					
					File imgDir = new File(ARTICLE_IMAGE_REPO + "\\" + _articleNO);
					
					if(imgDir.exists()){
						//DB�� ������ �۵��� �̹��� ���� ������ ������.
						FileUtils.deleteDirectory(imgDir);
					}	
				}
				
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('���� �����߽��ϴ�.');" + " location.href='"  
						 + request.getContextPath() + "/board/listArticles.do';"
						 +"</script>");
				return;
				
			}else if(action.equals("/replyForm.do")){//����� �ۼ��Ҽ� �ִ� â��û
				//����� �ۼ��Ҽ� �ִ� â ��û��...
				//�̸� �θ� �۹�ȣ�� ���ǿ����� ����
				int parentNO = Integer.parseInt(request.getParameter("parentNO"));
				//���ǿ��� �ϳ� ����
				session = request.getSession();
				//���ǿ����� �θ�۹�ȣ ����
				session.setAttribute("parentNO", parentNO);
				//���û�� ���� ����
				nextPage = "/board06/replyForm.jsp";
			
			//replyForm.jsp���� ��۳����� �Է��ϰ� ��۹ݿ��ϱ� ��ưŬ���� ��û����
			}else if(action.equals("/addReply.do")){
				//������ session������ �����  parentNo�� ���� �ɴϴ�.
				session = request.getSession(true);
				int parentNO = (Integer)session.getAttribute("parentNO");
				session.removeAttribute("parentNO");//session������ ����� �θ�۹�ȣ ���� 
				
				Map<String, String> articleMap = upload(request, response);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				
				//����� �θ�۹�ȣ�� ����
				articleVO.setParentNO(parentNO);
				//��� �ۼ��� ID�� lee�� ����
				articleVO.setId("lee");
				//��� ������ ������ ����
				articleVO.setTitle(title);
				//��� ���� ����
				articleVO.setContent(content);
				//��� �ۼ��� ÷���� �̹������ϸ� ����
				articleVO.setImageFileName(imageFileName);
				
				//�ۼ��� ��� ��ü ������(ArticleVO��ü�� ������)�� ?DB�� INSERT�ϱ� ����
				//BoardService�� �޼ҵ� ȣ��� ����
				int articleNO = boardService.addReply(articleVO);
				
				//��ۿ� ÷���� �̹����� temp�������� ��� ��ȣ ������ �̵��մϴ�.
				if (imageFileName != null && imageFileName.length() != 0) {
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
				
				PrintWriter pw = response.getWriter();
				pw.print("<script>" + "  alert('����� �߰� �߽��ϴ�.');" + " location.href='"  
						 + request.getContextPath() 
						 + "/board/viewArticle.do?articleNO="
						 + articleNO + "';" +"</script>");
				return;
	
				
			}

			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String, String> upload(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String> articleMap = new HashMap<String, String>();
		String encoding = "utf-8";
		File currentDirPath = new File(ARTICLE_IMAGE_REPO);
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(currentDirPath);
		factory.setSizeThreshold(1024 * 1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List items = upload.parseRequest(request);
			for (int i = 0; i < items.size(); i++) {
				FileItem fileItem = (FileItem) items.get(i);
				if (fileItem.isFormField()) {
					System.out.println(fileItem.getFieldName() + "=" + fileItem.getString(encoding));
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
				} else {
					System.out.println("�Ķ���͸�:" + fileItem.getFieldName());
					//System.out.println("���ϸ�:" + fileItem.getName());
					System.out.println("����ũ��:" + fileItem.getSize() + "bytes");
					//articleMap.put(fileItem.getFieldName(), fileItem.getName());
					if (fileItem.getSize() > 0) {
						int idx = fileItem.getName().lastIndexOf("\\");
						if (idx == -1) {
							idx = fileItem.getName().lastIndexOf("/");
						}

						String fileName = fileItem.getName().substring(idx + 1);
						System.out.println("���ϸ�:" + fileName);
						//�ͽ��÷η����� ���ε� ������ ��� ���� �� map�� ���ϸ� ����
						articleMap.put(fileItem.getFieldName(), fileName);  
						File uploadFile = new File(currentDirPath + "\\temp\\" + fileName);
						fileItem.write(uploadFile);

					} // end if
				} // end if
			} // end for
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articleMap;
	}

}
