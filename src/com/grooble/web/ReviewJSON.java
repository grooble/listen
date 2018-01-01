package com.grooble.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import com.grooble.model.JSONMaker;
import com.grooble.model.Test;

@SuppressWarnings("serial")
public class ReviewJSON extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		
		System.out.println("Review.java. about to send JSON.");
		Test test = (Test)session.getAttribute("test");
		JSONMaker jm = new JSONMaker();
		JSONArray jsonTest = jm.toJSON(test);
		System.out.println("Review -> json ");
		System.out.println(jsonTest.toString());
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.write(jsonTest.toString());
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
	}

}
