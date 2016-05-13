package com.brian.Servlets;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Survey {

	public String name;
	public LinkedHashMap<String, ArrayList<String>> questions;
	
	public Survey(LinkedHashMap<String, ArrayList<String>> qs, String n){
		//This class is used to store a survey.
		questions = qs;//This LinkedHashMap stores questions-answers in a first in, first out format.
		name = n;//name is used to reference the survey.
	}
}
