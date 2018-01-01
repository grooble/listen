package com.grooble.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class FBDeploy extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		
		String url = "http://www.facebook.com/dialog/oauth";
		String client_id = "359559514135121";
		String redirect_uri = "http://www.moeigo.com/FBLogin.do";
		String scope = "email";

		String fbURL = url + "?" +
						"client_id=" + client_id + "&" +
						"redirect_uri=" + redirect_uri + "&" +
						"scope=" + scope;

//		String fbUrlEncoded = java.net.URLEncoder.encode(fbURL, "UTF-8");
		System.out.println("FBDeploy->url: " + fbURL);
		
		response.sendRedirect(response.encodeRedirectURL(fbURL));
	}

}
