package ac.soton.eventb.probsupport;

import de.prob.core.IAnimationListener;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;

public class AnimationListener implements IAnimationListener{

	/**
	 * called by the Animation Listener for ProB when the state has changed
	 * Since the order of notifications of state changes can vary we use the history 
	 * and only update when the history contains information past the trace point of the last update
	 * The oracle is only updated for external operations.
	 * 
	 */
		public void currentStateChanged(State activeState, Operation operation) {
			AnimationManager.stateChanged();
		}	

		
}
