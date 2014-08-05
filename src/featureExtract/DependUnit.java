package featureExtract;

public class DependUnit {
	String relation;
	String head;
	int headOrder;
	String mod;
	int modOrder;
	
	//Constructor
	public DependUnit(String input){ //input must be in the format of Stanford parser dependency.
		String tmp = input.split(" ")[0];
		relation = tmp.substring(0, tmp.indexOf("("));
		head=tmp.substring(tmp.indexOf("(")+1, tmp.lastIndexOf("-"));
		headOrder = Integer.valueOf(tmp.substring(tmp.lastIndexOf("-")+1, tmp.indexOf(",", tmp.lastIndexOf("-")+1)));
		tmp = input.split(" ")[1];
		mod=tmp.substring(0, tmp.lastIndexOf("-"));
		modOrder = Integer.valueOf(tmp.substring(tmp.lastIndexOf("-")+1, tmp.length()-1));
	}
}
