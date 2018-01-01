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
import com.grooble.model.Person;


/**
 * Delete an existing friend from the list of friends.
 */
@SuppressWarnings("serial")
public class UnFriend extends HttpServlet {

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
		String exId = request.getParameter("exfriend");
		int toDelete = Integer.parseInt(exId);
		System.out.println("UnFriend-->xf: " + exId +
				", toDelete(integer): " + toDelete);
		Person user = null;
		String message = "";
		int status = 0;
		Object userOb = session.getAttribute("user");
		if(!(userOb instanceof Person)){
			message = "Logged in User not found";
			request.setAttribute("message", message);
			RequestDispatcher view = request.getRequestDispatcher("ShowProfile.do");
			view.forward(request, response);
		}
		else{
			user = (Person)userOb;
		}

		Friender f = new Friender();

		status = f.removeFriend(datasource, user.getId(), toDelete);
		if(status!=0){
			message = "friend removed successfully";
		} else{
			message = "remove friend failed";
		}	
		session.setAttribute("loggedInUser", user);
		request.setAttribute("message", message);
		
		System.out.println("UnFriend-->message: " + message);
		RequestDispatcher view = request.getRequestDispatcher("Setup.do");
		view.forward(request, response);
	}

}
