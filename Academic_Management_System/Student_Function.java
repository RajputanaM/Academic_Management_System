package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashSet;
public class Student_Function {
	private static Connection con;
	// SARE FUNCTION ME JO CONNECTION BNAYE H USSE ACHHA HOGA KI UPR ME BNA LENGE EK FUNCTION.
	
	// 
	public static int preReqcheck(String course_id,String user_id) {
		
		try {
			con = ConnectionProvider.getConnection();
			String course_completed = "SELECT * FROM STUDENT_COURSE_GRADE WHERE STUDENT_ID = ?";
			PreparedStatement crs_cmpltd = con.prepareStatement(course_completed);
//			crs_cmpltd.setString(1, course_id);
			crs_cmpltd.setString(1, user_id);
			ResultSet cmp_course = crs_cmpltd.executeQuery();
			List<String> crs_list=new ArrayList<String>(); 
			while(cmp_course.next()) {
				String crs = cmp_course.getString(2);
				crs_list.add(crs);
			}
			String pre_req_course = "SELECT * FROM PREREQUISITE WHERE COURSE_ID = ?";
			PreparedStatement crs_pre_course = con.prepareStatement(pre_req_course);
			crs_pre_course.setString(1, course_id);
			ResultSet crs_prereq = crs_pre_course.executeQuery();
			if(crs_prereq.next()) {
				Array pre_req_string = crs_prereq.getArray(2);
				String[] pre_req_strings = (String[]) pre_req_string.getArray();
				int count = 0;
				int size = pre_req_strings.length;
				if(size>crs_list.size())
					return 0;
				
				for(int i=0;i<size;i++) {
					if(crs_list.contains(pre_req_strings[i])) {
						count++;
					}
				}
				if(count==size) {
					System.out.println("good");
					return 1;
				}
				else 
					return 0;
			}
			else {
				return 1;
			}
		}
		catch(Exception Err) {
			Err.printStackTrace();
		}
		
		return 1;
	}
	
	public static int creditLimitcheck(String user_id,Float credit,int semester) {
		try {
			Connection con = ConnectionProvider.getConnection();
			
			int last_sem = semester-1;
			int second_last = last_sem-1;
			 int graded = 1;
			 int enrolled = 0;
			Float firstPrev = Common.semester_credit(user_id, last_sem,graded);
			Float secondPrev = Common.semester_credit(user_id, second_last,graded);
			
			Float current_credit = Common.semester_credit(user_id, semester,enrolled);

			Float credit_limit = (float) (((firstPrev+secondPrev)/2)*1.25);
			System.out.print("Your Credit Limit in this semester :");
			System.out.println(credit_limit);
			if(credit_limit>=(current_credit+credit)) {
				return 1;
			}
			else
				return 0;
		}
		catch(Exception Err) {
			Err.printStackTrace();
		}
		
		return 0;
	}
	
	public static int printCourse(String dept,int year,int i) {

			int isCourse_available=0;
			try {
				//String course_details = "SELECT * FROM offeredcourses WHERE dept_name=? and semester =?";
				String course_details = "SELECT * FROM OfferedCourses WHERE dept_name = ? AND ? = ANY(batches_allowed)";
				PreparedStatement course_stmt = con.prepareStatement(course_details);
				course_stmt.setString(1, dept);
				course_stmt.setInt(2, year);
				ResultSet offer = course_stmt.executeQuery();
				int t=1;
				int temp = 0;
				while(offer.next()) {
					temp = 1;
					isCourse_available =1;
					if(t == 1) {
						System.out.println("Courses Offered By " + dept + " Departments are: ");
						System.out.println("***** course_id | dept_name | credit | instructor_id | ltpsc | cgconstraint *****");
					}

					String course_id = offer.getString(1);
					String dept_name = offer.getString(2);
					Float credit = offer.getFloat(4);
					String ins_id = offer.getString(5);
					String ltpsc = offer.getString(6);
					Float cg = offer.getFloat(7);
					System.out.format("    %-3d%-8s| %-10s| %-6.1f| %-13s| %-5s| %-4.2f *****\n", t, course_id, dept_name, credit, ins_id, ltpsc, cg);
					t++;
				}
				if(temp==0 && i==0) {
					System.out.println("No PC or PE offered by any Faculty");
				}
				else if(temp==0){
					System.out.println("No "+dept+" Courses/Electives Offered by any Faculty");
				}
				
			}
			
			// TODO
//			IF ALL OFFERED COURSES ARE EMPTY THEN NO NEED TO SHOW THAT 
//			ENTER COURSE ID FOR REGISTER;
			
			catch(Exception err){
				err.printStackTrace();
			}
			return isCourse_available;
		}
	
