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

	void startAnimating(IMachineRoot mchRoot);
	
	void stopAnimating(IMachineRoot mchRoot);
	
	void updateAnimation(IMachineRoot mchRoot);

	void restartAnimation(IMachineRoot mchRoot);
	
}
