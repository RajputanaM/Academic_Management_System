package org.example;

import java.util.Scanner;

public class Student {
			private static Scanner sc= new Scanner(System.in);;
	public static void student_ui(String user_id) {
		try {
			
			// here i am writing ui part after login into database;

			int temp_for_while = 0;
			while(temp_for_while!=8) {
				System.out.println();
				System.out.println("********************* Welcome To AIMS, IIT Ropar*************************");
				System.out.println("*********************  STUDENT HOME PAGE  ************************");
				System.out.println("******* Hi... "+user_id+"*******");
				System.out.println("*** 1. Register for a Course  ***");
				System.out.println("*** 2. DeRegister for a Course  ***");
				System.out.println("*** 3. View Your Grade  ***");
				System.out.println("*** 4. Enrolled Courses  ***");
				System.out.println("*** 5. Compute Your CGPA  ***");
				System.out.println("*** 6. Check for Graduation Complete");
				System.out.println("*** 7. Update Your Profile");
				System.out.println("*** 8. Log Out  ***");
				
				int choice = sc.nextInt();
				temp_for_while = choice;
				if(choice==1) {
					Student_Function.register(user_id);
				}
				else if(choice==2) {
					Student_Function.deregister(user_id);
				}
				else if(choice==3) {
					Student_Function.viewGrade(user_id);
				}
				else if(choice==4) {
					Student_Function.enrolledCourses(user_id);
				}
				else if(choice==5) {
					Student_Function.computeCGPA(user_id);
				}
				else if(choice==6) {
					Student_Function.check_Graduation(user_id);
				}
				else if(choice ==7) {
					Student_Function.update_Profile(user_id);
				}
			}
//			sc.close();
			return ;
		}
		catch(Exception err) {
			err.printStackTrace();
		}
	}
	
}
