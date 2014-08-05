package preprocessing;

import java.io.*;
import java.util.Properties;



//Stanford Parser
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.Tree;

//Stanford segmenter
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import library.XmlFileProcess;

//CT to CS transfer
import taobe.tec.jcc.*;

public class CTCS_Preprocessing {
	private static String baseDir = "/Users/andylee/Documents/AS-Exp-NTCIR11/";
	private static String inputDir = baseDir+"11_OriCTFile/RITE2_CT/";
	private static String outputDir = baseDir+"12_PreprocessResult/";
	//private static String outputDir = baseDir+"21_StanfordInput/";
	private static String preprocessSteps = "ori_segment_parseTree";
	//private static String preprocessSteps = "segment";
	private static Boolean preprocessTags = true;
	
	//Stanford parser variables
	private static String grammars;
	private static LexicalizedParser lp;
	private static Tree root;
		
	//Stanford segmenter variables
	static Properties props = new Properties();
	static CRFClassifier<CoreLabel> segmenter;
	
	//Numerical form usage
	static String[] basicNumCT = {"零","ㄧ","二","三","四","五","六","七","八","九","两","一"};
	static String[] basicNumEn = {"0","1","2","3","4","5","6","7","8","9","2","1"};
	static String[] timesNumCT = {"十","百","千","万","亿","兆"};
	static String[] timesNumEn = {"10","100","1000","10000","100000000","1000000000000"};
	
	//main function.
	public static void main(String args[]){
		//Input File directory
		final File folderIn;
		final File folderOut;
		
		folderIn = new File(inputDir);
		folderOut = new File(outputDir);
		
		//Stanford segmenter variable initialization.
		props.setProperty("sighanCorporaDict", "/tools/stanford-segmenter-2014-06-16/data");
	    props.setProperty("serDictionary","/tools/stanford-segmenter-2014-06-16/data/dict-chris6.ser.gz");
	    props.setProperty("inputEncoding", "UTF-8");
	    props.setProperty("sighanPostProcessing", "true");
	    segmenter = new CRFClassifier<CoreLabel>(props);
	    segmenter.loadClassifierNoExceptions("/tools/stanford-segmenter-2014-06-16/data/ctb.gz", props);

	    //Stanford parser variable initialization.
	    grammars = "edu/stanford/nlp/models/lexparser/xinhuaFactored.ser.gz";
	    lp = LexicalizedParser.loadModel(grammars);
	    
		//Call function for going through files in folderIn and output preprocessing results to folderOut.
		listFilesForFolder(folderIn, folderOut);
	} //endof "public static void main(String args[])"
	
