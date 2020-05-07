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
	
//	/**
//	 * 
//	 * @param proBHistory
//	 */
//	public History_(History proBHistory) {
//		//History_ history = new History_();
//		HistoryItem[] historyItems = proBHistory.getAllItems();
//		int currentPos = proBHistory.getCurrentPosition();
//		for (int i=0; i<currentPos; i++) {
//			addItem(new Operation_(historyItems[i].getOperation()), new State_(historyItems[i+1].getState()));
//		}
//	}
	
	public void addItem(Operation_ operation, State_ state) {
		history.add(new HistoryItem_(operation, state));
	}
	
	public int size() {
		return history.size();
	}
	
	public HistoryItem_ getitem(int index){
		return history.get(index);
	}
	
	public List<HistoryItem_> getAllItems(){
		return Collections.unmodifiableList(history);
	}
	
}