	public static void register(String user_id) {
		try {
			con = ConnectionProvider.getConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("The Offered courses are :");
//			String query_for_offered_course = "select * from course catalog";
			
			Scanner sc= new Scanner(System.in);
			
			
			String cur_year_sem = "SELECT * FROM current_session";
			PreparedStatement c1 = con.prepareStatement(cur_year_sem);
			ResultSet c2 = c1.executeQuery();
			c2.next();
			int year = c2.getInt(1);
			String sem = c2.getString(2);
			int is_allow = c2.getInt(4);
			if(is_allow==0){
				System.out.println("Time Expired for Registering/taking a Course Kindly contact the academic section");
				return ;
			}
			
			
			String student_details = "SELECT * FROM STUDENT WHERE ENTRY_NUM=?";
			PreparedStatement student_stmt = con.prepareStatement(student_details);
			student_stmt.setString(1, user_id);
			ResultSet stud = student_stmt.executeQuery();
			stud.next();
			int YEAR = stud.getInt(3);
			int semester = (year-YEAR)*2;
			if(sem=="SUMMER") semester++;
			String dept = stud.getString(4);
			
			String[] dept_arr=new String[] {dept, "HS", "SE" , "GE"};
			int isCourse_available = 0;
			for(int i=0;i<4;i++) {
				isCourse_available = Math.max(printCourse(dept_arr[i],YEAR,i),isCourse_available);
			}
			int loop = 1;
			while(loop!=0 && isCourse_available==1) {
				
				int p =0;
				String course_id="";
				while(p!=1) {
					System.out.println("Enter Course_ID (like 'AB123') of course which you want to enroll or x to exit");
					course_id = br.readLine();
					if(course_id.equals("x")) {
						return ;
					}
					 if (course_id.matches("[A-Z]{2}\\d{3}")) {
				            p = 1;
				        } else {
				            System.out.println("Please Enter course_id in Proper Format, or x for exit");
				            
				        }
				}
				
				
				
				// check that this course is already taken or not;
				String istaken = "SELECT * FROM student_course_registration where student_id = ? and course_id = ? ";				
				PreparedStatement istakenstmt = con.prepareStatement(istaken);
				istakenstmt.setString(1,user_id);
				istakenstmt.setString(2,course_id);
				ResultSet istakencourse = istakenstmt.executeQuery();
				
				
				String isGraded = "SELECT * FROM student_course_grade where student_id = ? and course_id = ? ";				
				PreparedStatement istakengrade = con.prepareStatement(isGraded);
				istakengrade.setString(1,user_id);
				istakengrade.setString(2,course_id);
				ResultSet istakenG = istakengrade.executeQuery();
				
				
				
				
				// check that the course is offered or not;this is else if condition
				String check_course = "SELECT * FROM offeredcourses WHERE COURSE_ID = ?";
				PreparedStatement checkstmt = con.prepareStatement(check_course);
				checkstmt.setString(1, course_id);
				ResultSet checkstmt1 = checkstmt.executeQuery();
				
				
				
				// check that after this course credit limit valid or not?
				
				
				
				if(istakencourse.next() || istakenG.next()) {
					System.out.println("You have already taken "+course_id+" ");
//					System.out.println("1. Register another Course");
//					System.out.println("2. Back to Main Menu");
//					int temp = sc.nextInt();
//					if(temp==2) {
//						return ;
//					}
					return;
				}
				else if(checkstmt1.next()) {
					// TODO PREREQ CHECK but currently implementing easily;
					// yha se choose hone ke bad direct Enrolled courses bhi call kr skte h 
					// isse fayda hoga ki yha register krte hi wha dikha dega;
					
					
					// NOW WE CHECK PRE_REQUISITE COURSES;
					Float required_cgpa = checkstmt1.getFloat(7);
					Float actual_cgpa = Common.CGPA_func(user_id);
					String faculty = checkstmt1.getString(5);
					int check_pre_req = preReqcheck(course_id,user_id);
					
					float credit = checkstmt1.getFloat(4);
					
					int isValid_creditLimit = creditLimitcheck(user_id,credit,semester);
					
					
					if(check_pre_req==1 && (actual_cgpa>=required_cgpa) && (isValid_creditLimit==1)) {
						// IMPLEMENT COURSE DETAIL LIKE TABLE SOMETHING;
						
						String course_type = checkstmt1.getString(8);
						String insert_into_regis = "INSERT INTO student_course_registration VALUES (?,?,?,?,?,?)";
						PreparedStatement insert_into_reg = con.prepareStatement(insert_into_regis);
						insert_into_reg.setString(1,user_id);
						insert_into_reg.setString(2,course_id);
						insert_into_reg.setInt(3,semester);
						insert_into_reg.setString(4,course_type);
						insert_into_reg.setFloat(5,credit);
						insert_into_reg.setString(6, faculty);
						insert_into_reg.executeUpdate();		
						// TODO ONLY FALSE PRINTING YOU HAVE SUCCESSFULLY ENROLLED
						System.out.println("You Hava Successfully Enrolled in "+course_id);
						loop=0;
					}
					else {
						// yha pe isValid_creditLimit and check_pre_req ye sb la use
						// kr ke reason specify kr skte h
						if(check_pre_req!=1) {
							System.out.println("You don't have PreRequisite for this Course");
						}
						else if(isValid_creditLimit!=1) {
							System.out.println("You have exceed Credit Limit");
						}
						else {
							System.out.println("Your CGPA is less than CG constraint of "+course_id);
						}
						
						loop=0;
					}

				}
				else {
					System.out.println("The Course "+course_id+" you are looking, is not Offered Currently\n");
					System.out.println("1. Register another Course");
					System.out.println("2. Back to Main Menu");
					int temp = sc.nextInt();
					if(temp==2) {
						return ;
					}
				}
			}
			
			System.out.println("1. for Go back in the Menu");
			int a = sc.nextInt();
			if(a==1) {
				return ;
			}
		}
		catch(Exception err) {
			err.printStackTrace();
		}
		
	}
	
