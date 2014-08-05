package testCode;

import java.io.*;

//CT to CS transfer
import taobe.tec.jcc.*;

public class TestReadFile {
	//Numerical form usage
	static String[] basicNumCT = {"零","ㄧ","二","三","四","五","六","七","八","九","两","一"};
	static String[] basicNumEn = {"0","1","2","3","4","5","6","7","8","9","2","1"};
	static String[] timesNumCT = {"十","百","千","万","亿","兆"};
	static String[] timesNumEn = {"10","100","1000","10000","100000000","1000000000000"};
		
	public static void main(String[] args) {
		String testSent = "干炒牛河是广东菜色的一种。";
		try {
			testSent = JChineseConvertor.getInstance().t2s(testSent);
			System.out.println("Sent = "+testSent);
			System.out.println("After process = "+numericProcess(testSent));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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
		String[] numStrIndex=indexTmp.split(" "); //array storing the 
		
		for(String element:numStrIndex){
			int numStartIndex = Integer.valueOf(element.split("_")[0]);
			int numEndIndex = Integer.valueOf(element.split("_")[1]);
			
			if(unitWordChk(numEndIndex, sent)==true){ //
				String chNum = sent.substring(numStartIndex, numEndIndex);
				result = result.replaceFirst(chNum, chToNum(chNum));
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
