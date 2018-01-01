package com.grooble.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@SuppressWarnings("serial")
public class Manager extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
							throws ServletException, IOException {
		HttpSession session = request.getSession();
		String user = request.getParameter("user");
		String answered = request.getParameter("answered");
		String level = request.getParameter("difficulty");
		String ajax = request.getParameter("ajax");
		String fwdString = "";
		System.out.println("*Manager*\nuser: " + user + "\nanswered array: " + answered +
				"\ndifficulty: " + level + "\najax: " + ajax);

		if(user == null){
			System.out.println("Manager-->user is null");
			fwdString = "index.jsp";
			String userType = "notfound";
			request.setAttribute("loginMsg", "please login");
			session.setAttribute("userType", userType);
		} else {
			System.out.println("Manager-->user is NOT null");
		}
		

		if(answered != null){
			fwdString = "Complete.do";
		} else 
		if (level != null){
			fwdString = "GetTest.do";
		} else 
		if ((ajax != null)&&(ajax.equals("ajax"))){
			fwdString = "GetJSON.do";
		}
		
		System.out.println("Manager->fwdString: " + fwdString);
		System.out.println("--------------------------------------");
		
		RequestDispatcher fwd = 
			request.getRequestDispatcher(fwdString);
		fwd.forward(request, response);

	}

}
