package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Academic {
	private static Scanner sc = new Scanner(System.in);
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
   public static void academic_ui() {
	   try {
			// here i am writing ui part after login into database;
			int temp_for_while = 0;
			int choice = 0;
			while(temp_for_while!=6) {
				System.out.println();
				System.out.println("********************* Welcome To AIMS, IIT Ropar*************************");
				System.out.println();
				System.out.println("*** 1. Edit the Course Catalog  ***");
				System.out.println("*** 2. View grade of all students  ***");
				System.out.println("*** 3. Generate transcript of students ***");
				System.out.println("*** 4. Academic-Session Update");
				System.out.println("*** 5. Check Graduate For Students");
				System.out.println("*** 6. Log Out  ***");
				
				choice = sc.nextInt();
				temp_for_while = choice;
				if(choice==1) {
					Academic_Function.editCourse();
				}
				else if(choice==2) {
					Academic_Function.viewGrade();
				}
				else if(choice==3) {
					Academic_Function.generateTranscript();
				}
				else if(choice==4) {
					Academic_Function.academicSession();
				}
				else if(choice==5) {
					System.out.println("Enter students entry no for cheking Graduate condition");
					String st_id = br.readLine();
					Student_Function.check_Graduation(st_id);
				}
			}
			return ;
		}
		catch(Exception err) {
			err.printStackTrace();
		}
	  
   }
}
