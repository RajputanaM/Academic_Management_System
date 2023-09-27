package org.example;

import java.sql.SQLOutput;
import java.util.Scanner;

public class Instructor {
	private static Scanner sc = new Scanner(System.in);
	public static void instructor_ui(String user_id) {
		try {
			int temp_for_while = 0;
			int choice;
			while(temp_for_while!=6) {
				System.out.println();
				System.out.println("********************* Welcome To AIMS, IIT Ropar*************************");
				System.out.println("*********************  INSTRUCTOR HOME PAGE  ************************");
				System.out.println();
				System.out.println("*** 1. Register for a Course  ***");
				System.out.println("*** 2. DeRegister for a Course  ***");
				System.out.println("*** 3. Offered Course  ***");
				System.out.println("*** 4. View Grade  ***");
				System.out.println("*** 5. Update Grades  ***");
				System.out.println("*** 6. Log Out  ***");
		
				choice = sc.nextInt();
				temp_for_while = choice;
				if(choice==1) {
					Instructor_Function.register(user_id);
				}
				else if(choice==2) {
					Instructor_Function.deregister(user_id);
				}
				else if(choice==3) {
					Instructor_Function.offeredCourse(user_id);
				}
				else if(choice==4) {
					Instructor_Function.viewGrade(user_id);
				}
				else if(choice==5) {
					Instructor_Function.updateGrades(user_id);
				}
			}
			return;
		}catch(Exception err) {
			err.printStackTrace();
		}
	}
}
