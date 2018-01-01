package com.grooble.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.grooble.model.Person;

/**
 * This filter checks that a user is logged in, and if not
 * redirects them to the login page.
 */
public class AuthenticationFilter implements Filter {
	
	public void init(FilterConfig fConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, 
						  ServletResponse response, 
						  FilterChain chain) 
					throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession();
		Object userObj = session.getAttribute("user");
		if(!(userObj instanceof Person)){
			System.out.println("Authentication-->user not found");
			RequestDispatcher view = request.getRequestDispatcher("index-06.jsp");
			view.forward(request, response);
		} else {
			// Logged in, just continue chain.
			System.out.println("Authentication-->continue chain");
			chain.doFilter(request, response); 
		}
	}

	public void destroy() {
	}

}
