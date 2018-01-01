package com.grooble.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import BCrypt.BCrypt;

import com.grooble.model.Member;

/**
*	Used to login new members from
*	the Android app.
*	The response writes JSON back to the calling method:
*	email:the email submitted by the app.
*	password: the hashed password
*   email="error" for unsuccessful join attempt
*/

@SuppressWarnings("serial")
public class SimpleJoin extends HttpServlet{
	private DataSource ds;
	private String encoding;
	
//		データソースを初期化する
	public void init() throws ServletException {
		ServletContext context = getServletContext();
		encoding = context.getInitParameter("PARAMETER_ENCODING");
		try {
			ds = (DataSource) context.getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        if (encoding != null) {
            request.setCharacterEncoding(encoding);
        }
        response.setContentType("text/html");
		
//		リクエストからパラメーターを取得する
		String mail = request.getParameter("new_email").toLowerCase();
		String pwd = request.getParameter("pwd1");
		String dispatch = "index-06.jsp";
		Member m = new Member();
		if(m.isUser(ds, mail)){
			String msg = "This email already in use. /nPlease log in.";
			request.setAttribute("message", msg);
			request.setAttribute("messageAlert", "#FFCC66");
		} else{			

			String hashed = BCrypt.hashpw(pwd, BCrypt.gensalt());
			
			m.addMember(ds, mail, hashed);

			String message = "Please check your email for a confirmation link";
			request.setAttribute("message", message);
			request.setAttribute("messageAlert", "#FFCC66");
		}		
	RequestDispatcher view = request.getRequestDispatcher(dispatch);
	view.forward(request, response);
}

	
	public void doPost(HttpServletRequest request, 
							HttpServletResponse response)
							throws IOException, ServletException{
        processRequest(request, response);
	}
}