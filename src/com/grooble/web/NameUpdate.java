package com.grooble.web;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.grooble.model.Member;
import com.grooble.model.Person;

@SuppressWarnings("serial")
public class NameUpdate extends HttpServlet {
    private DataSource ds;

	public void init() throws ServletException {
		ServletContext context = getServletContext();
		try {
			ds = (DataSource) context.getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("test/html");
		HttpSession session = request.getSession();
		
		String password = (String)session.getAttribute("password");
		
		String firstName = request.getParameter("fname");
		String lastName = request.getParameter("lname");
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		String day = request.getParameter("day");	
		
		Person p = (Person)session.getAttribute("user");
		String email = p.getEmail().toLowerCase();
		
		// set fname, lastname, dob to person to pass to Member
		p.setFirstName(firstName);
		p.setLastName(lastName);
		p.setDOB(month + "/" + day + "/" + year);
		p.setEmail(email);
		p.setPassword(password);
		
		Member m = new Member();
		p = m.updateName(ds, p);
		
		session.setAttribute("user", p);
		
		RequestDispatcher view = request.getRequestDispatcher("ShowProfile.do");
		view.forward(request, response);
	}
}