	//function for going through all the files within the folderIn.
	private static void listFilesForFolder(final File folderIn, final File folderOut) {
	    for (final File fileEntry : folderIn.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry, folderOut);
	        }
	        else {
	        	String inputFileEntry = fileEntry.getName();
        		String fileName = folderIn.toString() +"/"+ inputFileEntry; // The name of the file to open.
        		String OutFileNamePre = folderOut.toString()+"/"+inputFileEntry.substring(0, inputFileEntry.indexOf(".")); //Generate File name of removed tag output file.
		        
        		preprocess(fileName, OutFileNamePre);
	        }
	    }
	} //endof "public static void listFilesForFolder(final File folderIn, final File folderOut)"
	
	//function for preprocessing
	private static void preprocess(String inputFile, String outputFileDir){
		BufferedReader br = null;
		BufferedWriter wr1 = null;
        BufferedWriter wr2 = null;
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
	    	System.out.println("Function preprocess: File [" + inputFile + "] opened for reading."); //debug
	    	
	    	XmlFileProcess xfp = new XmlFileProcess(br);
	    	
	    	if((inputFile.contains("RITE2_CT") || inputFile.contains("RITE-VAL-SV_CT") || inputFile.contains("RITE-VAL-SV_CS")) && inputFile.contains("training")){ //when input file is in RITE2 format
	    		String tmp = xfp.ExtractContentAndTagFromBr("pair");
	    		tmp=tmp.trim();
	    		while (tmp.equals("") == false){
	    			String id = xfp.ExtractTagLabelFromString("pair", "id", tmp);
	    			String label = xfp.ExtractTagLabelFromString("pair", "label", tmp);
	    			String outputfile1 = outputFileDir;
	    			String outputfile2 = outputFileDir;
	    			if(id!="" && label!=""){
	    				outputfile1+=".ID="+id+".LABEL="+label+".t1.txt";
	    				outputfile2+=".ID="+id+".LABEL="+label+".t2.txt";
	    				
                        wr1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputfile1)),"UTF8"));
						System.out.println("Output File [" + outputfile1 + "] opened for writing."); //debug
						wr2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputfile2)),"UTF8"));
						System.out.println("Output File [" + outputfile2 + "] opened for writing."); //debug
						
						preprocessSentence(wr1, xfp.ExtractContentFromString("t1", tmp));
						preprocessSentence(wr2, xfp.ExtractContentFromString("t2", tmp));
						
						wr1.flush(); wr1.close();
						wr2.flush(); wr2.close();
	    			}
	    			
	    			//for next iteration.
	    			tmp = xfp.ExtractContentAndTagFromBr("pair");
		    		tmp=tmp.trim();
	    		}
	    	}
	    	else if((inputFile.contains("RITE-VAL-SV_CT") || inputFile.contains("RITE-VAL-SV_CS")) && inputFile.contains("test")){ //when input file is in testing data format
	    		String tmp = xfp.ExtractContentAndTagFromBr("pair");
	    		tmp=tmp.trim();
	    		while (tmp.equals("") == false){
	    			String id = xfp.ExtractTagLabelFromString("pair", "id", tmp);
	    			String label = "Z";
	    			String outputfile1 = outputFileDir;
	    			String outputfile2 = outputFileDir;
	    			if(id!="" && label!=""){
	    				outputfile1+=".ID="+id+".LABEL="+label+".t1.txt";
	    				outputfile2+=".ID="+id+".LABEL="+label+".t2.txt";
	    				
                        wr1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputfile1)),"UTF8"));
						System.out.println("Output File [" + outputfile1 + "] opened for writing."); //debug
						wr2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputfile2)),"UTF8"));
						System.out.println("Output File [" + outputfile2 + "] opened for writing."); //debug
						
						preprocessSentence(wr1, xfp.ExtractContentFromString("t1", tmp));
						preprocessSentence(wr2, xfp.ExtractContentFromString("t2", tmp));
						
						wr1.flush(); wr1.close();
						wr2.flush(); wr2.close();
	    			}
	    			
	    			//for next iteration.
	    			tmp = xfp.ExtractContentAndTagFromBr("pair");
		    		tmp=tmp.trim();
	    		}
	    	}
		}
		catch(Exception e) {
            System.out.println("Error: " + e.getMessage() );
            e.printStackTrace();
        }
	} //endof "private static void preprocess"
	
	//Function for writing preprocessing result of sentence within tag to wr.
	private static void preprocessSentence(BufferedWriter wr, String sent){
		try {
			//transfer sent from CT to CS
			sent = JChineseConvertor.getInstance().t2s(sent);
			String numProcessed = numericProcess(sent);
			String seg = segmenter.classifyWithInlineXML(numProcessed).trim();
			
			if(preprocessSteps.contains("ori")){
				if (preprocessTags) wr.write("[[OriSent]]\n");
				wr.write(sent+"\n\n");
			}
			
			if(preprocessSteps.contains("segment")){
				if (preprocessTags) wr.write("[[Segmented]]\n");
				wr.write(seg+"\n\n");
			}
			
			if(preprocessSteps.contains("parseTree")){
				if (preprocessTags) wr.write("[[parseTree]]\n");
				Tree root = lp.parse(seg);
				wr.write(root+"\n\n");
			}
		}
		catch(Exception e) {
            System.out.println("Error: " + e.getMessage() );
            e.printStackTrace();
        }
	}
	
	//function for transfering numeric character.
	private static String numericProcess(String sent){
		String result = sent;
		
		String indexTmp="";
		boolean numStrStart=false;
		for(int i=0; i<sent.length(); i++){
			if(strExistInArray(sent.substring(i, i+1), basicNumCT) || strExistInArray(sent.substring(i, i+1), timesNumCT)){
				if(numStrStart==false){
					numStrStart=true;
					indexTmp+=Integer.toString(i)+"_";
				}
			}
			else{
				if(numStrStart==true){
					numStrStart=false;
					indexTmp+=Integer.toString(i)+" ";
				}
			}
		}
		indexTmp = indexTmp.trim();
		if(indexTmp.endsWith("_"))
			indexTmp+=Integer.toString(sent.length());
			
		if(!indexTmp.equals("")){
			String[] numStrIndex=indexTmp.split(" "); //array storing the 
			System.out.println("Function [numericProcess]: indexTmp = {"+indexTmp+"}"); //debug
			
			for(String element:numStrIndex){
				int numStartIndex = Integer.valueOf(element.split("_")[0]);
				int numEndIndex = Integer.valueOf(element.split("_")[1]);
				
				if(unitWordChk(numEndIndex, sent)==true){ //
					String chNum = sent.substring(numStartIndex, numEndIndex);
					result = result.replaceFirst(chNum, chToNum(chNum));
				}
			}
		}
		return result;
	}
	
	//Function that returns true if input is an element of array. false if else.
	private static boolean strExistInArray(String input, String[] array){
		for(String element:array)
			if(element.equals(input)) return true;
		return false;
	}
	private static int strIndexInArray(String input, String[] array){
		for(int i=0; i<array.length; i++)
			if(array[i].equals(input)) return i;
		return -1;
	}
	
	//Return true if there's a unit word starting from index.
	private static boolean unitWordChk(int index, String sent){
		String[] unitWords= {"把","本","埲","部","冊","册","层","场","处","出","次","道","顶","栋","堵","顿","朵","发","份","封","幅","个","根",
				"号","家","架","间","件","届","卷","棵","颗","口","类","粒","辆","列","轮","枚","门","面","名","盘","泡","匹","篇","片","扇","首",
				"水","艘","所","台","堂","趟","题","条","头","尾","位","项","宿","样","𠹻","坺","则","盏","张","只","支","枝","种","株","柱","尊","座",
				"班","帮","包","杯","辈","笔","串","床","袋","滴","点","段","堆","对","服","副","股","嚿","管","罐","行","盒","户","壶","伙","剂",
				"节","句","块","俩","缕","排","批","瓶","期","群","仨","束","双","套","团","坨","碗","些","行","扎","阵","注","组",
				"秒","分","刻","天","日","年","载","克","两","加仑","斤","公斤","吨","公分","厘米","寸","吋","尺","呎","英尺","公尺","米","里","哩","英里","公里",
				"天文单位","光年","秒差距","圆","元","块","蚊","角","毛","分",
				"遍","场","次","顿","回","声","趟","下","岁"};
		String tmp = sent.substring(index);
		for(String element:unitWords){
			if(tmp.startsWith(element)) return true;
		}
		return false;
	}
	
	//return numerical form of input
	private static String chToNum(String input){
		String tmp="";
		for(int i=0; i<input.length(); i++){
			String num = input.substring(i, i+1);
			int index;
			if((index=strIndexInArray(num, basicNumCT))>=0){
				tmp+=basicNumEn[index];
			}
			else{
				index=strIndexInArray(num, timesNumCT);
				tmp+=" "+timesNumEn[index]+" ";
			}
		}
		
		tmp=tmp.trim();
		
		if(tmp.contains(" ")){
			long sum=0;
			long thouSum=0;
			
			String[] valueList = tmp.split(" ");
			
			long preValue=-1;
			for(String element:valueList){
				if(element.equals("")) continue;
				
				long curValue=Long.valueOf(element);
				if(curValue>0 && curValue<10){
					preValue = curValue;
				}
				else if(curValue>=10 && curValue<=1000){
					if(preValue==-1)
						thouSum += curValue;
					else
						thouSum += preValue * curValue;
					preValue=-1;
				}
				else if(curValue > 1000){
					if(thouSum>0){
						if(preValue==-1){
							sum += thouSum * curValue;
						}
						else {
							sum += (thouSum + preValue) * curValue;
							preValue=-1;
						}
						thouSum = 0;
					}
					else{
						if(preValue==-1)
							sum += curValue;
						else{
							sum += preValue * curValue;
							preValue=-1;
						}
					}
				}
			}
			
			if (preValue!=-1)
				thouSum += preValue;
			
			if (thouSum > 0)
				sum += thouSum;
			
			return Long.toString(sum);
		}
		else 
			return tmp;
	}
}