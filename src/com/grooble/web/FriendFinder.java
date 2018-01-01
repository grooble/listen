package com.grooble.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.json.JSONArray;

import com.grooble.model.*;

@SuppressWarnings("serial")
public class FriendFinder extends HttpServlet {

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
		session.removeAttribute("found");

		String emailString = request.getParameter("emailText").toLowerCase();
		System.out.println("FriendFinder->emailText: " + emailString);
		Member m = new Member();
		Person user = (Person) session.getAttribute("user");
		Friender f = new Friender();
		
		// does the email belong to an existing user
		if(m.isUser(datasource, emailString)){
			Person p = m.lookup(datasource, emailString);
			System.out.println("FriendFinder->isUser: " + p.getFirstName());
			int toCheckId = p.getId();
			boolean alreadyFriend = f.isFriend(datasource, toCheckId, user.getId());
			boolean alreadyPended = f.isPended(datasource, user.getId(), toCheckId);
			
			// check if email address is already a friend or is a pending invite
			if(alreadyFriend || alreadyPended){
				JSONArray pendedJSON = new JSONArray();
				pendedJSON.put(emailString);
				pendedJSON.put(new Integer(0));
				String pendedString = pendedJSON.toString();
				PrintWriter out = response.getWriter();
				response.setContentType("text/html");
				System.out.println("FriendFinder->foundString: " + pendedString);
				out.write(pendedString);
			}
			else{ // is a member and not existing friend or pending invite, 
				  // so send ajax response to add data to page.	
				System.out.println("FriendFinder->member and not friend or pending");
				JSONMaker maker = new JSONMaker();
				JSONArray foundJSON = maker.toJSON(p);
				foundJSON.put(user.getId());
				//to indicate member friend addition
				foundJSON.put(new Integer(1));
				String foundString = foundJSON.toString();
				PrintWriter out = response.getWriter();
				response.setContentType("text/html");
				System.out.println("FriendFinder->foundString** " + foundString);
				out.write(foundString);
			}
		}
		else { // not a user 
			JSONArray inviteJSON = new JSONArray();
			inviteJSON.put(user.getFirstName());
			inviteJSON.put(emailString);
			inviteJSON.put(new Integer(-1));
			String inviteString = inviteJSON.toString();
			PrintWriter out = response.getWriter();
			response.setContentType("text/html");
			System.out.println("FriendFinder->foundString: " + inviteString);
			out.write(inviteString);
		}
			
	}

}
