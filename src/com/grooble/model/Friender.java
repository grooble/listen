package com.grooble.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.*;
//import com.grooble

public class Friender {
	private Connection con = null;
	
	public int makeFriend(DataSource ds, int p1, int p2){
		boolean aFriend = isFriend( ds, p1, p2);
		System.out.println("Friender-->aFriend: " + aFriend);
		Statement st = null;
		PreparedStatement ps = null;
		int status = -1;// -1 denotes failure to add friend
						// this will be amended to success in the try.
		
		if(!aFriend){
			String sqlInsert = "INSERT into friends (user, friend) ";
			String sqlData = "VALUES ( ?, ?)";
			
			try{
				System.out.println("Friender->makeFriend: inside try");
				con = ds.getConnection();
				st = con.createStatement();
				st.executeUpdate("USE teacher");
				String insertQry = sqlInsert + sqlData;
				ps = con.prepareStatement(insertQry);
				if (p1 < p2){
				    ps.setInt(1, p1);
				    ps.setInt(2, p2);
				}
				else{
                    ps.setInt(1, p2);
                    ps.setInt(2, p1);				    
				}
				ps.executeUpdate();
				status = 0; // to indicate success in adding friendship
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			finally {
				try {if (st != null) st.close();} catch (SQLException e) {}
				try {if (ps != null) ps.close();} catch (SQLException e) {}
				try {if (con != null) con.close();} catch (SQLException e) {}
			}		
		}
		System.out.println("Friender-->status(makeFriend): " + status);
		return status;
	}
	
	public boolean isFriend(DataSource ds, int p1, int p2){
		boolean isFriend = false;
		System.out.println("Friender... isFriend args: " + p1 + ", " + p2);

		// checks for user friend of friend and vise versa
		String sqlIsFriend = "SELECT friend FROM friends " + 
		        "WHERE (" +
		        "(user=? AND friend=?) " +
		        "OR " +
		        "(user=? AND friend=?))";
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		System.out.println("Friender...isFriend before try statement");
		
		try{
		    con = ds.getConnection();
		    st = con.createStatement();
		    st.executeUpdate("USE teacher");
		    ps = con.prepareStatement(sqlIsFriend);
		    ps.setInt(1, p1);
		    ps.setInt(2, p2);
		    ps.setInt(3, p2);
		    ps.setInt(4, p1);
		    System.out.println("Friender...isFriend ps: " + ps.toString());
		    rs = ps.executeQuery();
		    if(rs.next()){
		        System.out.println("Friender->isFriend: rs has a next");
		        isFriend = true;	// rs is non null and friendship exists
		    }
		}
		catch(Exception ex){
		    ex.printStackTrace();
		}
		finally {
		    try {if (st != null) st.close();} catch (SQLException e) {}
		    try {if (ps != null) ps.close();} catch (SQLException e) {}
		    try {if (con != null) con.close();} catch (SQLException e) {}
		}		
		
		return isFriend;
	}
	
	public int removeFriend(DataSource ds, int p1, int p2){
		int defriended = -1;
		
		String sqlDel = "DELETE from friends WHERE ((";
		String sqlAnd1 = "user= ? AND friend= ? ";
		String sqlAnd2 = "user= ? AND friend= ? ";
		String sqlOr = ") OR (";
		String sqlClose = "))";
		String deleteQry = sqlDel + sqlAnd1 + sqlOr + sqlAnd2 + sqlClose;
		Statement st = null;
		PreparedStatement ps = null;
		
		try{
			con = ds.getConnection();
			st = con.createStatement();
			st.executeUpdate("USE teacher");
			ps = con.prepareStatement(deleteQry);
			ps.setInt(1, p1);
			ps.setInt(2, p2);
			ps.setInt(3, p2);
			ps.setInt(4, p1);
			defriended = ps.executeUpdate();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (st != null) st.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e) {}
			try {if (con != null) con.close();} catch (SQLException e) {}
		}		
		return defriended;
	}
	
	public List<Integer> getFriends(DataSource ds, int p){
		ArrayList<Integer> al = new ArrayList<Integer>();
		
		String sqlUnion1 = "(select friend from friends where user= ?)";
		String sqlUnion2 = "(select user from friends where friend= ?)";
		String unionQry = sqlUnion1 + " UNION " + sqlUnion2;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			con = ds.getConnection();
			st = con.createStatement();
			st.executeUpdate("USE teacher");
			ps = con.prepareStatement(unionQry);
			ps.setInt(1, p);
			ps.setInt(2, p);
			rs = ps.executeQuery();
			if(rs!=null){
//				rs.first();
				while(rs.next()) {
					int friendId = rs.getInt(1);
					al.add(friendId);
					System.out.println("Friender-->friend: " + friendId);
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (st != null) st.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e) {}
			try {if (con != null) con.close();} catch (SQLException e) {}
		}				
		return al;
	}
	
