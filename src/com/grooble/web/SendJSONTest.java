package com.grooble.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import com.grooble.model.JSONMaker;
import com.grooble.model.Question;
import com.grooble.model.Test;

/**
 * Servlet implementation class SendJSONTest
 */
public class SendJSONTest extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// this is the Ajax call so send the JSON to page
		//response.setContentType("text/html");
		response.setContentType("application/json");
		
		// get session
		HttpSession session = request.getSession();
		// get request parameters
		String difficulty = request.getParameter("difficulty");
		String category = request.getParameter("category");
		String exclusion = request.getParameter("exclusion");
		String ajax = request.getParameter("ajax");
		System.out.println("SendJSONTest->category: " + category);
		System.out.println("SendJSONTest->dificulty: " + difficulty);
		System.out.println("SendJSONTest->exclusion: " + exclusion);
		System.out.println("SendJSONTest->ajax: " + ajax);
		
		// inialize test variables
		Test test = null;
		List<Question[]> testQuestions = null;
		int[] correct = null;
		
		test = (Test)session.getAttribute("test");
		testQuestions = test.getTest();
		correct = test.getCorrect();
		JSONMaker jm = new JSONMaker();
		JSONArray JTest = jm.toJSON(testQuestions, correct);
		System.out.println(JTest.toString());
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.write(JTest.toString());
	}
}
