package com.grooble.web;

import java.io.IOException;
//import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
//import java.util.List;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

//import org.json.JSONArray;


import com.grooble.model.*;


@SuppressWarnings("serial")
public class StatusUpdate extends HttpServlet {
	
//データソースを初期化する
	private DataSource ds;
	private String encoding;
	
	public void init() throws ServletException {
		ServletContext context = getServletContext();
		encoding = context.getInitParameter("PARAMETER_ENCODING");
		try {
			ds = (DataSource) getServletContext().getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding(encoding);
        response.setContentType("text/html");
        response.setCharacterEncoding(encoding);
        System.out.println("StatusUpdate->encoding: " + encoding);
		HttpSession session = request.getSession();
		
		String statusComment = request.getParameter("status");
		System.out.println("StatusUpdate-->status: " + statusComment);
		
		Person user = (Person)session.getAttribute("user");
		System.out.println("StatusUpdate->user: " + user.getFirstName() + ":: " + user.getId());
		ArrayList<Person> friends = (ArrayList<Person>)session.getAttribute("friends");
		System.out.println("StatusUpdate->friends size: " + friends.size());
		ArrayList<Integer> friendIds = new ArrayList<Integer>();
		Iterator<Person> i = friends.iterator();
		
		// populate ArrayList fiendIds of ids of friends
		while(i.hasNext()){
			Person per = i.next();
			if(!(null==per)){
			    Integer friendId = per.getId();
			    friendIds.add(friendId);
			    System.out.println("StatusUpdate->friend: " + per.getId() + 
			            ": " + per.getFirstName());
			}
			else{
				System.out.println("StatusUpdate->friend null: not adding to array.");
			}
		}
		
		// create Status and call setStatus in Update.java
		System.out.println("StatusUpdate->after friendId iterator");
		int acct = user.getId();
		System.out.println("StatusUpdate->userId: " + user.getId());
		Status status = new Status(
				acct, "comment", statusComment); 
		Update up = new Update();
		up.setStatus(ds, status);
		
		// retrieve a fresh list of 20 most recent status items 
		// (5 for testing)
		// to return to javascript callback
		Integer zeroInt = new Integer(0);
		session.setAttribute("statusIndex", zeroInt);
		RequestDispatcher dispatcher = request.getRequestDispatcher("StatusLoader.do");
		dispatcher.forward(request, response);
/*		List<Status> statusList = up.getStatus(ds, acct, 0, 20);
		if(statusList != null){
			session.setAttribute("statusIndex", 0 + statusList.size());
			JSONMaker jMaker = new JSONMaker();
			JSONArray JSONStatus = jMaker.toJSON(statusList);
			PrintWriter out = response.getWriter();
			response.setContentType("text/html");
			System.out.println("StatusUpdate-->JSONStatus: " + JSONStatus.toString());
			out.write(JSONStatus.toString());
		}
*/
	}
}