package com.grooble.model;

import java.util.*;
import java.sql.*;
import javax.sql.*;

/**
*	データソースとdifficultyのパラメーターを使って
*	データベースを二回アクセスする
*	一回目にcategory(問題も教科)リストを作る。長さはテスト問題の数です。
*	二回目に、そのリストを使ってcategory当りに、そのcategory のQuestionを４つ取得。
*	４つのQuestionをQuestionアレイにして、アレイをテストリストに入れる
*	Questionアレイは一個の問題です。
*/
public class TestMakerA {
    private static final int TEST_SIZE = 5;
    private static final int QUESTION_SIZE = 4;
	private Test test = new Test();
	private Connection conn = null;


	public Test makeTest(DataSource ds, String difficulty, String category, 
			int size, List<Question> exclusion) {
		
		final int SIZE = size;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		Statement stmt = null;
//		PreparedStatement ps = null;

		String diffSQL = "";
//		String toExecute = "";
		StringBuilder exSQL = new StringBuilder();
		
		if((exclusion != null)&&(exclusion.size()!=0)){
			Iterator<Question> i = exclusion.iterator();
			exSQL.append(" AND (name NOT IN (");
			while(i.hasNext()){
				Question q = (Question)i.next();
				exSQL.append("'");
				exSQL.append(q.getWord());
				exSQL.append("',");
			}
			int l = exSQL.lastIndexOf(",");
			exSQL.deleteCharAt(l);
			exSQL.append("))");
			System.out.println("TestMakerA->exSQL: " + exSQL);
		}
		
		if(difficulty.equals("easy")){
			diffSQL = "((easy=1) OR (medium=0) OR (hard=0))";
		}else
		if(difficulty.equals("medium")){
			diffSQL = "((easy=0) OR (medium=1) OR (hard=0))";
		}else
		if(difficulty.equals("hard")){
			diffSQL = "((easy=0) OR (medium=0) OR (hard=1))";
		}else{ //default case
			diffSQL = "((easy=1) OR (medium=1) OR (hard=1))";
		}
				
//		データベースを一回目にアクセスするのSQL
//		ランドムにcategory(教科)を決める
		String sql1a = "SELECT category FROM questions WHERE (";
		String sql1b = " ) GROUP BY category ORDER BY RAND()LIMIT ";

//		データベースを一回目にアクセスするのSQL
//		categoryリストのメンバー当りに、４つのQuestionを選択して、ランドムにQuestionアレイに入れる
		String sql2a = 
			"SELECT id, name, image, sound, category, level " +
			"FROM questions " +
			"WHERE (";
		String 	sqlLimit = ") ORDER BY RAND() LIMIT 4";

//		テストとcategoryリストを初期化
		List<Question[]> testQuestions = new ArrayList<Question[]>();
		List<String> categories = new ArrayList<String>();
		
//		データベースをアクセス
		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			
			if(category.equals("random")){
				String execSql = sql1a + diffSQL + sql1b + SIZE;
				System.out.println("TestmakerA-->sql b4 exec: " + execSql);
				rs1 = stmt.executeQuery(execSql);
				while(rs1.next()){
					categories.add(rs1.getString(1));
				}
				System.out.println("TestmakerA-->categories: " + categories.toString());
			} else {
				for(int i=0; i<SIZE; i++){
					categories.add(category);
				}
				System.out.println("TestmakerA-->categories: " + categories.toString());
			}

			for (int i = 0; i < SIZE; i++){		//	問題の数。
				String topic = categories.get(i);
				System.out.println("TestmakerA-->got topic from cats: " + topic);
				Question[] multiQn = new Question[4];
				String qnSql = sql2a + diffSQL + "AND category='" + topic +
								"' " + exSQL + sqlLimit;
				System.out.println("TestmakerA-->qnSql: " + qnSql);
				rs2 = stmt.executeQuery(qnSql);

				int j = 0;
//				問題に必要なデータ：　単語、画像ファイル（ストリング）、サウンドファイル、教科を取得
				while(rs2.next()){
					Question aQuestion = new Question();
					aQuestion.setQuestionId(rs2.getInt(1));
					aQuestion.setWord(rs2.getString(2));
					aQuestion.setImage(rs2.getString(3));
					aQuestion.setSound(rs2.getString(4));
					aQuestion.setCategory(rs2.getString(5));
					aQuestion.setLevel(rs2.getInt(6));
					multiQn[j++] = aQuestion;
				}
				testQuestions.add(multiQn);
			}
		conn.close();
		}
		catch(Exception ex){ex.printStackTrace();}
		finally {
			try {if (rs1 != null) rs1.close();} catch (SQLException e) {}
			try {if (rs2 != null) rs2.close();} catch (SQLException e) {}
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}
		int length = testQuestions.size();
		int[] correct = new int[length];		//	正しい答え
		for (int i = 0; i < length; i++){
			int rand = (int) (Math.random()*4);
			correct[i] = rand;			//	ランダムに正解を決める
		}
		int[] selected = new int[length];
		for(int i=0; i<selected.length; i++){
			selected[i] = -1;
		}
		test.setTest(testQuestions);
		test.setCorrect(correct);
		test.setSelected(selected);
		return test;
	}
	
	public Test getTest(DataSource ds, int testIndex) {
	    System.out.println("TestMakerA: getTest, testIndex(id): " + testIndex);
		Statement stmt1 = null;
		ResultSet rs1 = null;
		
		Statement stmt2 = null;
		ResultSet rs2 = null;
		
		String sql1Params = "SELECT choice1, choice2, choice3," + 
			" choice4, correct, given ";
		String sql1Tbl = " FROM tests WHERE ";
		String sql1Where = "test_id= '";
		String sql1Bracket = "'";
		
		String dropTbl = "DROP TABLE IF EXISTS testids";
		String createTbl = "CREATE TABLE testids(id int)";
		String tblInsert = "INSERT INTO testids VALUES";
		
		List<Question[]> testQuestions = new ArrayList<Question[]>();
		List<Integer> correctList = new ArrayList<Integer>();
		List<Integer> selectedList = new ArrayList<Integer>();
		
		try{
			conn = ds.getConnection();
			stmt1 = conn.createStatement();
			stmt1.executeUpdate("USE teacher");
			rs1 = stmt1.executeQuery(sql1Params + sql1Tbl + 
				sql1Where + testIndex + sql1Bracket);
			String allChoices = "";
			while(rs1.next()){
				for (int i = 1; i < 5; i++) {
					allChoices = allChoices + "(" + rs1.getInt(i) + "),";
				}
				correctList.add(rs1.getInt(5));
				selectedList.add(rs1.getInt(6));
			}
			int commaPos = allChoices.lastIndexOf(',');
			String inQuery = allChoices.substring(0, commaPos);
			
			stmt2 = conn.createStatement();
			stmt2.executeUpdate("USE teacher");
			stmt2.executeUpdate(dropTbl);
			stmt2.executeUpdate(createTbl);
			stmt2.executeUpdate(tblInsert + inQuery);

			rs2 = stmt2.executeQuery("select * from questions" + 
					" join testids on questions.id=testids.id");
			int rowcount = 0;
			if (rs2.last()) {
			    rowcount = rs2.getRow();
			    rs2.beforeFirst(); 
			}
			System.out.println("TMA: rs2 length..." + rowcount);
			// rowcount starts from  0
			if(rowcount == (TEST_SIZE * QUESTION_SIZE)){
			    int counter = 0;
			    Question[] multiqn = new Question[QUESTION_SIZE];
			    while (rs2.next()){
			        Question qn = new Question();
                    qn.setQuestionId(rs2.getInt(1));
                    qn.setWord(rs2.getString(2));
                    qn.setImage(rs2.getString(3));
                    qn.setSound(rs2.getString(4));
                    qn.setCategory(rs2.getString(5));
                    qn.setLevel(rs2.getInt(6));
                    System.out.println("TMA: Qn[" + counter + "]..." + qn.getWord());
			        multiqn[counter] = qn;
			        counter++;
			        if(counter%4==0){
			            testQuestions.add(multiqn);
			            multiqn= new Question[QUESTION_SIZE];
	                    System.out.println("TMA: multi..." + multiqn.length);
			            counter = 0;
			        }
			    }
			}			
			conn.close();
		}
		catch(Exception ex){ex.printStackTrace();}
		finally {
			try {if (rs1 != null) rs1.close();} catch (SQLException e) {}
			try {if (rs2 != null) rs1.close();} catch (SQLException e) {}
			try {if (stmt1 != null) stmt1.close();} catch (SQLException e) {}
			try {if (stmt2 != null) stmt2.close();} catch (SQLException e) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}
		int l = correctList.size();
		int[] correct = new int[l];
		int[] selected = new int[l];
		for (int i=0; i<l; i++){
			correct[i] = (Integer)correctList.get(i);
			selected[i] = (Integer)selectedList.get(i);
		}
		test.setCorrect(correct);
		test.setSelected(selected);
		test.setTest(testQuestions);
		return test;
	}
	
	public Test practise(DataSource ds, String topic){
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		Statement stmt = null;
		int limit = 0;
		List<Question[]> testQuestions = new ArrayList<Question[]>();

		try{
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate("USE teacher");
			rs1 = stmt.executeQuery(
					"SELECT COUNT(category) AS count " +
					"FROM questions "+ 
					"WHERE category = '" + topic + "'");
			
			int count = 0;
			while(rs1.next()){
				count = rs1.getInt(1);
			}
//			System.out.println("TestMakerA->practise->count: " + count);
			if(count < 12){
				limit = 8;
			} else {
				limit = 12;
			}
			
			rs2 = stmt.executeQuery(
					"SELECT id, name, image, sound, category, level " +
					"FROM questions " +
					"WHERE category = '" + topic + "' " +
					"ORDER BY rand() " +
					"LIMIT " + limit);
//			System.out.println("TestMakerA->practise->limit: " + limit);
			
			int qnCount = (int)limit/4;
//			System.out.println("TestMakerA->practise->qnCount: " + qnCount);
			
			rs2.first();
			for (int i = 0; i < qnCount; i++){		//	問題の数。
				Question[] multiQn = new Question[4];
				for(int j = 0; j<4; j++){
//				問題に必要なデータ：　単語、画像ファイル（ストリング）、サウンドファイル、教科を取得
						Question aQuestion = new Question();
						aQuestion.setQuestionId(rs2.getInt(1));
						aQuestion.setWord(rs2.getString(2));
						aQuestion.setImage(rs2.getString(3));
						aQuestion.setSound(rs2.getString(4));
						aQuestion.setCategory(rs2.getString(5));
						aQuestion.setLevel(rs2.getInt(6));
						multiQn[j] = aQuestion;
						rs2.next();
					}
				testQuestions.add(multiQn);
				}
//			}
		conn.close();
		}
		catch(Exception ex){ex.printStackTrace();}
		finally {
			try {if (rs1 != null) rs1.close();} catch (SQLException e) {}
			try {if (rs2 != null) rs2.close();} catch (SQLException e) {}
			try {if (stmt != null) stmt.close();} catch (SQLException e) {}
			try {if (conn != null) conn.close();} catch (SQLException e) {}
		}
		int length = testQuestions.size();
		int[] correct = new int[length];		//	正しい答え
		for (int i = 0; i < length; i++){
			int rand = (int) (Math.random()*4);
			correct[i] = rand;			//	ランダムに正解を決める
		}
		int[] selected = new int[length];
		for(int i=0; i<selected.length; i++){
			selected[i] = -1;
		}
		test.setTest(testQuestions);
		test.setCorrect(correct);
		test.setSelected(selected);

		return test;
	}
	
