package com.brian.Servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileMod {
	
	
	public FileMod(){
		//This class was originally used to read/write files
		//It is no longer needed now that I am using database.
	}
	public static void main(String[] args) throws IOException{
		FileMod fm = new FileMod();
		Survey s = fm.getSurveyData(new File("Test.txt"));//THis was to test that the file reader/writer worked
		fm.writeSurvey(s, new File("testOut.txt"));
	}
	public Survey getSurveyData(File f) throws IOException{
		
		String question = "", name="";
		ArrayList<String> answers;
		LinkedHashMap<String, ArrayList<String>> questions = new LinkedHashMap<String, ArrayList<String>>();
		BufferedReader read = new BufferedReader(new FileReader(f));
		name=read.readLine();
		System.out.println(name);
		while(read.ready()){
			answers = new ArrayList<String>();
			question = read.readLine();//This first reads the question(the first line)
			while(read.ready()){
				String answer = read.readLine();
				if (answer.equals("<-->")){//this symbol signifies the end of a question.
					break;
				} else {
					answers.add(answer);//and then reads all of the possible answers
				}
			}
			questions.put(question, answers);
		}
			
		read.close();
		
		return new Survey(questions, name);
	}
	
	public synchronized void writeSurvey(Survey survey, File file) throws FileNotFoundException{//this writes the contents of a survey to a file
		//System.out.println(file.getAbsolutePath());
		PrintWriter write = new PrintWriter(file);
		write.println(survey.name);
		int count = 1;
		for(Map.Entry<String, ArrayList<String>> question: survey.questions.entrySet()){
			write.println(question.getKey());//the first line is the question
			for (String answer: question.getValue()){
				write.println(answer);//and the remaining lines are the answers.
			}
			write.println("<-->");//this symbol signifies the end of a question.
			System.out.println("Wrote Question"+count);
			count++;
		}
		write.close();
		
	}
}
