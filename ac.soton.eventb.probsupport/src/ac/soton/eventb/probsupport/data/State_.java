package ac.soton.eventb.probsupport.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class State_ {

	private Map<String,String> state = new HashMap<String,String>();
	
	public void add(String id, String value) {
		state.put(id,value);
	}
	
	public int size() {
		return state.size();
	}
	
	public boolean contains(String id){
		return state.containsKey(id);
	}
	
	public String getValue(String id){
		return state.get(id);
	}
	
	public Map<String,String> getAllValues(){
		return Collections.unmodifiableMap(state);
	}
	
}
