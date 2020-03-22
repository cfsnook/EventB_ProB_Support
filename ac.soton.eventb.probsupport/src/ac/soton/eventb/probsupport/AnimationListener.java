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
