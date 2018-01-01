package com.grooble.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.json.JSONArray;
import com.grooble.model.JSONMaker;
import com.grooble.model.Question;
import com.grooble.model.Test;
import com.grooble.model.TestMakerA;

/**
 * Servlet implementation class SendJSONTest
 */
@SuppressWarnings("serial")
public class AndroidTest extends HttpServlet {
    private DataSource datasource;

    //  コネクションプールからコネクションを取得
    public void init() throws ServletException {
        try {
            datasource = (DataSource) getServletContext().getAttribute("DBCPool");
        } catch(Exception e) {
            throw new ServletException(e.getMessage());
        }
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// this is the Ajax call so send the JSON to page
		//response.setContentType("text/html");
		response.setContentType("application/json");
		
		// get request parameters
		String difficulty = request.getParameter("difficulty");
		String category = request.getParameter("category");
		String exclusion = request.getParameter("exclusion");
		String ajax = request.getParameter("ajax");
		System.out.println("SendJSONTest->category: " + category);
		System.out.println("SendJSONTest->dificulty: " + difficulty);
		System.out.println("SendJSONTest->exclusion: " + exclusion);
		System.out.println("SendJSONTest->ajax: " + ajax);
		
		// inialize test variables
		Test test = null;
		List<Question[]> testQuestions = null;
		int[] correct = null;
		
        TestMakerA maker = new TestMakerA();
		test = maker.makeTest(datasource, difficulty, category, 5, null);
		testQuestions = test.getTest();
		correct = test.getCorrect();
		
		System.out.println("AndroidTest: test qns--> " + test.getTest().size());
		System.out.println("AndroidTest: qn--> " + test.getTest().get(0).toString());
		System.out.print("AndroidTest: correct--> ");
		for(int i=0; i<correct.length; i++){
		    System.out.print(correct[i] + ", ");
		}
		System.out.println();
		
		JSONMaker jm = new JSONMaker();
		JSONArray JTest = jm.toJSON(testQuestions, correct);
		System.out.println("AndroidTest: " + JTest.toString());
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.write(JTest.toString());
	}
}
