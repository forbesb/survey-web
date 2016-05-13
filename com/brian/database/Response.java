package com.brian.database;

public class Response {
	private int questionID;
	private int response;

	public Response(int questionID, int response) {
		this.questionID = questionID;//This response is related to a question
		this.response = response;//This int represents the choice number that the user chose.
		//An example of a response entity would be "1" being related to the question "WHat is your favorite color", whose ID was 4.
		//THe choice entity would have a 'response' of "1", and a 'questionId' of 4.
		
	}

	public int getQuestionID() {
		return questionID;
	}

	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

	public int getResponse() {
		return response;
	}

	public void setResponse(int response) {
		this.response = response;
	}
	
	
}
