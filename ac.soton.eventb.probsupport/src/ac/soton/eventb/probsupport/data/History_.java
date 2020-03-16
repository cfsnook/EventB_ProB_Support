package ac.soton.eventb.probsupport.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

