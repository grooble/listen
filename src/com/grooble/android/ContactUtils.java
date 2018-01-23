package com.grooble.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.grooble.model.Member;
import com.grooble.model.Person;

@SuppressWarnings("serial")
public class ContactUtils extends HttpServlet {
    
    private static final String TAG = "ContactUtils ";
    
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
        String emailString = request.getParameter("emails");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        // Get user
        Member member = new Member(ds);
        
        // Obtain hash of email to verify user
        user = member.verify(email, password);
        if (user == null){return;}
        
        // Split on space and create ArrayList
        String[] emailArray = emailString.split(" ");
        List<String> mailList = new ArrayList<String>(Arrays.asList(emailArray));
        
        // Get registered mails
        List<String> registeredMails = new ArrayList<String>();
        registeredMails = (ArrayList<String>) findRegistered(((ArrayList<String>)mailList));
        
        // Convert to string for writing to output
        String registeredMailString = "";
        for(String regMail : registeredMails){
            registeredMailString = registeredMailString + regMail + " ";
        }
        registeredMailString.trim();
        
        // Write mail string to output
        response.setContentType("text/html");
        System.out.println(TAG + "output: " + registeredMailString);
        PrintWriter out = response.getWriter();
        out.write(registeredMailString); 
    }    

       
    /*
     * This method returns a List of all of the phonebook items that have the app installed,
     * minus the ones that have already been invited.
     */
    public List<String> findRegistered(ArrayList<String> users){
        List<String> foundList = new ArrayList<String>();
        
        String emailParameter = "";
        for (int i = 0; i < (users.size()-1); i++){
            emailParameter = emailParameter + "(\"" + users.get(i) + "\"), ";
        }
        emailParameter = emailParameter + "(\"" + users.get(users.size()-1) + "\")";
        
        // create temp DB and populate with foundList
        ResultSet rs = null;
        Statement stmt = null;
        
        // create temporary table 'emails' and store the emails retrieved from the sent parameter
        String createQuery = 
                "CREATE TEMPORARY TABLE emails (id SMALLINT NOT NULL AUTO_INCREMENT,"
                + " email VARCHAR(100),"
                + " email_hash VARCHAR(100),"
                + " PRIMARY KEY(id),"
                + " INDEX(email));";
        
        // add emails and email_hash to the table
        String insertQuery = 
                "INSERT INTO emails (email, email_hash)"
                + " VALUES (" + emailParameter + ", " + String.valueOf(emailParameter.hashCode()) + " );";
        
        // Selects those with the app installed except for those already pended.
        String selectQuery =
                "SELECT DISTINCT emails.email FROM emails "
                + "INNER JOIN students ON students.email_hash = emails.email_hash "
                + "LEFT JOIN "
                + "( "
                        + "SELECT pending.approver, students.email_hash "
                        + "FROM students "
                        + "INNER JOIN pending ON pending.approver = students.stdid "
                        + "WHERE pending.requester = ? "
                        + ") AS p "
                + "ON emails.email_hash = p.email_hash " 
                + "WHERE p.email_hash IS NULL;";
        
        String dropQuery = "DROP TABLE emails;";

        PreparedStatement cs = null, is = null, ss = null, drop_s = null;
        // query users against temp DB
        try{
            con = ds.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("USE teacher");
            cs = con.prepareStatement(createQuery);
            is = con.prepareStatement(insertQuery);
            ss = con.prepareStatement(selectQuery);
            ss.setInt(1, user.getId());
            drop_s = con.prepareStatement(dropQuery);
            System.out.println(TAG + " cs: " + cs.toString());
            System.out.println(TAG + " is: " + is.toString());
            System.out.println(TAG + " ss: " + ss.toString());

            cs.executeUpdate();
            is.executeUpdate();
            
            rs = ss.executeQuery();
            if(!rs.next()){
                System.out.println(TAG + " in !rs.next() if");    }
            else{
                do{
                    String found = rs.getString(1);
                    foundList.add(found);
                    System.out.println(TAG + " found: " + found);
                } while(rs.next());
            }
            
            // DROP TABLE after records extracted
            drop_s.executeUpdate();
            
        } catch(Exception ex){
            ex.printStackTrace();
        }
        finally {
            try {if (rs != null) rs.close();} catch (SQLException e) {}
            try {if (stmt != null) stmt.close();} catch (SQLException e) {}
            try {if (cs != null) cs.close();} catch (SQLException e ) {}
            try {if (is != null) is.close();} catch (SQLException e ) {}
            try {if (ss != null) ss.close();} catch (SQLException e ) {}
            try {if (drop_s != null) drop_s.close();} catch (SQLException e){}
            try {if (con != null) con.close();} catch (SQLException e) {}
        }
        
        
        // remove existing friends from the list
        ResultSet rs2 = null;
        PreparedStatement pStmt = null;
        String friendRequest = "SELECT friend FROM friends WHERE user = ? "
                             + "UNION "
                             + "SELECT user FROM friends WHERE friend = ? ";
        
        try{
            con = ds.getConnection();
            Statement st = con.createStatement();
            st.executeUpdate("USE teacher");
            pStmt = con.prepareStatement(friendRequest);
            pStmt.setInt(1, user.getId());
            pStmt.setInt(2, user.getId());
            rs2 = pStmt.executeQuery();
            while(rs2.next()){
                foundList.remove((Integer)rs2.getInt(1));
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally {
            try {if (rs2 != null) rs.close();} catch (SQLException e) {}
            try {if (pStmt != null) stmt.close();} catch (SQLException e) {}
            try {if (con != null) con.close();} catch (SQLException e) {}
        }

        System.out.println(TAG + "findRegistered -> foundList.size(): " + foundList.size());
        // return matches
        return foundList;
    }
}