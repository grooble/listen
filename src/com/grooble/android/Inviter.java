package com.grooble.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.grooble.model.Friender;
import com.grooble.model.Member;
import com.grooble.model.Person;


public class Inviter extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "Inviter: ";
	
	private DataSource ds;
	private Connection con;
	
	private Person user; 
	
	public void init() throws ServletException {
	    try {
	        ds = (DataSource) getServletContext().getAttribute("DBCPool");
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	   response.setContentType("text/html");
	   
	   String userEmail = request.getParameter("email");
	   String password  = request.getParameter("password");
	   String emailToInvite = request.getParameter("email_to_invite");
	   
	   String returnMessage = "fail";
	   
	   Member member = new Member();
	   user = member.verify(ds, userEmail, password);
	   if(member.verify(ds, emailToInvite)){

	       // email to invite is a user
	       Person personToInvite = member.lookup(ds, emailToInvite);

	       // check if they are an existing friend...
	       Friender friender = new Friender();	       
	       if(friender.isFriend(ds, user.getId(), personToInvite.getId())){
	           System.out.println("Inviter: existing friend invited.");
	       }
	       
	       else{
	           // not existing friend and not null: set invite
	           if((userEmail != null) && (password != null)){
	               Person user = member.verify(ds, userEmail, password);
	               if(user != null){
	                   Person invitee = member.lookup(ds, emailToInvite);
	                   int invitedStatus = friender.addToPending(ds, user.getId(), invitee.getId());
	                   // -1 from Friender indicates failure
	                   if (invitedStatus != -1){
	                       // success!
	                       returnMessage = "success";
	                   }
	               }
	           }
	       }
	   }
	   else {
           returnMessage = "notfound"; 
	   }
	   
       PrintWriter out = response.getWriter();
       response.setContentType("text/html");
       System.out.println(TAG + returnMessage);
       out.write(returnMessage);

	}

}
