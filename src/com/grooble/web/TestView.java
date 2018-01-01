package com.grooble.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;

import com.grooble.model.Person;

@SuppressWarnings("serial")
public class TestView extends HttpServlet{
	
	protected void doGet(HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("test/html");
		HttpSession session = request.getSession();
		Person user = (Person)session.getAttribute("user");
		if(user==null){
			request.setAttribute("loginMsg", "ログインしてください");
			RequestDispatcher view = request.getRequestDispatcher("index.jsp");
			view.forward(request, response);
		}
		else{
			session.removeAttribute("statusIndex");
			RequestDispatcher view = request.getRequestDispatcher("ShowProfile.do");
			view.forward(request, response);			
		}		
	}
}
