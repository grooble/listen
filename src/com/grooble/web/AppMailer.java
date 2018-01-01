package com.grooble.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.grooble.model.*;

@SuppressWarnings("serial")
public class AppMailer extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		String email = request.getParameter("email");
		String subject = request.getParameter("subject");
		String message = request.getParameter("message");
		
		try{
			JMailer jmail = new JMailer();
			jmail.sendMail("admin@moeigo.com", email, subject, message);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("AppMailer->: in catch");
		}
		
		RequestDispatcher view = request.getRequestDispatcher("msgSent.jsp");
		view.forward(request, response);
		
	}
}
