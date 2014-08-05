package featureExtract;

import java.io.*;
import featureExtract.EntailPairUnit;

public class EntailPair {
	private EntailPairUnit t1;
	private EntailPairUnit t2;
	
	//Constructor
	public EntailPair(File t1File, File t2File){
		t1 = new EntailPairUnit(t1File);
		t2 = new EntailPairUnit(t2File);
	}
	
	public EntailPairUnit get_t1(){
		return t1;
	}
	
	public EntailPairUnit get_t2(){
		return t2;
	}
}
