package preprocessing;

import java.io.*;

public class AppendDepToPreprocess {
	private static String baseDir = "/Users/andylee/Documents/AS-Exp-NTCIR11/";
	private static String inputDir = baseDir+"22_StanfordOutput/RITE2_CT_bc/";
	private static String outputDir = baseDir+"12_PreprocessResult/RITE2_CT_bc_training/";
	
	public static void main(String args[]){
		File folderIn = new File(inputDir);
		BufferedWriter wr = null;
		BufferedReader br = null;
		
		for (final File fileEntry : folderIn.listFiles()) {
	        if (!fileEntry.isDirectory() && fileEntry.getName().contains(".txt")) {
	        	File appendFile = new File(outputDir+fileEntry.getName());
	        	
	        	try{
	        		br = new BufferedReader(new InputStreamReader(new FileInputStream(fileEntry.toString()), "UTF8"));
	    	    	System.out.println("File [" + fileEntry.toString() + "] opened for reading."); //debug
	    	    	
	    	    	String line="";
	    	    	String appendData="[[Depend]]\n";
	    	    	while((line=br.readLine())!=null){
	    	    		line = line.trim();
	    	    		if(!line.equals("")) appendData+=line+"|";
	    	    	}
	    	    	if(appendData.endsWith("|")) appendData = appendData.substring(0, appendData.lastIndexOf("|"));
	    	    	System.out.println("appendData = "+appendData);//debug
	    	    	
	    	    	wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(appendFile, true),"UTF8"));
	    	    	System.out.println("File [" + appendFile.toString() + "] opened for appending."); //debug
	    	    	
	    	    	wr.write(appendData);
	    	    	
	    	    	br.close();
	    	    	wr.close();
	        	}
	        	catch(Exception e) {
	                System.out.println("Error: " + e.getMessage() );
	                e.printStackTrace();
	            }
	        }
		}
	}
}
