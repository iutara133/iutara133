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

//컨트롤러 당담하는 BoardController서블릿 클래스 입니다.
//주요역할:
//action변수의 값이 /articleForm.do이면 글쓰기창을 웹브라우저를 이용해 요청 하고,
//action변수의 값이 /addArticle.do이면 다음 과정으로 입력한 글쓴내용을 DB에 추가하는 작업을 함.
//아래에 우리 개발자가 직접 만든 upload()메소드를 호출해 글쓰기창에서 전송된 글관련정보를
//Map에  key/value 쌍으로 저장 합니다.
//파일을 첨부한 경우 먼저 파일 이름을 Map에 저장한 후 첨부한 파일을 저장소에 업로드 합니다.
//upload()메소드를 호출한후에는 반환한 Map에서 새글정보를 가져 옵니다.
//그런다음 service클래스의 addArticle()메소드의 인자로 새글 정보를 전달하면 새글이 등록됨.

//@WebServlet("/board/*") // /board/articleForm.do 
public class BoardController extends HttpServlet{
	
	//글쓰기 화면에서 글내용을 작성할떄 첨부한 파일의 업로드 경로 위치를 상수로 선언
	private static String ARTICLE_IMAGE_REPO = "C:\\board\\article_image";
	
	
	BoardService boardService;
	ArticleVO articleVO;
	
	
	
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
		String action = request.getPathInfo();// /articleForm.do
											  // /addArticle.do
		
		System.out.println("action : " + action);
		
