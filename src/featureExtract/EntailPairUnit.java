package featureExtract;

import java.io.*;
import edu.stanford.nlp.trees.Tree;
import featureExtract.Concepts;

public class EntailPairUnit {
	private String source;
	private String taskType; //bc or mc
	private String ID;
	private String pairType; //the sentence is t1 or t2.
	private String label; //"Y" or "N" in bc, "F" or "B" or "I" or "C" in mc
	private String OriSent;
	private String[] Segmented;
	private String parseTree;
	private String[] dependency;
	private Concepts[] concept;
	private int conceptCount;
	private String[] pos;
	
	//Constructor
	public EntailPairUnit(File inputFile){
		BufferedReader br = null;
		
		try{
			//Open inputFile to read data.
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
	    	System.out.println("Function preprocess: File [" + inputFile + "] opened for reading."); //debug
	    	
	    	String[] split = inputFile.getName().split("\\.");
	    	source = split[0];
	    	taskType = source.split("_")[2];
	    	ID = split[1].split("=")[1];
	    	pairType = split[3];
	    	label = split[2].split("=")[1];
	    	conceptCount = 0;
	    	
	    	String line = "";
	    	while ((line = br.readLine()) != null){
	    		line = line.trim();
	    		if(line.contains("[[OriSent]]")){
	    			if((line = br.readLine())!=null){
	    				OriSent = line.trim();
	    			}
	    		}
	    		else if(line.contains("[[Segmented]]")){
	    			if((line = br.readLine())!=null){
	    				Segmented = line.trim().split(" ");
	    			}
	    		}
	    		else if(line.contains("[[parseTree]]")){
	    			if((line = br.readLine())!=null){
	    				parseTree = line.trim();
	    			}
	    		}
	    		else if(line.contains("[[Depend]]")){
	    			if((line = br.readLine())!=null){
	    				dependency = line.trim().split("\\|");
	    			}
	    		}
	    		else if(line.contains("[[Concept]]")){
	    			concept = null;
	    			if((line = br.readLine())!=null){
	    				line=line.trim();
	    				if(!line.equals("")){
	    					String[] tmp = line.trim().split("\\|\\|");
	    					conceptCount = tmp.length;
		    				concept = new Concepts[tmp.length];
		    				for(int i=0; i<tmp.length; i++){
		    					concept[i]=new Concepts(tmp[i]);
		    				}
	    				}
	    			}
	    		}
	    	}
	    	
	    	//POS tag generation:
	    	String[] tmpTree = parseTree.split(" ");
	    	String posList = "";
	    	for(int i=0; i<tmpTree.length; i++){
	    		if(tmpTree[i].contains(")")){
	    			posList+=tmpTree[i-1].replaceAll("\\(", "")+" ";
	    		}
	    	}
	    	pos = posList.trim().split(" ");
		} //endof try
        catch(Exception e) {
            System.out.println("Error: " + e.getMessage() );
            e.printStackTrace();
        }
	}
	
	public String get_source(){ return source;}
	public String get_taskType(){ return taskType;}
	public String get_ID(){ return ID;}
	public String get_pairType(){ return pairType;}
	public String get_label(){ return label;}
	public String get_OriSent(){ return OriSent;}
	public String[] get_Segmented(){ return Segmented;}
	public String get_parseTree(){ return parseTree;}
	public String[] get_Dependency(){ return dependency;}
	public Concepts[] get_concepts(){ return concept;}
	public int get_conceptCount(){ return conceptCount;}
	public String[] get_pos(){ return pos;}
}
