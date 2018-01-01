package com.grooble.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/*
*	to contain overloaded "updater" methods to update various student table data.
*/

public class Update {
    private static final String TAG = "Update ";
	private Connection conn;
	
	public void updater(DataSource ds, String email, String picFile){
//		MySQLクエリー
		String updateQry = 
			"UPDATE students SET profilepic=?" +
			"WHERE email=?";

		Statement stmt = null;
		PreparedStatement ps = null;
		
		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			ps = conn.prepareStatement(updateQry);
			ps.setString(1, picFile);
			ps.setString(2, email.toLowerCase());
			ps.executeUpdate();
		} catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (ps != null) ps.close();} catch (SQLException e) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}		
	}

	public void setStatus(DataSource ds, Status status){
		Statement stmt = null;
		PreparedStatement ps = null;
//		MySQLクエリー
		String insertQuery = 
			"INSERT INTO activities " +
			"(user, act_type, act_value, parent)" +
			"VALUES( ?, ?, ?, ?)";
		System.out.println("Update-->insertQuery: " + insertQuery);
		try{
			conn = ds.getConnection();
			ps = conn.prepareStatement(insertQuery);
			stmt = conn.createStatement();
			ps.setInt(1, status.getStdId());
			ps.setString(2, status.getType());
			ps.setString(3, status.getContent());
            ps.setInt(4, status.getParent());
			stmt.executeUpdate("USE teacher");
			ps.executeUpdate();
		} catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}		
	}
	
	/*
	 * Called by RegUpdate to update the fcm token to the user
	 */
	public void updateReg(DataSource ds, String token, int userId){
        Statement stmt = null;
        PreparedStatement ps = null;

        String insertQuery = 
            "UPDATE students " +
            "SET fcm_token = ? " +
            "WHERE stdid = ?";

        try{
            conn = ds.getConnection();
            ps = conn.prepareStatement(insertQuery);
            stmt = conn.createStatement();
            ps.setString(1, token);
            ps.setInt(2, userId);
            System.out.println(TAG + "Insert: " + ps.toString());

            stmt.executeUpdate("USE teacher");
            ps.executeUpdate();
        }
        catch(Exception e){e.printStackTrace();}
    
	}
	
	
    public Status setComment(DataSource ds, Status status){
        Statement stmt = null, stmt2 = null;
        PreparedStatement ps = null, ps2 = null;
        ResultSet rs = null, rs2 = null;
        Status comment = null;
        int id = 0;
//      MySQLクエリー
        String insertQuery = 
            "INSERT INTO activities " +
            "(user, act_type, act_value, parent) " +
            "VALUES( ?, ?, ?, ?)";
        String selectRecent = "SELECT LAST_INSERT_ID()";
        String selectStatus =
                "SELECT id, user, curr_date, act_type, act_value, parent " + 
                "FROM activities " + 
                "WHERE id = ?";
        //System.out.println("Update-->insertQuery: " + insertQuery);
        try{
            conn = ds.getConnection();
            ps = conn.prepareStatement(insertQuery);
            ps2 = conn.prepareStatement(selectStatus);
            stmt = conn.createStatement();
            stmt2 = conn.createStatement();
            ps.setInt(1, status.getStdId());
            ps.setString(2, status.getType());
            ps.setString(3, status.getContent());
            ps.setInt(4, status.getParent());
            System.out.println(TAG + "Insert: " + ps.toString());
            stmt.executeUpdate("USE teacher");
            ps.executeUpdate();
            rs = stmt2.executeQuery(selectRecent);
            
            while(rs.next()){
                id = rs.getInt(1);
            }
            System.out.println(TAG + "id: " + id);
            
            ps2.setInt(1, id);
            System.out.println(TAG + "Select: " + ps2.toString());
            rs2 = ps2.executeQuery();
            while(rs2.next()){
                comment = new Status();
                comment.setId(rs2.getInt(1));
                comment.setStdId(rs2.getInt(2));
                //convert Timestamp to string
                Timestamp timestamp = rs2.getTimestamp(3);
                comment.setDate(timestamp.toString());
                comment.setType(rs2.getString(4));
                comment.setContent(rs2.getString(5));
                comment.setParent(rs2.getInt(6));
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        finally {
            try {if (stmt != null) stmt.close();} catch (SQLException e) {}
            try {if (ps != null) ps.close();} catch (SQLException e) {}
            try {if (conn != null) conn.close();} catch (SQLException e) {}
        }
        
        System.out.println(TAG + "Status: " + status.toString());
        
        return comment;
    }
    
	public List<Status> getStatus
				(DataSource ds, int user, int start, int size){

		ArrayList<Status> recents = new ArrayList<Status>();
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
			"WHERE stdid = ?" +
			") " +
			"AS listed " +
			"INNER JOIN activities " +
			"ON listed.friend = activities.user " +
			"INNER JOIN students " +
			"ON listed.friend = students.stdid " +
			"ORDER BY activities.curr_date DESC " +
			"LIMIT ?,?";		
		ResultSet rs = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		
		try{
			System.out.println("Update-->in getStatus try");
			conn = ds.getConnection();
			stmt = conn.createStatement();
			ps = conn.prepareStatement(selectQuery);
			ps.setInt(1, user);
			ps.setInt(2, user);
			ps.setInt(3, user);
			ps.setInt(4, start);
			ps.setInt(5, size);
			System.out.println("Update-->ps: " + ps);
			stmt.executeUpdate("USE teacher");
			rs = ps.executeQuery();
			//System.out.println("Update-->executed query");
			if(!rs.isBeforeFirst()){
				System.out.println("Update-->result set is empty");		
			} else{
				while(rs.next()){
					Status status = new Status();
					status.setId(rs.getInt(1));
					status.setStdId(rs.getInt(2));
                    //convert Timestamp to string
                    Timestamp timestamp = rs.getTimestamp(3);
                    status.setDate(timestamp.toString());
					status.setParent(rs.getInt(4));
					status.setUser(rs.getString(5));
					status.setPic(rs.getString(6));
					status.setContent(rs.getString(7));
					status.setType(rs.getString(8));
					System.out.println("Update-->getStatus: " + 
							status.getId() + " " +
							status.getContent() +
							" type: " + status.getType() + 
							" name: " + status.getUser());
					recents.add(status);
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
		return recents;
	}
	
	/*
	 * Overloaded method to return updates showing user and 
	 * friend for the page that shows friend's status
	 */
	public List<Status> getStatus
	(DataSource ds, int user, int friend, int start, int size){
		ArrayList<Status> recents = new ArrayList<Status>();
		String selectQuery = 
				"SELECT " +
						"activities.id AS act_id, " +
						"students.stdid AS std_id," + 
						"DATE_FORMAT(activities.curr_date, '%a, %d %b %Y - %l:%i %p') AS act_date, " +
						"students.firstname AS fname, " +
						"students.profilepic AS pic, " +
						"activities.act_value, " +
						"activities.act_type " +
						"activities.parent " +
						"FROM " +
						"activities INNER JOIN students " +
						"ON students.stdid = activities.user " +
						"WHERE activities.user IN (?,?) " +
						"ORDER BY activities.curr_date DESC " +
						"LIMIT ?,?";		
		ResultSet rs = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		
		try{
System.out.println("Update-friend overload-->in getStatus try");
			conn = ds.getConnection();
			stmt = conn.createStatement();
			ps = conn.prepareStatement(selectQuery);
			ps.setInt(1, user);
			ps.setInt(2, friend);
			ps.setInt(3, start);
			ps.setInt(4, size);
			System.out.println("Update-->ps: " + ps);
			stmt.executeUpdate("USE teacher");
			rs = ps.executeQuery();
//System.out.println("Update-->executed query");
			if(!rs.isBeforeFirst()){
				System.out.println("Update-->result set is empty");		
			} else{
				while(rs.next()){
					Status status = new Status();
					status.setId(rs.getInt(1));
					status.setStdId(rs.getInt(2));
					//convert Timestamp to string
					Timestamp timestamp = rs.getTimestamp(3);
					status.setDate(timestamp.toString());
					status.setUser(rs.getString(4));
					status.setPic(rs.getString(5));
					status.setContent(rs.getString(6));
					status.setType(rs.getString(7));
					status.setParent(rs.getInt(8));
					System.out.println("Update-->getStatus: " + 
							status.getId() + " " +
							status.getContent() +
							" type: " + status.getType() + 
							" name: " + status.getUser());
					recents.add(status);
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
		return recents;
}	
	
	public int deleteStatus(DataSource ds, int statusId){
		String updateQuery = 
			"DELETE from activities " +
			"WHERE id=?";
		Statement stmt = null;
		PreparedStatement ps = null;
		int deleteStatus = -1;
		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			ps = conn.prepareStatement(updateQuery);
			ps.setInt(1, statusId);
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
	
	public void setFbid(DataSource ds, String fbid, String email){
//		MySQLクエリー
		String updateQry = 
			"UPDATE students SET fbid=?" +
			"WHERE email=?";

		Statement stmt = null;
		PreparedStatement ps = null;
		
		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			ps = conn.prepareStatement(updateQry);
			ps.setString(1, fbid);
			ps.setString(2, email.toLowerCase());
			ps.executeUpdate();
		} catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (ps != null) ps.close();} catch (SQLException e) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}		
	}
}