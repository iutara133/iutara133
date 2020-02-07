package sec02.ex02;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.net.aso.r;

//��Ʈ�ѷ� ������ �ϴ� MemberController���� Ŭ����.
//�� ��Ʈ�ѷ������� request��  getPathInfo()�޼ҵ带 �̿��� �δܰ�� �̷���� ��û�ּҸ� ���� �ɴϴ�.
//action���� ���� ���� if���� �б��ؼ� ��û�� �۾��� �����մϴ�.

// listMembers.jsp���������� ȸ������ ��ũ Ŭ����...
// /member/memberForm.do �ּҷ� ��û�� ���� (ȸ������ �Է� �������� �̵� ������~) 

// memberForm.jsp���������� DB�� �߰��� ȸ������ �Է���..�����ϱ� ��ư Ŭ����...
// /member/addMember.do �ּҷ� ��û�� ����(DB�� ���ο� ȸ�� �߰�����~)

// Ŭ���̾�Ʈ�� ���������� �̿��Ͽ� ��Ʈ�ѷ��� ��ó�ϸ� request��ü�� getPathInfo()�޼ҵ带 �̿���
// ���� ��û �ּ���?  /member/modMemberForm.do ��  /member/modeMember.do �� �������� 
// if������ �б��Ͽ� �۾��� ���� �ϵ��� MemberControllerŬ������ �ۼ�����.

// listMembers.jsp���������� ȸ�������� ���� ������ũ�� Ŭ�� ������...
// ���� ��û �ּ���? /member/delMember.do �� ������ �� 
// if������ �б��Ͽ� �۾��� ���� �ϵ���  MemberControllerŬ������ �ۼ� ����.

@WebServlet("/member/*") //������������ ��û��.. �δܰ�� ��û�� �̷����
public class MemberController extends HttpServlet {

	MemberDAO memberDAO;

	// init()�޼ҵ忡�� MemberDAO��ü�� �����ؼ� �ʱ�ȭ
	@Override
	public void init() throws ServletException {
		memberDAO = new MemberDAO();
	}
	// alt+shift+s v doGet doPost�޼ҵ� �������̵�
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
		
			//MVC�߿� View�ּ� ������ �뵵
			String nextPage = null;
		
			//�ѱ�ó��
			request.setCharacterEncoding("UTF-8");
			//������ ������ ���� ����
			response.setContentType("text/html;charset=utf-8");
			
			//Ŭ���̾�Ʈ�� 2�ܰ��� ��û�ּҰ� ���
			String action = request.getPathInfo();
			// /listMember.do
			// /memberForm.do
			// /addMember.do
			// /modMemberForm.do
			// /modMember.do
			// /delMember.do
			
			System.out.println("action : " + action);
			
