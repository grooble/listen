package com.grooble.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

public class FriendsOfFriend extends HttpServlet {
    
    private static final String TAG = "FriendsOfFriend::";
    
    private DataSource ds;
    private Connection con;
    private String email;
    private String password, friendIdString;
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
        email = request.getParameter("email");
        password = request.getParameter("password");
        friendIdString = request.getParameter("friendid");
        
        int friendId = Integer.parseInt(friendIdString);
        
        System.out.println(TAG + " email: " + email + ", pwd: " + password);
                
        Member member = new Member();
        
        // get user
        user = member.verify(ds, email, password);
        if (user == null){return;}
        
        // get list of friends of friendid
        System.out.println(TAG + "user id: " + user.getId());
        List<Person> fofs = getFriendsOfFriend(friendId);
        
        // get list of friend results
        Person friend = member.verify(ds, friendId);
        List<Result> results = new UserBuilderT(friend, ds).fetchResults();
        
        JSONMaker jm = new JSONMaker();
        
        JSONObject fofObjectJson = new JSONObject();
        JSONArray resultsJson = null;
        JSONArray fofArrayJSON = new JSONArray();
        
        Iterator<Person> it = fofs.iterator();
        
        resultsJson = jm.getJSONArray(results);
        
        try {
            while(it.hasNext()){
                fofArrayJSON.put(it.next().getJSONObject());
            }
            System.out.println(TAG + "fofArrayJSON size: " + fofArrayJSON.length());
            fofObjectJson.put("fofarray", fofArrayJSON);
            fofObjectJson.put("results", resultsJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        // Write mail string to output
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.write(fofObjectJson.toString()); 

    }
    
    List<Person> getFriendsOfFriend(int id){
        List<Person> foundFriends = new ArrayList<Person>();
        
        
        // select users that are friends of current user
        ResultSet rs = null;
        Statement stmt = null;
        String selectQuery = 
                "SELECT  stdid, firstname, lastname, email, profilepic, points FROM students "
                + "INNER JOIN friends ON students.stdid = friends.user "
                + "WHERE friends.friend=? "
                + "UNION "
                + "SELECT  stdid, firstname, lastname, email, profilepic, points FROM students "
                + "INNER JOIN friends ON students.stdid = friends.friend "
                + "WHERE friends.user=? ";

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
                System.out.println(TAG + "in resultSet while");
                Person p = new Person();
                
                p.setId(rs.getInt(1));
                p.setFirstName(rs.getString(2));
                p.setLastName(rs.getString(3));
                p.setEmail(rs.getString(4));
                p.setProfilePic(rs.getString(5));
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
        System.out.println(TAG + "friends list size: " + foundFriends.size());
        
        return foundFriends;

    }

}
