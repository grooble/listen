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

@SuppressWarnings("serial")
public class JoinConfirmation extends HttpServlet {
	private DataSource ds;
	
//		データソースを初期化する
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
        response.setContentType("text/html");
		HttpSession session = request.getSession();

		String confString = request.getParameter("confid");
		String confCode = request.getParameter("confcode");
		String dispatch = "";
		System.out.println("JoinConfirmation servlet");
		Person signedUser = null;
		
		if((confString!=null)&&(confCode!=null)){
			System.out.println("JoinConfirmation-->confString: " + confString);
			System.out.println("JoinConfirmation-->confCode: " + confCode);
			
			Member m = new Member(ds);
			// TODO Unconfirmed users info is not yet encrypted
			signedUser = m.getConfirm(confString, confCode);
			session.setAttribute("password", signedUser.getPassword());
			if (signedUser != null){
				m.addMember(signedUser.getEmail().toLowerCase(), signedUser.getPassword());
				signedUser = m.lookup(signedUser.getEmail().toLowerCase());
				if(signedUser != null){
					m.deleteConfirm(confString);
					System.out.println("JoinConfirmation: p created and conf'd");
					System.out.println("JoinConfirmation->" + 
							"id: " + signedUser.getId() +
							" email: " + signedUser.getEmail().toLowerCase());
					session.setAttribute("userType", "member");
					session.setAttribute("user", signedUser);
					dispatch = "Setup.do";
				}
			}
		}else{
			request.setAttribute("message", "ログイン失敗");
			dispatch = "index-06.jsp";
		}
		
		RequestDispatcher view = request.getRequestDispatcher(dispatch);
		view.forward(request, response);
	}

}