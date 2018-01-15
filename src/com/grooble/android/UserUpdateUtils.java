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
import com.grooble.model.Person;

public class UserUpdateUtils extends HttpServlet {
    
    private DataSource ds;
    private String encoding;
    private static final String TAG = "UserUpdateUtils";
    
    // get UTF-8 PARAMETER ENCODING to ensure that non-English text is handled correctly
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        encoding = context.getInitParameter("PARAMETER_ENCODING");
        try {
            ds = (DataSource) getServletContext().getAttribute("DBCPool");
        } catch(Exception e) {
            throw new ServletException(e.getMessage());
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        request.setCharacterEncoding(encoding);
        response.setContentType("text/html");
        response.setCharacterEncoding(encoding);
        System.out.println(TAG + "-->encoding: " + encoding);
        
        String fname = request.getParameter("fname");
        String lname = request.getParameter("lname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        System.out.println(TAG + " got params...fname: " + fname + ", lname: " + lname);
        System.out.println(TAG + " got login params...email: " + email + ", pwd: " + password);
        
        Member member = new Member();
        JSONObject outJSON = new JSONObject();
        Person user = member.verify(ds, email, password);
        
        
        if(user != null){
            // add name to user for update
            if(fname != null){
                user.setFirstName(fname);
            }
            else{
                user.setFirstName("");
            }
            
            if(lname != null){
                user.setLastName(lname);
            }
            else{
                user.setLastName("");
            }

            Person updatedUser = member.updateName(ds, user);
            
            try {
                outJSON.put("user", updatedUser.getJSONObject());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println(TAG + "user was null... update not performed");
            try {
                outJSON.put("error", "error");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.write(outJSON.toString());
        
    }

}
