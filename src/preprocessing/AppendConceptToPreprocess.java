package preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class AppendConceptToPreprocess {
	private static String baseDir = "/Users/andylee/Documents/AS-Exp-NTCIR11/";
	private static String inputDir = baseDir+"SinicaSementicParser-CS/output/";
	private static String outputDir = baseDir+"12_PreprocessResult/";
	
	public static void main(String args[]){
		String conceptFile = inputDir+"output-byVirk.txt";
		String mapFile = inputDir+"map.txt";

		BufferedReader conBr = null; //for reading concept file.
		BufferedReader mapBr = null; //for reading map file.
		BufferedWriter wr = null;
		
		String conLine="";
		String mapLine="";
		
		try{
			conBr = new BufferedReader(new InputStreamReader(new FileInputStream(conceptFile), "gb2312"));
	    	System.out.println("Concept file [" + conceptFile.toString() + "] opened for reading."); //debug
	    	mapBr = new BufferedReader(new InputStreamReader(new FileInputStream(mapFile), "UTF8"));
	    	System.out.println("Mapping file [" + conceptFile.toString() + "] opened for reading."); //debug
	    	
	    	
	    	while((conLine=conBr.readLine())!=null && (mapLine=mapBr.readLine())!=null){
	    		conLine = conLine.trim();
	    		mapLine = mapLine.trim();
	    		if(mapLine.equals("")) break;
	    		
	    		String appendData="\n\n[[Concept]]\n"+conLine;
	    		if(appendData.endsWith("||")) appendData = appendData.substring(0, appendData.lastIndexOf("||"));
	    		System.out.println("appendData = "+appendData);//debug
	    		
	    		File appendFile = new File(outputDir+mapLine);
	    		wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(appendFile, true),"UTF8"));
		    	System.out.println("File [" + appendFile.toString() + "] opened for appending."); //debug
		    	wr.write(appendData);
		    	wr.close();
	    	}
	    	
	    	conBr.close();
	    	mapBr.close();
    	}
    	catch(Exception e) {
            System.out.println("Error: " + e.getMessage() );
            e.printStackTrace();
        }
	}
}
