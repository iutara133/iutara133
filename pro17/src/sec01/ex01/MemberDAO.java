package sec01.ex01;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

//자바빈 클래스의 종류 -> DAO, VO또는 DTO

//DAO클래스의 역할 : DB에 연결하여 DB작업 하는 클래스 
public class MemberDAO {

	//DB작업 관련 필요한 객체를 저장할 변수 선언
	private Connection con;
	private PreparedStatement pstmt;
	private DataSource ds;
	
	//커넥션풀(DataSource)객체를 얻는 생성자
	public MemberDAO() {
		try{
			//InitialContext객체가 하는 역할은  톰캣 실행시..
			//context.xml에 의해서 생성된 Context객체들에 접근을 하는 역할을 함.
			Context ctx = new InitialContext();
			
			//JDNI방법으로 접근하기 위해 기본경로(java:/comp/env)를 지정합니다.
			//환결설정에 관련된 컨텍스트 객체에 접근하기 위한 기본 주소 입니다.
			Context envContext = (Context)ctx.lookup("java:/comp/env");
			
			/*커넥션풀 자원 얻기*/
			//그런후 다시 톰캣은 context.xml에 설정한 <Resource name="jdbc/oracle".../>
			//태그의  name속성값인? "jdbc/oracle"을 이용해 톰캣이 미리 DB에 연결해 놓은
			//DataSource객체(커넥션풀 역할을 하는 객체)를 받아 옵니다.
			ds = (DataSource)envContext.lookup("jdbc/oracle");
			
		}catch(Exception err){
			err.printStackTrace();
		}
	}//생성자 끝
	
	//DB에 새회원정보를 INSERT추가 시킬 메소드 
	//매개변수로 .. MemberBean객체를 전달 받은 이유는?
	//Insert문장의 ? ? ? ?에 대응 되는 INSERT할 값을 만들기 위함!
	public void addMember(MemberVO memberBean){
		try{
			//DB연결
			//커넥션풀(DataSource)객체안의 미리 DB연결한 정보를 지니고 있는 Connection객체 빌려옴
			con = ds.getConnection();
			
			//Insert문장을 만들기 위해..매개변수로 전달받은 MemberBean객체의 각변수값 리턴 받기
			String id = memberBean.getId();
			String pwd = memberBean.getPwd();
			String name = memberBean.getName();
			String email = memberBean.getEmail();
			
			//insert문장 만들기
			String query = "insert into t_member(id,pwd,name,email)";
				   query += "values(?,?,?,?)";
		
			//OraclePreparedStatementWrapper실행객체<------INSERT문장을 DB에 전송하여 실행
			// ?기호에 대응되는 설정값을 제외한 나머지 insert문장을 임시로..
			// OraclePreparedStatementWrapper실행객체에 담아...
			// OraclePreparedStatementWrapper실행객체 자체를 반환 받아 얻기
			pstmt = con.prepareStatement(query); 
			
			//?기호에 대응 되는 설정 값을  우리가입력한 새회원 정보의 값으로 설정
			//OraclePreparedStatementWrapper실행객체에 ? 4개의 값을 설정
			pstmt.setString(1, id);
			pstmt.setString(2, pwd);
			pstmt.setString(3, name);
			pstmt.setString(4, email);
			
			//OraclePreparedStatementWrapper실행객체를 이용하여 
			//DB에 테이블에 INSERT문장을 실행
			pstmt.executeUpdate();
			
			//자원해제
			pstmt.close();
			con.close();
		}catch(Exception err){
			//이클립스의 console탭에 예외 메세지 출력
			err.printStackTrace();
		}
	}//addMember메소드 끝

	//DB에 저장 되어 있는 모든 회원정보를 (조회)검색 하는 메소드
	public ArrayList listMembers(){ //member.jsp에서 호출하는 메소드 
		
		//DB에 저장 되어 있는 모든 회원정보를 레코드 단위(한명의 회원정보 단위)로 검색해서 
		//가져온 후  MemberBean객체에 각각 저장후.....
		//MemberBean객체들을......각각 추가 하여 저장시킬 ArrayList배열 객체 생성
		ArrayList<MemberVO> list = new ArrayList<MemberVO>();
		
		try{
			//DB연결
			//커넥션풀(DataSource)객체안의 미리 DB연결한 정보를 지니고 있는 Connection객체 빌려옴
			con = ds.getConnection();
			
			//SQL문 만들기 : 회원정보를 최근 가입일 순으로 내림차순 정렬하여 검색할 SELECT문 만들기
			String query = "select * from t_member order by joinDate desc";
			
			//?기호에 대응 되는 값을 제외한  
			//SELECT문장을 임시로 OraclePreparedStatementWrapper실행객체에 담아.. 
			//OraclePreparedStatementWrapper실행객체 반환 받기 
			//<----SELECT문장을 DB에 전송하여 실행할 역할
			pstmt = con.prepareStatement(query);
			
			//위의 query 변수에 저장된 select문장을 DB에 전송하여 검색한 그결과를 
			//MemberDAO.java페이지로 전달 받기 위해..
			//검색결과 데이터들을? Table형식의 구조로 저장할 임시 저장소 역할을 하는 객체가 필요하다.
			//그 객체가 바로 OracleReusltSetImpl객체 인  것이다.
			// OracleReusltSetImpl객체에 검색한 결과 데이터를 Table형식의 구조로 똑같이 저장하여
			// OracleReusltSetImpl객체 자체를 리턴 받는다.
			ResultSet rs = pstmt.executeQuery();
			
			//OracleResultSetImpl객체의 구조는 Table형식의 구조로써
			//처음에는 커서(화살표:데이터를 가리키는 줄자)가  컬럼명이 있는 줄을 가리키고 있다.
			//rs.next()메소드를 호출 하면 커서 위치가 한줄 아래로 내려오면서 
			//그다음 줄에 레코드가 존재 하는지 묻게 된다.
			//next()메소드는 그다음 줄에 검색한 레코드 가 존재하면 true를 반환 하고
			//존재 하지 않으면 false를 반환 한다.
			while (rs.next()) {
				
				//오라클 DB의 t_member테이블에서 검색한 레코드의 각 컬럼값을
				//OracleReusltSetImpl객체에서 꺼내와 변수에 저장
				String id = rs.getString("id");
				String pwd = rs.getString("pwd");
				String name = rs.getString("name");
				String email = rs.getString("email");
				Date joinDate = rs.getDate("joinDate");
				
				//검색한 회원정보를 MemberBean객체의 각변수에 저장하기 
				MemberVO vo = new MemberVO(id, pwd, name, email, joinDate);
//				vo.setId(id);
//				vo.setPwd(pwd);
//				vo.setName(name);
//				vo.setEmail(email);
//				vo.setJoinDate(joinDate);
				
				//ArryList배열에 MemberBean객체를 추가하여 저장
				list.add(vo);
				
			}//while 반복문 끝
			
			
		}catch(Exception err){
			err.printStackTrace();
		}
		
		return list;//검색한 회원정보들(MemberBean객체들)을 저장하고 있는 ArrayList반환
		
	}//listMembers메소드 끝
	

}//MemberDAO 클래스 끝











