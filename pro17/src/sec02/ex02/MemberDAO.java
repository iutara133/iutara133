package sec02.ex02;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

//�ڹٺ� Ŭ������ ���� -> DAO, VO�Ǵ� DTO

//DAOŬ������ ���� : DB�� �����Ͽ� DB�۾� �ϴ� Ŭ���� 
public class MemberDAO {

	//DB�۾� ���� �ʿ��� ��ü�� ������ ���� ����
	private Connection con;
	private PreparedStatement pstmt;
	private DataSource ds;
	
	//Ŀ�ؼ�Ǯ(DataSource)��ü�� ��� ������
	public MemberDAO() {
		try{
			//InitialContext��ü�� �ϴ� ������  ��Ĺ �����..
			//context.xml�� ���ؼ� ������ Context��ü�鿡 ������ �ϴ� ������ ��.
			Context ctx = new InitialContext();
			
			//JDNI������� �����ϱ� ���� �⺻���(java:/comp/env)�� �����մϴ�.
			//ȯ�ἳ���� ���õ� ���ؽ�Ʈ ��ü�� �����ϱ� ���� �⺻ �ּ� �Դϴ�.
			Context envContext = (Context)ctx.lookup("java:/comp/env");
			
			/*Ŀ�ؼ�Ǯ �ڿ� ���*/
			//�׷��� �ٽ� ��Ĺ�� context.xml�� ������ <Resource name="jdbc/oracle".../>
			//�±���  name�Ӽ�����? "jdbc/oracle"�� �̿��� ��Ĺ�� �̸� DB�� ������ ����
			//DataSource��ü(Ŀ�ؼ�Ǯ ������ �ϴ� ��ü)�� �޾� �ɴϴ�.
			ds = (DataSource)envContext.lookup("jdbc/oracle");
			
		}catch(Exception err){
			err.printStackTrace();
		}
	}//������ ��
	
	//DB�� ��ȸ�������� INSERT�߰� ��ų �޼ҵ� 
	//�Ű������� .. MemberBean��ü�� ���� ���� ������?
	//Insert������ ? ? ? ?�� ���� �Ǵ� INSERT�� ���� ����� ����!
	public void addMember(MemberVO memberBean){
		try{
			//DB����
			//Ŀ�ؼ�Ǯ(DataSource)��ü���� �̸� DB������ ������ ���ϰ� �ִ� Connection��ü ������
			con = ds.getConnection();
			
			//Insert������ ����� ����..�Ű������� ���޹��� MemberBean��ü�� �������� ���� �ޱ�
			String id = memberBean.getId();
			String pwd = memberBean.getPwd();
			String name = memberBean.getName();
			String email = memberBean.getEmail();
			
			//insert���� �����
			String query = "insert into t_member(id,pwd,name,email)";
				   query += "values(?,?,?,?)";
		
			//OraclePreparedStatementWrapper���ఴü<------INSERT������ DB�� �����Ͽ� ����
			// ?��ȣ�� �����Ǵ� �������� ������ ������ insert������ �ӽ÷�..
			// OraclePreparedStatementWrapper���ఴü�� ���...
			// OraclePreparedStatementWrapper���ఴü ��ü�� ��ȯ �޾� ���
			pstmt = con.prepareStatement(query); 
			
			//?��ȣ�� ���� �Ǵ� ���� ����  �츮���Է��� ��ȸ�� ������ ������ ����
			//OraclePreparedStatementWrapper���ఴü�� ? 4���� ���� ����
			pstmt.setString(1, id);
			pstmt.setString(2, pwd);
			pstmt.setString(3, name);
			pstmt.setString(4, email);
			
			//OraclePreparedStatementWrapper���ఴü�� �̿��Ͽ� 
			//DB�� ���̺� INSERT������ ����
			pstmt.executeUpdate();
			
			//�ڿ�����
			pstmt.close();
			con.close();
		}catch(Exception err){
			//��Ŭ������ console�ǿ� ���� �޼��� ���
			err.printStackTrace();
		}
	}//addMember�޼ҵ� ��

