package featureExtract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import featureExtract.EntailPair;
import featureExtract.DependUnit;

public class FeatureExtractCore {
	private static String baseDir = "/Users/andylee/Documents/AS-Exp-NTCIR11/";
	
	private static String inputDir = baseDir+"12_PreprocessResult-backup/"; //ori
	private static String outputDir = baseDir+"13_Features/"; //ori
	//private static String inputDir = baseDir+"91_DebugInput1/"; //debug
	//private static String outputDir = baseDir+"92_DebugOutput1/"; //debug
	
	private static int featureVectorMaxSize = 100;
	private static int cosSimVectorSize = 3000;
	
	//main function
	public static void main(String args[]){
		File folderIn = new File(inputDir);
		
		for (final File t1file : folderIn.listFiles()) {
			if (!t1file.isDirectory() && t1file.getName().contains("t1.txt")) {
				File t2file = new File(t1file.toString().replaceAll("t1.txt", "t2.txt"));
				EntailPair ep = new EntailPair(t1file, t2file);
				String outputFileName = outputDir+t1file.getName().replaceAll("t1.txt", "txt");
				
				genFeatureVector(ep, outputFileName);
			}
		}
	} //endof public static void main(String args[]){
	
	//funtion for generating the feature vector of a EntailPair ep to file outputFileName
	public static void genFeatureVector(EntailPair ep, String outputFileName){
		BufferedWriter wr = null;
		String[] featureVector = new String[featureVectorMaxSize];
		for (int i=0; i<featureVectorMaxSize; i++) featureVector[i]="";
		
		try{
			wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFileName)),"UTF8"));
			System.out.println("Output File [" + outputFileName + "] opened for writing."); //debug
			
			//Write label to file
			if(ep.get_t1().get_taskType().equals("bc")) { //binary classification
				if(ep.get_t1().get_label().equals("Y")) wr.write("1 "); //exist entailment
				else wr.write("0 "); //no entailment
			}
			else { //multi-class classification
				if(ep.get_t1().get_label().equals("F")) wr.write("3 "); //forward
				else if(ep.get_t1().get_label().equals("B")) wr.write("2 "); //backward
				else if(ep.get_t1().get_label().equals("C")) wr.write("1 "); //contradict
				else wr.write("0 "); //independent
			}
			
			//----VVVV---- feature vector calculation ----VVVV----
			//[CT-MC-1] features
			featureVector[0] = exclusiveToken(ep.get_t1(), ep.get_t2()); //Amount of tokens in t2 that's not in t1 //1
			featureVector[1] = exclusiveToken(ep.get_t2(), ep.get_t1()); //Amount of tokens in t1 that's not in t2 //2
			featureVector[2] = sentLengthBaseEntailHgtT(ep); //=1 when length(amount of tokens) of t2>t1, =0 else. //3
			featureVector[3] = sentLengthBaseEntailTgtH(ep); //=1 when length(amount of tokens) of t1>t2, =0 else. //4
			
			//[CTI-MC-2] features:
			featureVector[11] = nGramOverlap(ep, 2); //Amount of bi-grams on character level //5
			featureVector[12] = nGramOverlap(ep, 3); //Amount of tri-grams on character level //6
			featureVector[13] = nWordOverlap(ep, 2); //Amount of bi-grams on token level //7
			featureVector[14] = nWordOverlap(ep, 3); //Amount of tri-grams on token level //8
			featureVector[15] = matchCoefficient(ep); //Amount of identical tokens within t1 & t2. //9
			featureVector[16] = LCSsimilarity(ep); //Maximum length of common string(token level) in t1 & t2 then divided by the shortest length of t1 or t2//10
			featureVector[17] = LDist(ep); //Amount of changes needed for t1 to turn into t2 on token level. //11
			featureVector[18] = commonStringOverlap(ep.get_t1(), ep.get_t2()); //Sum of all length of common string divided by length of t2. //12
			featureVector[19] = commonStringOverlap(ep.get_t2(), ep.get_t1()); //Sum of all length of common string divided by length of t1. //13
			featureVector[20] = cosineSimilarity(ep); //14
			
			//[ACL2013] features:
			featureVector[31] = DisconnectRelation(ep.get_t1(), ep.get_t2()); //When all nouns in t2 exist in t1, return the amount of dependencies with same relation but different head and modifier. //15
			featureVector[32] = DisconnectRelation(ep.get_t2(), ep.get_t1()); //When all nouns in t1 exist in t2, return the amount of dependencies with same relation but different head and modifier. //16
			featureVector[33] = MissingArgument(ep.get_t1(), ep.get_t2()); //The amount of nouns in t2 that's not in t1. //17
			featureVector[34] = MissingArgument(ep.get_t2(), ep.get_t1()); //The amount of nouns in t1 that's not in t2. //18
			featureVector[35] = MissingRelation(ep.get_t1(), ep.get_t2()); //The amount of dependencies in t2 that can't find a dependency of the same head in t1. //19
			featureVector[36] = MissingRelation(ep.get_t2(), ep.get_t1()); //The amount of dependencies in t1 that can't find a dependency of the same head in t2. //20
			
			featureVector[81] = DisconnectRelationLoose(ep.get_t1(), ep.get_t2()); //Return the amount of dependencies with same relation but different head and modifier of t2. //41
			featureVector[82] = DisconnectRelationLoose(ep.get_t2(), ep.get_t1()); //Return the amount of dependencies with same relation but different head and modifier of t1. //42
			featureVector[83] = ConnectRelation(ep.get_t1(), ep.get_t2()); //When all nouns in t2 exist in t1, return the amount of dependencies with same relation, head and modifier in the aspect of t2. //43
			featureVector[84] = ConnectRelation(ep.get_t2(), ep.get_t1()); //When all nouns in t1 exist in t2, return the amount of dependencies with same relation, head and modifier in the aspect of t1. //44
			featureVector[83] = ConnectRelationLoose(ep.get_t1(), ep.get_t2()); //Return the amount of dependencies with same relation, head and modifier in the aspect of t2. //45
			featureVector[84] = ConnectRelationLoose(ep.get_t2(), ep.get_t1()); //Return the amount of dependencies with same relation, head and modifier in the aspect of t1. //46
			
			//concept features:
			featureVector[51] = ConceptAmountDiff(ep.get_t1(), ep.get_t2()); //(amount of concepts in t1) - (amount of concepts in t2) //21
			featureVector[52] = ConceptAmountDiff(ep.get_t2(), ep.get_t1()); //(amount of concepts in t1) - (amount of concepts in t2) //22
			featureVector[53] = PredicateMiss(ep.get_t1(), ep.get_t2()); //amount of predicates in t2 that did not appear in t1. //23
			featureVector[54] = PredicateMiss(ep.get_t2(), ep.get_t1()); //amount of predicates in t1 that did not appear in t2. //24
			featureVector[55] = ArgumentMiss(ep.get_t1(), ep.get_t2(), true); //Under the same predicate, the amount of arguments in t2 that is not in t1 determined by t1.argument.contains(t2.argument). //25
			featureVector[56] = ArgumentMiss(ep.get_t2(), ep.get_t1(), true); //Under the same predicate, the amount of arguments in t1 that is not in t2 determined by t2.argument.contains(t1.argument). //26
			featureVector[57] = ArgumentMiss(ep.get_t1(), ep.get_t2(), false); //Under the same predicate, the amount of arguments in t2 that is not in t1 determined by t2.argument.contains(t1.argument). //27
			featureVector[58] = ArgumentMiss(ep.get_t2(), ep.get_t1(), false); //Under the same predicate, the amount of arguments in t1 that is not in t2 determined by t1.argument.contains(t2.argument). //28
			featureVector[59] = ArgumentSRLMiss(ep.get_t1(), ep.get_t2(), true); //Under the same predicate and same SRL of the argument, the amount of arguments in t2 that is not in t1 determined by t1.argument.contains(t2.argument). //29
			featureVector[60] = ArgumentSRLMiss(ep.get_t2(), ep.get_t1(), true); //Under the same predicate and same SRL of the argument, the amount of arguments in t1 that is not in t2 determined by t2.argument.contains(t1.argument). //30
			featureVector[61] = ArgumentSRLMiss(ep.get_t1(), ep.get_t2(), false); //Under the same predicate and same SRL of the argument, the amount of arguments in t2 that is not in t1 determined by t2.argument.contains(t1.argument). //31
			featureVector[62] = ArgumentSRLMiss(ep.get_t2(), ep.get_t1(), false); //Under the same predicate and same SRL of the argument, the amount of arguments in t1 that is not in t2 determined by t1.argument.contains(t2.argument). //32
			featureVector[63] = SRLMissAmount(ep.get_t1(), ep.get_t2(), "ARGM-LOC", true); //Under the same predicate and same SRL("ARGM-LOC") of the argument, the amount of arguments in t2 that is not in t1 determined by t1.argument.contains(t2.argument). //33
			featureVector[64] = SRLMissAmount(ep.get_t2(), ep.get_t1(), "ARGM-LOC", true); //Under the same predicate and same SRL("ARGM-LOC") of the argument, the amount of arguments in t1 that is not in t2 determined by t2.argument.contains(t1.argument). //34
			featureVector[65] = SRLMissAmount(ep.get_t1(), ep.get_t2(), "ARGM-LOC", false); //Under the same predicate and same SRL("ARGM-LOC") of the argument, the amount of arguments in t2 that is not in t1 determined by t2.argument.contains(t1.argument). //35
			featureVector[66] = SRLMissAmount(ep.get_t2(), ep.get_t1(), "ARGM-LOC", false); //Under the same predicate and same SRL("ARGM-LOC") of the argument, the amount of arguments in t1 that is not in t2 determined by t1.argument.contains(t2.argument). //36
			featureVector[67] = SRLMissAmount(ep.get_t1(), ep.get_t2(), "ARGM-TMP", true); //Under the same predicate and same SRL("ARGM-TMP") of the argument, the amount of arguments in t2 that is not in t1 determined by t1.argument.contains(t2.argument). //37
			featureVector[68] = SRLMissAmount(ep.get_t2(), ep.get_t1(), "ARGM-TMP", true); //Under the same predicate and same SRL("ARGM-TMP") of the argument, the amount of arguments in t1 that is not in t2 determined by t2.argument.contains(t1.argument). //38
			featureVector[69] = SRLMissAmount(ep.get_t1(), ep.get_t2(), "ARGM-TMP", false); //Under the same predicate and same SRL("ARGM-TMP") of the argument, the amount of arguments in t2 that is not in t1 determined by t2.argument.contains(t1.argument). //39
			featureVector[70] = SRLMissAmount(ep.get_t2(), ep.get_t1(), "ARGM-TMP", false); //Under the same predicate and same SRL("ARGM-TMP") of the argument, the amount of arguments in t1 that is not in t2 determined by t1.argument.contains(t2.argument). //40
			//----^^^^---- feature vector calculation ----^^^^----
			
			//----VVVV---- output feature vector to file ----VVVV----
			int counter=1;
			for (int i=0; i<featureVectorMaxSize; i++){
				if(!featureVector[i].equals("")){
					wr.write(Integer.toString(counter)+":"+featureVector[i]+" ");
					counter++;
				}
			}
			//----^^^^---- output feature vector to file ----^^^^----
			
			//Flush file
			wr.flush(); wr.close();
		}
		catch(Exception e) {
            System.out.println("Error: " + e.getMessage() );
            e.printStackTrace();
        }
	}
	
	//[CT-MC-1]Exclusive token. return the amount of exclusive token from the aspect of H.
	public static String exclusiveToken(EntailPairUnit epu1, EntailPairUnit epu2){
		int counter = 0;
		String[] t1split = epu1.get_Segmented();
		String[] t2split = epu2.get_Segmented();
		Boolean existToken = false;
		
		for(String curT2:t2split){
			existToken = false;
			for(String curT1:t1split){
				if (curT2.equals(curT1)) {
					existToken = true;
					break;
				}
			}
			if (!existToken) counter++;
		}
		
		return Double.toString(((double) counter)/((double) t2split.length));
	}//endof public static String exclusiveToken(EntailPair ep)
	
	//[CT-MC-1]Sentence Length Based Entailment Recognition, return 1 if T is longer than H, return 0 else.
	public static String sentLengthBaseEntailTgtH(EntailPair ep){
		String result="";
		int t1Leng = ep.get_t1().get_Segmented().length;
		int t2Leng = ep.get_t2().get_Segmented().length;
		
		if (t1Leng > t2Leng) result = "1";
		else result = "0";
		
		return result;
	} //endof public static String sentLengthBaseEntail(EntailPair ep)
	
	//[CT-MC-1]Sentence Length Based Entailment Recognition, return 1 if H is longer than T, return 0 else.
	public static String sentLengthBaseEntailHgtT(EntailPair ep){
		String result="";
		int t1Leng = ep.get_t1().get_Segmented().length;
		int t2Leng = ep.get_t2().get_Segmented().length;
		
		if (t2Leng > t1Leng) result = "1";
		else result = "0";
		
		return result;
	} //endof public static String sentLengthBaseEntail(EntailPair ep)
	
	//[CTI-MC-2]N-gram Overlap, n=2 is bi-gram, n=3 is tri-gram
	public static String nGramOverlap(EntailPair ep, int n){
		String t1Ori = ep.get_t1().get_OriSent();
		String t2Ori = ep.get_t1().get_OriSent();
		int counter = 0;
		
		int startIndex = 0;
		int endIndex = startIndex+n;
		
		while (endIndex <= t1Ori.length()){
			String t1sub = t1Ori.substring(startIndex, endIndex);
			if (t2Ori.contains(t1sub)) counter++;
			startIndex++;
			endIndex++;
		}
		
		if(t1Ori.length() > t2Ori.length()) return Double.toString(counter/t2Ori.length());
		else return Double.toString(((double) counter)/((double) t1Ori.length()));
	}
	
	//[CTI-MC-2]Word Overlap
	public static String nWordOverlap(EntailPair ep, int n){
		String[] t1Split = ep.get_t1().get_Segmented();
		String[] t2Split = ep.get_t2().get_Segmented();
		int counter=0;
		
		int startIndext1 = 0;
		int endIndext1 = startIndext1+n-1;
		
		while(endIndext1 < t1Split.length){
			String t1Sub="";
			for(int i=startIndext1; i<=endIndext1; i++) t1Sub+=t1Split[i];
			
			int startIndext2 = 0;
			int endIndext2 = startIndext2+n-1;
			while(endIndext2 < t2Split.length){
				String t2Sub="";
				for(int i=startIndext2; i<=endIndext2; i++) t2Sub+=t2Split[i];
				
				if (t1Sub.equals(t2Sub)) counter++;
				
				startIndext2++;
				endIndext2++;
			}
			
			startIndext1++;
			endIndext1++;
		}
		
		if(t1Split.length > t2Split.length) return Double.toString(counter/t2Split.length);
		else return Double.toString(((double) counter)/((double) t1Split.length));
	}
	
	//[CTI-MC-2]Matching Coefficient, amount of Segmented tokens that match.
	public static String matchCoefficient(EntailPair ep){
		String[] t1Split = ep.get_t1().get_Segmented();
		String[] t2Split = ep.get_t2().get_Segmented();
		int counter=0;
		
		for(String elementT1:t1Split)
			for(String elementT2:t2Split)
				if (elementT1.equals(elementT2)) counter++;
		
		if(t1Split.length > t2Split.length) return Double.toString(counter/t2Split.length);
		else return Double.toString(((double) counter)/((double) t1Split.length));
	}
	
	//[CTI-MC-2]LCS Similarity
	public static String LCSsimilarity(EntailPair ep){
		int maxLength = 0;
		int t1Leng = ep.get_t1().get_Segmented().length;
		int t2Leng = ep.get_t2().get_Segmented().length;
		
		for(int i=1; i<ep.get_t1().get_OriSent().length(); i++){
			if(nGramOverlap(ep, i).equals("0")==false) maxLength=i;
		}
		
		if (t1Leng > t2Leng) return Double.toString(((double) maxLength)/((double) t2Leng));
		else return Double.toString(((double) maxLength)/((double) t1Leng));
	}
	
	//[CTI-MC-2]Levenshtein Distance at token level.
	public static String LDist(EntailPair ep){
		String[] tokens = new String[cosSimVectorSize];
		int[] t1Count = new int[cosSimVectorSize];
		int[] t2Count = new int[cosSimVectorSize];
		String[] t1Split = ep.get_t1().get_Segmented();
		String[] t2Split = ep.get_t2().get_Segmented();
		int modCount=0;
		
		for(int i=0; i<cosSimVectorSize; i++){ //Initialize
			tokens[i]="";
			t1Count[i]=0;
			t2Count[i]=0;
		}
		
		for(int j=0; j<t1Split.length; j++){ //Generate vector for t1
			for(int i=0; i<cosSimVectorSize; i++){
				if(tokens[i].equals(t1Split[j])){
					t1Count[i]++;
					break;
				}
				else if(tokens[i].equals("")){
					tokens[i] = t1Split[j];
					t1Count[i]++;
					break;
				}
			}
		}
		for(int j=0; j<t2Split.length; j++){ //Generate vector for t2
			for(int i=0; i<cosSimVectorSize; i++){
				if(tokens[i].equals(t2Split[j])){
					t2Count[i]++;
					break;
				}
				else if(tokens[i].equals("")){
					tokens[i] = t2Split[j];
					t2Count[i]++;
					break;
				}
			}
		}
		
		for(int i=0; i<cosSimVectorSize; i++){ //Calculate Levenshtein Distance
			if(tokens[i].equals("")) break;
			if(t1Count[i]!=t2Count[i]){
				modCount += Math.abs(t1Count[i]-t2Count[i]);
			}
		}
		
		return Integer.toString(modCount);
	}
	
	//[CTI-MC-2]Common String Overlap
	public static String commonStringOverlap(EntailPairUnit epu1, EntailPairUnit epu2){
		String t1Ori = epu1.get_OriSent();
		String t2Ori = epu2.get_OriSent();
		int counter = 0;
		
		for(int i=1; i<t1Ori.length(); i++){
			int startIndex = 0;
			int endIndex = startIndex+i;
			while(endIndex<=t1Ori.length()){
				if(t2Ori.contains(t1Ori.substring(startIndex, endIndex))) counter+=i;
				startIndex++;
				endIndex++;
			}
		}
		
		return Double.toString(((double) counter)/((double) t2Ori.length()));
	}
	
	//----VVVV----[CTI-MC-2]Cosine similarity, base on word frequency with supposing a base vector space of 3000 dimensions.----VVVV----
	public static String cosineSimilarity(EntailPair ep){
		String result= "0";
		String[] tokens = new String[cosSimVectorSize];
		int[] t1Count = new int[cosSimVectorSize];
		int[] t2Count = new int[cosSimVectorSize];
		String[] t1Split = ep.get_t1().get_Segmented();
		String[] t2Split = ep.get_t2().get_Segmented();
		
		for(int i=0; i<cosSimVectorSize; i++){
			tokens[i]="";
			t1Count[i]=0;
			t2Count[i]=0;
		}
		
		for(int j=0; j<t1Split.length; j++){
			for(int i=0; i<cosSimVectorSize; i++){
				if(tokens[i].equals(t1Split[j])){
					t1Count[i]++;
					break;
				}
				else if(tokens[i].equals("")){
					tokens[i] = t1Split[j];
					t1Count[i]++;
					break;
				}
			}
		}
		for(int j=0; j<t2Split.length; j++){
			for(int i=0; i<cosSimVectorSize; i++){
				if(tokens[i].equals(t2Split[j])){
					t2Count[i]++;
					break;
				}
				else if(tokens[i].equals("")){
					tokens[i] = t2Split[j];
					t2Count[i]++;
					break;
				}
			}
		}
		
		result = Double.toString(dot(t1Count, t2Count)/(norm(t1Count)*norm(t2Count)));
		
		return result;
	}

	private static double dot(int[] a, int[] b){
		int vectorSize = a.length;
		Double sum = 0.0;
		
		for(int i=0; i<vectorSize; i++)
			sum += a[i]*b[i];
		
		return sum;
	}
	
	private static double norm(int[] a){
		int vectorSize = a.length;
		Double sum = 0.0;
		
		for(int i=0; i<vectorSize; i++)
			sum += a[i] * a[i];
		
		return Math.sqrt(sum);
	}
	//----^^^^----[CTI-MC-2]Cosine similarity, base on word frequency with supposing a base vector space of 3000 dimensions.----^^^^----
	
	//[ACL2013] Noun List generation
	private static String[] getNounSeg(EntailPairUnit epu){
		String nounList = "";
		for(int i=0; i<epu.get_pos().length; i++){
			if(epu.get_pos()[i].contains("NN") || epu.get_pos()[i].contains("NR") || epu.get_pos()[i].contains("NT")){
				if(!nounList.contains(epu.get_Segmented()[i]))
					nounList += epu.get_Segmented()[i]+" ";
			}
		}
		
		return nounList.trim().split(" ");
	}
	
	//[ACL2013] Disconnected Relation: H=t2, T=t1
	public static String DisconnectRelation(EntailPairUnit epu1, EntailPairUnit epu2){
		String[] t1Noun = getNounSeg(epu1);
		String[] t2Noun = getNounSeg(epu2);
		
		String sameNounList="";
		for(int i=0; i<t2Noun.length; i++){
			for(String element:t1Noun){
				if (t2Noun[i].equals(element)){
					sameNounList+=element+" ";
				}
			}
		}
		String[] sameNoun = sameNounList.trim().split(" ");
		if(sameNoun.length != t2Noun.length) return "-1"; //making sure all nouns in t2Noun is in t1Noun
		
		int counter=0;
		for(String element:epu2.get_Dependency()){
			DependUnit d2 = new DependUnit(element);
			for(String element2: epu1.get_Dependency()){
				DependUnit d1 = new DependUnit(element2);
				if(d1.relation.equals(d2.relation)){
					if((!d1.head.equals(d2.head))&&(!d1.mod.equals(d2.mod))){
						counter++;
					}
				}
			}
		}
		
		return Integer.toString(counter);
	}
	
	//Disconnected Relation: without the restriction of same noun list.
	public static String DisconnectRelationLoose(EntailPairUnit epu1, EntailPairUnit epu2){
		int counter=0;
		for(String element:epu2.get_Dependency()){
			DependUnit d2 = new DependUnit(element);
			for(String element2: epu1.get_Dependency()){
				DependUnit d1 = new DependUnit(element2);
				if(d1.relation.equals(d2.relation)){
					if((!d1.head.equals(d2.head))&&(!d1.mod.equals(d2.mod))){
						counter++;
					}
				}
			}
		}
		
		return Integer.toString(counter);
	}
	
	//When all nouns in t2 exist in t1, return the amount of dependencies with same relation, head and modifier in the aspect of t2.
	public static String ConnectRelation(EntailPairUnit epu1, EntailPairUnit epu2){
		String[] t1Noun = getNounSeg(epu1);
		String[] t2Noun = getNounSeg(epu2);
		
		String sameNounList="";
		for(int i=0; i<t2Noun.length; i++){
			for(String element:t1Noun){
				if (t2Noun[i].equals(element)){
					sameNounList+=element+" ";
				}
			}
		}
		String[] sameNoun = sameNounList.trim().split(" ");
		if(sameNoun.length != t2Noun.length) return "-1"; //making sure all nouns in t2Noun is in t1Noun
		
		int counter=0;
		for(String element:epu2.get_Dependency()){
			DependUnit d2 = new DependUnit(element);
			for(String element2: epu1.get_Dependency()){
				DependUnit d1 = new DependUnit(element2);
				if(d1.relation.equals(d2.relation)){
					if((d1.head.equals(d2.head))&&(d1.mod.equals(d2.mod))){
						counter++;
						continue;
					}
				}
			}
		}
		
		return Integer.toString(counter);
	}
	
	//Return the amount of dependencies with same relation, head and modifier in the aspect of t2.
	public static String ConnectRelationLoose(EntailPairUnit epu1, EntailPairUnit epu2){
		int counter=0;
		for(String element:epu2.get_Dependency()){
			DependUnit d2 = new DependUnit(element);
			for(String element2: epu1.get_Dependency()){
				DependUnit d1 = new DependUnit(element2);
				if(d1.relation.equals(d2.relation)){
					if((d1.head.equals(d2.head))&&(d1.mod.equals(d2.mod))){
						counter++;
						continue;
					}
				}
			}
		}
		
		return Integer.toString(counter);
	}
	
	//[ACL2013] Missing Argument: the amount of nouns in epu2 that is not in epu1.
	public static String MissingArgument(EntailPairUnit epu1, EntailPairUnit epu2){
		String[] t1Noun = getNounSeg(epu1);
		String[] t2Noun = getNounSeg(epu2);
		
		String sameNounList="";
		for(int i=0; i<t2Noun.length; i++){
			for(String element:t1Noun){
				if (t2Noun[i].equals(element)){
					sameNounList+=element+" ";
				}
			}
		}
		
		if(sameNounList.trim().equals("")){
			return Integer.toString(t2Noun.length);
		}
		else{
			String[] sameNoun = sameNounList.trim().split(" ");
			if(sameNoun.length != t2Noun.length) return Integer.toString(t2Noun.length-sameNoun.length);
			else return "0";
		}
	}
	
	//[ACL2013] Missing Relation: the amount of dependencies in t2 that can't find a dependency of the same head in t1.
	public static String MissingRelation(EntailPairUnit epu1, EntailPairUnit epu2){
		int counter=0;
		Boolean noMatch = true;
		for(String element:epu2.get_Dependency()){
			DependUnit d2 = new DependUnit(element);
			DependUnit d1;
			noMatch = true;
			for(String element2: epu1.get_Dependency()){
				d1 = new DependUnit(element2);
				if(d2.head.equals(d1.head)){
					noMatch = false;
					break;
				}
			}
			if(noMatch) 
				counter++;
		}
		
		return Integer.toString(counter);
	}
	
	//[Concepts] Concept Amount Difference: (amount of concepts in  T) - (amount of concepts in H)
	public static String ConceptAmountDiff(EntailPairUnit T, EntailPairUnit H){
		return Integer.toString(T.get_conceptCount()-H.get_conceptCount());
	}
	
	//[Concepts] Predicate miss amount: amount of predicates in H that did not appear in T.
	public static String PredicateMiss(EntailPairUnit T, EntailPairUnit H){
		int counter = 0;
		if(H.get_conceptCount()>0 && T.get_conceptCount()>0){ //both sentence's concepts amount have to be more than 0.
			String handledPred = "";
			for(Concepts element:H.get_concepts()){
				if(handledPred.contains(element.pred) == false){
					boolean samePred=false;
					handledPred+=element.pred+" ";
					
					for(Concepts ele:T.get_concepts()){
						if(ele.pred.equals(element.pred)){
							samePred=true;
							break;
						}
					}
					
					if(samePred) samePred=false;
					else counter++;
				}
			}
			
			return Integer.toString(counter);
		}
		else
			return "-1";
	}
	
	//[Concepts] Argument miss amount: under the same predicate, the amount of arguments in H that is not in T. inclusiveDirection=true represents the inclusive direction is T.argument.contains(H.argument).
	public static String ArgumentMiss(EntailPairUnit T, EntailPairUnit H, boolean inclusiveDirection){
		int counter = 0;
		if(H.get_conceptCount()>0 && T.get_conceptCount()>0){ //both sentence's concepts amount have to be more than 0.
			for(Concepts element:H.get_concepts()){
				boolean sameArg=false;
				
				for(Concepts ele:T.get_concepts()){
					if(ele.pred.equals(element.pred)){
						if(inclusiveDirection){
							if(ele.argument.contains(element.argument)){
								sameArg=true;
								break;
							}
						}
						else{
							if(element.argument.contains(ele.argument)){
								sameArg=true;
								break;
							}
						}
					}
				}
				
				if(sameArg) sameArg=false;
				else counter++;
			}
			
			return Integer.toString(counter);
		}
		else
			return "-1";
	}
	
	//[Concepts] Argument and SRL miss amount: under the same predicate, the amount of arguments in H that is 1) not in T, or 2) exist but not the same SRL.
	public static String ArgumentSRLMiss(EntailPairUnit T, EntailPairUnit H, boolean inclusiveDirection){
		int counter = 0;
		if(H.get_conceptCount()>0 && T.get_conceptCount()>0){ //both sentence's concepts amount have to be more than 0.
			for(Concepts element:H.get_concepts()){
				boolean sameArg=false;
				
				for(Concepts ele:T.get_concepts()){
					if(ele.pred.equals(element.pred) && ele.srl.equals(element.srl)){
						if(inclusiveDirection){
							if(ele.argument.contains(element.argument)){
								sameArg=true;
								break;
							}
						}
						else{
							if(element.argument.contains(ele.argument)){
								sameArg=true;
								break;
							}
						}
					}
				}
				
				if(sameArg) sameArg=false;
				else counter++;
			}
			
			return Integer.toString(counter);
		}
		else
			return "-1";
	}
	
	//[Concepts] Certain SRL miss-match amount: amount of argument in H with certain SRL(ARGM-TMP, ARGM-LOC) that does not have a match in T.
	public static String SRLMissAmount(EntailPairUnit T, EntailPairUnit H, String SRL, boolean inclusiveDirection){
		int counter = 0;
		if(H.get_conceptCount()>0 && T.get_conceptCount()>0){ //both sentence's concepts amount have to be more than 0.
			for(Concepts element:H.get_concepts()){
				if(element.srl.equals(SRL)){
					boolean sameArg=false;
					
					for(Concepts ele:T.get_concepts()){
						if(ele.srl.equals(element.srl)){
							if(inclusiveDirection){
								if(ele.argument.contains(element.argument)){
									sameArg=true;
									break;
								}
							}
							else{
								if(element.argument.contains(ele.argument)){
									sameArg=true;
									break;
								}
							}
						}
					}
					
					if(sameArg) sameArg=false;
					else counter++;
				}
			}
			
			return Integer.toString(counter);
		}
		else
			return "-1";
	}
}
