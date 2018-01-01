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

@SuppressWarnings("serial")
public class TutUpdateHandler extends HttpServlet {
    
    private DataSource ds;
    private String encoding;
    private static final String TAG = "TutUpdateHandler";
    
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
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String tut = request.getParameter("tutorial");
        
        // Set tutorialStatus to integer value based on parameter string
        System.out.println(TAG + " got params...tut: " + tut);
        int tutorialStatus = -1;
        if(tut.equals("true")){
            tutorialStatus = 1;
        }
        if(tut.equals("false")){
            tutorialStatus = 0;
        }

        // get Member instance and update tutorialStatus to DB
        Member m = new Member();
        Person p = m.verify(ds, email, password);
        if(p.getEmail() != null){            
            m.updateTutorial(ds, email, password, tutorialStatus);
        }
        response.setContentType("text/html");
        JSONObject job = new JSONObject();
        try {
            job.put("update", "updated");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        // No particular return value so return that DB updated
        PrintWriter out = response.getWriter();
        System.out.println(TAG + " JSON output: " + job);
        out.write(job.toString());
        
    }

}
