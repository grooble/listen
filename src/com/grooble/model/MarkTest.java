package com.grooble.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import junit.framework.Assert;

/**
*	markAllとupdateメソッド
*	markallはまだログインしてないユーザーのために、成績を計算する。
*	updateはログインしているユーザーのために、成績を計算して、データベースにインサートクエリーで
*	レコードを入れる。
*/

public class MarkTest {
    
    private DataSource ds;
	private Connection conn;
	private static final String TAG = "MarkTest "; 
	
	public MarkTest(DataSource ds){
	    this.ds = ds;
	}
	
	/*
	 *  This allows android to update the profile screen with the test results, 
	 *  without having to wait for a callback from the server.
	 *  1) Get latest testId and create blank entry in DB.
	 *  2) Return the recovered testId to the app for use when user takes a test.
	 *  3) Note: sometimes testIds will be created but not used.
	 *     TODO: These will need be periodically cleaned up.
	 */
	
	public int getTestId(Person user, String password){
	    
	    Statement stmt = null;
	    PreparedStatement ps = null;
	    String insertQuery = "INSERT INTO tests (test_id, user, qn_index, choice1, choice2, choice3, choice4, correct, given) "
	                         + "VALUES (?, ?, 0, 0, 0, 0, 0, 0, 0)";
	    int newTestId = 0;

	    try{
	        conn = ds.getConnection();
	        stmt = conn.createStatement();
            // get index of this test for insertion
	        String maxTestQry = "SELECT MAX(test_id) AS max FROM tests";
	        ResultSet rsMax = stmt.executeQuery(maxTestQry);
	        if(rsMax.next()){
	            newTestId = rsMax.getInt(1)+1; 
	        }
	        
	        // set newTestId and user id to PreparedStatement 
	        ps = conn.prepareStatement(insertQuery);
	        ps.setInt(1, newTestId);
	        ps.setInt(2, user.getId());
	        System.out.println(TAG + " getTestId() ps: " + ps.toString());
	        
	        stmt.executeUpdate("USE teacher");
	        
            // Insert new placemarker test value into tests in order to use this test_id in the application
            ps.executeUpdate();
	        
	    }
	    catch(Exception ex){
	        ex.printStackTrace();
	    }
	    finally {
	        try {if (stmt != null) stmt.close();} catch (SQLException e) {}
	        try {if (conn != null) conn.close();} catch (SQLException e) {}
	    }    
	    return newTestId;
	}

	
	public int markAll(int[] answers, int[] correct){
		int corr = 0;
//		正しい答えのリストとユーザー答えたリストを比べる。
		for (int i = 0; i < answers.length; i++){
			if (answers[i] == correct[i]){
				corr++;
			}
		}
		int result = (corr*100)/(answers.length);
		return result;
	}
	
	
/**
 *  This is the version of update used for the Android app.
 *  Set completed test to DB including testId and level.
*	テスト全部データベースに入れる。
*	ユーザーは後で前やったテストの問題を確認したり、友達に挑戦したりすることができる。
*/	
	
