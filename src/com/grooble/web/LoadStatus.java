package com.grooble.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.grooble.model.JSONMaker;
import com.grooble.model.Person;
import com.grooble.model.Status;
import com.grooble.model.Update;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

@SuppressWarnings("serial")
public class LoadStatus extends HttpServlet {

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
		// retrieve a fresh list of 20 most recent status items 
		// (5 for testing)
		// to return to javascript callback
		request.setCharacterEncoding(encoding);
        response.setContentType("text/html");
        response.setCharacterEncoding(encoding);
		HttpSession session = request.getSession();
		Update up = new Update();
		Person user = (Person)session.getAttribute("user");
		Integer statusIndex = (Integer)session.getAttribute("statusIndex");
		int acct = user.getId();
		String friendid = request.getParameter("friend");
		System.out.println("LoadStatus, friendid: " + friendid);
		int friend = 0;
		boolean gotFriend = false; // a flag for which overloaded version of getStatus to call
		if(friendid != null){
			try {
     			friend = Integer.parseInt(friendid);
     			gotFriend = true;
			}
     		catch(NumberFormatException ne){
     			ne.printStackTrace();
     			gotFriend = false;
     		}
		}
		
		System.out.println("LoadStatus called, gotFriend: " + gotFriend +
				", friend: " + friend);
		
		List<Status> statusList = null;
		if (gotFriend)
			statusList = up.getStatus(ds, acct, friend, statusIndex, statusIndex + 20);
		else
			statusList = up.getStatus(ds, acct, statusIndex, statusIndex + 20);
		if(statusList != null){
			session.setAttribute("statusIndex", 0 + statusList.size());
			JSONMaker jMaker = new JSONMaker();
			JSONArray JSONStatus = jMaker.toJSON(statusList);
			PrintWriter out = response.getWriter();
			response.setContentType("text/html");
			System.out.println("StatusUpdate-->JSONStatus: " + JSONStatus.toString());
			out.write(JSONStatus.toString());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
