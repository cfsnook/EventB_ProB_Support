/*******************************************************************************
 *  Copyright (c) 2020-2020 University of Southampton.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *   
 *  Contributors:
 *  University of Southampton - Initial implementation
 *******************************************************************************/

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
