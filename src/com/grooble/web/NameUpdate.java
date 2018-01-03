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
		java.sql.Date sqlDate = null;
		System.out.println("NameUpdate->month: " +
				month +
				"day: " + day +
				"year: " + year);
		
		if(!(month.isEmpty() || day.isEmpty() || year.isEmpty())){
			String date = month + "/" + day + "/" + year;
			String dateFormatString = "MM/dd/yy";
			SimpleDateFormat df = new SimpleDateFormat(dateFormatString);
			Date myDate = null;
			try {
				myDate = df.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			sqlDate = new java.sql.Date(myDate.getTime());
		} 
		
		Member m = new Member();
		p = m.updateName(ds, email, password, firstName, lastName, sqlDate);
		
		session.setAttribute("user", p);
		
		RequestDispatcher view = request.getRequestDispatcher("ShowProfile.do");
		view.forward(request, response);
	}
}
