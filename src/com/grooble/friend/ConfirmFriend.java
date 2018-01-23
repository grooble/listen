package com.grooble.friend;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.grooble.model.Friender;
import com.grooble.model.Member;
import com.grooble.model.Person;

/**
 * Approve or deny a pending friend invitation.
 */
@SuppressWarnings("serial")
public class ConfirmFriend extends HttpServlet {

	private DataSource datasource; 
	//	コネクションプールからコネクションを取得
	
	public void init() throws ServletException {
		try {
			datasource = (DataSource) getServletContext().getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		HttpSession session = request.getSession();
		String email = request.getParameter("friend");
		String action = request.getParameter("action");
		String message = "";
		int status = -1;
		
		/* 
		 *   Get user and verify it's a Person.
		 *   If not, redirect to login page.
		 */
		Person user = null;
		Object userObj = session.getAttribute("user");
		if (!(userObj instanceof Person)){
			message = "Logged in User not found";
			request.setAttribute("message", message);
			RequestDispatcher view = request.getRequestDispatcher("index-06.jsp");
			view.forward(request, response);
		}
		else {
			user = (Person)userObj;
		}
		
		Member m = new Member(datasource);
		Friender f = new Friender();
		
		Person friend = new Person();
		friend = (Person)m.lookup(email); 
		
		// Accept request for friend.
		// Delete pended request from database (b/c friend accepted and
		// successfully added to friend db.
		if(action.equalsIgnoreCase("accept")){
			status = f.makeFriend(datasource, user.getId(), friend.getId());
			if(status == 0){
				message = "friend added successfully";
				System.out.println("ConfirmFriend->accept, status: " + status);
				int pStatus = f.deleteRequest(datasource, friend.getId(), user.getId());
				System.out.println("ConfirmFriend->delete pending, pStatus: " + pStatus);
			}else{
				message = "add friend failed";
			} 
			session.removeAttribute("found");//where is this added to session?
		}
		else{
			System.out.println("ConfirmFriend->deny");
			status = 
				f.deleteRequest(datasource, user.getId(), friend.getId());
			message = "friend request denied";
		}
		
		RequestDispatcher view = request.getRequestDispatcher("Setup.do");
		view.forward(request, response);
	}
}
