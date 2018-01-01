package com.grooble.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.grooble.model.JMailer;

@SuppressWarnings("serial")
public class FriendInviter extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		String getMail = (String)request.getParameter("email");
		String mailAddress = getMail.toLowerCase();
		String inviterName = (String)request.getParameter("inviterName");
		
		String mailSubject = "あなたの友だち" + inviterName + "がMoeigoにしょうたいしています";
		String mailText = inviterName + 
							"は英語リスニング力をアップ中です。\n\n" + 
							"あなたも一緒に楽しく勉強しませんか。\n\n" +
							"以下のリンクをクリックしてください。\n\n" +
							"http://www.moeigo.com/";
		String senderAddress = "noreply@moeigo.com";
			
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		System.out.println("FriendInviter->inviteString");
		out.write("inviteString");
		
		try{
			JMailer jmail = new JMailer();
			jmail.sendMail(mailAddress, senderAddress, mailSubject, mailText);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("FriendInviter->: in JMailer catch");
		}

	}

}
