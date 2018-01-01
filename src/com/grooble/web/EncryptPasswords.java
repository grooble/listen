package com.grooble.web;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import BCrypt.BCrypt;

@SuppressWarnings("serial")
public class EncryptPasswords extends HttpServlet{

	private DataSource ds;
	
	public void init() throws ServletException {
		ServletContext context = getServletContext();
		try {
			ds = (DataSource) context.getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}
	private Connection con;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
							throws ServletException, IOException {
        response.setContentType("text/html");
        try {
        	con = ds.getConnection();
			Statement st1 = con.createStatement();
			Statement st2 = con.createStatement();
			st1.executeUpdate("USE teacher");
			ResultSet rs1 = 
				st1.executeQuery("select stdid, password from students where length(password)<20");
			boolean trueFlag = true;
			trueFlag = rs1.first();
			while(trueFlag){
				int id = rs1.getInt(1);
				String pwd = rs1.getString(2);
				String hashed = BCrypt.hashpw(pwd, BCrypt.gensalt());					
				String updateQuery = 
					"UPDATE students SET password='" + 
					hashed +
					"' WHERE stdid=" + id;
				System.out.println("EncryptPasswords->updateQuery: " +
						updateQuery);
				st2.executeUpdate(updateQuery);
				trueFlag = rs1.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
							throws ServletException, IOException {
		doPost(request, response);
	}
}
