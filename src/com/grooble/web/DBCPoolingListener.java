package com.grooble.web;

import javax.servlet.*;

import javax.sql.*;
import javax.naming.*;

/**
*	コネクションプールとの接続初期化
*/
public class DBCPoolingListener implements ServletContextListener {
	public void contextInitialized(ServletContextEvent e){
		
		try{
			//JNDIネーミングコンテクストを取得
			Context envCtx = (Context) new InitialContext().lookup("java:comp/env");
		
			//データソース取得
			DataSource ds = (DataSource) envCtx.lookup("jdbc/LDB");
			
			//サーブレトコンテクスト取得
			ServletContext sc = e.getServletContext();
			sc.setAttribute("DBCPool", ds);
		} catch (NamingException ne) {ne.printStackTrace();}
	}
	
	public void contextDestroyed(ServletContextEvent e){
	}
}