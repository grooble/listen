package com.grooble.web;

import java.io.IOException;

import com.grooble.model.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
//import org.json.*;
//import java.util.*;

/**
 * Makes int[] from JSon sent from completed test
 * and sets int[] to session as well as adding to the Test object
 */
@SuppressWarnings("serial")
public class CompleteTest extends HttpServlet {
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		
		// セッションを取得
		HttpSession session = request.getSession();
		Test test = (Test)session.getAttribute("test");

		String answersFromJSON = request.getParameter("answered");
		int[] answers = null;
		if (answersFromJSON != null){
//			System.out.println(answersFromJSON);
			answers = makeArray(answersFromJSON);
			session.setAttribute("answers", answers);
			test.setSelected(answers);
			RequestDispatcher view = request.getRequestDispatcher("GradeMe.do");
			view.forward(request, response);
			return;
		}else{
			// 三回以上失敗があって、テスト作れなかった。エラーページを返す。
			RequestDispatcher view = request.getRequestDispatcher("error/generalerror.jsp");
			view.forward(request, response);
		}

	}
	
	int[] makeArray(String input){
		System.out.println("CompleteTest-->input: " + input);
		StringBuilder is = new StringBuilder(input);
		String s = is.substring(1, is.length()-1);
		String[] ans = s.split(",");
		int l = ans.length;
		int[] answers = new int[l];
		for (int i=0; i<l; i++){
			System.out.println("ans[" + i + "]: " + ans[i]);
			if (!ans[i].equals("null")){
				answers[i] = Integer.parseInt(ans[i].trim());
				System.out.println("If loop: answers[" + i + "]: " + answers[i]);
			}
			else{
				answers[i] = -1;
			}
		}
		System.out.println("CompleteTest-->makeArray output: " + answers.toString());
		return answers;
	}

}
