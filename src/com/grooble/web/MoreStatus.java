package com.grooble.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.json.JSONArray;

import com.grooble.model.*;

@SuppressWarnings("serial")
public class MoreStatus extends HttpServlet {
	//データソースを初期化する
	private DataSource ds;
	
	public void init() throws ServletException {
		try {
			ds = (DataSource) getServletContext().getAttribute("DBCPool");
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
		
		System.out.println("before Status index: ");
		int statusIndex = 0;
		if(session.getAttribute("statusIndex")!= null){
			statusIndex = (Integer)session.getAttribute("statusIndex");
			System.out.println("MoreStatus-->got statusIndex: " + statusIndex);
		}
		System.out.println("Status index: " + statusIndex);
		Person currentUser = (Person)session.getAttribute("user");
		int userID = -1;
		if(currentUser != null){
			userID = currentUser.getId();
		}
		Update upd = new Update();
		List<Status> status = upd.getStatus(ds, userID, statusIndex, 20);
		if(status != null){
			session.setAttribute("statusIndex", statusIndex + status.size());
			JSONMaker jMaker = new JSONMaker();
			JSONArray moreJSONStatus = jMaker.toJSON(status);
			PrintWriter out = response.getWriter();
			response.setContentType("text/html");
			out.write(moreJSONStatus.toString());
		}
	}
}