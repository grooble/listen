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
import com.grooble.model.Update;

public class RegUpdate extends HttpServlet {
    
    private DataSource ds;
    private String encoding;
    private static final String TAG = "RegUpdate";
    
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
        
        String token = request.getParameter("token");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        System.out.println(TAG + " got params... token: " + token);
        System.out.println(TAG + " got login params...email: " + email);
        
        // verify login credentials
        Person user = new Member().verify(ds, email, password);
        
        //JSONObject for the response
        JSONObject responseJSON = new JSONObject();
        
        if(user != null){            
            Update updater = new Update();
            updater.updateReg(ds, token, user.getId());
            try{
                responseJSON.put("update", "success");
            }
            catch(JSONException je){je.printStackTrace();}
        }
        else{
            System.out.println(TAG + "user was null... update not performed");
            try{
                responseJSON.put("update", "failure");
            }
            catch(JSONException je){je.printStackTrace();}
        }
        
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.write(responseJSON.toString());
    }

}