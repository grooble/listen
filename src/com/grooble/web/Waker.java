package com.grooble.web;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.grooble.model.*;

import javax.sql.*;

/**
*	the server is to hit this servlet every hour
*	to keep it awake. it calls a test login and
*	accesses the database for a test user.
*/
@SuppressWarnings("serial")
public class Waker extends HttpServlet {

//	データソースを初期化する
	private DataSource datasource;
	public void init() throws ServletException {
		try {
			datasource = (DataSource) getServletContext().getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}
	
	public void doGet(HttpServletRequest request, 
							HttpServletResponse response)
							throws IOException, ServletException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, 
							HttpServletResponse response)
							throws IOException, ServletException {
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		
		String user = request.getParameter("user");
		String pwd = request.getParameter("password");
		System.out.println("Waker->parameters user:" + user + " password:" + pwd);

		String userType = "";
		Member check = new Member(datasource);
		Person signedUser = check.verify(user, pwd);
		
		// verify user logged in and set attribute values
		if (signedUser != null){
			userType = "member";
			System.out.println("Waker->user.name: " + signedUser.getFirstName());
		}
		else {
			userType = "notfound";
			System.out.println("Login failed: user not found");
		}
		System.out.println("Waker->userType: " + userType);
		synchronized(this){
			try{
				wait(1000);
			}
			catch(Exception e){e.printStackTrace();}
		}
		session.invalidate();
	}
}