package org.example;

import java.sql.Connection;
import java.util.Scanner;
import java.sql.*;
public class Login {

    // Profile Details bhi bnana h

    private static Connection con;
    private static Scanner sc;
    public static void main(String []args) {

        try {  // this try catch is not necessory
            con = ConnectionProvider.getConnection();
             sc = new Scanner(System.in);
            if (con != null) {
                System.out.println("Connection established successfully!");

                // Your code to execute SQL queries here
            } else {
                System.out.println("Failed to establish connection!");
            }
        }
        catch(Exception e){
            System.out.println(e);
        }

        try {
            System.out.println("****************Welcome To AIMS IIT ROPAR ******************\n\n\n");

            int choice;
            do {
                System.out.println("1. Student");
                System.out.println("2. Faculty");
                System.out.println("3. Academic Office");
                System.out.println("0. Exit");

                choice = sc.nextInt();
                if(choice==1) {
                    LoginFunction.studentLogin();
                }
                else if(choice==2) {
                    LoginFunction.facultyLogin();
                }
                else if(choice==3) {
                    LoginFunction.academicLogin();
                }

            }while(choice!=0);
            System.out.println("************************* Thank You! ***************************");

            System.out.println("Complete");

        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}























