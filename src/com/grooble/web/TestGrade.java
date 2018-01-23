package com.grooble.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.grooble.model.MarkTest;
import com.grooble.model.Member;
import com.grooble.model.Person;
import com.grooble.model.Result;
import com.grooble.model.Status;
import com.grooble.model.Test;
import com.grooble.model.Update;

/**
*	テスト終わったら、MarkTestをインスタンシエートして、呼び出す。
*	結果次第、jspのheaderアトリビュートを設定する。
*	ユーザーはログインされていたら、updateメソッドを使う、
*	ログインしてない場合、markallメソッドを使う。
*/
@SuppressWarnings("serial")
public class TestGrade extends HttpServlet {

	//	データソースを初期化する
	private DataSource ds;
	public void init() throws ServletException {
		try {
			ds = (DataSource) getServletContext().getAttribute("DBCPool");
		} catch(Exception e) {
			throw new ServletException(e.getMessage());
		}
	}

/**
*	正しい答えとユーザー答えたアレイをMarkTestクラスを使ってテスト成績を出す。
*	ユーザーがログインしていたらupdateメソッドを使って成績とテスト問題を
*	データベースに入れる。
*	ログインしてないユーザーの場合、markAllメソッドを使って成績を計算する
*	result2.jspで使うheaderをセッションにつける。
*/
	public void doPost(HttpServletRequest request, 
							HttpServletResponse response)
							throws IOException, ServletException{
	
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		
		int[] correct = (int[]) session.getAttribute("correct");
		int[] answers = (int[]) session.getAttribute("answers");
		Integer friendPercent = (Integer)session.getAttribute("friendPercent");
		Test test = (Test)session.getAttribute("test");
		Person p = (Person) session.getAttribute("user");

		if (null == test){
			
		} else if (null == p){
			
		}else {
			
		}
		// whether the test is processed correctly or there is
		// an error, we'll still get rid of this attribute
		session.removeAttribute("friendPercent");

		List<Status> news = null;
		Integer statusIndex = new Integer(0);
		
		// Calculate test grade
		MarkTest marker = new MarkTest(ds);
		int percent = 0;
		percent = marker.markAll(answers, correct);

		session.removeAttribute("statusIndex");
		session.removeAttribute("news"); //getting new news below
		
		int testId = 0;
		test.setSelected(answers);
		
		Result added = marker.update(p, test); 
		testId = added.getTestId();
		
		Member m = new Member(ds);
		Integer tests = new Integer(0);
		tests = m.testCount(p.getId());
		session.setAttribute("testCount", tests);
		
		// Set result to status
		Status st = new Status(
				p.getId(), "test", ""+testId
				);
		Update upd = new Update();
		upd.setStatus(ds, st);
		news = upd.getStatus(ds, p.getId(), 0, 20);
		statusIndex = news.size(); // may be less than 20 if fewer in DB!	
		
		//	英語のヘダー
		String header = "";
		if(friendPercent != null){
//			int friendScore = Integer.parseInt(friendPercent);
			if(percent > friendPercent)
				header = "You won the challenge!";
			else if(percent == friendPercent)
				header = "You drew your friend's score.";
			else
				header = "Sorry, your challenge failed.";
			session.removeAttribute("friendPercent");
		}
		else{			
			if (percent >= 90)
				header = "Fantastic!";
			else if ((percent >= 80) && (percent < 90))
				header = "Great Job!";
			else if ((percent >= 70) && (percent < 80))
				header = "Not bad,";
			else if ((percent >= 60) && (percent < 70))
				header = "I've seen better.";
			else if ((percent >= 50) && (percent < 60))
				header = "Passed by the skin of your teeth!";
			else if ((percent >= 40) && (percent < 50))
				header = "Close, but no cigar,";
			else
				header = "Ouch!";
		}
			
		session.setAttribute("statusIndex", statusIndex);
		session.setAttribute("news", news);
		session.setAttribute("result", percent);
		session.setAttribute("user", p);
		request.setAttribute("header", header);
		
		RequestDispatcher view = request.getRequestDispatcher("Result.do");
		view.forward(request, response);
	}
}