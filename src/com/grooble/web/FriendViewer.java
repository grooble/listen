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
public class FriendViewer extends HttpServlet {
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
		String friendIdString = request.getParameter("friendId");
		int friendId = 0;
		try {
			friendId = Integer.parseInt(friendIdString);
		}
		catch(NumberFormatException ne){
			ne.printStackTrace();
		}
		
		System.out.println("FriendViewer-->friendId: " + friendId);

		Member m = new Member();
		Person friend = m.verify(datasource, friendId);
		String msg = "";
		String dispatch = "ShowProfile.do";

		// login process should not proceed if user attribute is null
		// return to login screen with error msg.
		if(null==friend){
			msg = (String)request.getAttribute("message");
			dispatch = "ShowProfile.do";
			System.out.println("FriendViewer->friend was null");
			msg = msg + "\n" + "エラーが発生しました。<br /> 友だち見つからなかった。";
			session.removeAttribute("friend");
			request.setAttribute("message", msg);
			RequestDispatcher view = request.getRequestDispatcher(dispatch);
			view.forward(request, response);
		}
		else
		// friend found. populate data: friends of friend, newsfeed and tests
		{
			Friender fr = new Friender();
			dispatch = "ShowFriend.do";
						
			// get list of friend friends
			ArrayList<Person> fOfFriends = new ArrayList<Person>();
			ArrayList<Integer> fOfFriendIds = (ArrayList<Integer>)fr.getFriends(datasource, friendId);
			Iterator<Integer> i = fOfFriendIds.iterator();
			while (i.hasNext()){
				Person foundFriend = m.verify(datasource, (Integer)i.next());
				fOfFriends.add(foundFriend);
			}
						
			// initialize statusIndex to zero
			Integer statusIndex = new Integer(0);
			
			// set current user, friends list, pending list
			// news (friends' status) and test count to session attributes
			System.out.println("FriendViewer->statusIndex: " + statusIndex);
			request.setAttribute("message", msg);
			session.setAttribute("user", user);
			request.setAttribute("friend", friend);
			session.setAttribute("statusIndex", statusIndex);
			session.setAttribute("fof", fOfFriends);
				
			System.out.println("FriendViewer->dispatch: " + dispatch);
			RequestDispatcher view = request.getRequestDispatcher(dispatch);
			view.forward(request, response);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}