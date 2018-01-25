package com.grooble.android;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONException;
import org.json.JSONObject;

import com.grooble.model.Member;

@SuppressWarnings("serial")
public class PasswordRecovery extends HttpServlet {
    private static final String TAG = "Login ";
    private DataSource ds;
    private String encoding;
    int result = -1;
    

    public void init() throws ServletException {
        ServletContext context = getServletContext();
        encoding = context.getInitParameter("PARAMETER_ENCODING");

        try {
            ds = (DataSource) getServletContext().getAttribute("DBCPool");
        } catch(Exception e) {
            throw new ServletException(e.getMessage());
        }
    }
    
    public void doGet(HttpServletRequest request, 
                            HttpServletResponse response)
                            throws IOException, ServletException {
        doPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, 
                            HttpServletResponse response)
                            throws IOException, ServletException {
        response.setContentType("text/html");
        response.setCharacterEncoding(encoding);

        String email = request.getParameter("email");
        String newPassword = request.getParameter("password");
        String recovery = request.getParameter("recovery");

        if(
                ((email != null) && !(email.isEmpty())) &&
                ((newPassword != null) && !(newPassword.isEmpty())) &&
                ((recovery != null) && !(recovery.isEmpty())) 
          ){
            Member m = new Member(ds);
            result = m.resetPassword(email, recovery, newPassword);
        }
 
        // Initialize JSONContainer to return JSON to terminal
        JSONObject JSONContainer = new JSONObject();
 
        // add user, status and results to JSONContainer
        try {
            // Add status and user to response JSONObject
            JSONContainer.put("result", result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        // return status list or empty JSONContainer
        System.out.println(TAG + "return: " + JSONContainer.toString());
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.write(JSONContainer.toString());        
    }
    
}