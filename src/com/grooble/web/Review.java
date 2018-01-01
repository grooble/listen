package com.grooble.web;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;

import com.grooble.model.*;


@SuppressWarnings("serial")
public class Review extends HttpServlet {	
	private DataSource datasource;

	//	コネクションプールからコネクションを取得
	public void init() throws ServletException {
		try {
			datasource = (DataSource) getServletContext().getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}

	public void doPost(HttpServletRequest request,
					HttpServletResponse response)
					throws IOException, ServletException {
		doGet(request, response);
	}
	
	public void doGet(HttpServletRequest request, 
					HttpServletResponse response)
					throws IOException, ServletException {

		response.setContentType("text/html");
		HttpSession session = request.getSession();
		Person user = (Person)session.getAttribute("user");
		String qnFlag = request.getParameter("timestamp");
		System.out.println("Review-->qnFlag: " + qnFlag);
		String testIdString = request.getParameter("testid");
		Test test;
		
		if(null==user){
			request.setAttribute("loginMsg", "ログインしてください");
			RequestDispatcher view = request.getRequestDispatcher("index.jsp");
			view.forward(request, response);
			return;
		}

		if (testIdString != null){
			int testId = Integer.parseInt(testIdString);
			TestMakerA tm = new TestMakerA();
			test = tm.getTest(datasource, testId);
			session.setAttribute("test", test);
		} 
			RequestDispatcher view = request.getRequestDispatcher("Check.do");
			view.forward(request, response);
	}
}