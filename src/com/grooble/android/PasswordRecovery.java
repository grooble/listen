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
public class PasswordRecovery extends HttpServlet {
    private static final String TAG = "Login ";
    private DataSource ds;
    private String encoding;
    

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
            m.resetPassword(email, recovery, newPassword);
        }
    }


}