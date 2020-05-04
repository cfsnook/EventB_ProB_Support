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

/**
 * A listener for ProB to call.
 * This just defers to the Animation manager
 * 
 * @author cfsnook
 *
 */
public class AnimationListener implements IAnimationListener{

	/**
	 * called by the Animation Listener for ProB when the state has changed
	 * 
	 * This defers to the Animation Manager.
	 * 
	 */
		public void currentStateChanged(State activeState, Operation operation) {
			AnimationManager.stateChanged();
		}	

}
