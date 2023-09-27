package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
public class LoginFunction {
	
	private static Connection con;

	public static int auth(String user_id,String pass,String role) {
		try {
			con = ConnectionProvider.getConnection();
			Scanner sc = new Scanner(System.in);
			 String q = "select * from user_details where user_id = ? and password = ? and roles = ?";
			  PreparedStatement newcreating_statemnt = con.prepareStatement(q);
			  newcreating_statemnt.setString(1, user_id);
			  newcreating_statemnt.setString(2, pass);
			  newcreating_statemnt.setString(3, role);
			  ResultSet newSet = newcreating_statemnt.executeQuery();
			  if(newSet.next()) {
				  return 1;
			  }
			  else {
				  return 0;
			  }
		}
		catch(Exception err) {
			err.printStackTrace();
		}
		return 0;  
	}
	
	public static void studentLogin() {
		try {
			boolean isLoggedIn = false;
			while (!isLoggedIn) {
			    System.out.println("*** 1. Login ****");
			    System.out.println("*** 2. Back ****");
			    String user_id, password, role;
			    Scanner sc = new Scanner(System.in);
			    int choice = sc.nextInt();
			    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			    if (choice == 1) {
			        System.out.println("Please Enter Your User_ID");
			        user_id = br.readLine();
			        System.out.println("Please Enter Your password");
			        password = br.readLine();
			        int check = auth(user_id, password, "student");
			        if (check == 1) {
			            Student.student_ui(user_id);
			            isLoggedIn = true; // set isLoggedIn to true if the user is successfully authenticated
			        } else {
			            System.out.println("Invalid Credentials");
						isLoggedIn = true;
			            // do not set isLoggedIn to true, so that the loop will continue and the user can enter their credentials again
			        }
			    }
			    else if(choice==2){
			    	return;
			    }
			}
		}
		catch(Exception err) {
			err.printStackTrace();
		}
		
	}
	
	public static void facultyLogin() {
		try {
			boolean isLoggedIn = false;
			while (!isLoggedIn) {
			    System.out.println("*** 1. Login ****");
			    System.out.println("*** 2. Back ****");
			    String user_id, password, role;
			    Scanner sc = new Scanner(System.in);
			    int choice = sc.nextInt();
			    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			    if (choice == 1) {
			        System.out.println("Please Enter Your User_ID");
			        user_id = br.readLine();
			        System.out.println("Please Enter Your password");
			        password = br.readLine();
			        int check = auth(user_id, password, "faculty");
			        if (check == 1) {
			            Instructor.instructor_ui(user_id);
			            isLoggedIn = true; // set isLoggedIn to true if the user is successfully authenticated
			        } else {
			            System.out.println("Invalid Credentials");
						isLoggedIn = true;
			            // do not set isLoggedIn to true, so that the loop will continue and the user can enter their credentials again
			        }
			    }
			    else if(choice==2){
			    	return;
			    }
			}
		}
		catch(Exception err) {
			err.printStackTrace();
		}
		
	}
	public static void academicLogin() {
		try {
			boolean isLoggedIn = false;
			while (!isLoggedIn) {
			    System.out.println("*** 1. Login ****");
			    System.out.println("*** 2. Back ****");
			    String user_id, password, role;
			    Scanner sc = new Scanner(System.in);
			    int choice = sc.nextInt();
			    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			    if (choice == 1) {
			        System.out.println("Please Enter Your User_ID");
			        user_id = br.readLine();
			        System.out.println("Please Enter Your password");
			        password = br.readLine();
			        int check = auth(user_id, password, "Academic");
			        if (check == 1) {
			            Academic.academic_ui();
			            isLoggedIn = true; // set isLoggedIn to true if the user is successfully authenticated
			        } else {
			            System.out.println("Invalid Credentials");
						isLoggedIn = true;
			            // do not set isLoggedIn to true, so that the loop will continue and the user can enter their credentials again
			        }
			    }
			    else if(choice==2){
			    	return;
			    }
			}
		}
		catch(Exception err) {
			err.printStackTrace();
		}
	}
}
