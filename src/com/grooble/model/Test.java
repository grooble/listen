package com.grooble.model;

import java.util.Iterator;
import java.util.List;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Test implements Serializable{
	private int[] correct;
	private int[] selected;
	private List<Question[]> testQns;
	
	public int[] getCorrect(){
		return correct;
	}
	public void setCorrect(int[] correct){
		this.correct = correct;
	}
	
	public int[] getSelected(){
		return selected;
	}
	public void setSelected(int[] selected){
		this.selected = selected;
	}
	
	public List<Question[]> getTest(){
		return testQns;
	}
	public void setTest(List<Question[]> testQns){
		this.testQns = testQns;
	}
	
	public String toString(){
		String output = "";
		Iterator<Question[]> it = testQns.iterator();
		while(it.hasNext()){
			Question[] aQn = it.next();
			for(int i=0; i<aQn.length; i++){
				output = output + aQn[i] + ", ";
			}
			output = output + "\n";
		}
		return output;
	}
}
