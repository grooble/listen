package com.grooble.android;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.grooble.model.Status;

public class StatusHandler {
    
    private static final String TAG = "StatusHandler ";
    private Connection conn;

    public int deleteStatus(DataSource ds, int statusId){
        String updateQuery = 
            "DELETE from activities WHERE id=? OR parent=?";
        Statement stmt = null;
        PreparedStatement ps = null;
        int deleteStatus = -1;
        try{
            conn = ds.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("USE teacher");
            ps = conn.prepareStatement(updateQuery);
            ps.setInt(1, statusId);
            ps.setInt(2, statusId);
            deleteStatus = ps.executeUpdate();
            if(deleteStatus != -1){
                deleteStatus = statusId;
            }
        } catch(Exception ex){ex.printStackTrace();}
        finally {
            try {if (ps != null) ps.close();} catch (SQLException e) {}
            try {if (conn != null) conn.close();} catch (SQLException e) {}
        }
        return deleteStatus;
    }
    
    public List<Status> getStatus
    (DataSource ds, int user, int start, int size){
        System.out.println(TAG + "indices: " + start + ", " + size);
        List<Status> recents = new ArrayList<Status>();
        List<Status> comments = new ArrayList<Status>();
        String selectQuery = 
                "SELECT " +
                "activities.id AS act_id, " +
                "students.stdid AS std_id, " + 
                "activities.curr_date AS act_date, " +
                "activities.parent AS parent, " +
                "students.firstname AS fname, " +
                "students.profilepic AS pic, " +
                "activities.act_value, " +
                "activities.act_type " +
                "FROM " +
                // this gives the friends ids
                "(" +
                "SELECT " +
                "friend FROM friends " +
                "WHERE friends.user = ? " +
                "UNION " +
                "SELECT " +
                "user FROM friends " +
                "WHERE friends.friend = ? " +
                //this also adds current user id
                "UNION " +
                "SELECT " +
                "stdid FROM students " +
                "WHERE stdid = ? " +
                ") " +
                "AS listed " +
                "INNER JOIN activities " +
                "ON listed.friend = activities.user " +
                "INNER JOIN students " +
                "ON listed.friend = students.stdid " +
                "WHERE activities.parent = 0 " +  /* added here */
                "ORDER BY activities.curr_date DESC " +
                "LIMIT ?,?";
        
        String inClause = "";
        
        String commentsSelect = "SELECT id, user, curr_date, act_type, act_value, parent "
                              + "FROM activities WHERE parent IN ";
        ResultSet rs = null, rs2 = null;
        Statement stmt = null, stmt2 = null;
        PreparedStatement ps = null;
        
        try{
            System.out.println("StatusHandler-->in getStatus try");
            conn = ds.getConnection();
            stmt = conn.createStatement();
            ps = conn.prepareStatement(selectQuery);
            ps.setInt(1, user);
            ps.setInt(2, user);
            ps.setInt(3, user);
            ps.setInt(4, start);
            ps.setInt(5, size);
            System.out.println("StatusHandler-->ps: " + ps);
            stmt.executeUpdate("USE teacher");
            rs = ps.executeQuery();

            inClause = "(";
            if(!rs.isBeforeFirst()){
                System.out.println("StatusHandler-->status list is empty");     
            } else{
                while(rs.next()){
                    Status status = new Status();
                    int id = rs.getInt(1);
                    status.setId(id);
                    inClause = inClause + id + ", ";
                    status.setStdId(rs.getInt(2));
                    //convert Timestamp to string
                    Timestamp timestamp = rs.getTimestamp(3);
                    status.setDate(timestamp.toString());
                    status.setParent(rs.getInt(4));
                    status.setUser(rs.getString(5));
                    status.setPic(rs.getString(6));
                    status.setContent(rs.getString(7));
                    status.setType(rs.getString(8));
                    System.out.println("StatusHandler-->getStatus: " + 
                            status.getId() + " " +
                            status.getContent() +
                            " parent: " + status.getParent() + 
                            " stdId: " + status.getStdId());
                    recents.add(status);
                }
                inClause = inClause.trim();
                inClause = inClause.substring(0, inClause.length()-1);
                inClause = inClause + ")";
                System.out.println(TAG + "inClause: " + inClause);
            }
            System.out.println(TAG + "recents.size(): " + recents.size());
            if (recents.size() > 0){ 
                commentsSelect = commentsSelect + inClause;
                System.out.println(TAG + "commentsSelect: " + commentsSelect);
                stmt2 = conn.createStatement();
                rs2 = stmt2.executeQuery(commentsSelect);
                
                while(rs2.next()){
                    Status s = new Status();
                    s.setId(rs2.getInt(1));
                    s.setStdId(rs2.getInt(2));
                    Timestamp timestamp = rs2.getTimestamp(3);
                    s.setDate(timestamp.toString());
                    s.setType(rs2.getString(4));
                    s.setContent(rs2.getString(5));
                    s.setParent(rs2.getInt(6));
                    System.out.println("StatusHandler-->comments: " + 
                            s.getId() + " " +
                            s.getContent() +
                            " parent: " + s.getParent() + 
                            " stdId: " + s.getStdId());
                    
                    comments.add(s);
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally {
            try {if (stmt != null) stmt.close();} catch (SQLException e) {}
            try {if (ps != null) ps.close();} catch (SQLException e) {}
            try {if (conn != null) conn.close();} catch (SQLException e) {}
        }                   
        recents.addAll(comments);
        return recents;
    }
}