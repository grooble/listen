package com.grooble.friend;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.grooble.model.Person;
import com.grooble.model.Friender;

/**
 * Invoked when a user resquest another user to become friends.
 * The requesting user is added to the pending table and appears
 * in the requested user's pending list until approved or denied.
 */
@SuppressWarnings("serial")
public class RequestFriend extends HttpServlet {

	private DataSource datasource; 
	//	コネクションプールからコネクションを取得
	public void init() throws ServletException {
		try {
			datasource = (DataSource) getServletContext().getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		/*
		 * Get user attribute from session. Test if it is 
		 * a Person, and if so, assign to user Person variable.
		 * If user not found, set error message and dispatch to login page.
		 */
		HttpSession session = request.getSession();
		Person user = null;
		String message = "";
		Object userOb = session.getAttribute("user");
		if(!(userOb instanceof Person)){
			message = "Logged in User not found";
			request.setAttribute("message", message);
			RequestDispatcher view = request.getRequestDispatcher("index-06.jsp");
			view.forward(request, response);
		}
		else{
			user = (Person)userOb;
		}
		String friendIdString = (String)request.getParameter("friendId");
		System.out.println("RequestFriend->user: " + user.getId() + " friend: " + friendIdString);
		int friendId = Integer.parseInt(friendIdString);
		
		//call addToPending method in Friender class to add to pending db
		Friender f = new Friender();
		int pended = f.addToPending(datasource, user.getId(),friendId );
		
		//write status -1: fail, 0: success
		//This is used in page javascript.
		PrintWriter out = response.getWriter();
		System.out.println("RequestFriend->pended: " + pended);
		out.write("" + pended);
	}
}
