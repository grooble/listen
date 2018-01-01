package com.grooble.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.grooble.model.*;



/**
 * Servlet Filter implementation class ImageFilter
 */
public class ImageFilter implements Filter {
	
	private FilterConfig fc;

	public void init(FilterConfig config) throws ServletException {
		this.fc = config;
	}
	
	public void destroy() {
	}

	public void doFilter(ServletRequest request, 
							ServletResponse response, 
							FilterChain chain) 
				throws IOException, ServletException {
	    boolean authorized = false;

	    if (request instanceof HttpServletRequest) {
	        HttpSession session = ((HttpServletRequest)request).getSession(false);
	        if (session != null) {
	            Person user = (Person) session.getAttribute("user");
	            if (user != null){
	            	int id = user.getId();
	            	StringBuffer requestURL = ((HttpServletRequest)request).getRequestURL();
	            	String profileID = "profile/" + id;
	            	if (requestURL.indexOf(profileID) != -1) {
	            		authorized = true;
	            	}
	            }
	        }
	    }

	    if (authorized) {
	        chain.doFilter(request, response);
	        return;
	    } else if (fc != null) {
        	fc.getServletContext().getRequestDispatcher("/Block.do").
	                    forward(request, response);
	            return;
	    }
	}
}
