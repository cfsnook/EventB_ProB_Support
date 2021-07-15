/*******************************************************************************
 * Copyright (c) 2020, 2020 University of Southampton.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    University of Southampton - initial API and implementation
 *******************************************************************************/

package ac.soton.eventb.probsupport.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A data structure for the current state of an animation
 * 
 * This is a map of variable identifiers to values (as strings)
 * 
 * @author cfsnook
 *
 */
public class State_ {

	private Map<String,String> state = new HashMap<String,String>();
	

	/**
	 * adds a name, value pair to the state
	 * @param id
	 * @param value
	 */
	public void add(String id, String value) {
		state.put(id,value);
	}
	
	/**
	 * returns the number of data items (pairs) in the state
	 * @return
	 */
	public int size() {
		return state.size();
	}
	
	/**
	 * tests whether the state contains a pair with the given id
	 * @param id
	 * @return
	 */
	public boolean contains(String id){
		return state.containsKey(id);
	}
	
	/**
	 * returns the value associated with the given id
	 * or null if the id is not in the state.
	 * (note that null will also be returned if the pair 'id,null' has been added to the state)
	 * 
	 * @param id
	 * @return
	 */
	public String getValue(String id){
		return state.get(id);
	}
	
	/**
	 * returns a map representing all id,value pairs in the state
	 * @return
	 */
	public Map<String,String> getAllValues(){
		return Collections.unmodifiableMap(state);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object st) {
		if (st instanceof State_ &&
				((State_)st).getAllValues().equals(this.getAllValues())
				) {
			return true;
		}
		return false;
	}
	
}
