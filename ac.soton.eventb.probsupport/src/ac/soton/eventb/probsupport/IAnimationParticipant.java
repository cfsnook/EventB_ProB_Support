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

public interface IAnimationParticipant {

	void startAnimating(IMachineRoot mchRoot);
	
	void stopAnimating(IMachineRoot mchRoot);
	
}
