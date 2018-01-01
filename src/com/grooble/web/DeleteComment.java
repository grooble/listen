package com.grooble.web;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.grooble.model.Update;

@SuppressWarnings("serial")
public class DeleteComment extends HttpServlet {

    private DataSource ds; 

//	データソースを初期化する
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
        String idString = (String)request.getAttribute("commid");
        int id = 0;
        if(!idString.isEmpty()){
        	id = Integer.parseInt(idString);
        }
        if(id!=0){
        	Update up = new Update();
        	up.deleteStatus(ds, id);
        }
	}

}
