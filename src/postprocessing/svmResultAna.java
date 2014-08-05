package postprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class svmResultAna {
	private static String baseDir = "/Users/andylee/Documents/AS-Exp-NTCIR11/";
	private static String inputDir1 = baseDir+"14_LibsvmInput/20140804-with_concept/";
	private static String inputDir2 = baseDir+"15_LibsvmResult/20140804-with_concept/";
	
	//main function
	public static void main(String args[]){
		String inputBCtestFile = inputDir1+"bc.test";
		String inputBCpredictFile = inputDir2+"bc.out";
		String inputMCtestFile = inputDir1+"mc.test";
		String inputMCpredictFile = inputDir2+"mc.out";
		
		double pre = precision(inputBCpredictFile, inputBCtestFile, "1");
		double rec = recall(inputBCpredictFile, inputBCtestFile, "1");
		
		System.out.printf("BC precision = %f\n", pre);
		System.out.printf("BC recall = %f\n", rec);
		System.out.printf("BC accuracy = %f\n", accuracy(inputBCpredictFile, inputBCtestFile));
		System.out.printf("BC f1 score = %f\n", f1Score(pre, rec));
		
		pre = macroPrecision(inputMCpredictFile, inputMCtestFile, 4);
		rec = macroRecall(inputMCpredictFile, inputMCtestFile, 4);
		System.out.printf("MC precision = %f\n", pre);
		System.out.printf("MC recall = %f\n", rec);
		System.out.printf("MC accuracy = %f\n", accuracy(inputMCpredictFile, inputMCtestFile));
		System.out.printf("MC f1 score = %f\n", f1Score(pre, rec));
		System.out.printf("MC-I f1 score = %f\n", f1Score(precision(inputMCpredictFile, inputMCtestFile, "0"), recall(inputMCpredictFile, inputMCtestFile, "0")));
		System.out.printf("MC-C f1 score = %f\n", f1Score(precision(inputMCpredictFile, inputMCtestFile, "1"), recall(inputMCpredictFile, inputMCtestFile, "1")));
		System.out.printf("MC-B f1 score = %f\n", f1Score(precision(inputMCpredictFile, inputMCtestFile, "2"), recall(inputMCpredictFile, inputMCtestFile, "2")));
		System.out.printf("MC-F f1 score = %f\n", f1Score(precision(inputMCpredictFile, inputMCtestFile, "3"), recall(inputMCpredictFile, inputMCtestFile, "3")));
	} //endof public static void main(String args[]){
	
	public static double precision(String pred, String gold, String positiveLabel){
		BufferedReader brPred = null;
		BufferedReader brGold = null;
		
		ArrayList<String> predLabel = new ArrayList<String>();
		ArrayList<String> goldLabel = new ArrayList<String>();
		
		try{
			brPred = new BufferedReader(new InputStreamReader(new FileInputStream(pred), "UTF8"));
	    	//System.out.println("Function preprocess: File [" + pred + "] opened for reading."); //debug
	    	brGold = new BufferedReader(new InputStreamReader(new FileInputStream(gold), "UTF8"));
	    	//System.out.println("Function preprocess: File [" + gold + "] opened for reading."); //debug
	    	
	    	String line = "";
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
	    	
	    	brPred.close();
	    	brGold.close();
		}
		catch(Exception e) {
            System.out.println("Error: " + e.getMessage() );
            e.printStackTrace();
        }
		
		int a = 0;
		int b = 0;
		for(int i=0; i<predLabel.size(); i++){
			if(predLabel.get(i).equals(positiveLabel)){
				if(goldLabel.get(i).equals(positiveLabel))
					a++;
				b++;
			}
		}
		
		return ((double)a/(double)b);
	}
	
	public static double recall(String pred, String gold, String positiveLabel){
		BufferedReader brPred = null;
		BufferedReader brGold = null;
		
		ArrayList<String> predLabel = new ArrayList<String>();
		ArrayList<String> goldLabel = new ArrayList<String>();
		
		try{
			brPred = new BufferedReader(new InputStreamReader(new FileInputStream(pred), "UTF8"));
	    	//System.out.println("Function preprocess: File [" + pred + "] opened for reading."); //debug
	    	brGold = new BufferedReader(new InputStreamReader(new FileInputStream(gold), "UTF8"));
	    	//System.out.println("Function preprocess: File [" + gold + "] opened for reading."); //debug
	    	
	    	String line = "";
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
	    	
	    	brPred.close();
	    	brGold.close();
		}
		catch(Exception e) {
            System.out.println("Error: " + e.getMessage() );
            e.printStackTrace();
        }
		
		int a = 0;
		int b = 0;
		for(int i=0; i<predLabel.size(); i++){
			if(goldLabel.get(i).equals(positiveLabel)){
				if(predLabel.get(i).equals(positiveLabel))
					a++;
				b++;
			}
		}

		return ((double)a/(double)b);
	}

	public static double macroPrecision(String pred, String gold, int labelAmount){
		double sum = 0.0;
		
		for(int i=0; i<labelAmount; i++){
			sum += precision(pred, gold, Integer.toString(i));
		}
		
		return sum/labelAmount;
	}
	
	public static double macroRecall(String pred, String gold, int labelAmount){
		double sum = 0.0;
		
		for(int i=0; i<labelAmount; i++){
			sum += recall(pred, gold, Integer.toString(i));
		}
		
		return sum/labelAmount;
	}
	
	public static double accuracy(String pred, String gold){
		BufferedReader brPred = null;
		BufferedReader brGold = null;
		
		ArrayList<String> predLabel = new ArrayList<String>();
		ArrayList<String> goldLabel = new ArrayList<String>();
		
		try{
			brPred = new BufferedReader(new InputStreamReader(new FileInputStream(pred), "UTF8"));
	    	//System.out.println("Function preprocess: File [" + pred + "] opened for reading."); //debug
	    	brGold = new BufferedReader(new InputStreamReader(new FileInputStream(gold), "UTF8"));
	    	//System.out.println("Function preprocess: File [" + gold + "] opened for reading."); //debug
	    	
	    	String line = "";
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
	    	
	    	brPred.close();
	    	brGold.close();
		}
		catch(Exception e) {
            System.out.println("Error: " + e.getMessage() );
            e.printStackTrace();
        }
		
		int a=0;
		for(int i=0; i<predLabel.size(); i++){
			if(predLabel.get(i).equals(goldLabel.get(i))) a++;
		}
		
		return (double) a/(double) predLabel.size();
	}
	
	public static double f1Score(double prec, double rec){
		return (2*prec*rec)/(prec+rec);
	}
}
