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

import BCrypt.BCrypt;

import com.grooble.model.Member;
import com.grooble.model.Person;

/**
 * Gets password1 attribute from the pwdEntry.jsp
 * Updates password in the database
 * @author grooble
 *
 */
@SuppressWarnings("serial")
public class PwdEntry extends HttpServlet {
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
		
		String password1 = request.getParameter("password1");
		//		String password2 = request.getParameter("password2");
		// TODO
		// CHECK PWDS SAME AND SET ERROR MSG FOR 
		// PWD CHANGE PAGE IF NOT
		String dispatch = "";
		Person user = (Person)session.getAttribute("user");
		Member m = new Member();
		if (user == null){
			System.out.println("PwdEntry-->user null. Getting user from email.");
			String email = (String)session.getAttribute("userMail");			
			user = m.lookup(ds, email);
			if (!(user instanceof Person)){
				System.out.println("PwdEntry-->User not found. Returning to index-06.jsp.");
				request.setAttribute("message", "エラー：ユーザー不明。");
				RequestDispatcher view = request.getRequestDispatcher("index-06.jsp");
				view.forward(request, response);				
			}
		}
		System.out.println("PwdEntry-->user loaded and about to reset pwd.");
		String pwd1Hash = BCrypt.hashpw(password1, BCrypt.gensalt());
		user =
			m.updatePwd(ds, user.getEmail(), pwd1Hash);
		m.deleteRecovery(ds, user.getEmail());
		session.setAttribute("user", user);
		request.setAttribute("message", "パスワード更新しました。"); 
		dispatch = "Setup.do";
		RequestDispatcher view = request.getRequestDispatcher(dispatch);
		view.forward(request, response);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
}