/*  Make a 5 question test from five of the questions on 
 *  the givenQns list and other questions from the same
 *  category, with no duplicates.	
 */
	public Test makeTest(DataSource ds, List<Question> givenQns){
		ArrayList<Question> startQns = new ArrayList<Question>();
		for (int i = 0; i < givenQns.size(); i++){
			startQns.add((Question)givenQns.get(i));
		}
		String category = startQns.get(0).getCategory();
		String difficulty = "any";
		
		while(startQns.size()>5){
			int index = (int)(Math.random()*startQns.size());
			startQns.remove(index);
		}
		Test test = this.makeTest(ds, difficulty, category, 5, startQns);
//		System.out.println("TestMakerA->test: " + test.toString());
		int[] correct = new int[5];
		ArrayList<Question[]> questions = (ArrayList<Question[]>)test.getTest();

		for(int i = 0; i < 5; i++){
			correct[i] = (int)(Math.random()*4);
			if(questions.get(i)[correct[i]] != startQns.get(i)){
				questions.get(i)[correct[i]] = startQns.get(i);
			}
		}

		test.setCorrect(correct);
		return test;
	}	
	
	/*
	 * Called by android.GetAllQuestions
	 */
	public List<Question> listQuestions(DataSource ds){
	    ArrayList<Question> questions = new ArrayList<Question>();
        ResultSet rs = null;
        Statement stmt = null;
        try{
            conn = ds.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(
                    "SELECT id, name, image, sound, category, level, easy, medium, hard FROM questions");
            while(rs.next()){
                Question currentQuestion = new Question();
                currentQuestion.setQuestionId(rs.getInt(1));
                currentQuestion.setWord(rs.getString(2));
                currentQuestion.setImage(rs.getString(3));
                currentQuestion.setSound(rs.getString(4));
                currentQuestion.setCategory(rs.getString(5));
                currentQuestion.setLevel(rs.getInt(6));
                String difficulty = "";
                if (rs.getInt(7)==1)
                    difficulty = "easy";
                else 
                    if(rs.getInt(8)==1)
                        difficulty = "medium";
                    else
                        if(rs.getInt(9)==1)
                            difficulty = "hard";
                currentQuestion.setDifficulty(difficulty);
                questions.add(currentQuestion);
            }
            conn.close();
        }
        catch(Exception ex){ex.printStackTrace();}
        finally {
            try {if (rs != null) rs.close();} catch (SQLException e) {}
            try {if (stmt != null) stmt.close();} catch (SQLException e) {}
            try {if (conn != null) conn.close();} catch (SQLException e) {}
        }
        System.out.println("TMA...All qns count: " + questions.size());
        
	    return questions;
	}
}