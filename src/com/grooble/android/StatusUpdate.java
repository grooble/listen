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
import com.grooble.model.Result;
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
public class StatusUpdate extends HttpServlet {
    
    private DataSource ds;
    private String encoding;
    private static final String TAG = "StatusUpdate";
    
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
        
        response.setContentType("text/html");
        response.setCharacterEncoding(encoding);
        System.out.println(TAG + "-->encoding: " + encoding);
        
        String email = request.getParameter("email");
        String statusComment = request.getParameter("content");
        String statusType = request.getParameter("type");
        String parentString = request.getParameter("parent");
        String flagString = request.getParameter("retrieve");
        String startString = request.getParameter("start");
        String sizeString = request.getParameter("size");
        
        boolean retrieve = false;
        int parent = 0, start = 0, size =0;
    
        
        // Parse strings into Integers
        if((flagString != null) && (!flagString.isEmpty())){
            retrieve = Boolean.parseBoolean(flagString);
            System.out.println("StatusUpdate... setting retrieve flag: " + retrieve);
        }
        
        if ((parentString != null) && (!parentString.isEmpty())){
            parent = Integer.parseInt(parentString);
        }

        if ((startString != null) && (!startString.isEmpty())){
            start= Integer.parseInt(startString);
        }

        if ((sizeString != null) && (!sizeString.isEmpty())){
            size = Integer.parseInt(sizeString);
        }

        System.out.println(TAG + "-->"
                + "email: "  + email + ", " 
                + "status: " + statusComment + ", " 
                + "type: "   + statusType + ", " 
                + "parent: " + parent + ", "
                + "retrv: "  + retrieve + ", "
                + "start: "  + start + ", "
                + "size: "   + size);
        
        Member memberTools = new Member();
        Person user = (Person) memberTools.lookup(ds, email);
        UserBuilderT builder = new UserBuilderT(user, ds);
        System.out.println(TAG + "-->user: " + user.getFirstName());
        
        // check for empty status and if not empty
        // create Status and call setStatus in Update.java
        Update up = new Update();
        Status status;
        if ((statusComment != null) && (!statusComment.isEmpty())){
            if(statusType == null){ // set type to comment
                status = new Status(user.getId(), "comment", statusComment, parent);
            }
            else{ // otherwise include type in the constructor
                status = new Status(
                    user.getId(), statusType, statusComment, parent); 
                    System.out.println("StatusUpdate... Status: " + status.getContent());
            }
            up.setStatus(ds, status);
        }



        // Initialize JSONContainer to return JSON to terminal
        JSONObject JSONContainer = new JSONObject();
        
        if(retrieve){            
            // Initialize JSONMaker to create results and status JSON
            JSONMaker jMaker = new JSONMaker();
            
            // Get test Result list and put to JSON
            List<Result> results = (ArrayList<Result>) builder.fetchResults();
            
            // get JSONResult JSONArray if results not null or empty
            JSONArray JSONResult = null;
            if ((results==null) || (results.isEmpty())){
                System.out.println("grooble.android.Login... results empty or null");
            }
            else {
                System.out.println("grooble.android.Login... results not empty");
                JSONResult = jMaker.getJSONArray(results);
            }
            
            // retrieve a fresh list of 20 most recent status items 
            // and their comments
            StatusHandler handler = new StatusHandler();
            List<Status> statusList = new ArrayList<Status>();
            List<Status> newStatus = handler.getStatus(ds, user.getId(), start, size);
            
            if(newStatus != null){
                statusList = newStatus;
            }
            
            JSONArray JSONStatus = jMaker.getJSONArray(statusList);
            // Get JSONObject of user
            JSONObject userJson = user.getJSONObject();
            
            // add user, status and results to JSONContainer
            try {
                // Add status and user to response JSONObject
                JSONContainer.put("user", userJson);
                JSONContainer.put("status", JSONStatus);
                JSONContainer.put("results", JSONResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
        }

        // return status list or empty JSONContainer
        System.out.println(TAG + "return: " + JSONContainer.toString());
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.write(JSONContainer.toString());        
    }
}