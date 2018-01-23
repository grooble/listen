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
public class FriendProcesses extends HttpServlet {
    
    private static final String TAG = "FriendProcesses ";
    
    private DataSource ds;
    private Connection con;
    private String email;
    private String password;
    private String encoding;

    private Person user; 
        
    public void init() throws ServletException {
        try {
            ds = (DataSource) getServletContext().getAttribute("DBCPool");
            ServletContext context = getServletContext();
            encoding = context.getInitParameter("PARAMETER_ENCODING");
       } catch(Exception e) {
            throw new ServletException(e.getMessage());
        }
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding(encoding);
        response.setContentType("text/html");
        response.setCharacterEncoding(encoding);

        // Get parameter containing string of space-separated mails
        String pendingString = request.getParameter("pendingList");
        String inviteString = request.getParameter("inviteList");
        email = request.getParameter("email");
        password = request.getParameter("password");
        
        System.out.println(TAG + " email: " + email + ", pwd: " + password);
        
        List<String> pendingEmails = null;
        List<String> invitedEmails = null;

        // parse pendingString to get pended emails if not empty
        if((pendingString != null) && (!pendingString.isEmpty())){
            pendingEmails = this.getListFromString(pendingString);
        }
        
        // parse inviteString to get invited emails if not empty
        if((inviteString != null) && (!inviteString.isEmpty())){
            invitedEmails = this.getListFromString(inviteString);
        }

        // Initialize Member instance for functions to add pending and invited users
        Member member = new Member(ds);
        
        // get user
        user = member.verify(email, password);
        if (user == null){return;}
        
        // get userids from emails 
        // add pending ids list as approved friend
        // add installed ids list as pending approval
        List<Integer> pendingIdsList = null;
        List<Integer> invitedIdsList = null;
        
        // Get space separated list of friend ids that were added from the pending list
        String returnIds = "";
        if(pendingEmails != null){
            pendingIdsList = findFriendIds((List<String>) pendingEmails);
            returnIds = addFriends(user.getId(), pendingIdsList);
            System.out.println(TAG + "added ids: " + returnIds);
        }
        
        // Get space separated list of ids that were added to pending and invited to become friends
        String returnPendedIds = "";
        if(invitedEmails != null){
            invitedIdsList = findFriendIds((List<String>) invitedEmails);
            returnPendedIds = addToPending(user.getId(), invitedIdsList);
        }
        
        // Response to return is a space separated string of added friend ids
        JSONObject jsonResponse = new JSONObject();
        try {
            jsonResponse.put("added", returnIds.trim());
            jsonResponse.put("pended", returnPendedIds.trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        // return status list or empty JSONContainer
        System.out.println(TAG + "response:" + jsonResponse.toString());
        PrintWriter out = response.getWriter();
        out.write(jsonResponse.toString());        
    }
    
    private List<String> getListFromString(String emailString){
        String[] arrayOfStrings = emailString.split(" ");
        return (List<String>) Arrays.asList(arrayOfStrings);
    }
    
    public List<Integer> findFriendIds(List<String> friends){
        List<Integer> extractedIds = new ArrayList<Integer>();
        
        String emailParameter = "";
        for (int i = 0; i < (friends.size()-1); i++){
            emailParameter = 
                    emailParameter + "(\"" + friends.get(i) + "\"), ";
        }
        emailParameter = 
                emailParameter + "(\"" + friends.get(friends.size()-1) + "\")";
        
        // create temp DB and populate with foundList
        ResultSet rs = null;
        Statement stmt = null;
        String createQuery = 
                "CREATE TEMPORARY TABLE emails (id SMALLINT NOT NULL AUTO_INCREMENT,"
                + " email VARCHAR(60),"
                + " PRIMARY KEY(id),"
                + " INDEX(email));";
        String insertQuery = 
                "INSERT IGNORE INTO emails (email)"
                + " VALUES " + emailParameter + ";";
        
        String selectQuery =
                "SELECT students.stdid FROM students INNER JOIN emails "
                + "ON emails.email = students.email;";
        
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
                    Integer found = rs.getInt(1);
                    extractedIds.add(found);
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
        
        return extractedIds;
    }

    // Add selected from the pending list to become friends
    String addFriends(int userId, List<Integer> pended){
        
        // Create string of ordered user friend insert elements. 
        String insertString = "";
        for(Integer value : pended){
            insertString = insertString + "(";
            if (userId < value)
                insertString = insertString + userId + ", " + value;
            else
                insertString = insertString + value + ", " + userId;
            insertString = insertString + "), ";
        }
        String finalInsertString = insertString.substring(0, insertString.length()-2);
        
        System.out.println(TAG + "finalInsertString: " + finalInsertString);
        
        String insertQuery = 
                "INSERT INTO friends (user, friend)"
                + " VALUES " + finalInsertString + ";";

        Statement stmt = null;

        PreparedStatement is = null;
        
        int returnValue = -1;
        // query users against temp DB
        try{
            con = ds.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("USE teacher");
            is = con.prepareStatement(insertQuery);
            System.out.println(TAG + " is: " + is.toString());

            returnValue = is.executeUpdate();
            System.out.println(TAG + "_ret: " + returnValue);
                        
        } catch(Exception ex){
            ex.printStackTrace();
        }
        finally {
            try {if (stmt != null) stmt.close();} catch (SQLException e) {}
            try {if (is != null) is.close();} catch (SQLException e ) {}
            try {if (con != null) con.close();} catch (SQLException e) {}
        }
        
        // return matches
        if (returnValue != 0){
            FriendUtils friender = new FriendUtils(ds, email, password);
            for(Integer pendedFriend : pended){
                friender.deleteRequest(ds, pendedFriend, userId);
            }
        }

        // space-separated-string of added ids
        String returnIds = "";
        for(Integer pendedFriend : pended){
            returnIds = returnIds + String.valueOf(pendedFriend) + " ";
        }
        returnIds.trim();
        return returnIds;
    }

    
    // Invite users who have the app installed.
    // Added to the pending table to await approval
    // Return space separated string of pended ids
    String addToPending(int userId, List<Integer> toPend){
        
        // TODO eliminate already pended from this list:
        // get userid, select from pending where requester is userId
        // remove those values from the list then insert.
        
        List<Integer> currentToPend = new ArrayList<Integer>();
        currentToPend.addAll(toPend);
        System.out.println(TAG + "to be pended size: " + currentToPend.size());
        
        // Get pending awaiting approval by this user
        String selectPended = "SELECT requester FROM pending WHERE approver = ?";
        Statement stmt = null;
        PreparedStatement sp = null;
        ResultSet rs = null;
        try {
            con = ds.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("USE teacher");

            sp = con.prepareStatement(selectPended);
            sp.setInt(1, userId);
            rs = sp.executeQuery();
            while(rs.next()){  // remove already pended ids
                currentToPend.remove((Integer)rs.getInt(1));
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        finally {
            try {if (stmt != null) stmt.close();} catch (SQLException e) {}
            try {if (sp != null) sp.close();} catch (SQLException e) {}
            try {if (con != null) con.close();} catch (SQLException e) {}
            try {if (rs != null) rs.close();} catch (Exception e){e.printStackTrace();}
        }
        
        // Create string of ordered user friend insert elements. 
        String insertString = "";
        if(currentToPend.size() > 1){
            for(int i = 0; i < currentToPend.size()-1; i++){
                insertString = insertString + "(" +userId + ", " + currentToPend.get(i) + "), ";
            }
        }
        insertString = insertString + "(" +userId + ", " + currentToPend.get(currentToPend.size()-1) + ")";
                
        System.out.println(TAG + "finalInsertString: " + insertString);
        
        String insertQuery = 
                "INSERT IGNORE INTO pending (requester, approver)"
                + " VALUES " + insertString + ";";

        Statement stmt2 = null;

        PreparedStatement is = null;
        
        // query users against temp DB
        try{
            con = ds.getConnection();
            stmt2 = con.createStatement();
            stmt2.executeUpdate("USE teacher");
            is = con.prepareStatement(insertQuery);
            System.out.println(TAG + " is: " + is.toString());

            is.executeUpdate();
                        
        } catch(Exception ex){
            ex.printStackTrace();
        }
        finally {
            try {if (stmt2 != null) stmt2.close();} catch (SQLException e) {}
            try {if (is != null) is.close();} catch (SQLException e ) {}
            try {if (con != null) con.close();} catch (SQLException e) {}
        }

        // space-separated-string of pended ids
        String returnIds = "";
        for(Integer pendedFriend : toPend){
            returnIds = returnIds + String.valueOf(pendedFriend) + " ";
        }
        return returnIds;
    }
    
    List<Person> getFriendsOfFriend(int id){
    List<Person> foundFriends = new ArrayList<Person>();
        
        
        // select users that are friends of current user
        ResultSet rs = null;
        Statement stmt = null;
        String selectQuery = 
                "SELECT stdid, firstname, lastname, email, profilepic, points FROM students "
                + "INNER JOIN friends "
                + "ON students.stdid = friends.user " 
                + "WHERE (user=?) OR (friend=?) ";

        PreparedStatement sq = null;
        // query users against temp DB
        try{
            con = ds.getConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("USE teacher");
            sq = con.prepareStatement(selectQuery);
            sq.setInt(1, id);
            sq.setInt(2, id);
            System.out.println(TAG + " sq: " + sq.toString());

            rs = sq.executeQuery();
            while(rs.next()){
                    Person p = new Person();
                    
                    p.setId(rs.getInt(1));
                    p.setFirstName(rs.getString(2));
                    p.setLastName(rs.getString(3));
                    p.setEmail(rs.getString(4));
                    p.setProfilePic(rs.getString(4));
                    p.setPoints(rs.getInt(6));
                    
                    foundFriends.add(p);
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        finally {
            try {if (rs != null) rs.close();} catch (SQLException e) {}
            try {if (stmt != null) stmt.close();} catch (SQLException e) {}
            try {if (sq != null) sq.close();} catch (SQLException e ) {}
            try {if (con != null) con.close();} catch (SQLException e) {}
        }
        
        return foundFriends;

    }

}