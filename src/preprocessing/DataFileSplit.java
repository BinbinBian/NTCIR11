package preprocessing;

import java.io.*;

import library.XmlFileProcess;

public class DataFileSplit {
	private static String baseDir = "/Users/andylee/Documents/AS-Exp-NTCIR11/";
	private static String inputFile = baseDir + "01_OriWikifile/enwiki-20130204-pages-meta-current.xml";
	private static String outputDir = baseDir + "02_WikiFileSplit/";
	private static int pagePerFile = 100;
	
	public static void main(String[] args) {
		String outputFile = outputDir + "partialEnwiki";
		int counter = 0;
		int outputfilecount = 0;
		String extractStr = "";
		
		BufferedWriter wr = null;
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
	    	System.out.println("Function resultFileGen: File [" + inputFile + "] opened for reading."); //debug
	    	
	    	XmlFileProcess xfp = new XmlFileProcess(br);
			
			while(true){
				extractStr = xfp.ExtractContentFromBr("page");
				if (extractStr.equals("")){
					wr.flush();
					wr.close();
					break;
				}
				else{
					if (counter==0){
						wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile+String.format("%04d", outputfilecount)+".txt"),"UTF8"));
						System.out.println("Output File [" + outputFile+String.format("%04d", outputfilecount)+".txt" + "] opened for writing."); //debug
						
						wr.write("<page>\n"+extractStr+"\n</page>\n");
						counter++;
					}
					else if (counter==(pagePerFile-1)){
						counter=0;
						outputfilecount++;
						wr.write("<page>\n"+extractStr+"\n</page>");
						wr.flush();
						wr.close();
					}
					else{
						wr.write("<page>\n"+extractStr+"\n</page>\n");
						counter++;
					}
				}
			}
		}
		catch(Exception e) {
            System.out.println("Error: " + e.getMessage() );					
            // Or we could just do this: 
            e.printStackTrace();
        }
	}
}