	/*
	 * adds a request to the pending table
	 * checks isPending to avoid adding duplicate
	 */
	/*
	 * TODO stop the add if friend already exists
	 */
	public int addToPending(DataSource ds, int requester, int appendee){
		boolean appended = isPended( ds, requester, appendee);
		System.out.println("Friender-->addPending: " + appended);
		Statement st = null;
		PreparedStatement ps = null;
		int status = -1;// -1 denotes failure to append
						// this will be ammended to success in the try.
		
		if(!appended){
			String sqlInsert = "INSERT into pending (requester, approver) ";
			String sqlValues = "VALUES ( ? , ? )";
			
			try{
				System.out.println("Friender->addPending: inside try");
				con = ds.getConnection();
				st = con.createStatement();
				st.executeUpdate("USE teacher");
				String insertQry = sqlInsert + sqlValues;
				ps = con.prepareStatement(insertQry);
				ps.setInt(1, requester);
				ps.setInt(2, appendee);
				ps.execute();
				status = 0; // to indicate success in adding pending
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			finally {
				try {if (st != null) st.close();} catch (SQLException e) {}
				try {if (ps != null) ps.close();} catch (SQLException e) {}
				try {if (con != null) con.close();} catch (SQLException e) {}
			}		
		}
		System.out.println("Friender-->status(add pending): " + status);
		return status;
	}
	
	/*
	 * check for existing pending request
	 * used in the addPending function to avoid 
	 * adding duplicate requestss
	 */
	public boolean isPended(DataSource ds, int requester, int approver){
		boolean pended =false;
		String sqlIsPended = "SELECT approver FROM pending " + 
			"WHERE " +
			"((requester=?) AND (approver=?))";
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			con = ds.getConnection();
			st = con.createStatement();
			st.executeUpdate("USE teacher");
			ps = con.prepareStatement(sqlIsPended);
			ps.setInt(1, requester);
			ps.setInt(2, approver);
			rs = ps.executeQuery();
			if(rs.next()){
				System.out.println("Friender->isPended: rs has a next");
				pended = true;	// rs is non null and pending exists
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (st != null) st.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e) {}
			try {if (con != null) con.close();} catch (SQLException e) {}
		}		
	return pended;
	}
	
	/*
	 * returns a list of pending request ids 
	 * searched on the id of the logged in user 
	 */
	public List<Integer> getPending(DataSource ds, int user){
		System.out.println("Friender-->getPending user: " + user);
		ArrayList<Integer> pendingIds = new ArrayList<Integer>();
		int testid = user;
		System.out.println("Friender-->getPending userid:" + testid);
		String sqlSelect = 
			"SELECT requester FROM pending " +
			"WHERE approver=?";
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			con = ds.getConnection();
			st = con.createStatement();
			ps = con.prepareStatement(sqlSelect);
			st.executeUpdate("USE teacher");
			ps.setInt(1, user);
			System.out.println("Friender-->getPending ps:" + ps.toString());
			rs = ps.executeQuery();
			if(rs!=null){
				while(rs.next()) {
					int pendingId = rs.getInt(1);
					pendingIds.add(pendingId);
					System.out.println("Friender-->pending: " + pendingId);
				}
			}
			else{
				System.out.println("Friender-->getPending resultSet was null");
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (st != null) st.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e) {}
			try {if (con != null) con.close();} catch (SQLException e) {}
		}				
		return pendingIds;
	}
	
	
	/*
	 * Removes the request from the pending table
	 * search by requester and approver
	 */
	public int deleteRequest(DataSource ds, int requester, int approver){
		int delPending = -1;
		
		String deleteQry= "DELETE from pending WHERE " + 
			"((requester=? AND approver=?) OR (requester=? AND approver=?))";
		
		Statement st = null;
		PreparedStatement ps = null;
		try{
			con = ds.getConnection();
			st = con.createStatement();
			ps = con.prepareStatement(deleteQry);
			ps.setInt(1, requester);
			ps.setInt(2, approver);
            ps.setInt(3, approver);
            ps.setInt(4, requester);
			st.executeUpdate("USE teacher");
			delPending = ps.executeUpdate();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (st != null) st.close();} catch (SQLException e) {}
			try {if (ps != null) ps.close();} catch (SQLException e) {}
			try {if (con != null) con.close();} catch (SQLException e) {}
		}		
		System.out.println("Friender->deleteRequest: " + delPending);
		return delPending;
	}
}
