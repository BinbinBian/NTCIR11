package preprocessing;

import java.io.*;

public class DataFilePartialExtract {
	private static String baseDir = "/Users/andylee/Documents/AS-Exp-NTCIR11/";
	private static int extractLines = 20000;
	
	public static void main(String[] args) {
		String inputFile = baseDir + "01_OriWikifile/enwiki-20130204-pages-meta-current.xml";
		String outputFile = baseDir + "partialEnwiki.txt";
		int counter = 0;
		String line = "";
		
		BufferedWriter wr = null;
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
	    	System.out.println("Function resultFileGen: File [" + inputFile + "] opened for reading."); //debug
	    	
	    	wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile),"UTF8"));
			System.out.println("Output File [" + outputFile + "] opened for writing."); //debug
			
			while(counter < extractLines && (line = br.readLine()) != null){
				line = line.trim();
				wr.write(line+"\n");
				counter++;
			}
			wr.flush();
			wr.close();
		}
		catch(Exception e) {
            System.out.println("Error: " + e.getMessage() );					
            // Or we could just do this: 
            e.printStackTrace();
        }
	}
}
