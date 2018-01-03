package com.grooble.web;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.grooble.model.JMailer;
import com.grooble.model.Member;
import com.grooble.model.Person;
import com.grooble.model.TextUtils;

/**
 * Servlet implementation class RecoverPwd
 */

// TODO update the password recovery process
// current one no longer works

public class RecoverPwd extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DataSource datasource;
	public void init() throws ServletException {
		try {
			datasource = (DataSource) getServletContext().getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		
		String getMail = request.getParameter("usermail");
		String email = getMail.toLowerCase();
		String code = "";
		String mailText = "";
		
		Member member = new Member();
		// TODO this lookup only returns a person with the user id!
		Person p = member.lookup(datasource, email);
		if (p != null){
			
			String id = UUID.randomUUID().toString();
			try{
				code = new TextUtils().makeCode(email);
			}
			catch(Exception e){e.printStackTrace();}
			String subject = "password recovery - no reply";
			mailText = "Please click the link below to " +
			"recover your password: \n\n" +
			"<a href=\"http://www.moeigo.com/PwdSetter.to?confid=" + id + 
			"&confcode=" + code +
			"\">click this link</a>";
			System.out.println("RecoverPwd-->mailText: " + mailText);
			try{
				JMailer jmail = new JMailer();
				jmail.sendMail(email, "no-reply@moeigo.com", subject, mailText);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("RecoverPwd->: in JMailer catch");
			}
			member.setRecovery(datasource, email, id, code);
			
			request.setAttribute("message", "パスワード設定リンクをメールに送ろました。");
		}
		else{
			String notFoundText = "";
			notFoundText = notFoundText + "メールアドレス" + "<br />" + 
						email + "<br />" + "は登録されていません";
			request.setAttribute("message", notFoundText);
		}
		RequestDispatcher view = request.getRequestDispatcher("index-06.jsp");
		view.forward(request, response);

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

}