	public static void deregister(String user_id) {
		try {
			con = ConnectionProvider.getConnection();

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			String cur_year_sem = "SELECT * FROM current_session";
			PreparedStatement c1 = con.prepareStatement(cur_year_sem);
			ResultSet c2 = c1.executeQuery();
			c2.next();
			int year = c2.getInt(1);
			String sem = c2.getString(2);
			int is_allow = c2.getInt(4);
			if(is_allow==0){
				System.out.println("Time Expired for Drop a course Kindly contact the academic section");
				return ;
			}
			enrolledCourses(user_id);  // show enrolled courses;

			int p =0;
			String course_id="";
			while(p!=1) {
				System.out.println("Enter Course_ID (like 'AB123') of course which you want to drop or x to exit");
				course_id = br.readLine();
				if(course_id.equals("x")) {
					return ;
				}
				 if (course_id.matches("[A-Z]{2}\\d{3}")) {
			            p = 1;
			        } else {
			            System.out.println("Please Enter course_id in Proper Format, or x for exit");
			            
			        }
			}
			String course_get = "SELECT * FROM STUDENT_COURSE_REGISTRATION WHERE STUDENT_ID = ? AND COURSE_ID = ?";
			PreparedStatement d1 = con.prepareStatement(course_get);
			d1.setString(1, user_id);
			d1.setString(2, course_id);
			
			ResultSet d2 = d1.executeQuery();
			if(d2.next()) {
				String delete = "DELETE FROM STUDENT_COURSE_REGISTRATION WHERE STUDENT_ID = ? AND COURSE_ID = ?";
				d1 = con.prepareStatement(delete);
				d1.setString(1, user_id);
				d1.setString(2, course_id);
				d1.executeUpdate();
				System.out.println("You Have Successfully dropped Your course");
			}
			else {
				System.out.println("You haven't taken "+course_id+ " in this semester");
			}
//			System.out.println("1. for Go back in the Menu");
//			Scanner sc= new Scanner(System.in);
//			int a = sc.nextInt();
//			if(a==1) {
//				return ;
//			}
		}
		catch(Exception err) {
			err.printStackTrace();
		}
	}
	
