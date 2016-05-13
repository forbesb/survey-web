package com.brian.Servlets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SurveyEditor extends HttpServlet {
	private int validNum=-1;
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SurveyEditor() {
        super();
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
   
    private boolean validate(Hashtable<String,String[]> hTable, PrintWriter out){
		if (hTable.get("password")!=null){
			if(hTable.get("password")[0].equals("newpassword")){//The password is hardcoded, and there is no way to change it other than by changing this.
				validNum=(int)(Math.random()*9999);//This number is used to keep someone who is validated revalidated.
				return true;
			} else {
				out.println("<BR>Incorrect Password.<BR>");
			}
		} else if(hTable.get("rNum")!=null){//if there is no password, but the number assigned before to validNum is the same as the one passed into rNum
			if(Integer.parseInt(hTable.get("rNum")[0])==validNum){
				return true;
			}
		}else {//if the user has neither a validation number or a password
			
			out.println("<form name=\"password\" method=\"post\" action=\"/servlet/com.brian.Servlets.SurveyEditor\"><BR>");
			out.println("Enter the Password.<BR>");//it brings up the password entry screen.
			out.println("<input type=\"password\" name=\"password\">");
			out.println("<input type=\"submit\" value=\"Submit\">");
			out.println("</form>");
		}
		return false;
		
    }
    
    private void updateSurvey(Hashtable<String,String[]> hTable, PrintWriter out, int qCount) throws FileNotFoundException{
    	LinkedHashMap<String, ArrayList<String>> questions = new LinkedHashMap<String, ArrayList<String>>();
    	String n = hTable.get("newN")[0];
    	out.println("New Survey: "+n+"<BR>");
    	System.out.println(qCount);
    	
    	for(int i = 1; i<=qCount; i++){
			String q = hTable.get("newQ"+i)[0];//The question is the first value of newQ[1+], and a contains the answers
			out.println("New Question"+i+": "+q+"<br>");
			ArrayList<String> a = new ArrayList<String>();
			out.println("New Answers: <br>"); 			//This section gets the survey from the hashtable
			for (String answer: hTable.get("newA"+i)){	//and puts it into the file.
				if (!answer.equals("")){
					a.add(answer);
					out.println(answer+"<br>");
				}
			}
			questions.put(q, a);
			out.println("<br>");
		}
    	Survey serv = new Survey(questions, n);
		ServletAttempt.getController().insertSurvey(serv);
		ServletAttempt.update(serv);//This causes the other servlet to update the value changed survey
		
    }
    
    private void makeOnClick(PrintWriter out, int qCount){
    	out.println("<script type=\"text/javascript\">");
    	out.println("function submitAdd(){");
    	out.println("var add = document.getElementsByName(\"number\")[0]");//this section defines a javascript function that submits the form
    	//out.println("add.type=\"hidden\"");
    	//out.println("add.name=\"number\"");
    	out.println("add.value=\""+(qCount+1)+"\"");
    	out.println("document.forms[\"editor\"].appendChild(add)");
    	
    	out.println("var rem = document.getElementsByName(\"newQ1\")[0]");//This bit makes it so that the servlet doesn't try to 
    	out.println("document.forms[\"editor\"].removeChild(rem)");//update when it adds a question, which would cause an error.
    	
    	out.println("document.forms[\"editor\"].submit()");
    	out.println("} </script>");
    }
    
    private void makeEditor(PrintWriter out, int qCount){
    	out.println("<form name=\"editor\" method = \"post\" action=\"/servlet/com.brian.Servlets.SurveyEditor\">");
    	out.println("New Survey: <input type=\"text\" name=\"newN\"><br>");
    	out.println("Answers: Maximum of 6. Leave any unused answer slots blank.<br>");
    	
    	for(int i=1; i<=qCount;i++){
    		out.println("<BR>");
			out.println("Question "+i+": <input type=\"text\" name=\"newQ"+i+"\"><br>");
			for (int y = 1; y<=6; y++){//this is the form to submit the survey change
				out.println("Answer "+y+": <input type=\"text\" name=\"newA"+i+"\"> <BR>");
			}
			out.println("<BR>");
    	}
    	out.println("<input type = \"button\" value=\"Add Question\" onclick=\"submitAdd()\">");
		out.println("<input type=\"submit\" value=\"Change Survey\">");
		out.println("<input type=\"hidden\" name=\"rNum\" value=\""+validNum+"\"> ");//This value is to validate the user
		out.println("<input type=\"hidden\" name=\"number\" value=\""+qCount+"\">");//This value is the number of questions
		out.println("</form>");
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Hashtable<String, String[]> hTable = new Hashtable<String, String[]>();
		String key;
		String[] values;
		for(Enumeration<?> e = request.getParameterNames(); e.hasMoreElements();){
			key = (String) e.nextElement();//this section gets all of the parameters and puts them into the hashtable.
			values = request.getParameterValues(key);
			hTable.put(key, values); 
		}
		String[] qc = hTable.get("number");
		int qCount=1;
		if (qc!=null){
			qCount=Integer.parseInt(qc[0]);//The current number of questions is in 0 of the array.
			//If there's no number parameter, then the user has not pressed 'add questions' and the number is therefore one.
		}
		
		// TODO Auto-generated method stub
		PrintWriter out = new PrintWriter(response.getWriter());
		HeaderCreator h = new HeaderCreator();
		h.doHeader(out, "Survey Editor");
		out.println("<body>");
		
		
		boolean valid = validate(hTable, out);//it first validates the user.
		
		if(valid){
			if(hTable.get("newQ1")!=null){//This section shows the user their changes after they make a new question.
				updateSurvey(hTable, out, qCount);
			}
			
			makeOnClick(out, qCount);//then it makes the javascript function
			makeEditor(out,qCount);//and then the editor.
			
		}
			
		
		out.println("<form name=\"Surveys\" method=\"post\" action=\"/servlet/com.brian.Servlets.ServletAttempt\">");
		out.println("<input type=\"submit\" value=\"Surveys\">");//This button goes back to the survey selection screen.
		out.println("</form>");
		
		out.println("<form name=\"results\" method=\"post\" action=\"/servlet/com.brian.Servlets.Results\">");
		out.println("<input type=\"submit\" value=\"Results\">");//THis button goes to results
		out.println("</form>");
		
		
		out.println("</body>");
		out.println("</html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}