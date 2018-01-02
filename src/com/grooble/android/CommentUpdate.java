package com.grooble.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.grooble.model.JSONMaker;
import com.grooble.model.Member;
import com.grooble.model.Person;
import com.grooble.model.Status;
import com.grooble.model.Update;

/*
 * Take new status as a parameter add to DB, then
 * Get lists of status and results from DB and return to 
 * app with JSON.
 * Can also take a "retrieve" parameter which indicates no 
 * new status added, but is a way to update recent data to app.
 */

@SuppressWarnings("serial")
public class CommentUpdate extends HttpServlet {
    
    private DataSource ds;
    private String encoding;
    private static final String TAG = "CommentUpdate";
    
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
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String statusComment = request.getParameter("content");
        String statusType = request.getParameter("type");
        String parentString = request.getParameter("parent");
        
        int parent = 0;
        
        if ((parentString != null) && (!parentString.isEmpty())){
            parent = Integer.parseInt(parentString);
        }
        
        System.out.println(TAG + "-->email, status, type, flag: " 
                + email + ", " 
                + statusComment + ", " 
                + statusType + ", " 
                + parent + ", ");
        
        Member memberTools = new Member();
        JSONMaker jMaker = new JSONMaker();
        
        Person user = (Person) memberTools.verify(ds, email, password);
        System.out.println(TAG + "-->user: " + user.getFirstName());
        
        // check for empty status and if not empty
        // create Status and call setStatus in Update.java
        Update up = new Update();
        Status status = null;
        if ((statusComment != null) && (!statusComment.isEmpty())){
            if(statusType == null){ // set type to comment
                status = new Status(user.getId(), "comment", statusComment, parent);
            }
            else{ // otherwise include type in the constructor
                status = new Status(
                    user.getId(), statusType, statusComment, parent); 
                    System.out.println(TAG + " Status: " + status.getContent());
            }
            status = up.setComment(ds, status);
        }

        List<Status> statusList = new ArrayList<Status>();
        statusList.add(status);
        System.out.println(TAG + "status: " + status.toString());
        
        // Initialize JSONContainer to return JSON to terminal
        JSONObject JSONContainer = new JSONObject();
        JSONArray jsonStatusList = jMaker.getJSONArray(statusList); 
        
        try {
            JSONContainer.put("status", jsonStatusList);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }        

        // return status
        System.out.println(TAG + "return: " + JSONContainer.toString());
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.write(JSONContainer.toString());        
    }
}