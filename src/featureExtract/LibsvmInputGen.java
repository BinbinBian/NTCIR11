package featureExtract;

import java.io.*;

//For summing up the files in inputDir to generate input data for Libsvm put within outputDir.
public class LibsvmInputGen {
	private static String baseDir = "/Users/andylee/Documents/AS-Exp-NTCIR11/";
	private static String inputDir = baseDir+"13_Features/";
	private static String outputDir = baseDir+"14_LibsvmInput/";
	
	//main function
	public static void main(String args[]){
		genFile("bc");
		genFile("mc");
	}
	
	public static void genFile(String taskType){
		File folderIn = new File(inputDir);
		String outputFileName = outputDir+taskType+".txt";
		String outputMapFileName = outputDir+taskType+"Map.txt";
		
		BufferedReader br = null;
		BufferedWriter wr = null;
		BufferedWriter mapWr = null;
		
		try{
			wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFileName)),"UTF8"));
			System.out.println("Output File [" + outputFileName + "] opened for writing."); //debug
			
			mapWr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputMapFileName)),"UTF8"));
			System.out.println("Map File [" + outputMapFileName + "] opened for writing."); //debug
			
			for (final File fileEntry : folderIn.listFiles()) {
				if (!fileEntry.isDirectory() && fileEntry.getName().contains(taskType)) {
					//Open inputFile to read data.
					br = new BufferedReader(new InputStreamReader(new FileInputStream(fileEntry), "UTF8"));
			    	System.out.println("Function preprocess: File [" + fileEntry.toString() + "] opened for reading."); //debug
			    	
			    	mapWr.write(fileEntry.getName()+"\n");
			    	
			    	String line = "";
			    	while ((line = br.readLine()) != null){
			    		line = line.trim();
			    		wr.write(line+"\n");
			    	}
				}
			}//endof for
			
			mapWr.flush(); mapWr.close();
			wr.flush(); wr.close();
		}
		catch(Exception e) {
            System.out.println("Error: " + e.getMessage() );
            e.printStackTrace();
        }
	}
}
