package library;

import java.io.BufferedReader;

public class XmlFileProcess {
	private BufferedReader br;
	
	public XmlFileProcess(BufferedReader inputBr){
		br = inputBr;
	}
	
	public BufferedReader getBr(){
		return br;
	}
	
	//Function for getting the content of the 1st tag met.
	public String ExtractContentFromBr(String tag){
		String result="";
		String line = "";
		
		try {
			String tagStart = "<"+tag;
			String tagEnd = "</"+tag+">";
			Boolean foundTag = false;
			while((line = br.readLine()) != null){
				line = line.trim();
				
				if(foundTag){
					if(line.contains(tagEnd)){
						result += line.substring(0, line.indexOf(tagEnd));
						foundTag = false;
						break;
					}
					else {
						result += line + "\n";
					}
				}
				else{
					if(line.contains(tagStart)){
						result += line.substring(line.indexOf(">", line.indexOf(tagStart))+1);
						foundTag = true;
					}
				}
			}
		}
		catch(Exception e) {
            System.out.println("Error: " + e.getMessage() );
            e.printStackTrace();
        }
		
		return result;
	}
	
	//Function for getting the content and tag of the 1st tag met.
	public String ExtractContentAndTagFromBr(String tag){
		String result="";
		String line = "";
		
		try {
			String tagStart = "<"+tag;
			String tagEnd = "</"+tag+">";
			Boolean foundTag = false;
			while((line = br.readLine()) != null){
				line = line.trim();
				
				if(foundTag){
					if(line.contains(tagEnd)){
						result += line.substring(0, line.indexOf(tagEnd)+tagEnd.length());
						foundTag = false;
						break;
					}
					else {
						result += line + "\n";
					}
				}
				else{
					if(line.contains(tagStart)){
						result += line.substring(line.indexOf(tagStart));
						foundTag = true;
					}
				}
			}
		}
		catch(Exception e) {
            System.out.println("Error: " + e.getMessage() );
            e.printStackTrace();
        }
		
		return result;
	}
	
	//Function for getting the content of the 1st tag met within the inputString.
	public String ExtractContentFromString(String tag, String inputString){
		String result="";
		String tagStart = "<"+tag;
		String tagEnd = "</"+tag+">";
		
		result = inputString.substring(inputString.indexOf(">", inputString.indexOf(tagStart))+1, inputString.indexOf(tagEnd));
		
		return result;
	}
	
	//Function for getting the label value of the 1st tag met within the inputStr
	public String ExtractTagLabelFromString(String tag, String label, String inputStr){
		String result="";
		String tmp="";
		String tagStart="<"+tag;
		String labelStart=label+"=\"";
		int startIndex = inputStr.indexOf(tagStart)+tagStart.length();
		
		tmp = inputStr.substring(startIndex, inputStr.indexOf(">", startIndex));
		if (tmp.contains(labelStart)){
			int lableStartEnd = tmp.indexOf(labelStart)+labelStart.length();
			result = tmp.substring(lableStartEnd, tmp.indexOf("\"", lableStartEnd));
		}
		
		return result;
	}
	
	public String removeContentwithin(String inputString, String startStr, String endStr){
		String result = "";
		
		return result;
	}
}
