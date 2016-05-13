package com.brian.Servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brian.controller.Controller;

/**
 * Servlet implementation class ServletAttempt
 */
//@WebServlet("/ServletAttempt")
public class ServletAttempt extends HttpServlet {
	private static Hashtable<String,Survey> surveys = new Hashtable<String, Survey>();//This hashtable holds all of the surveys.
	private static final long serialVersionUID = 1L;
	private static Controller controller = new Controller();//This controller is used to interface with the database.

    /**
     * Default constructor. 
     * @throws IOException 
     */
    public ServletAttempt() throws IOException {
    	//THis class has the main survey answering. It also holds the list of the surveys, and the controller.
    	for (String name: controller.getSurveyNames()){//This section loads all the surveys from the database.
    		surveys.put(name, controller.getSurvey(name));
    	}
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Hashtable<String, String[]> hTable = new Hashtable<String, String[]>();
		String key;
		String[] values;
		for(Enumeration<?> e = request.getParameterNames(); e.hasMoreElements();){
			key = (String) e.nextElement();
			values = request.getParameterValues(key);//This gets all of the parameters passed to the web site
			hTable.put(key, values); 
		}

		Survey s;
		PrintWriter out = response.getWriter(); //This is how the program outputs HTML
		HeaderCreator h = new HeaderCreator();
		if (hTable.get("survey1")!=null){//This will return true if the survey has been answered.
			
			h.doHeader(out, "Thank you!");
			out.println("<body>");
			SurveyResponse resp = new SurveyResponse(surveys.get(hTable.get("choice")[0]), hTable);//It creates a response with the given answers.
			//All of the work for a response is done in the constructor - no other methods are called.
			out.println("Thanks for answering!");
		} else {
			 if(hTable.get("choice")!=null){
					String ch = hTable.get("choice")[0];//The chosen survey will be the first value of choice
					s=surveys.get(ch);
					makeSurvey(out, h, s);
			}else{
				makeChoice(out, h, "ServletAttempt");
			}
		}
		
		
		out.println("<br><br><br>");
		
		out.println("<form name=\"editor\" method=\"post\" action=\"/servlet/com.brian.Servlets.SurveyEditor\">");
		out.println("<input type=\"submit\" value=\"Editor\">");//This button leads to the survey editor.
		out.println("</form>");
		
		out.println("<form name=\"results\" method=\"post\" action=\"/servlet/com.brian.Servlets.Results\">");
		if(hTable.get("survey1")!=null){//If the user just answered a survey, this button will go directly to the results of the chosen survey.
			out.println("<input type = \"hidden\" name=\"choice\" value=\""+hTable.get("choice")[0]+"\">");
		}
		out.println("<input type=\"submit\" value=\"Results\">");//This button leads to results.
		out.println("</form>");
		
		if(hTable.get("choice")!=null){//This button would move back to the same page in this case.
			out.println("<form name=\"surveys\" method=\"post\" action=\"/servlet/com.brian.Servlets.ServletAttempt\">");
			out.println("<input type=\"submit\" value=\"Surveys\">");//This button leads back to the survey choice.
			out.println("</form>");
		}
		out.println("</body>");
		out.println("</html>");		
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// This just makes it so if the servlet is accessed with a Post request, it will do the same as if it was a get.
		doGet(request, response);
	}
	private void makeSurvey(PrintWriter out, HeaderCreator h, Survey s){
		h.doHeader(out,"Survey");
		out.println("<body>");
		
		//The following section outputs the survey to the page
		out.println("<form name= \"surveyForm\" method=\"post\" action=\"/servlet/com.brian.Servlets.ServletAttempt\">");
		
		
		int count = 1;
		int c2=1;
		for(Map.Entry<String, ArrayList<String>> question: s.questions.entrySet()){
			out.println(question.getKey()+"<BR>");//the key is the question
			out.println("<select name = \"survey"+count+"\"  required>");
			for (String answer: question.getValue()){
				out.println("<option value=\""+c2+"\">"+answer+"</option>");//this makes an option on the dropdown
				c2++;
			}
			out.println("</select><BR>");//THis ends the dropdown.
			count++;       //and the values are the answers.
			c2=1;
		}
		out.println("<input type=\"hidden\" name=\"choice\" value=\""+s.name+"\">");
		out.println("<input type = \"hidden\" name=\"answered\" value=\"yes\">");
		out.println("<input type=\"submit\" value=\"Answer Survey\">");//This button leads back to the same page, with different parameters.
		out.println("</form>");
	}
	
	protected static void makeChoice(PrintWriter out, HeaderCreator h, String servletname){
		//This bit makes the input to choose a survey. It is used in this as well as in Results.
		h.doHeader(out, "Choose a Survey!");
		out.println("<body>");
		out.println("<form name=\"sChoice\" method=\"post\" action=\"/servlet/com.brian.Servlets."+servletname+"\">");
		for (Entry<String, Survey> ent: surveys.entrySet()){
			out.print("<input type=\"radio\" name=\"choice\" value =\"");
			out.print(ent.getValue().name);//This radio button is used to choose a survey.
			out.println("\">"+ent.getValue().name+"<BR><BR>");
		}
		out.println("<input type=\"submit\" value=\"Select\">");
		out.println("</form>");
	}
	
	public static void update(Survey s){
		surveys.put(s.name, s);//put will overwrite the previous value, or create a new one.
		//This is called after putting the survey into thte database. There is no point in
		//Retrieving it from the database after putting it in; I already have it, so I might as well use it.
	}
	
	public static Survey getSurvey(String name){
		return surveys.get(name);
		//This method returns a survey by name.
		//It is called from other servlets.
	}
	
	public static void loadSurvey(File f){
		//this method is mostly for testing (unless I use it somewhere else).
		//It's job is to load a specified survey. This is probably only needed when not testing on the server.
		Survey s;
		try {
			s = new FileMod().getSurveyData(f);
			System.out.println(s.name);
			surveys.put(s.name, s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Set<String> getNames(){
		//This is also usually for testing off the server. It just returns all the keys(to reference the currently loaded surveys).
		Set<String> keySet = surveys.keySet();
		return keySet;
	}
	
	public static Controller getController(){
		return controller;//this method is used to access the controller from other servlets.
	}
	
}