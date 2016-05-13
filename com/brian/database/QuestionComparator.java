package com.brian.database;

import java.util.Comparator;

public class QuestionComparator implements Comparator<Question> {

	public int compare(Question arg0, Question arg1) {
		//This comparator's function is to sort an Array of Questions by order.
		if (arg0.getOrder()<arg1.getOrder()){
			return -1;
		} else if (arg0.getOrder()>arg1.getOrder()){
			return 1;
		}
		return 0;
	}
	
}