	public Result update(Person p, Test test, int testId, Timestamp timestamp, int level, String testType){
		
		Statement stmt = null;
		List<Question[]> testQuestions = test.getTest();
		int[] correct = test.getCorrect();
		int[] answers = test.getSelected();
		Date lastUpdateDate = null;
		int score = 0, newTestTotal = 0;
		
		score = calculateScore(correct, answers);
		score = p.getPoints() + score;
		System.out.println(TAG + " score: " + score);
		
		PreparedStatement ps1 = null, ps2 = null, ps3 = null;
		String insertQuery = "";
		String pointsInsertQuery = "UPDATE students SET points = ? WHERE email_hash = ?";
		String pointsSelectQuery = "SELECT points FROM students WHERE email_hash = ?";
		String deleteQuery = "DELETE FROM tests WHERE test_id = ?";
		
		String emailHash = String.valueOf(p.getEmail().hashCode());
		
		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			
			// create and initialize INSERT PreparedStatement
			ps1 = conn.prepareStatement(pointsInsertQuery);
			ps1.setInt(1, score);
			ps1.setString(2,emailHash);
			
			// create and initialize SELECT PreparedStatement to get points
			ps2 = conn.prepareStatement(pointsSelectQuery);
			ps2.setString(1, emailHash);
			
			// create and initialize PreparedStatement for DELETE query for reserved 
			ps3 = conn.prepareStatement(deleteQuery);
			ps3.setInt(1, testId);
			
            System.out.println(TAG + " INSERT query: " + ps1.toString());
			System.out.println(TAG + " SELECT query: " + ps2.toString());
			System.out.println(TAG + " DELETE query: " + ps3.toString());
            
			stmt.executeUpdate("USE teacher");

			// テストの問題を一個ずつうインサートクエリーでデータベースに入れる。
			for (int i = 0; i < testQuestions.size(); i++){
				Question[] qnArr = testQuestions.get(i);
				insertQuery = "INSERT INTO tests(test_id, "
				                              + "date_taken, "
				                              + "user, "
				                              + "qn_index, "
				                              + "choice1, "
				                              + "choice2, "
				                              + "choice3, "
				                              + "choice4, "
				                              + "correct, "
				                              + "given, "
				                              + "level, "
				                              + "comment) "
					        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement insert = conn.prepareStatement(insertQuery);
				insert.setInt(1, testId);
				insert.setTimestamp(2, timestamp);
				insert.setInt(3, p.getId());
				insert.setInt(4, i);
				insert.setInt(5, qnArr[0].getQuestionId());
                insert.setInt(6, qnArr[1].getQuestionId());
                insert.setInt(7, qnArr[2].getQuestionId());
                insert.setInt(8, qnArr[3].getQuestionId());
                insert.setInt(9, correct[i]);
                insert.setInt(10, answers[i]);
                insert.setInt(11, level);
                insert.setString(12, testType);
				insert.executeUpdate();
			}
			
			// Get TIMESTAMP of last inserted question element by this user
			String timestampQuery = 
			        "SELECT date_taken, user FROM tests "
			        + "WHERE user = ? "
			        + "ORDER BY date_taken DESC "
			        + "LIMIT 1";
			PreparedStatement timePS = conn.prepareStatement(timestampQuery);
			timePS.setInt(1, p.getId());
			ResultSet lastUpdate = timePS.executeQuery();
			if (lastUpdate.next()){	    
			    lastUpdateDate = lastUpdate.getTimestamp(1);
			}
			
			System.out.println("MarkTest... Date: " + lastUpdateDate.toString());

			// update points to user
			ps1.executeUpdate();
			
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		finally {
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}
		
		// create Result to return
		// get testQuestion ids
		List<Integer> questionIds = new ArrayList<Integer>();
		for(Question[] qnBlock : testQuestions){
		    for(int i = 0; i < 4; i++){
		        questionIds.add(qnBlock[i].getQuestionId());
		    }
		}
		System.out.println(TAG + " questionIds.size(): " + questionIds.size());

		List<Integer> correctList = new ArrayList<Integer>();
		for(int j = 0; j < correct.length; j++){
		    correctList.add(correct[j]);
		}
        System.out.println(TAG + " correctList.size(): " + correctList.size());
        
        List<Integer> answeredList = new ArrayList<Integer>();
        for(int k = 0; k < answers.length; k++){
            answeredList.add(answers[k]);
        }
        System.out.println(TAG + " answeredList.size(): " + answeredList.size());

        Result result = new Result(newTestTotal, lastUpdateDate, questionIds, correctList, answeredList, null);
		
	// Return the just completed test as a Result
        System.out.println(TAG + " result: " + result.toJson().toString());
	
	return result;
	}


