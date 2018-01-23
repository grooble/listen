package com.grooble.android;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.grooble.model.Member;

@SuppressWarnings("serial")
public class AddBackup extends HttpServlet{
    //private static final String TAG = "Join ";
    private DataSource ds;
    private String encoding;
    // private JSONObject JSONUser, JSONContainer;

    // get init parameter and initialize datasource
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        
        try {
            ds = (DataSource) context.getAttribute("DBCPool");
        } catch(Exception e) {
            throw new ServletException(e.getMessage());
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // get encoding to set output to UTF-8
        if (encoding != null) {
            request.setCharacterEncoding(encoding);
        }
        response.setContentType("text/html");
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String recovery = request.getParameter("recovery");
        
        if(    ((email != null) && (email.length() > 0)) &&
               ((password != null) && (password.length() > 0)) &&
               ((recovery != null) && (recovery.length() > 0))
           ){
            Member m = new Member(ds);
            m.updateBackupPassword(email, password, recovery);
        }
    }

    public void doPost(HttpServletRequest request, 
            HttpServletResponse response)
            throws IOException, ServletException{
        processRequest(request, response);
    }
}