		try{
			List<ArticleVO> articlesList;
			
			if(action == null){//요청 URL이 없을떄..
				
				articlesList = boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage = "/board02/listArticles.jsp";
				
			}else if(action.equals("/listArticles.do")){//요청URL이 
														//DB에 저장된 모든 글을 검색하는 요청을 했을떄
				articlesList = boardService.listArticles();
				request.setAttribute("articlesList", articlesList);
				nextPage = "/board02/listArticles.jsp";
			
			//listArticles.jsp에서 글쓰기 링크를 클릭 했을떄..
			//글을 쓸수 있는 화면으로 이동 시켜줘~ 라는 요청이 들어 왔을떄..
			}else if(action.equals("/articleForm.do")){
				
				nextPage = "/board02/articleForm.jsp";//글쓰기 화면
				
			//articleForm.jsp에서 추가할 새글내용을 입력하고  글쓰기 버튼을 눌렀을떄..
			//글추가 요청이 들어 왔을때..
			}else if(action.equals("/addArticle.do")){
		
				Map<String, String> articleMap = upload(request, response);	
			
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
		
				articleVO = new ArticleVO();
				articleVO.setParentNO(0);//추가할 새글의 부모글번호를 0으로 저장
				articleVO.setId("hong");//추가할 새글 작성자 ID를  hong으로 저장
				articleVO.setTitle(title);//추가할 새글 제목 저장
				articleVO.setContent(content);//입력하여 추가할 새글 내용 저장
				articleVO.setImageFileName(imageFileName);//업로드할 파일명을 저장
				
				//t_board테이블에 새로운 글을 추가한후 추가한 새글에 대한 글번호를 반환받는다.
				int articleNO = boardService.addArticle(articleVO);
				
				//파일을 첨부한 경우에만 수행 합니다.
				if(imageFileName != null && imageFileName.length() != 0){
					
					//temp폴더에 임시로 업로드된 파일 객체를 생성함.
					 File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					 
					 
					 File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					 //C:\\board\\article_image의 경로 하위에 글번호로 풀더를 생성 합니다.
					 destDir.mkdirs();
					 
					 //temp폴더의 파일을 글번호를 이름으로 하는 폴더로 이동시킵니다.
					 FileUtils.moveFileToDirectory(srcFile, destDir, true);	
				}
				
				PrintWriter pw = response.getWriter();
				pw.print("<script>" 
				         +"  alert('새글을 추가했습니다.');" 
						 +" location.href='"+request.getContextPath()+"/board/listArticles.do';"
				         +"</script>");

				return;
				
			}				
			//디스패치 방식으로 뷰페이지 재요청 (포워딩)
			RequestDispatcher dispatch = request.getRequestDispatcher(nextPage);
			dispatch.forward(request, response);			
		}catch(Exception e){
			e.printStackTrace();
		}	
	}//doHandle메소드 끝부분	
	
	//파일 업로드 처리를 위한 upload메소드
	private Map<String, String> upload(HttpServletRequest request,
									   HttpServletResponse response) 
									   throws ServletException,IOException{
		
		Map<String, String> articleMap = new HashMap<String,String>();
		
		String encoding = "utf-8";
		
		//글쓰기를 할때 첨부한 이미지를 저장할 폴더 경로에 대해 접근 하기 위한 File객체 생성
		File currentDirPath = new File(ARTICLE_IMAGE_REPO);
		
		//업로드할 파일 데이터를 임시로 저장할 객체 메모리 생성
		DiskFileItemFactory factory = new DiskFileItemFactory();
		
		//파일 업로드시.. 사용할 임시 메모리 최대 크기 1메가 바이트로 지정
		factory.setSizeThreshold(1024*1024*1);
		
		//임시메모리에 파일업로드시.. 지정한 1메가 바이트 크기를 넘길 경우 실제 업로드될 파일 경로를 지정함
		factory.setRepository(currentDirPath);
		
		//참고
		//DiskFileitemFactory클래스는 업로드 파일의 크기가 지정한 크기를 넘기 전까지는
		//업로드한 파일 데이터를 임시 메모리인? DiskFileItemFactory객체 메모리(임시메모리)에 저장하고
		//지정한 크기를 넘길 경우 업로드될 경로에 파일로 업로드(저장) 한다.
		
		//파일업로드할 메모리를 생성자 쪾으로 전달 받아 저장한!! 파일업로드를 처리할 객체 생성
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		try {
			//업로드할 파일에 대한 요청정보가 저장되어 있는 request객체를?
			//ServletFileUpload객체의 parseRequest메소드 호출시... 인자로 전달 하면
			//request객체에 저장되어 있는 정보를 파싱해서...
			//DiskFileItem객체에 저장한 후 ~ 
			//DiskFileItem객체들을 ArrayList에 추가 합니다.
			//그후~ ArrayList를 반환함.
			List items = upload.parseRequest(request);
			
			//ArrayList크기 만큼(DiskFileItem객체의 갯수만큼)반복
			for(int i=0; i<items.size(); i++){
				
				//ArrayList가변길이 배열에서.. DiskFileItem객체의 정보(아이템 하나의 정보)를 얻는다.
				FileItem fileItem = (FileItem)items.get(i);
				
				//DiskFileItem객체의 정보가  파일 아이템이 아닐경우 
				if(fileItem.isFormField()){
					
					System.out.println(fileItem.getFieldName() + "=" + fileItem.getString(encoding));
					
					//articleForm.jsp페이지에서 입력한 글제목, 글내용만 따로~ HsahMap에  key,value형식으로 저장함
					//HashMap에 저장된 데이터의 예 -> {title=입력한글제목, content=입력한글내용}
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
					
				}else{//DiskFileItem객체의 정보가 파일 아이템일 경우!!
					
					System.out.println("파라미터명 : " + fileItem.getFieldName());
					System.out.println("업로드할 파일명 : " + fileItem.getName());
					System.out.println("업로드할 파일 크기 : " + fileItem.getSize() + "bytes");
					
					//articleForm.jsp에서 입력한 글제목, 글내용, 요청한 업로드 파일 정보등...
					//모든 정보를? HashMap에  key,value 형식으로 저장함.
					//{imageFileName=업로드할파일이름}
					articleMap.put(fileItem.getFieldName(), fileItem.getName());
					
					//전체 : 업로드할 파일이 존재 할 경우  업로드할 파일의 파일 이름을 이용해 저장소에 업로드함
					//업로드할 파일 크기가 0보다 크다면?(업로드할 파일이 존재 한다면?)
					if(fileItem.getSize() > 0){
						
						//업로드할 파일명을 얻어 뒤에서부터 \\문자열이 들어 있는지 그 인덱스 위치를 알려주는데
						int idx = fileItem.getName().lastIndexOf("\\");
						
						if(idx == -1){
							idx = fileItem.getName().lastIndexOf("/"); // -1얻기 
						}	
						//업로드할 파일명 얻기
						String fileName = fileItem.getName().substring(idx + 1);
						
						//업로드할 파일 경로 + 파일명 을 이용하여 업로드할 파일에 대한 객체 생성
						File uploadFile = new File(currentDirPath + "\\temp\\" + fileName);
						
						//실제 파일 업로드
						fileItem.write(uploadFile);
					}//end if
				}//end if
			}//end for
		} catch (Exception e) {
			e.printStackTrace();
		}
		return articleMap;//해쉬맵 리턴
	}//upload메소드 끝
	
}//BoardController의 끝 







