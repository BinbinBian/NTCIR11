package preprocessing;

import java.io.*;

public class ConceptInputGen {
	private static String baseDir = "/Users/andylee/Documents/AS-Exp-NTCIR11/";
	private static String inputDir = baseDir+"12_PreprocessResult/RITE2_CT_bc_training/";
	private static String outputFile = baseDir+"SinicaSementicParser-CS/input/input_RITE2_CT_bc_training.txt";
	private static String outputMapFile = baseDir+"SinicaSementicParser-CS/output/map_RITE2_CT_bc_training.txt";

	private static boolean counterEnable = false;
	private static int counter = 6;
	
	//Main function
	public static void main(String args[]){
		File folderIn = new File(inputDir);
		BufferedReader br = null;
		BufferedWriter wr1 = null; //bufferwriter for generating input file for concept extractor.
        BufferedWriter wr2 = null; //bufferwriter for generating mapping file.
		
        try {
        	wr1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFile)),"gb2312"));
			System.out.println("Output File [" + outputFile + "] opened for writing."); //debug
			wr2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputMapFile)),"UTF8"));
			System.out.println("Output File [" + outputMapFile + "] opened for writing."); //debug
			
        	//Call function for going through files in folderIn and output results to folderOut.
        	for (final File fileEntry : folderIn.listFiles()) {
    	        if (!fileEntry.isDirectory() && fileEntry.getName().contains(".txt")) {
    	        	String inputFileEntry = fileEntry.getName();
            		String fileName = folderIn.toString() +"/"+ inputFileEntry; // The name of the file to open.
            		String line = "";
    		        
            		br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
        	    	System.out.println("Function preprocess: File [" + fileName + "] opened for reading."); //debug
        	    	
        	    	while((line = br.readLine()) != null){
        	    		line = line.trim();
        	    		if(line.contains("[[parseTree]]")){
        	    			if((line = br.readLine())!=null){
        	    				line = line.trim();
        	    				if(!line.equals("")){
        	    					wr1.write(line+"\n");
        	    					wr2.write(inputFileEntry+"\n");
        	    				}
        	    			}
        	    			else break;
        	    		}
        	    	}
        	    	
        	    	if(counterEnable){
        	    		counter--;
        	    		if(counter==0) break;
        	    	}
    	        }
    	    } //endof for (final File fileEntry : folderIn.listFiles())
        	
        	wr1.flush(); wr1.close();
        	wr2.flush(); wr2.close();
        } //endof try
        catch(Exception e) {
            System.out.println("Error: " + e.getMessage() );
            e.printStackTrace();
        }
	} //endof public static void main(String args[]){
}
