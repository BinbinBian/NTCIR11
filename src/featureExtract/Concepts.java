package featureExtract;

public class Concepts {
	public String pred;
	public String argument;
	public String srl;
	
	public Concepts(String input){
		String[] tmp = input.split("::pred,");
		pred = tmp[0];
		argument = tmp[1].split("::")[0];
		srl = tmp[1].split("::")[1];
	}
}
