package postprocessing;

import java.io.*;
import java.util.ArrayList;

public class svmErrorAnaBC {
	private static String baseDir = "/Users/andylee/Documents/AS-Exp-NTCIR11/";
	private static String inputDir1 = baseDir+"14_LibsvmInput/";
	private static String inputDir2 = baseDir+"15_LibsvmResult/";
	private static String outputDir = baseDir+"16_LibsvmResultAna/";
	private static int vectorSize=2;
	
	static String[][] matrix = new String[vectorSize][vectorSize]; //confusion Matrix
	
	//main function
		public static void main(String args[]){
			String inputTrainFile = inputDir1+"bc.train";
			String inputTestFile = inputDir1+"bc.test";
			String inputPredictFile = inputDir2+"bc.out";
			String inputMapfile = inputDir1+"bcMap.test";
			
			BufferedReader brTrain = null;
			BufferedReader brPred = null;
			BufferedReader brGold = null;
			BufferedReader brMap = null;
			ArrayList<String> trainLabel = new ArrayList<String>();
			ArrayList<String> predLabel = new ArrayList<String>();
			ArrayList<String> goldLabel = new ArrayList<String>();
			ArrayList<String> bcMapName = new ArrayList<String>();
			
			for(int i=0; i<vectorSize; i++)
				for(int j=0; j<vectorSize; j++)
					matrix[i][j]="";
			
			//Read input
			try{
				brTrain = new BufferedReader(new InputStreamReader(new FileInputStream(inputTrainFile), "UTF8"));
		    	//System.out.println("Function preprocess: File [" + pred + "] opened for reading."); //debug
				brPred = new BufferedReader(new InputStreamReader(new FileInputStream(inputPredictFile), "UTF8"));
		    	//System.out.println("Function preprocess: File [" + pred + "] opened for reading."); //debug
		    	brGold = new BufferedReader(new InputStreamReader(new FileInputStream(inputTestFile), "UTF8"));
		    	//System.out.println("Function preprocess: File [" + gold + "] opened for reading."); //debug
		    	brMap = new BufferedReader(new InputStreamReader(new FileInputStream(inputMapfile), "UTF8"));
		    	//System.out.println("Function preprocess: File [" + gold + "] opened for reading."); //debug
		    	
		    	String line = "";
		    	while((line = brTrain.readLine()) != null){
		    		if(!line.contains("labels")){
		    			line = line.trim();
		    			if(!line.equals("")){
		    				trainLabel.add(line.split(" ")[0]);
		    			}
		    		}
		    	}
		    	while((line = brPred.readLine()) != null){
		    		if(!line.contains("labels")){
		    			line = line.trim();
		    			if(!line.equals("")){
		    				predLabel.add(line.split(" ")[0]);
		    			}
		    		}
		    	}
		    	while((line = brGold.readLine()) != null){
		    		if(!line.contains("labels")){
		    			line = line.trim();
		    			if(!line.equals("")){
		    				goldLabel.add(line.split(" ")[0]);
		    			}
		    		}
		    	}
		    	while((line = brMap.readLine()) != null){
		    		if(!line.contains("labels")){
		    			line = line.trim();
		    			if(!line.equals("")){
		    				bcMapName.add(line);
		    			}
		    		}
		    	}
		    	
		    	brPred.close();
		    	brGold.close();
			}
			catch(Exception e) {
	            System.out.println("Error: " + e.getMessage() );
	            e.printStackTrace();
	        }
			
			//Confusion matrix generation
			for(int i=0; i<predLabel.size(); i++){
				matrix[Integer.valueOf(predLabel.get(i))][Integer.valueOf(goldLabel.get(i))] += bcMapName.get(i)+" ";
			}
			
			//Error analysis output
			String readOriSentDir = baseDir+"12_PreprocessResult-backup/";
			String readVectorDir = baseDir+"13_Features/";
			String matrixOut = outputDir+"bc_matrix.txt";
			
			try{
				BufferedWriter brMatrix = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(matrixOut)),"UTF8"));
				
				brMatrix.write("predict\\gold\t0\t1\n");
				
				for(int i=0; i<vectorSize; i++){
					brMatrix.write(Integer.toString(i)+"\t\t");
					for(int j=0; j<vectorSize; j++){
						String fileName[] = matrix[i][j].trim().split(" ");
						if(fileName[0].equals("")){
							brMatrix.write("0\t");
							continue;
						}
						brMatrix.write(Integer.toString(fileName.length)+"\t");
						
						BufferedWriter brErrorAna = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputDir+"bc_"+Integer.toString(j)+"_PredAs_"+Integer.toString(i)+".txt")),"UTF8"));;
						
						for(String element:fileName){
							brErrorAna.write("{{"+element+"}}\n");
							brErrorAna.write("<<Feature Vector>>\n");
							String line = "";
							BufferedReader brvector = new BufferedReader(new InputStreamReader(new FileInputStream(readVectorDir+element), "UTF8"));;
							while((line = brvector.readLine()) != null){
								line = line.trim();
								brErrorAna.write(line+"\n");
							}
							brErrorAna.write("\n");
							
							brErrorAna.write("<<T1>>\n");
							BufferedReader brOriSent = new BufferedReader(new InputStreamReader(new FileInputStream(readOriSentDir+element.replaceAll(".txt", ".t1.txt")), "UTF8"));;
							while((line = brOriSent.readLine()) != null){
								line = line.trim();
								brErrorAna.write(line+"\n");
							}
							brErrorAna.write("\n");
							
							brErrorAna.write("<<T2>>\n");
							BufferedReader brOriSent2 = new BufferedReader(new InputStreamReader(new FileInputStream(readOriSentDir+element.replaceAll(".txt", ".t2.txt")), "UTF8"));;
							while((line = brOriSent2.readLine()) != null){
								line = line.trim();
								brErrorAna.write(line+"\n");
							}
							brErrorAna.write("\n================================這是分隔線================================\n\n");
						}
						
						brErrorAna.flush(); brErrorAna.close();
					}
					brMatrix.write("\n");
				}
				
				brMatrix.flush(); brMatrix.close();
			}
			catch(Exception e) {
	            System.out.println("Error: " + e.getMessage() );
	            e.printStackTrace();
	        }
		} //endof public static void main(String args[]){
}
