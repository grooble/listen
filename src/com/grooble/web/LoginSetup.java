package com.grooble.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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
 * 
 * @author USER
 * This class is used to supply friends, pending
 * and any other information that may be need by a logged 
 * in user.  
 * Status updates handled later via ajax call.
 */
@SuppressWarnings("serial")
public class LoginSetup extends HttpServlet {
//	データソースを初期化する
	private DataSource datasource;
	public void init() throws ServletException {
		try {
			datasource = (DataSource) getServletContext().getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();

		Person user = (Person)session.getAttribute("user");
		String msg = "";
		String dispatch = "";

		// login process should not proceed if user attribute is null
		// return to login screen with error msg.
		if(null==user){
			msg = (String)request.getAttribute("message");
			dispatch = "index-06.jsp";
			System.out.println("LoginSetup->user was null");
			msg = msg + "\n" + "ロギン失敗:<br /> ユーザ見つからなかった。";
			session.removeAttribute("user");
			request.setAttribute("message", msg);
			RequestDispatcher view = request.getRequestDispatcher(dispatch);
			view.forward(request, response);
		}
		else
		// user found. populate friends, pending and newsfeed.
		{
			msg = (String)request.getAttribute("message");
			Integer tests = new Integer(0);
			ArrayList<Person> friends;
			ArrayList<Person> pending;
			
			dispatch = "ShowProfile.do";
			
			Friender fr = new Friender();
			Member check = new Member();
			
			// get current completed test count
			int userId = user.getId();
			tests = check.testCount(datasource, userId);
			
			// get list of user friends
			friends = new ArrayList<Person>();
			ArrayList<Integer> friendIds = (ArrayList<Integer>)fr.getFriends(datasource, userId);
			Iterator<Integer> i = friendIds.iterator();
			while (i.hasNext()){
				Person foundFriend = check.verify(datasource, (Integer)i.next());
				friends.add(foundFriend);
			}
			
			// get list of pending friend requests
			ArrayList<Integer> pendingIds = (ArrayList<Integer>)fr.getPending(datasource, userId);
			if((pendingIds != null) && (pendingIds.size() != 0)){
				pending = new ArrayList<Person>();
				Iterator<Integer> it = pendingIds.iterator();
				while(it.hasNext()){
					Person pendant = check.verify(datasource, (Integer)it.next());
					pending.add(pendant);
				}
			}
			// or set to null if none pending
			else {
				pending = null; 
			}
			
			// initialize statusIndex to zero
			Integer statusIndex = new Integer(0);
			
			// set current user, friends list, pending list
			// news (friends' status) and test count to session attributes
			System.out.println("Login->statusIndex: " + statusIndex);
			request.setAttribute("message", msg);
			session.setAttribute("user", user);
			session.setAttribute("userType", "member");
			session.setAttribute("statusIndex", statusIndex);
			session.setAttribute("friends", friends);
			session.setAttribute("pending", pending);
			session.setAttribute("testCount", tests);
				
			System.out.println("LoginSetup->dispatch: " + dispatch);
			RequestDispatcher view = request.getRequestDispatcher(dispatch);
			view.forward(request, response);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}