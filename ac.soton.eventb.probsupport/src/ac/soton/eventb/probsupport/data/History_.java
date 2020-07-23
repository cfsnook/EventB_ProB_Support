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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A data structure for the current history of an animation
 * 
 * This is a list of history items, each consisting of an operation and a state.
 * 	I.e. the state after the operation invocation
 * The first history item will be the setup
 * 
 * @author cfsnook
 *
 */
public class History_ {
	
	public class HistoryItem_ {
		public Operation_ operation;
		public State_ state;
		
		private HistoryItem_(Operation_ operation, State_ state) {
			this.operation = operation;
			this.state = state;
		}
	}
	
	private List<HistoryItem_> history = new ArrayList<HistoryItem_>();
	
	/**
	 * construct and add a new item to the history
	 * 
	 * @param operation
	 * @param state
	 */
	public void addItem(Operation_ operation, State_ state) {
		history.add(new HistoryItem_(operation, state));
	}
	
	/**
	 * returns the number of history items in the history 
	 * @return
	 */
	public int size() {
		return history.size();
	}
	
	/**
	 * gets the history item at the given index
	 * 
	 * @param index
	 * @return
	 */
	public HistoryItem_ getitem(int index){
		return history.get(index);
	}
	
	/**
	 * returns an (unmodifiable) list of all the history items
	 * @return
	 */
	public List<HistoryItem_> getAllItems(){
		return Collections.unmodifiableList(history);
	}
	
}