	public static void viewGrade(String user_id) {	
		try {
			con = ConnectionProvider.getConnection();
			Common.viewGrade(user_id, "Student");
		}
		catch(Exception err) {
			err.printStackTrace();
		}
		
	}
	
	public static void enrolledCourses(String user_id) {
		try {
			
			con = ConnectionProvider.getConnection();
			String allgrade="SELECT * FROM STUDENT_COURSE_REGISTRATION WHERE STUDENT_ID = ?";
			
			PreparedStatement all = con.prepareStatement(allgrade);
			all.setString(1, user_id);
			ResultSet all1 = all.executeQuery();
			System.out.println("Here are the List of all Enrolled Course");
			System.out.println("-".repeat(120));
			System.out.printf("%-15s | %-10s | %-8s | %-7s | %-15s | %10s%n", "Student ID", "Course ID", "Semester", "Credit", "Course Type","Instructor ID");
			System.out.println("-".repeat(120));
			int t = 1;
			while(all1.next()) {
				String student_id = all1.getString(1);
				String course_id = all1.getString(2);
				int semester = all1.getInt(3);
				String ins_id = all1.getString(6);
				String course_type = all1.getString(4);
				Float Credit = all1.getFloat(5);
				System.out.printf("%-15s | %-10s | %-8d | %-7.2f | %-5s | %10s%n", student_id, course_id, semester, Credit,course_type,ins_id);

				// System.out.println("    "+t+". "+student_id+"| "+course_id+"     |   "+semester+"   \t| "+Credit+" \t |   "+ins_id+" \t |   "+Grade+"    \t"+course_type+"   *****");
				t++;
			}
//			System.out.println("1. for Go back in the Menu");
//			Scanner sc= new Scanner(System.in);
//			int a = sc.nextInt();
//			if(a==1) {
//				return ;
//			}
//			
		}
		catch(Exception err) {
			err.printStackTrace();
		}
		
	}
	public static void computeCGPA(String user_id) {
		try {
			
			
			Float cgpa = Common.CGPA_func(user_id);
			System.out.println("Your CGPA is "+cgpa);
//			System.out.println("1. for Go back in the Menu");
//			Scanner sc= new Scanner(System.in);
//			int a = sc.nextInt();
//			if(a==1) {
//				return ;
//			}
		}
		catch(Exception err) {
			err.printStackTrace();
		}
	}
	
