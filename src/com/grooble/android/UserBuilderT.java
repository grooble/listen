package com.grooble.android;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import com.grooble.model.Friender;
import com.grooble.model.Member;
import com.grooble.model.Person;
import com.grooble.model.Result;

public class UserBuilderT {
    private final static String TAG = "UserBuilderT";
    private final static int TEST_SIZE = 5;
    private Person user;
    private String password;
    private DataSource datasource;
    private Member memberTools;
    private Friender friender;
    private Connection conn;

    public UserBuilderT(Person user, String password, DataSource datasource){
        this.user = user;
        this.password = password;
        this.datasource = datasource;
        this.memberTools = new Member();
        this.friender = new Friender();
    }
    
    protected List<Person> getUserFriends(){

        // ArrayLists to hold friends and pending friends
        ArrayList<Person> friends;
                
        int userId = user.getId();

        // get list of user friends
        friends = new ArrayList<Person>();
        ArrayList<Integer> friendIds = (ArrayList<Integer>)friender.getFriends(datasource, userId);
        Iterator<Integer> i = friendIds.iterator();
        while (i.hasNext()){
            Person foundFriend = memberTools.verify(datasource, (Integer)i.next());
            friends.add(foundFriend);
        }
        
        return friends;
    }
    
    protected List<Person> getUserPending(){
        ArrayList<Person> pending;
        
        // get list of pending friend requests
        ArrayList<Integer> pendingIds = (ArrayList<Integer>)friender.getPending(datasource, user.getId());
        if((pendingIds != null) && (pendingIds.size() != 0)){
            pending = new ArrayList<Person>();
            Iterator<Integer> it = pendingIds.iterator();
            while(it.hasNext()){
                Person pendant = memberTools.verify(datasource, (Integer)it.next());
                pending.add(pendant);
            }
        }
        // or set to null if none pending
        else {
            pending = null;
        }
        return pending;
    }
    
    List<Result> fetchResults(){
            ResultSet rs = null;
            Statement stmt = null;
            //MySQLクエリー
            String selectQry = "SELECT * "
                    + "FROM tests " 
                    + "WHERE (user=? AND choice1!=0) "
                    + "ORDER BY test_id DESC, qn_index "
                    + "LIMIT 100"; // = 5 test question items * 20 tests
            List<Result> results = new ArrayList<Result>();
            Result resultRow = new Result();
            PreparedStatement ps = null; 
            
            try{
                conn = datasource.getConnection();
                stmt = conn.createStatement();
                stmt.executeUpdate("USE teacher");
                ps = conn.prepareStatement(selectQry);
                ps.setInt(1, user.getId());
                System.out.println(TAG + " FetchResults " + ps.toString());
                rs = ps.executeQuery();
                List<Integer> testIds =  new ArrayList<Integer>();
                List<Integer> correct =  new ArrayList<Integer>();
                List<Integer> answered = new ArrayList<Integer>();
                // results may contain questions of multiple level.
                // set level as the highest value.
                int level = 0;
                
                int counter = 0;
                while(rs.next()){
                    testIds.add(rs.getInt(6));
                    testIds.add(rs.getInt(7));
                    testIds.add(rs.getInt(8));
                    testIds.add(rs.getInt(9));
                    correct.add(rs.getInt(10));
                    answered.add(rs.getInt(11));
                    // Check the level for this part of the question and 
                    // if it is greater than the previous question units, set as level. 
                    int currentQnLevel = rs.getInt(12);
                    if (currentQnLevel > level){
                        level = currentQnLevel;
                    }
                    counter++;
                    if(counter == TEST_SIZE){
                        resultRow.setTestId(rs.getInt(3));
                        resultRow.setQuestions(testIds);
                        resultRow.setCorrect(correct);
                        resultRow.setAnswered(answered);
                        resultRow.setDate(rs.getTimestamp(2));
                        String comment = rs.getString(13);
                        if(comment == null){
                            comment = "unknown";
                        }
                        resultRow.setComment(comment);
                        results.add(resultRow);
                        counter = 0;
                        testIds =  new ArrayList<Integer>();
                        correct =  new ArrayList<Integer>();
                        answered = new ArrayList<Integer>();
                        resultRow = new Result();
                    }
                }
            } catch(Exception ex){
                ex.printStackTrace();
            }
            finally {
                try {if (rs != null) rs.close();} catch (SQLException e) {}
                try {if (stmt != null) stmt.close();} catch (SQLException e) {}
                try {if (ps != null) ps.close();} catch (SQLException e ) {}
                try {if (conn != null) conn.close();} catch (SQLException e) {}
            }
            System.out.println(TAG + " results.size(): " + results.size());
            if(results.size()>0){
                System.out.println(TAG + " date: " + results.get(0).getDate());
            }
            
            return results;
        }
}