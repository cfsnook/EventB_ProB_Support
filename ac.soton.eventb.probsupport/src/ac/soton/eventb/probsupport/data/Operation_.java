package ac.soton.eventb.probsupport.data;

import java.util.List;

public class Operation_ {

	private String name;
	private List<String> arguments;
	
	public Operation_(String name, List<String> arguments) {
		this.name = name;
		this.arguments = arguments;
		
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getArguments(){
		return arguments;
	}
	
	/**
	 * 
	 * @return
	 */
	public String inStringFormat() {
		//String s1 = toString();
		//String s1 = name+arguments;
		return name+" "+arguments; //toString().replaceFirst("\\(", " (");
	}
}