	public static void check_Graduation(String user_id) {

		try{
			Connection con  = ConnectionProvider.getConnection();
			Float total_Credit = Common.get_totalCredit(user_id);


			String nameinfo = "SELECT * FROM STUDENT WHERE ENTRY_NUM = ?";
			PreparedStatement n1 = con.prepareStatement(nameinfo);
			n1.setString(1, user_id);
			ResultSet namedata = n1.executeQuery();
			String dept="";
			if(namedata.next())
				dept = namedata.getString(4);
			else{
				System.out.println("This Students does not exists. ");
				return ;
			}

			// Retrieve Program Core of student_dept ;
			String pc = "SELECT * FROM COURSE_CATALOG WHERE TYPE = ? AND DEPT_NAME=?";
			PreparedStatement pcst = con.prepareStatement(pc);
			pcst.setString(1,"PC");
			pcst.setString(2,dept);
			ResultSet rs = pcst.executeQuery();
			List<String> pc_str=new ArrayList<String>();
			List<String> pe_str=new ArrayList<String>();
			while(rs.next()){
				String pccore = rs.getString(1);
				pc_str.add(pccore);
			}
			String pe = "SELECT * FROM COURSE_CATALOG WHERE TYPE = ? AND DEPT_NAME=?";
			PreparedStatement pest = con.prepareStatement(pe);
			pest.setString(1,"PE");
			pest.setString(2,dept);
			ResultSet rspe = pest.executeQuery();
			while(rspe.next()){
				String pecore = rspe.getString(1);
				pe_str.add(pecore);
			}

			HashSet<String> cmp_pc = new HashSet<String>();
			List<Common.Course> courseHistoryPC = Common.retrieveCourseHistory(user_id,"PC",dept);
			List<Common.Course> courseHistoryPE = Common.retrieveCourseHistory(user_id,"PE",dept);
			List<Common.Course> courseHistoryBTP = Common.retrieveCourseHistory(user_id,"BTP",dept);
			int pc_check = 1;
			int pecnt = 0;
			Float BTPcredit = (float) 0;
			for (Common.Course course : courseHistoryPC) {
				String course_id = course.getCourseCode();
				String grade = course.getCourseGrade();
				//System.out.println(course_id);
				if(!grade.equals("F"))
					cmp_pc.add(course_id);
			}
			for(Common.Course course : courseHistoryPE){
				String grade = course.getCourseGrade();
				//System.out.println(course_id);
				  if(!grade.equals("F"))
					  pecnt++;
			}
			for(Common.Course course : courseHistoryBTP){
				String grade = course.getCourseGrade();
				Float cr = course.getCredits();
				//System.out.println(course_id);
				if(!grade.equals("F"))
					BTPcredit+=cr;
			}


			for(int i=0;i<pc_str.size();i++){
				if(!cmp_pc.contains(pc_str.get(i))){
					pc_check = 0;
				}
			}

			if(total_Credit>=60 && pc_check==1 && pecnt>=2 && BTPcredit>=6){
				System.out.println("Congratulations You have passed all the requirement for getting Graduation Degree");
			}
			else{
				System.out.println("You don't have all parameter to get Graduation degree");
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}

	}
	
	public static void update_Profile(String user_id) {


		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			Scanner sc = new Scanner(System.in);
			Connection con  = ConnectionProvider.getConnection();
			String contactq = "SELECT * FROM STUDENT WHERE ENTRY_NUM = ?";
			PreparedStatement c1 = con.prepareStatement(contactq);
			c1.setString(1,user_id);
			ResultSet contactdata = c1.executeQuery();
			contactdata.next();
			String contact = contactdata.getString(5);
			System.out.println("Your Current details are :");
			System.out.println("Contact No: "+ contact);
			System.out.println();
			System.out.println("1. for update the contact details");
			System.out.println("2. Back to main menu");
			int a = sc.nextInt();
			if(a==1){
				System.out.println("Enter new contact number");
				String newcont = br.readLine();
				String q = "update student set contact_no = ? where entry_num = ?";
				PreparedStatement q1 = con.prepareStatement(q);
				q1.setString(1,newcont);
				q1.setString(2,user_id);
				q1.executeUpdate();
				System.out.println("You have successfully update your contact number");
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}

		
	}
}
