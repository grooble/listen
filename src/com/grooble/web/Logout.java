package com.grooble.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class Logout extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession();

		session.removeAttribute("test");
		session.removeAttribute("index");
		session.removeAttribute("friends");
		session.removeAttribute("pending");
		session.removeAttribute("user");
		session.removeAttribute("userStatus");
		session.removeAttribute("message");
		session.removeAttribute("password");
		
		session.invalidate();
		RequestDispatcher view = request.getRequestDispatcher("/index.html");
		view.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}