package com.grooble.web;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.grooble.model.*;

import javax.sql.*;

/**
*	ユーザーログインのサーブレト
*	データソースを初期化して、
*	Memberクラスのverifyメソッドを使ってログインする。
*	ログイン失敗の場合、userTypeのフラグを"member not found"に設定する。
*	フラグはform.jspに使われている
*/
@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

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

		String userAcct = request.getParameter("user");
		String pwd = request.getParameter("pwd");
		String dispatcherString = "";
		String userType = null;
		
		session.setAttribute("password", pwd);
/*
 * userTypeのフラグがjspで使われている
 * ユーザーが見つけてなかったら、"member not found" に設定する
 * Use Member.java to lookup the user.
 * If not found set an error msg and return user to login page.
 * Check by testing for null email (found users will have the email set.)
 */
		//lookup user with email and password
		Member check = new Member(datasource);
		Person user = check.verify(userAcct, pwd);
		
		if (user.getEmail() != null){
			userType = "member";
			session.setAttribute("user", user);
			dispatcherString = "Setup.do";
		}
		else {
			userType = "notfound";
			System.out.println("LoginSetup->user was null");
			String msg = "ロギン失敗: <br />ユーザ見つからなかった。";
			session.removeAttribute("loggedInUser");
			request.setAttribute("message", msg);
			dispatcherString = "index-06.jsp";
		}
		System.out.println("LoginServlet->userType: " + userType);
		if(userType.equals("member")){
			session.setAttribute("userType", userType);
		} else{
			request.setAttribute("userType", userType);
		}
		RequestDispatcher view = request.getRequestDispatcher(dispatcherString);
		view.forward(request, response);
	}
}