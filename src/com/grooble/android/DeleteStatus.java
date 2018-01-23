package com.grooble.android;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.grooble.model.Member;
import com.grooble.model.Person;

@SuppressWarnings("serial")
public class DeleteStatus extends HttpServlet {
    
    private static final String TAG = "DeleteStatus";
    
    private DataSource ds;
    private int deleted; 
        
    public void init() throws ServletException {
        try {
            ds = (DataSource) getServletContext().getAttribute("DBCPool");
        } catch(Exception e) {
            throw new ServletException(e.getMessage());
        }
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String statusIdString = request.getParameter("statusid");
        System.out.println(TAG + "got email: " + email);
        int statusId = Integer.parseInt(statusIdString);
        
        Person user = new Member(ds).verify(email, password);

        if(user != null){
            deleted = new StatusHandler().deleteStatus(ds, statusId);
        }

        System.out.println(TAG + "got deleted from response: " + deleted);
        // return int indicating success or otherwise of delete friend
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.write(String.valueOf(deleted));
    }
        
}