	// Version of the update method that returns a test_id integer
    public int update(Person p, Test test, Timestamp timestamp, int level, String testType){
        Statement stmt = null;
        List<Question[]> testQuestions = test.getTest();
        int[] correct = test.getCorrect();
        int[] answers = test.getSelected();
        int score = 0;
        int testId = 0;
        
        score = calculateScore(correct, answers);
        score = p.getPoints() + score;
        System.out.println(TAG + "update->score: " + score);
        
        PreparedStatement ps1 = null, ps2 = null, ps3 = null;
        String insertQuery = "";
        String pointsInsertQuery = "UPDATE students SET points = ? WHERE email_hash = ?";
        String pointsSelectQuery = "SELECT points FROM students WHERE email_hash = ?";
        String maxIdQuery = "SELECT MAX(test_id) FROM tests";
        
        try{
            conn = ds.getConnection();
            stmt = conn.createStatement();
            
            // create and initialize INSERT PreparedStatement
            // lookup email hash
            String hashedEmail = String.valueOf(p.getEmail().hashCode());

            ps1 = conn.prepareStatement(pointsInsertQuery);
            ps1.setInt(1, score);
            ps1.setString(2,hashedEmail);
            
            // create and initialize SELECT PreparedStatement to get points
            // lookup email_hash
            ps2 = conn.prepareStatement(pointsSelectQuery);
            ps2.setString(1, hashedEmail);

            // create and initialize PreparedStatement to get max test id
            ps3 = conn.prepareStatement(maxIdQuery);
            
            //System.out.println(TAG + " INSERT query: " + ps1.toString());
            //System.out.println(TAG + " SELECT query: " + ps2.toString());
            //System.out.println(TAG + " TEST_ID query: " + ps3.toString());
            
            stmt.executeUpdate("USE teacher");
            
            // Fetch highest test id for use in insert
            ResultSet testRS = ps3.executeQuery();
            while(testRS.next()){
                testId = testRS.getInt(1);
            }
            Assert.assertTrue(testId > 0);
            
            // Insert question items into DB one at a time
            for (int i = 0; i < testQuestions.size(); i++){
                Question[] qnArr = testQuestions.get(i);
                insertQuery = "INSERT INTO tests(test_id, "
                        + "date_taken, "
                        + "user, "
                        + "qn_index, "
                        + "choice1, "
                        + "choice2, "
                        + "choice3, "
                        + "choice4, "
                        + "correct, "
                        + "given, "
                        + "level, "
                        + "comment) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insert = conn.prepareStatement(insertQuery);
                insert.setInt(1, testId);
                insert.setTimestamp(2, timestamp);
                insert.setInt(3, p.getId());
                insert.setInt(4, i);
                insert.setInt(5, qnArr[0].getQuestionId());
                insert.setInt(6, qnArr[1].getQuestionId());
                insert.setInt(7, qnArr[2].getQuestionId());
                insert.setInt(8, qnArr[3].getQuestionId());
                insert.setInt(9, correct[i]);
                insert.setInt(10, answers[i]);
                insert.setInt(11, level);
                insert.setString(12, testType);
                insert.executeUpdate();
            }
            
            // update points to user
            ps1.executeUpdate();
            
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        finally {
            try {if (stmt != null) stmt.close();} catch (SQLException e) {}
            try {if (conn != null) conn.close();} catch (SQLException e) {}
        }
                
        return testId;
	        
	    }

	
	
	/*
	 * Version for the webapp:
	 * Add completed test to user and return result
	 */
	public Result update(Person p, Test test){
	    
	    Statement stmt = null;
	    List<Question[]> testQuestions = test.getTest();
	    int[] correct = test.getCorrect();
	    int[] answers = test.getSelected();
	    int testId = 0;
	    Date lastUpdateDate = null;
	    int score = 0, newTestTotal = 0;
	    score = calculateScore(correct, answers);
	    score = p.getPoints() + score;
	    System.out.println(TAG + " score: " + score);
	    PreparedStatement ps1 = null, ps2 = null, ps3 = null;
	    String insertQuery = "";
	    String pointsInsertQuery = "UPDATE students SET points = ? WHERE email = ?";
	    String pointsSelectQuery = "SELECT points FROM students WHERE email = ?";
	    
	    try{
	        conn = ds.getConnection();
	        stmt = conn.createStatement();
	        
	        // create and initialize INSERT PreparedStatement
	        ps1 = conn.prepareStatement(pointsInsertQuery);
	        ps1.setInt(1, score);
	        ps1.setString(2,p.getEmail().toLowerCase());
	        
	        // create and initialize SELECT PreparedStatement to get points
	        ps2 = conn.prepareStatement(pointsSelectQuery);
	        ps2.setString(1, p.getEmail().toLowerCase());
	        
	        System.out.println(TAG + " INSERT query: " + ps1.toString());
	        System.out.println(TAG + " SELECT query: " + ps2.toString());
	        
	        stmt.executeUpdate("USE teacher");
	        ResultSet testIdSet = stmt.executeQuery("SELECT MAX(test_id) FROM tests");
	        while(testIdSet.next()){
	            testId = testIdSet.getInt(1);
	        }
	        System.out.println(TAG + " got testId: " + testId);
	        
	        // テストの問題を一個ずつうインサートクエリーでデータベースに入れる。
	        for (int i = 0; i < testQuestions.size(); i++){
	            Question[] qnArr = testQuestions.get(i);
	            insertQuery = "INSERT INTO tests(test_id, user, qn_index, choice1, choice2," +
	                    "choice3, choice4, correct, given) " +
	                    "VALUES ('" + testId + "', '" + p.getId() + "', '" + i + "', '" + 
	                    qnArr[0].getQuestionId() + "', '" + qnArr[1].getQuestionId() + "', '" +
	                    qnArr[2].getQuestionId() + "', '" + qnArr[3].getQuestionId() + "', '" +
	                    correct[i] + "', '" + answers[i] + "')";
	            stmt.executeUpdate(insertQuery);
	        }
	        
	        // Get TIMESTAMP of last inserted question element by this user
	        String timestampQuery = 
	                "SELECT date_taken, user FROM tests "
	                        + "WHERE user = ? "
	                        + "ORDER BY date_taken DESC "
	                        + "LIMIT 1";
	        PreparedStatement timePS = conn.prepareStatement(timestampQuery);
	        timePS.setInt(1, p.getId());
	        ResultSet lastUpdate = timePS.executeQuery();
	        if (lastUpdate.next()){     
	            lastUpdateDate = lastUpdate.getTimestamp(1);
	        }
	        
	        System.out.println("MarkTest... Date: " + lastUpdateDate.toString());
	        
	        // update points to user
	        ps1.executeUpdate();
	        
	    }
	    catch(Exception ex){
	        ex.printStackTrace();
	    }
	    finally {
	        try {if (stmt != null) stmt.close();} catch (SQLException e) {}
	        try {if (conn != null) conn.close();} catch (SQLException e) {}
	    }
	    
	    // create Result to return
	    // get testQuestion ids
	    List<Integer> questionIds = new ArrayList<Integer>();
	    for(Question[] qnBlock : testQuestions){
	        for(int i = 0; i < 4; i++){
	            questionIds.add(qnBlock[i].getQuestionId());
	        }
	    }
	    
	    List<Integer> correctList = new ArrayList<Integer>();
	    for(int j = 0; j < correct.length; j++){
	        correctList.add(correct[j]);
	    }

	    List<Integer> answeredList = new ArrayList<Integer>();
	    for(int k = 0; k < answers.length; k++){
	        answeredList.add(answers[k]);
	    }
	    
	    Result result = new Result(newTestTotal, lastUpdateDate, questionIds, correctList, answeredList, null);
	    
	    // Return the just completed test as a Result
	    System.out.println(TAG + " result: " + result.toJson().toString());
	    
	    return result;
	}


	private int calculateScore(int[] answers, int[] correct){
	    int score = 0;
	    if(answers.length != correct.length){
	        return -1;
	    }
	    else{
	        for(int i = 0; i < answers.length; i++){
	            if(answers[i] == correct[i]){
	                score = score + 2;
	            }
	            else{
	                score = score + 1;
	            }
	        }
	        return score;
	    }
	}
	
}