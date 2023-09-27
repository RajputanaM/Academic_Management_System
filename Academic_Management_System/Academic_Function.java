package org.example;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Academic_Function {
	private static Scanner sc;

	
	
	public static void Generate_Transcript(String user_id) 
	{
	   
		try {
			String studentName;
			Connection con = ConnectionProvider.getConnection();
			String nameinfo = "SELECT * FROM STUDENT WHERE ENTRY_NUM = ?";
			PreparedStatement n1 = con.prepareStatement(nameinfo);
			n1.setString(1, user_id);
			ResultSet namedata = n1.executeQuery();
			if(namedata.next()) {
				
				String cur_year_sem = "SELECT * FROM current_session";
				PreparedStatement c1 = con.prepareStatement(cur_year_sem);
				ResultSet c2 = c1.executeQuery();
				c2.next();
				int year = c2.getInt(1);
				String sem = c2.getString(2);
				
				int batch_year = namedata.getInt(3);
				int diff = (year-batch_year)*2;
				List<Integer> semArr=new ArrayList<Integer>();  
				
				studentName = namedata.getString(1);
				int i=1;
				diff = Math.min(8,diff);
				for( i=1;i<=diff;i++) {
					semArr.add(i);
				}
				if(diff!=8)
				if(sem.equals("SUMMER")) {
					semArr.add(i);
				}
				
				// Calculate overall GPA
				Float overallGPA = Common.CGPA_func(user_id);
				// Create a text file and write student information
				try (PrintWriter writer = new PrintWriter(new File(user_id+"transcript.txt"))) {
				    writer.println("TRANSCRIPT");
				    writer.println("Name: " + studentName);
				    writer.println("Entry No: " + user_id);
				    writer.println();
				}

				// Loop through each semester and append course history to transcript file
				Float prev = (float) 0;
				int d=1;
				for(int X:semArr) {
				    List<Common.Course> courseHistory = Common.retrieveCourseHistory(user_id,X);
				    try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(user_id+"transcript.txt"), true))) {
				        if(X==1) {
				            writer.println("--------------------------COURSE HISTORY----------------------------------");
				            writer.println("--------------------------------------------------------------------------");
				        }
				        writer.println("                              Semester "+X);
				        writer.println("Course\t\t\t\t\t\tCourse_Code\t\tGrade\t\t\tCredit");
			            writer.println("---------------------------------------------------------------------------");
				        for (Common.Course course : courseHistory) {
				            writer.printf("%-40s%-16s%-16s%f%n", course.getCourseName(),course.getCourseCode(),course.getCourseGrade(), course.getCredits());
				        }
				        writer.println("---------------------------------------------------------------------------");
						Float sgpa = Common.Semester_GPA_func(user_id, X);
				        writer.printf("GPA for Semester %d: %f%n ", X, sgpa);
//						writer.print("            ");
//						prev+= sgpa;
//						writer.printf("CGPA : %f%n", prev/d);
//						d++;
				        writer.println();
				        writer.println();
				    } catch (IOException e) {
				        e.printStackTrace();
				    }
				}
				// Append overall GPA to transcript file
				try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(user_id+"transcript.txt"), true))) {
				    writer.println("----------------------------------------------------------------------------");
				    writer.printf("Overall GPA: %f%n", overallGPA);
				    File file = new File(user_id+"transcript.txt");
				    System.out.println("File saved to: " + file.getAbsolutePath());
				} catch (IOException e) {
				    e.printStackTrace();
				}
			}
				
			else {
				System.out.println("This student does not exists");
			}
		}

		catch(Exception Err){
			Err.printStackTrace();
		}
	}

		 
	
	
	public static void editCourse() {
		System.out.println("Here are the courses list ");
		try{
			Connection con = ConnectionProvider.getConnection();
			Scanner sc = new Scanner(System.in);
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String allcourse = "SELECT * FROM COURSE_CATALOG";
			PreparedStatement allc = con.prepareStatement(allcourse);
			ResultSet rs = allc.executeQuery();
			System.out.format("%-10s | %-40s | %-10s | %-10s | %-10s | %-10s%n", "Course ID", "Title", "Dept Name", "LTPSC", "Credit", "Type");
			System.out.println("-".repeat(100));
			while(rs.next()){
				String course_id = rs.getString("course_id");
				String title = rs.getString("title");
				String dept_name = rs.getString("dept_name");
				String ltpsc = rs.getString("ltpsc");
				double credit = rs.getDouble("credit");
				String type = rs.getString("type");

				System.out.format("%-10s | %-40s | %-10s | %-10s | %-10.2f | %-10s%n", course_id, title, dept_name, ltpsc, credit, type);
			}
			int z=83;
			while(z!=3) {
				System.out.println("1. for add the course ");
				System.out.println("2. for edit the course");
				System.out.println("3. for back to main menu");
				z = sc.nextInt();
				if (z == 1) {
					int p = 0;
					String course_id = "";
					while (p != 1) {
						System.out.println("Enter Course_ID (like 'AB123') of new Course or x to exit");
						course_id = br.readLine();
						if (course_id.equals("x")) {
							return;
						}
						if (course_id.matches("[A-Z]{2}\\d{3}")) {
							p = 1;
						} else {
							System.out.println("Please Enter course_id in Proper Format, or x for exit");
						}
					}
					String course_details = "SELECT * FROM course_catalog WHERE course_id = ?";
					PreparedStatement stmt = con.prepareStatement(course_details);
					stmt.setString(1, course_id);
					ResultSet newSet = stmt.executeQuery();
					if (newSet.next()) {
						System.out.println("This course has been already added");
						return;
					}
					System.out.println("Enter Title of new Course");
					String title = br.readLine();
					System.out.println("Enter Dept Name LIKE (AB)");
					String dept_name = br.readLine();
					System.out.println("Enter L-T-P-S-C seperated by dash ");
					String ltpsc = br.readLine();
					System.out.println();
					System.out.println("Enter credit of Course");
					Float credit = sc.nextFloat();
					System.out.println("Enter course type ");
					String type = br.readLine();
					String addc = "INSERT INTO COURSE_CATALOG VALUES(?,?,?,?,?,?)";
					PreparedStatement addcourse = con.prepareStatement(addc);
					addcourse.setString(1, course_id);
					addcourse.setString(2, title);
					addcourse.setString(3, dept_name);
					addcourse.setString(4, ltpsc);
					addcourse.setFloat(5, credit);
					addcourse.setString(6, type);
					addcourse.executeUpdate();
					System.out.println("You have Successfully added a course");
				} else if (z == 2) {
					int p = 0;
					String course_id = "";
					while (p != 1) {
						System.out.println("Enter Course_ID (like 'AB123') of which you want to Edit or x to exit");
						course_id = br.readLine();
						if (course_id.equals("x")) {
							return;
						}
						if (course_id.matches("[A-Z]{2}\\d{3}")) {
							p = 1;
						} else {
							System.out.println("Please Enter course_id in Proper Format, or x for exit");
						}
					}
					String course_details = "SELECT * FROM course_catalog WHERE course_id = ?";
					PreparedStatement stmt = con.prepareStatement(course_details);
					stmt.setString(1, course_id);
					ResultSet newSet = stmt.executeQuery();
					if (newSet.next()) {
						String title = newSet.getString(2);
						String deptName = newSet.getString(3);
						String ltpsc = newSet.getString(4);
						Float credit = newSet.getFloat(5);
						String type = newSet.getString(6);
						System.out.println("Current course structure of :"+course_id );
						System.out.println();
						System.out.println("Course_ID   TITLE       \t       DEPT_NAME \t  L-T-P-S-C  CREDIT \tTYPE");
						System.out.println(course_id+"\t\t"+title+"\t\t\t"+deptName+"\t\t\t"+ltpsc+"\t"+credit+"\t\t"+type);
						System.out.println();
						System.out.println("Enter New L-T-P-S-C or x for exit");
						ltpsc= br.readLine();
						if(ltpsc.equals("x"))return;
						System.out.println("Enter new credit or 1 for exit");
						credit = sc.nextFloat();
						if(credit==1) return ;
						String up = "UPDATE COURSE_CATALOG SET LTPSC = ? , CREDIT = ? WHERE COURSE_ID = ?";
						PreparedStatement st = con.prepareStatement(up);
						st.setString(1,ltpsc);
						st.setFloat(2,credit);
						st.setString(3,course_id);
						st.executeUpdate();
						System.out.println("You have successfully update the Course structure of "+course_id);

						return;
					} else {
						System.out.println("This course is not exist. Please add in the course catalog first");
						return;
					}
				} else {
					System.out.println("Enter correct option or 3 for go back to main menu");
				}

			}
		}
		catch (Exception e){
			e.printStackTrace();
		}

	}
	
	public static void viewGrade() {
		try {
			Common.viewGrade();
		}
		catch(Exception Err) {
			Err.printStackTrace();
		}

	}
	
	public static void generateTranscript() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			Connection conn = ConnectionProvider.getConnection();

			String query = "SELECT * FROM student ORDER BY batch ASC";
			PreparedStatement stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			System.out.println("Student Details:");

			while (rs.next()) {
				String entry_num = rs.getString("entry_num");
				String student_name = rs.getString("student_name");
				int batch = rs.getInt("batch");
				String dept_name = rs.getString("dept_name");
				String contact_no = rs.getString("contact_no");

				System.out.println(entry_num + " | " + student_name + " | " + batch + " | " + dept_name + " | " + contact_no);

			}
			System.out.println("Enter the Student Entry No for Generating the Transcript");
			String user_id = br.readLine();
			Generate_Transcript(user_id);
		}
		 catch(Exception Err){
			 Err.printStackTrace();
		 }
	}

	public static void academicSession(){
		try{
			Connection con = ConnectionProvider.getConnection();
			Scanner sc =  new Scanner(System.in);
			int temp_for_while = 0;
			int choice = 0;
			int year = 0,is_stallow=0,is_insallow=0;
			String sem = "";

				String cur_year_sem = "SELECT * FROM current_session";
				PreparedStatement c1 = con.prepareStatement(cur_year_sem);
				ResultSet c2 = c1.executeQuery();
				c2.next();
				year = c2.getInt(1);
				sem = c2.getString(2);
				is_insallow = c2.getInt(3);
				is_stallow = c2.getInt(4);

			while(temp_for_while!=6) {
				System.out.println("***************************************************");
				System.out.println("***************************************************");
				System.out.println("Please choose an option from the following:");
				System.out.println("1. Open the Session for students for Course Registration");
				System.out.println("2. Close the Session for students for Course Registration");
				System.out.println("3. Open the Session for Faculty for Course offering");
				System.out.println("4. Close the Session for Faculty for Course Registration");
				System.out.println("5. Change the Academic Session");
				System.out.println("6. Go Back to Main Menu");
				System.out.print("Enter your choice: ");

				choice = sc.nextInt();
				temp_for_while = choice;
				if(choice==1) {
					if(is_stallow==1){
						System.out.println("You have already open the window for course Registration for students");
					}
					else{
						String close = "update current_session set st_allow =?  where year = ?";
						PreparedStatement closestmt = con.prepareStatement(close);
						closestmt.setInt(1,1);
						closestmt.setInt(2,year);
						System.out.println("You have successfully opened window for Student course Registration ");
						closestmt.executeUpdate();
						return ;
					}
				}
				else if(choice==2) {
					if(is_stallow==0){
						System.out.println("You have already close the window for course Registration for students");
					}
					else{
						String close = "update current_session set st_allow =?  where year = ?";
						PreparedStatement closestmt = con.prepareStatement(close);
						closestmt.setInt(1,0);
						closestmt.setInt(2,year);
						System.out.println("You have successfully closed window for Student course Registration ");
						closestmt.executeUpdate();
						return ;
					}
				}
				else if(choice==3) {
					if(is_insallow==1){
						System.out.println("You have already open the window for offer the course for faculty");
					}
					else{
						String close = "update current_session set ins_allow =?  where year = ?";
						PreparedStatement closestmt = con.prepareStatement(close);
						closestmt.setInt(1,1);
						closestmt.setInt(2,year);
						System.out.println("You have successfully opened window for offer the course for faculty ");
						closestmt.executeUpdate();
						return ;
					}
				}
				else if(choice==4) {
					if(is_insallow==0){
						System.out.println("You have already close the window for offer the course for faculty");
						System.out.println();
					}
					else{
						String close = "update current_session set ins_allow =?  where year = ?";
						PreparedStatement closestmt = con.prepareStatement(close);
						closestmt.setInt(1,0);
						closestmt.setInt(2,year);
						closestmt.executeUpdate();
						System.out.println("You have successfully closed window for Student course Registration ");
						System.out.println();
						return ;
					}
				}
				else if(choice==5){
					System.out.println("The Current Academic Year is "+ year+ " and "+sem+" semester");
					String queryfd = "SELECT * FROM STUDENT_COURSE_REGISTRATION";
					PreparedStatement qfd = con.prepareStatement(queryfd);
					ResultSet rsfd = qfd.executeQuery();
					while(rsfd.next()){

						String st_id = rsfd.getString(1);
						String c_id = rsfd.getString(2);
						int s = rsfd.getInt(3);
						String c_type = rsfd.getString(4);
						Float cr = rsfd.getFloat(5);
						String ins_id = rsfd.getString(6);
						String grade = "U";
						String insert_grade = "INSERT INTO STUDENT_COURSE_GRADE VALUES(?,?,?,?,?,?,?)";
						PreparedStatement insstmt = con.prepareStatement(insert_grade);
						insstmt.setString(1,st_id);
						insstmt.setString(2,c_id);
						insstmt.setInt(3,s);
						insstmt.setString(4,grade);
						insstmt.setString(5,ins_id);
						insstmt.setString(6,c_type);
						insstmt.setFloat(7,cr);
						insstmt.executeUpdate();
					}
					if(sem.equals("WINTER")){
						String q = "update current_session set sem = ?";
						PreparedStatement q1 = con.prepareStatement(q);
						q1.setString(1,"SUMMER");
						q1.executeUpdate();
					}
					else{
						String q = "update current_session set year = ? , sem = ?";
						PreparedStatement q1 = con.prepareStatement(q);
						year++;
						q1.setInt(1,year);
						q1.setString(2,"WINTER");
						q1.executeUpdate();
					}
					String del = "delete from student_course_registration";
					PreparedStatement del1 = con.prepareStatement(del);
					del1.executeUpdate();
					System.out.println("You have successfully update the academic session");
					System.out.println();
				}
			}
			return ;
		}
		catch(Exception err){
			err.printStackTrace();
		}
	}
}
