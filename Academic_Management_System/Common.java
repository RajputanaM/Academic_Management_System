package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Common {
	private static Connection con;
	public static HashMap<String,Integer> value=new HashMap<String,Integer>();
	public static Float total_credit_reg;
    public static  void Hashmap_init() {
  	    value.put("A", 10);
        value.put("A-", 9);
        value.put("B", 8);
        value.put("B-", 7);
        value.put("C", 6);
        value.put("C-", 5);
        value.put("D", 4);
        value.put("E", 3);
        value.put("F", 0);
		value.put("U", 0);
        total_credit_reg = (float) 0;
        con = ConnectionProvider.getConnection();
    }
    public class Course {
	  private String courseCode;
	  private String courseName;
	  private Float credits;
	  private String grade;
	  // other necessary fields and methods

	  public Course(String courseCode, String courseName, Float credits,String grade) {
	    this.courseCode = courseCode;
	    this.courseName = courseName;
	    this.credits = credits;
	    this.grade = grade;
	  }
	  // getters and setters for all fields
	  public String getCourseCode() {
		    return courseCode;
		  }
	  
		  public String getCourseName() {
		    return courseName;
		  }

		  public Float getCredits() {
		    return credits;
		  }
		  
		  public String getCourseGrade() {
			    return grade;
		  }
	}



public static Float CGPA_func(String user_id) {
	   Float cg=(float) 0.00;
	try {
		
		 Hashmap_init();
      
      String Graded = "SELECT * FROM student_course_grade where student_id = ? ";				
		 PreparedStatement gradestmt = con.prepareStatement(Graded);
		 gradestmt.setString(1,user_id);
		 ResultSet istakenG = gradestmt.executeQuery();
		 Float totalvalue = (float) 0;
		 while (istakenG.next()) {
             String grade = istakenG.getString(4);
             Float credit = istakenG.getFloat(7);

             totalvalue += (value.get(grade)) * (credit);
             if (grade.equals("F")) {
             	
             } else {
                 total_credit_reg += credit;
             }
		 }
		 if(totalvalue==0)
			 return (float) 0;
		 cg = totalvalue/total_credit_reg;
		 return cg;
	}
	catch(Exception Err) {
		Err.printStackTrace();
	}
	 
	return cg;
}

public static Float get_totalCredit(String user_id) {
	CGPA_func(user_id);
	return total_credit_reg;
}



public static Float Semester_GPA_func(String user_id,int semester) {
		   Float cg=(float) 0.00;
		try {
			Hashmap_init();
	         
	         String Graded = "SELECT * FROM student_course_grade where student_id = ? and semester = ? ";				
			 PreparedStatement gradestmt = con.prepareStatement(Graded);
			 gradestmt.setString(1,user_id);
			 gradestmt.setInt(2, semester);
			 ResultSet istakenG = gradestmt.executeQuery();
			 Float totalvalue = (float) 0;
			 
			 while (istakenG.next()) {
	                String grade = istakenG.getString(4);
	                Float credit = istakenG.getFloat(7);

	                totalvalue += (value.get(grade)) * (credit);
	                if (grade.equals("F")) {
	                	
	                } else {
	                    total_credit_reg += credit;
	                }
			 }
			 if(totalvalue==0)
				 return (float) 0;
			 cg = totalvalue/total_credit_reg;
			 return cg;
		}
		catch(Exception Err) {
			Err.printStackTrace();
		}
		 
		return cg;
	}
	


public static Float semester_credit(String user_id,int semester,int enrollment_status) {
	
	 Float total_credit=(float) 0.00;
		try {
			Hashmap_init();
	         String Graded;
	         if(enrollment_status==1) {
	        	 Graded = "SELECT * FROM student_course_grade where student_id = ? AND SEMESTER = ? ";

	         }
	         else {

	        	 Graded = "SELECT * FROM student_course_registration where student_id = ? AND SEMESTER = ? ";
	         }
	         				
			 PreparedStatement gradestmt = con.prepareStatement(Graded);
			 gradestmt.setString(1,user_id);
			 gradestmt.setInt(2, semester);
			 ResultSet istakenG = gradestmt.executeQuery();

			 if(istakenG.isBeforeFirst()) {

				 while (istakenG.next()) {
					 Float credit;

					 if(enrollment_status==1) {
						  credit = istakenG.getFloat(7);
					 }
					 else {
						 credit = istakenG.getFloat(5);
					 }
					 total_credit+=credit;
				 }
				 return total_credit;
			 }
			 else {
				 if(enrollment_status==1)
				 return (float) 18;
				 return (float) 0;
			 }
			 
		}
		catch(Exception Err) {
			Err.printStackTrace();
		}
		 
		return total_credit;
	
}

public static void viewGrade() {
	
	
	try {
		con = ConnectionProvider.getConnection();
		String allgrade = "SELECT * FROM STUDENT_COURSE_GRADE order by student_id desc";
		PreparedStatement all = con.prepareStatement(allgrade);
		ResultSet all1 = all.executeQuery();
		System.out.println("Here are the List of all Graded Course");
		System.out.println("-".repeat(120));
		System.out.printf("%-15s | %-10s | %-8s | %-7s | %-15s | %-5s | %10s%n", "Student ID", "Course ID", "Semester", "Credit", "Instructor ID", "Grade","Course Type");
		System.out.println("-".repeat(120));
		int t = 1;
		while(all1.next()) {
			String student_id = all1.getString(1);
			String course_id = all1.getString(2);
			int semester = all1.getInt(3);
			String Grade = all1.getString(4);
			String ins_id = all1.getString(5);
			String course_type = all1.getString(6);
			Float Credit = all1.getFloat(7);
			System.out.printf("%-15s | %-10s | %-8d | %-7.2f | %-15s | %-5s | %10s%n", student_id, course_id, semester, Credit, ins_id, Grade,course_type);

			// System.out.println("    "+t+". "+student_id+"| "+course_id+"     |   "+semester+"   \t| "+Credit+" \t |   "+ins_id+" \t |   "+Grade+"    \t"+course_type+"   *****");
			t++;
		}
	}
	catch(Exception Err) {
		Err.printStackTrace();
	}
	
}

public static void viewGrade(String user_id,String role) {
	try {
		con = ConnectionProvider.getConnection();
		String allgrade;
		if(role.equals("ins")) {
			 allgrade = "SELECT * FROM STUDENT_COURSE_GRADE WHERE INSTRUCTOR_ID = ?";
		}
		else {
			allgrade = "SELECT * FROM STUDENT_COURSE_GRADE WHERE STUDENT_ID = ?";
		}
		System.out.println("looks Good");
		PreparedStatement all = con.prepareStatement(allgrade);
		all.setString(1, user_id);
		ResultSet all1 = all.executeQuery();
		System.out.println("Here are the List of all Graded Course");
		System.out.println("-".repeat(120));
		System.out.printf("%-15s | %-10s | %-8s | %-7s | %-15s | %-5s | %10s%n", "Student ID", "Course ID", "Semester", "Credit", "Instructor ID", "Grade","Course Type");
		System.out.println("-".repeat(120));
		int t = 1;
		while(all1.next()) {
			String student_id = all1.getString(1);
			String course_id = all1.getString(2);
			int semester = all1.getInt(3);
			String Grade = all1.getString(4);
			String ins_id = all1.getString(5);
			String course_type = all1.getString(6);
			Float Credit = all1.getFloat(7);
			System.out.printf("%-15s | %-10s | %-8d | %-7.2f | %-15s | %-5s | %10s%n", student_id, course_id, semester, Credit, ins_id, Grade,course_type);

			// System.out.println("    "+t+". "+student_id+"| "+course_id+"     |   "+semester+"   \t| "+Credit+" \t |   "+ins_id+" \t |   "+Grade+"    \t"+course_type+"   *****");
			t++;
		}
	}
	catch(Exception Err) {
		Err.printStackTrace();
	}
 }

 public static List<Course> retrieveCourseHistory(String user_id,int semester) {
	 
	 try {
		 Connection con = ConnectionProvider.getConnection();
		
		 String allgrade = "SELECT scg.course_id,scg.credit, scg.grade FROM Student_Course_Grade scg WHERE scg.student_id = ? AND scg.semester = ?";
		 PreparedStatement all = con.prepareStatement(allgrade);
			all.setString(1, user_id);
			all.setInt(2,semester);
			ResultSet all1 = all.executeQuery();
			 
			List<Course> courses = new ArrayList<>();
			 
			while(all1.next()) {
				String course_id = all1.getString(1);
				String temp = "SELECT * FROM COURSE_CATALOG WHERE COURSE_ID = ?";
				PreparedStatement T = con.prepareStatement(temp);
				T.setString(1,course_id);
				ResultSet t1 = T.executeQuery();
				String title = "";
				if(t1.next()) title = t1.getString(2);
				Float Credit = all1.getFloat(2);
				String Grade = all1.getString(3);
				Common common = new Common();
				Course course = common.new Course(course_id, title, Credit, Grade);
				courses.add(course);	
			}
			return courses;
	 }
	 catch(Exception Err){
		 Err.printStackTrace();
	 }
	return null; 
 }
public static List<Course> retrieveCourseHistory(String user_id,String type,String dept) {
	 
	 try {
		 Connection con = ConnectionProvider.getConnection();
		
		 String core = " select course_catalog.course_id,course_catalog.title,course_catalog.credit,student_course_grade.grade  from student_course_grade,course_catalog where course_catalog.dept_name = ? AND COURSE_CATALOG.TYPE = ? AND COURSE_CATALOG.COURSE_ID = STUDENT_COURSE_GRADE.COURSE_ID AND STUDENT_COURSE_GRADE.STUDENT_ID = ?";
		 PreparedStatement core1 = con.prepareStatement(core);
		 core1.setString(1,dept);
		 core1.setString(2,type);
		 core1.setString(3,user_id);
		 ResultSet core2 = core1.executeQuery();
			List<Course> courses = new ArrayList<>();

			while(core2.next()) {
				String course_id = core2.getString(1);
				String title = core2.getString(2);
				Float Credit = core2.getFloat(3);
				String Grade = core2.getString(4);
				Common common = new Common();
				Course course = common.new Course(course_id, title, Credit, Grade);
				courses.add(course);	
			}
			return courses;
	 }
	 catch(Exception Err){
		 Err.printStackTrace();
	 }
	return null; 
 }
 
}
