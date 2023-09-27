package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Instructor_Function {
	
	public static void register(String user_id) {
		
		try {
			Connection con = ConnectionProvider.getConnection();
			if(con!=null) System.out.println("looks good");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			Scanner sc = new Scanner(System.in);
			// current session getting;
			String cur_year_sem = "SELECT * FROM current_session";
			PreparedStatement c1 = con.prepareStatement(cur_year_sem);
			ResultSet c2 = c1.executeQuery();
			c2.next();
			int year = c2.getInt(1);
			String sem = c2.getString(2);
			int is_allow = c2.getInt(3);
			if(is_allow==0){
				System.out.println("Time Expired for course Floating Kindly contact the academic section");
				return ;
			}
			System.out.println("Please Enter the details of Course which you want to offer or x for exit");
			int p =0;
			String Course_id="";
			while(p!=1) {
				System.out.println("Enter Course_ID (like 'AB123') of course which you want to Offer");
				Course_id = br.readLine();
				if(Course_id.equals("x")) {
					return ;
				}
				 if (Course_id.matches("[A-Z]{2}\\d{3}")) {
			            p = 1;
			        } else {
			            System.out.println("Please Enter course_id in Proper Format, or x for exit");
			            return;
			        }
			}
			// session getting

			String course_details = "SELECT * FROM course_catalog WHERE course_id = ?";
			PreparedStatement stmt = con.prepareStatement(course_details);
			stmt.setString(1, Course_id);
			ResultSet newSet = stmt.executeQuery();
			
			
			if(newSet.next()) {
				
				String c_type = newSet.getString(6);
				String offer_details = "SELECT * FROM offeredcourses WHERE course_id = ?";
				PreparedStatement stmt1 = con.prepareStatement(offer_details);
				stmt1.setString(1, Course_id);
				ResultSet newSet1 = stmt1.executeQuery();
				if(!newSet1.isBeforeFirst()) {
				String Dept_name = newSet.getString(3);
				String LTPSC = newSet.getString(4);
				float credit = newSet.getFloat(5);
	
				System.out.println("Enter years for which you want to offer your course");
				ArrayList<Integer> years = new ArrayList<Integer>();
				System.out.println("Enter 1 if you completed entering year");
				int temp = 0;
				while(temp!=1) {
					temp = sc.nextInt();
					if(temp!=1)
					years.add(temp);
				}
				int size = years.size();
				int[] yearArr = new int[size];
				for (int i = 0; i < size; i++) {
				    yearArr[i] = years.get(i);
				}
				Integer[] yearObjectArr = Arrays.stream(yearArr).boxed().toArray(Integer[]::new);
				Array semesterSqlArray = con.createArrayOf("integer", yearObjectArr);
				
				System.out.println("Enter minumium cgpa required for this course");
				int cg = sc.nextInt();
				
				String faculty_details = "SELECT * FROM INSTRUCTOR WHERE instructor_id = ?";
				PreparedStatement newcreating_statemnt = con.prepareStatement(faculty_details);
				newcreating_statemnt.setString(1, user_id);
				ResultSet newSet3 = newcreating_statemnt.executeQuery();

				String name="",deptName="";
				while(newSet3.next()) {
	                name = newSet3.getString(1);
	                deptName = newSet3.getString(3);  
				}
				
				if(!deptName.equals(Dept_name)) {
					System.out.println(" This course does not belong to your Department.");
					}
				
				else {

					
					String push_course_offering = "INSERT INTO OfferedCourses (Course_id, dept_name, batches_allowed, credit, Instructor_id, LTPSC, cgConstraint,type,year,sem) VALUES (?,?,?,?, ?, ?, ?, ?, ?, ?)";
					PreparedStatement st = con.prepareStatement(push_course_offering);
					st.setString(1,Course_id);
					st.setString(2,Dept_name);
			
					
					
					st.setArray(3, semesterSqlArray);
					st.setFloat(4,credit);
					st.setString(5,user_id);
					st.setString(6,LTPSC);
					st.setInt(7,cg);
					st.setString(8,c_type);
					st.setInt(9,year);
					st.setString(10,sem);
					st.executeUpdate();
					System.out.println("You have Successfully Offered a course");
				}
			}
			else {
				System.out.println("This Course has already Offered");
			}
		}
			else {
				System.out.println("This Course does not Exists.");
			}
			

//			System.out.println("1. for Go back in the Menu");
//			int a = sc.nextInt();
//			if(a==1) {
//				return ;
//			}
		}
		catch(Exception err) {
			err.printStackTrace();
		}
		
	}
	
	public static void deregister(String user_id) {
		
		
		try {
			
			// krna ye h ki sbse phle faculty se input lenge ki aapko kaun sa course drop krna h uska course id , enter kijiye iske bad
			// jo mera enrolled course krke jo table h usme se is course id , instructor id, recent semester ke acoording filter krke usko delete kr denge
			Connection con = ConnectionProvider.getConnection();
	
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			String cur_year_sem = "SELECT * FROM current_session";
			PreparedStatement c1 = con.prepareStatement(cur_year_sem);
			ResultSet c2 = c1.executeQuery();
			c2.next();
			int year = c2.getInt(1);
			String sem = c2.getString(2);
			int is_allow = c2.getInt(3);
			if(is_allow==0){
				System.out.println("Time Expired for course Floating Kindly contact the academic section");
				return ;
			}

			offeredCourse(user_id);
			int p =0;
			String course_id="";
			while(p!=1) {
				System.out.println("Enter Course_ID (like 'AB123') of course which you want to Delete from offering");
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

			String offer_details = "SELECT * FROM offeredcourses WHERE course_id = ?";
			PreparedStatement stmt1 = con.prepareStatement(offer_details);
			stmt1.setString(1, course_id);
			ResultSet newSet1 = stmt1.executeQuery();
			if(newSet1.next()) {
				String deletefromstudentregistration = "DELETE FROM STUDENT_COURSE_REGISTRATION WHERE course_id = ?";
				PreparedStatement d1 = con.prepareStatement(deletefromstudentregistration);
				d1.setString(1,course_id);
				d1.executeUpdate();
				String deletefromofferedcourses = "DELETE FROM OFFEREDCOURSES WHERE course_id = ?";
				 d1 = con.prepareStatement(deletefromofferedcourses);
				d1.setString(1,course_id);
				d1.executeUpdate();
				System.out.println("You have Successfully Dropped Your Course "+course_id);
			}
			else {
				System.out.println("You have not offered this course");
			}
			
			System.out.println("1. for Go back in the Menu");
			Scanner sc= new Scanner(System.in);
			int a = sc.nextInt();
			if(a==1) {
				return ;
			}
		}
		catch(Exception err) {
			err.printStackTrace();
		}
	}
	
	public static void offeredCourse(String user_id) {

		
		
		try {
			Connection con = ConnectionProvider.getConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			Scanner sc= new Scanner(System.in);
			
			String getCourse = "SELECT * FROM OfferedCourses WHERE Instructor_id = ?";
			
			PreparedStatement my_stmt = con.prepareStatement(getCourse);
			my_stmt.setString(1, user_id);
			ResultSet newSet = my_stmt.executeQuery();
			int t = 1;

			int is_offer=0;
			while(newSet.next()) {
				is_offer=1;
				if(t==1)
				{
					System.out.println("This are the courses offered by You");
					System.out.println();
					System.out.format("%-10s | %-10s | %-20s | %-10s | %-15s | %-15s | %-10s | %-10s | %-10s | %-15s%n", "Course ID", "Dept Name","Instructor ID", "Credit", "LTPSC", "CG Constraint", "Type", "Year", "Semester","Batches Allowed");
					System.out.println("-".repeat(152));
				}

				// System.out.println(" ***** course_id | dept_name | B_Allowed | credit | instructor_id | ltpsc | cgconstraint *****");
				String course_id = newSet.getString(1);
				String dept_name = newSet.getString(2);
				Array years = newSet.getArray(3);
				Float credit = newSet.getFloat(4);
				String ins_id = newSet.getString(5);
				String ltpsc = newSet.getString(6);
				Float cg = newSet.getFloat(7);
				String type = newSet.getString("type");
				int year = newSet.getInt("year");
				String sem = newSet.getString("sem");
				//System.out.println("    "+t+".\t"+course_id+"    |  "+dept_name+"      |"+years+"  \t| "+credit+" \t |   "+ins_id+" \t | "+ltpsc+"  |   "+cg+" *****");
				System.out.format("%-10s | %-10s  | %-20s | %-10.2f | %-15s | %-15.2f | %-10s | %-10d | %-10s | ",
						course_id, dept_name, ins_id, credit, ltpsc, cg, type, year, sem);
				System.out.println(years);
				t++;
			}
			if(is_offer==0) {
				System.out.println("No Course offered By You");
			}

		}
		catch(Exception err) {
			err.printStackTrace();
		}
		
		
		
		
		
		
	}
	
	
	
	public static void viewGrade(String user_id) {
		
		
		try {
			Connection con = ConnectionProvider.getConnection();
			
			
			// session getting
			String cur_year_sem = "SELECT * FROM current_session";
			PreparedStatement c1 = con.prepareStatement(cur_year_sem);
			ResultSet c2 = c1.executeQuery();
			c2.next();
			int year = c2.getInt(1);
			String sem = c2.getString(2);
			
//			System.out.println(year+sem);
			// Yha pe ye kiye h qki grade me se ye nhi mil payega ki kaun sa
			// sem ye h offered
			// puchho ki bs ye same me dekhna h ya sare me agr sare me to change krna pdega query




		    String course_offered = "SELECT scg.student_id, oc.course_id, scg.semester, oc.credit, oc.instructor_id, scg.grade FROM OfferedCourses oc INNER JOIN Student_Course_Grade scg ON oc.course_id = scg.course_id AND oc.instructor_id = scg.instructor_id WHERE oc.year = ? AND oc.sem = ? AND oc.instructor_id = ? order by course_id";
			PreparedStatement stmt = con.prepareStatement(course_offered);
			stmt.setInt(1, year);
			stmt.setString(2, sem);
			stmt.setString(3, user_id);
			ResultSet stmt1 = stmt.executeQuery();
			int t=1;
			if(stmt1.isBeforeFirst()) {
				System.out.println("Here are the courses graded by you in this semester:");
				System.out.println("-".repeat(120));
				System.out.printf("%-15s | %-10s | %-8s | %-7s | %-15s | %-5s%n", "Student ID", "Course ID", "Semester", "Credit", "Instructor ID", "Grade");
				System.out.println("-".repeat(120));
				while(stmt1.next()) {
					String student_id = stmt1.getString(1);
					String course_id = stmt1.getString(2);
					int semester = stmt1.getInt(3);
					float credit = stmt1.getFloat(4);
					String ins_id = stmt1.getString(5);
					String grade = stmt1.getString(6);
					System.out.printf("%-15s | %-10s | %-8d | %-7.2f | %-15s | %-5s%n", student_id, course_id, semester, credit, ins_id, grade);
				}
			}
			else {
				System.out.println("You have not Graded any course in this semester or Entering invalid input");
			}

			String alls = "SELECT * FROM STUDENT_COURSE_GRADE WHERE INSTRUCTOR_ID = ? order by course_id";
			PreparedStatement stmta = con.prepareStatement(alls);

			stmta.setString(1, user_id);
			stmt1 = stmta.executeQuery();

			if(stmt1.isBeforeFirst()) {
				System.out.println();
				System.out.println("Here are the courses graded by you till now: ");
				System.out.println("-".repeat(120));
				System.out.printf("%-15s | %-10s | %-8s | %-7s | %-15s | %-5s%n", "Student ID", "Course ID", "Semester", "Credit", "Instructor ID", "Grade");
				System.out.println("-".repeat(120));
				while(stmt1.next()) {
					String student_id = stmt1.getString(1);
					String course_id = stmt1.getString(2);
					int semester = stmt1.getInt(3);
					float credit = stmt1.getFloat(7);
					String ins_id = stmt1.getString(5);
					String grade = stmt1.getString(4);
					System.out.printf("%-15s | %-10s | %-8d | %-7.2f | %-15s | %-5s%n", student_id, course_id, semester, credit, ins_id, grade);
				}
			}
			else {
				System.out.println("You have not offered any course ");
			}
//			System.out.println("1. for Go back in the Menu");
//			Scanner sc= new Scanner(System.in);
//			int a = sc.nextInt();
//			if(a==1) {
//				return ;
//			}
		}
		catch(Exception Err) {
			Err.printStackTrace();
		}
		
	}
	
	public static void updateGrades(String user_id) {
		// connection part 
		
		
		try {
			Connection con = ConnectionProvider.getConnection();
			if(con!=null) System.out.println("looks good");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			Scanner sc = new Scanner(System.in);
			String cur_year_sem = "SELECT * FROM current_session";
			PreparedStatement c1 = con.prepareStatement(cur_year_sem);
			ResultSet c2 = c1.executeQuery();
			c2.next();
			int year = c2.getInt(1);
			String sem = c2.getString(2);
			
			
			
//			System.out.println("Enter Course_id for which you want to updateGrades");
//			course_id = br.readLine();
			System.out.println("Please Enter File path that contains grade report of Student");
			String filePath = br.readLine();
			File csvGrade = new File(filePath);
			if(!csvGrade.exists()) {
				System.out.println("file not found! Please input correct file path");
				return ;
			}
			BufferedReader br1 = new BufferedReader(new FileReader(csvGrade)); 
			String nextLine;
			int firstlineskip = 0;
			while((nextLine=br1.readLine())!=null) {
				if(firstlineskip==0) {
					firstlineskip++;
					continue;
				}
				String course_id, student_id, Grade ;
				String [] data = nextLine.split(",");
				course_id = data[0];
				student_id = data[1];
				Grade = data[2];
				// HERE WE CHECK THAT THIS COURSE HAS OFFERED BY THIS INSTRUCTOR OR NOT 
				
				String checkoffered = "SELECT * FROM OFFEREDCOURSES WHERE INSTRUCTOR_ID = ? AND COURSE_ID=?";
				PreparedStatement checkofferedstmt = con.prepareStatement(checkoffered);
				checkofferedstmt.setString(1, user_id);
				checkofferedstmt.setString(2, course_id);
				ResultSet is_offered = checkofferedstmt.executeQuery();
				if(is_offered.next()) {
					// Here we check that this course has offer in current sem or not;
					String check_cur_sem = "SELECT year,sem FROM OFFEREDCOURSES WHERE COURSE_ID = ? AND YEAR = ? AND SEM = ?";
					PreparedStatement chek = con.prepareStatement(check_cur_sem);
					chek.setString(1, course_id);
					chek.setInt(2, year);
					chek.setString(3, sem);
					ResultSet chek1 = chek.executeQuery();
					if(chek1.next()) {
//						System.out.println("Enter student_id ");
//						student_id = br.readLine();
//						System.out.println("Enter Grade");
//						Grade = br.readLine();
						String inCourseReg = "SELECT * FROM student_course_registration where student_id = ? and course_id = ? and instructor_id=?";
						PreparedStatement st = con.prepareStatement(inCourseReg);
						st.setString(1, student_id);
						st.setString(2, course_id);
						st.setString(3,user_id);
						ResultSet first = st.executeQuery();
						
						String inGrade = "SELECT * FROM Student_Course_Grade WHERE STUDENT_ID = ? AND COURSE_ID = ? AND INSTRUCTOR_ID = ?";
						PreparedStatement st1 = con.prepareStatement(inGrade);
						st1.setString(1, student_id);
						st1.setString(2, course_id);
						st1.setString(3, user_id);
						ResultSet second = st1.executeQuery();
						
						if(first.next()) {
							int semester = first.getInt(3);
							Float credit = first.getFloat(5);
							String course_type = first.getString(4);
							String insertintoGrade = "INSERT INTO Student_Course_Grade VALUES (?,?,?,?,?,?,?)";
							PreparedStatement st2 = con.prepareStatement(insertintoGrade);
							st2.setString(1,student_id);
							st2.setString(2,course_id);
							st2.setInt(3,semester);
							st2.setString(4,Grade);
							st2.setString(5,user_id);
							st2.setString(6, course_type);
							st2.setFloat(7, credit);
							st2.executeUpdate();
							String delete = "DELETE FROM Student_Course_Registration WHERE course_id = ? AND student_id = ?";
							PreparedStatement st4 = con.prepareStatement(delete);
							st4.setString(1, course_id);
							st4.setString(2, student_id);
							st4.executeUpdate();
							System.out.println("You have Successfully graded "+course_id+" for "+student_id);
							}
						else if(second.next()) {
							String updatequery = "UPDATE Student_Course_Grade SET grade = ? WHERE student_id = ? AND course_id = ?";
							PreparedStatement st3 = con.prepareStatement(updatequery);
							st3.setString(1, Grade);
							st3.setString(2, student_id);
							st3.setString(3, course_id);
							st3.executeUpdate();
							System.out.println("You have Successfully updated grade for  "+course_id+" for "+student_id);
						}
						else {
							System.out.println("This student has not taken course "+course_id);
						}
					}
					else {
						System.out.println("Grade Update date is Expire. Kindly contact to Academic Section");
					}
				}
				else {
					System.out.println("The Course "+course_id+" is not offered by You");
				}
			}
		}
		catch(Exception Err) {
			Err.printStackTrace();
		}
	}
	
	
	
	}// instructor_function close braces;
