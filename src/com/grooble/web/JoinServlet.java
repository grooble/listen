package com.grooble.web;

import javax.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.util.UUID;

import BCrypt.BCrypt;

import com.grooble.model.*;

/**
*	新しいメンバーを登録するサーブレト
*	パラメーターはjoin.htmlフォームから取得して、
*	もう登録してあるかどうかメールとlookupメソットで確認する
*	addMemberメソッドを使ってデータベースに入れて
*	verifyメソッドで、これからのテストで使うPersonを取得
*/
@SuppressWarnings("serial")
public class JoinServlet extends HttpServlet{
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
		String code = "";
		String mailText = "";
		
		Member m = new Member();
		if(m.verify(ds, mail)){
			String msg = "This email already in use. /nPlease log in.";
			request.setAttribute("message", msg);
			request.setAttribute("messageAlert", "#FFCC66");
		} else{			
			//		このメール使っているユーザー存在してないから、データベースに入れる
			String id = UUID.randomUUID().toString();
			try{
				code = new TextUtils().makeCode(mail);
			}
			catch(Exception e){e.printStackTrace();}
			
			String hashed = BCrypt.hashpw(pwd, BCrypt.gensalt());
			
			m.setConfirm(ds, mail, hashed, id, code);
			String subject = "account confirmation - no reply";
			mailText = "Please click the link below to " +
			"confirm your registration: \n\n" +
			"<a href=\"http://www.moeigo.com/Confirm.to?confid=" + id + 
			"&confcode=" + code +
			"\">click this link</a>";
			System.out.println("JoinServlet-->mailText: " + mailText);
			String message = "Please check your email for a confirmation link";
			request.setAttribute("message", message);
			request.setAttribute("messageAlert", "#FFCC66");
			try{
				JMailer jmail = new JMailer();
				jmail.sendMail(mail, "no-reply@moeigo.com", subject, mailText);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("JoinServlet->: in JMailer catch");
			}
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