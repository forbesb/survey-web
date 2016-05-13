package com.brian.database;

public class Choice {
	//This entity is a possible choice for a survey.
	private int questionID;
	private int order;
	private String choice;

	public Choice(int questionID, int order, String choice) {
		this.questionID = questionID;//It has a relationship with Question.
		this.order = order;//This is the order for it to be displayed
		this.choice = choice;//This is the string of the actual choice.
		//An example of a question entity would be "Blue" being related to the question "WHat is your favorite color", whose ID was 4.
		//THe choice entity would have a 'choice' of blue, and a 'questionId' of 4.
	}

	public int getQuestionId() {
		return questionID;
	}

	public void setQuestionId(int questionID) {
		this.questionID = questionID;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getChoice() {
		return choice;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}
	
	
}