			//action������ �������� if���� �б��ؼ� ��û�� �۾��� �����ϴµ�..
			//���� action������ ���� null�̰ų�   /listMembers.do�� ��쿡 ȸ���˻������ ������.
			if(action == null || action.equals("/listMembers.do")){
				
				//MemberDAO�� listMembers()�޼ҵ带 ȣ���Ͽ� ȸ������ ��ȸ��û�� ����...
				//DB�� ���� �˻��� ȸ�������� ArrayList�� ��ȯ �޽��ϴ�.
				ArrayList membersList = memberDAO.listMembers();
				
				//�̶� DB�κ��� �˻��� ȸ�������� (ArrayList�� ��� MemberVO��ü��)�� View��������
				//���� �ϱ� ���� �ӽ� ���� ������ request���尴ü ������ ����(���ε�)��.
				request.setAttribute("membersList", membersList);
//				request.setAttribute("msg", "deleted");
//				request.setAttribute("msg", "modified");
				//�˻��� ȸ������(����޼���)�� ������ View������ �ּ� ����
				//= test02������ listMembers.jsp�� ������ �ϱ� ���� �ּ� ����
				nextPage = "/test03/listMembers.jsp";
			
			
			}else if(action.equals("/memberForm.do")){//ȸ������ �Է��������� �̵� ������~
			
				nextPage = "/test03/memberForm.jsp";//ȸ������ �Է�������(VIEW)�ּ� ����
			
			}else if(action.equals("/addMember.do")){//�Է��� ���ο� ȸ�������� DB�� �߰� ����~		
				//��û������(�Է��� ���ο� ȸ����������) request�������� ���
				String id = request.getParameter("id");
				String pwd = request.getParameter("pwd");
				String name = request.getParameter("name");
				String email = request.getParameter("email");		
				//MemberVO�� ����
				MemberVO memberVO = new MemberVO(id, pwd, name, email);	
				//��û�� ȸ�� ������ DB�� ���̺� insert�ϱ� ���� �޼ҵ� ȣ��
				memberDAO.addMember(memberVO);			
				//���ο� ȸ�������� DB�� ���̺� insert�� �����ϸ� ���������� ������ �޼��� ����
				request.setAttribute("msg", "addMember");
				//ȸ�� ����� �ٽ� ȸ������� �˻��ؼ� �����ִ� View�������� �̵� �ϱ� ����..
				//�ٽ�~ MemberController.javat������ ���û�� �ּ� ����
				nextPage = "/member/listMembers.do";			
					
			//listMembers.jsp���������� ������ũ�� Ŭ�� ������..
			//��Ʈ�ѷ��� ȸ������ ����â ��û�� ID�� ȸ�������� ��ȸ���� ����â���� ������ ��.
			}else if(action.equals("/modMemberForm.do")){
				
				//�����ϱ����� ������ ȸ�� ID�� ���� �޾�.. �˻� �ϱ� ����.
				String id = request.getParameter("id");
				
				//������ ȸ�� ID�� �ش��ϴ� ȸ�� ���� �˻�
				MemberVO memberInfo = memberDAO.findMember(id);
				
				//������ ȸ�� �Ѹ��� ������ �˻��ؼ� �����ͼ� View������(����â)���� ���� �ϱ� ����
				//request���尴ü�� ����
				request.setAttribute("memInfo", memberInfo);
				
				//ȸ������ ����â View�������� ������ �ϱ� ���� �ּ� ����
				nextPage = "/test03/modMemberForm.jsp";							
			
			//ȸ������ ����â(modMemberForm.jsp)���� ȸ������������ �Է��� �����ϱ� ��ư Ŭ������..
			// /member/modMember.do�ּҷ�  DB���̺� ����� ȸ�������� ���� ��û�� ������
			}else if(action.equals("/modMember.do")){
				//��û�����(ȸ����������)
				String id = request.getParameter("id");
				String pwd = request.getParameter("pwd");
				String name = request.getParameter("name");
				String email = request.getParameter("email");
				//MemberVO�� �������� ����
				MemberVO memberVO = new MemberVO(id, pwd, name, email);
				//DB ȸ�� ���̺��� ������ ���� ��û
				memberDAO.modMember(memberVO);//UPDATE
			    //����UPDATE�� �����ϸ� listMembers.jsp�� �����۾��Ϸ� �޼����� ���� �ϱ� ����..
				//request�� �޼��� ����
				request.setAttribute("msg", "modified");
				//������  ��� ȸ���� �˻��Ͽ� �����ֱ� ���� 
				//MemberController.java�������� ��û�� �ּ� ����
				nextPage = "/member/listMembers.do";
				
			}else if(action.equals("/delMember.do")){//���� ��û�� ��� ������
				//������ ȸ���� ID�� �޾ƿɴϴ�.
				String id = request.getParameter("id");
				
				//������ ȸ�� ID�� �̿��Ͽ� DB����� ȸ������ �۾� ���~
				memberDAO.delMember(id);
				
				//������ ���� �ϸ� ȸ�����â(listMembers.jsp) View�������� 
				//���� �Ϸ� �޼����� ��� �ϱ� ����  �޼����� request���尴ü ������ ����
				request.setAttribute("msg", "deleted");
				
				//���� ������ �ٽ� ��� ȸ���� �˻� �ϱ� ���� ��û �ּҸ� ����
				nextPage = "/member/listMembers.do";
				
			}else{//�׿� action������ �ٸ� ��û URL�� ����Ǿ� ������ ȸ������� �����.
				
				ArrayList membersList = memberDAO.listMembers();
				request.setAttribute("membersList", membersList);
				nextPage = "/test03/listMembers.jsp";
			}
					
			//nextPage������ �ּҸ� ����  ������ ��
			 RequestDispatcher dispatche = 
					 request.getRequestDispatcher(nextPage);
			 dispatche.forward(request, response);
		
//			 <jsp:forward page="/test01/listMembers.jsp">
			 
	}

}




