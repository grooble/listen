package com.grooble.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
public class TestIdFetcher extends HttpServlet {
    
    private static final String TAG = "TestIdFetcher: ";
    
    private DataSource ds;
    private Connection con;
    
    private Person user;
    
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
        
        // Get parameter containing string of space-separated mails
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // the testId value to return to app
        int nextTestId = 0;
        
        // Get user
        Member member = new Member(ds);
        user = member.verify(email, password);
        if (user == null){return;}
        else{
            nextTestId = getTestId(user.getId());
        }
        
        JSONObject testIdJson = new JSONObject();
        try {
            testIdJson.put("nexttestid", nextTestId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Write mail string to output
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.write(testIdJson.toString()); 
    }    

       
    /*
     * This method returns a List of all of the phonebook items that have the app installed,
     * minus the ones that have already been invited.
     */
    private int getTestId(int userId){

        int nextTestId = 0;
        ResultSet rs = null;
        Statement stmt = null;
        String maxQuery = 
                "SELECT MAX(test_id) FROM tests WHERE user=?";

        String insertQuery =
                "INSERT INTO tests (user, test_id) VALUES(?, ?)";
        
        PreparedStatement ms = null, is = null;
        // query users against temp DB
        try{
            con = ds.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("USE teacher");
            ms = con.prepareStatement(maxQuery);
            ms.setInt(1, user.getId());

            System.out.println(TAG + " ms: " + ms.toString());
            
            rs = ms.executeQuery();
            
            while(rs.next()){
                nextTestId = rs.getInt(1);
            }
            //increment nextTestId for the value of the next test
            nextTestId++;
            
            is = con.prepareStatement(insertQuery);
            is.setInt(1, user.getId());
            is.setInt(2, nextTestId);
            System.out.println(TAG + " is: " + is.toString());
            is.executeUpdate();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        finally {
            try {if (rs != null) rs.close();} catch (SQLException e) {}
            try {if (stmt != null) stmt.close();} catch (SQLException e) {}
            try {if (ms != null) ms.close();} catch (SQLException e ) {}
            try {if (is != null) is.close();} catch (SQLException e ) {}
            try {if (con != null) con.close();} catch (SQLException e) {}
        }
        
        // return matches
        
        return nextTestId;
    }

}