	//DB�� ���� �Ǿ� �ִ� ��� ȸ�������� (��ȸ)�˻� �ϴ� �޼ҵ�
	public ArrayList listMembers(){ //member.jsp���� ȣ���ϴ� �޼ҵ� 
		
		//DB�� ���� �Ǿ� �ִ� ��� ȸ�������� ���ڵ� ����(�Ѹ��� ȸ������ ����)�� �˻��ؼ� 
		//������ ��  MemberBean��ü�� ���� ������.....
		//MemberBean��ü����......���� �߰� �Ͽ� �����ų ArrayList�迭 ��ü ����
		ArrayList<MemberVO> list = new ArrayList<MemberVO>();
		
		try{
			//DB����
			//Ŀ�ؼ�Ǯ(DataSource)��ü���� �̸� DB������ ������ ���ϰ� �ִ� Connection��ü ������
			con = ds.getConnection();
			
			//SQL�� ����� : ȸ�������� �ֱ� ������ ������ �������� �����Ͽ� �˻��� SELECT�� �����
			String query = "select * from t_member order by joinDate desc";
			
			//?��ȣ�� ���� �Ǵ� ���� ������  
			//SELECT������ �ӽ÷� OraclePreparedStatementWrapper���ఴü�� ���.. 
			//OraclePreparedStatementWrapper���ఴü ��ȯ �ޱ� 
			//<----SELECT������ DB�� �����Ͽ� ������ ����
			pstmt = con.prepareStatement(query);
			
			//���� query ������ ����� select������ DB�� �����Ͽ� �˻��� �װ���� 
			//MemberDAO.java�������� ���� �ޱ� ����..
			//�˻���� �����͵���? Table������ ������ ������ �ӽ� ����� ������ �ϴ� ��ü�� �ʿ��ϴ�.
			//�� ��ü�� �ٷ� OracleReusltSetImpl��ü ��  ���̴�.
			// OracleReusltSetImpl��ü�� �˻��� ��� �����͸� Table������ ������ �Ȱ��� �����Ͽ�
			// OracleReusltSetImpl��ü ��ü�� ���� �޴´�.
			ResultSet rs = pstmt.executeQuery();
			
			//OracleResultSetImpl��ü�� ������ Table������ �����ν�
			//ó������ Ŀ��(ȭ��ǥ:�����͸� ����Ű�� ����)��  �÷����� �ִ� ���� ����Ű�� �ִ�.
			//rs.next()�޼ҵ带 ȣ�� �ϸ� Ŀ�� ��ġ�� ���� �Ʒ��� �������鼭 
			//�״��� �ٿ� ���ڵ尡 ���� �ϴ��� ���� �ȴ�.
			//next()�޼ҵ�� �״��� �ٿ� �˻��� ���ڵ� �� �����ϸ� true�� ��ȯ �ϰ�
			//���� ���� ������ false�� ��ȯ �Ѵ�.
			while (rs.next()) {
				
				//����Ŭ DB�� t_member���̺��� �˻��� ���ڵ��� �� �÷�����
				//OracleReusltSetImpl��ü���� ������ ������ ����
				String id = rs.getString("id");
				String pwd = rs.getString("pwd");
				String name = rs.getString("name");
				String email = rs.getString("email");
				Date joinDate = rs.getDate("joinDate");
				
				//�˻��� ȸ�������� MemberBean��ü�� �������� �����ϱ� 
				MemberVO vo = new MemberVO(id, pwd, name, email, joinDate);
//				vo.setId(id);
//				vo.setPwd(pwd);
//				vo.setName(name);
//				vo.setEmail(email);
//				vo.setJoinDate(joinDate);
				
				//ArryList�迭�� MemberBean��ü�� �߰��Ͽ� ����
				list.add(vo);
				
			}//while �ݺ��� ��
			
			
		}catch(Exception err){
			err.printStackTrace();
		}
		
		return list;//�˻��� ȸ��������(MemberBean��ü��)�� �����ϰ� �ִ� ArrayList��ȯ
		
	}//listMembers�޼ҵ� ��

	//ȸ�� id�� �̿��� ȸ�� ���� ��ȸ
	public MemberVO findMember(String id) {

		//��ȸ�� ȸ�������� ������ MemberVOŬ���� Ÿ���� ���� ����
		MemberVO memInfo = null;
		
		try {
			//Ŀ�ؼ�Ǯ�κ��� Ŀ�ؼ� ���
			con = ds.getConnection();
			//SQL�� ����� SQL���� ������ PreparedStatement��ü ���
			pstmt = con.prepareStatement("select * from t_member where id=?");
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			memInfo = new MemberVO(rs.getString("id"),
								   rs.getString("pwd"),
								   rs.getString("name"),
								   rs.getString("email"), 
								   rs.getDate("joinDate"));
			//�ڿ�����
			rs.close();
			pstmt.close();
			con.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return memInfo;
	}

	//�Ű������� ���� ���� MemberVO��ü�� �̿��Ͽ� ȸ������ ���� UPDATE
	public void modMember(MemberVO memberVO) {
		//������ ������ MemberVO���� ��������
		String id = memberVO.getId();
		String pwd = memberVO.getPwd();
		String name = memberVO.getName();
		String email = memberVO.getEmail();
		
		try {
			//Ŀ�ؼ�Ǯ�κ��� Ŀ�ؼ� ���
			con = ds.getConnection();
			pstmt = con.prepareStatement("update t_member set pwd=?, name=?, email=? where id=?");
			pstmt.setString(1, pwd);
			pstmt.setString(2, name);
			pstmt.setString(3, email);
			pstmt.setString(4, id);
			pstmt.executeUpdate(); //UPDATE
			//�ڿ�����
			pstmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	//�Ű������� ���޵� ������ id�� �̿��� t_member���̺� ����� ȸ������
	public void delMember(String id){
		try {
			//Ŀ�ؼ�Ǯ�� ���� Ŀ�ؼ� ���
			con = ds.getConnection();
			//DELETE���� �����
			String query = "delete from t_member where id=?";
			pstmt = con.prepareStatement(query);
			pstmt.setString(1, id);
			pstmt.executeUpdate(); //DELETE����
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}//MemberDAO Ŭ���� ��











