package com.grooble.web;

import java.io.IOException;

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

/**
 * parses the confid and confcode attributes from the 
 * password recovery mail url and recovers the user.
 * Sets user (Person) to the session and goes to the 
 * password update page.
 * @author grooble
 *
 */
@SuppressWarnings("serial")
public class ResetPassword extends HttpServlet {
    private DataSource ds; 
//	データソースを初期化する
	public void init() throws ServletException {
		ServletContext context = getServletContext();
		try {
			ds = (DataSource) context.getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        HttpSession session = request.getSession();
		
		String dispatch = "";
		String confString = request.getParameter("confid");
		String confCode = request.getParameter("confcode");
		System.out.println("ResetPassword servlet");
		Person p = null;
		
		if((confString!=null)&&(confCode!=null)){
			System.out.println("ResetPassword-->confString: " + confString);
			System.out.println("ResetPassword-->confCode: " + confCode);
			
			Member m = new Member();
			p = m.getRecovery(ds, confString, confCode); 
			if(p != null){	
				System.out.println("ResetPassword-->email: " + p.getEmail().toLowerCase());
				session.setAttribute("user", p);
				session.setAttribute("userType", "member");
				session.setAttribute("userMail", p.getEmail().toLowerCase());
				dispatch = "ChangePwdForm.to";
			}
			else{
				request.setAttribute("message", "エラーが発生しました。" +
						"\nもう一度ログインしてみてください。");
				dispatch = "index-06.jsp";
				// TODO
				// cleanRecovery()
				// delete all recovery DB entries for given user.
			}
		}
		else{
			request.setAttribute("message", "アカウント不明。\nもう一度ログインしてみてください。");
			dispatch = "index-06.jsp";
		}
		
		RequestDispatcher view = request.getRequestDispatcher(dispatch);
		view.forward(request, response);

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
