package com.grooble.model;

import java.io.Serializable;

/**
*	QuestionはquestionId一意的なインデックス、単語、画像とサウンドファイル
*	それぞれの変数のgetter/setterメソッドも用意してある。
*/
@SuppressWarnings("serial")
public class Question implements Serializable{

	private int questionId;		//一意的なインデックス
	private String word;		// 単語
	private String image;		// イメージのURL
	private String sound;		// サウンドのURL
	private String category;	// 教科
	private int level;
	private String difficulty;	// レベル：easy, medium, hard
	
//	getters and setters
	public int getQuestionId(){
		return questionId;
	}
	public void setQuestionId(int questionId){
		this.questionId = questionId;
	}
	
	public String getWord(){
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	
	public String getImage(){
		return image;
	}
	public void setImage(String image){
		this.image = image;
	}
	
	public String getSound(){
		return sound;
	}
	public void setSound(String sound){
		this.sound = sound;
	}
	
	public String getCategory(){
		return category;
	}
	public void setCategory(String category){
		this.category = category;
	}
	
	public int getLevel() {
	    return level;
	}
	public void setLevel(int level){
	    this.level = level;
	}
	
	public String getDifficulty(){
		return difficulty;
	}
	public void setDifficulty(String difficulty){
		this.difficulty = difficulty;
	}
	
	public String toString() {
		return category + "/" + word;
	}
}