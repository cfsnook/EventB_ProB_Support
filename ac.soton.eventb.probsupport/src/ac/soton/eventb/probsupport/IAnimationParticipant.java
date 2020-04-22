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

import org.eventb.core.IMachineRoot;

/**
 * The interface for animation participants
 * 
 * An implementation of this interface must be declared in the extension point
 * of each animation participant.
 * 
 * @author cfsnook
 *
 */
public interface IAnimationParticipant {

	/**
	 * Start a new animation for the given machine root.
	 * 
	 * @param mchRoot
	 */
	void startAnimation(IMachineRoot mchRoot);
	
	/**
	 * Stop the current animation of the given machine root.
	 * 
	 * @param mchRoot
	 */
	void stopAnimation(IMachineRoot mchRoot);
	
	/**
	 * Update the current animation of the given machine root.
	 * 
	 * (This is called after ProB has notified the animation manager's listener).
	 * 
	 * The participant should update by calling the animation manager to get the current state,
	 * enabled operations and history.
	 * 
	 * @param mchRoot
	 */
	void updateAnimation(IMachineRoot mchRoot);

	/**
	 * Restart the current animation of the given machine root.
	 * 
	 * (This is called after the animation manager has restarted ProB animation of the given machine root).
	 * 
	 * @param mchRoot
	 */
	void restartAnimation(IMachineRoot mchRoot);
	
}
