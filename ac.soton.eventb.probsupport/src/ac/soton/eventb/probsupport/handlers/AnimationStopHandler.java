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

package ac.soton.eventb.probsupport.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IMachineRoot;

import ac.soton.eventb.probsupport.AnimationManager;

/**
 * The Handler for animation stop commands.
 * This assumes that an IMachineRoot is selected (e.g. in the Event-B Navigator)
 * 
 * 	 Client handlers can override this to provide other ways to obtain a machine from whatever is selected
 * 	(The plugin.xml should declare the specialised handler is for command id "ac.soton.eventb.probsupport.StopAnimation"
 * 			as well as when the specialised handler is enabled)
 * 
 * @author cfsnook
 *
 */
public class AnimationStopHandler extends AbstractHandler implements IHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public final Object execute(ExecutionEvent event) throws ExecutionException {
		
		IMachineRoot mchRoot = getRoot(event);
		
		// If a machine is selected, stop the animations for it
		if (mchRoot != null) {
			AnimationManager.stopAnimation(mchRoot);
		}

		return null;
	}

	/**
	 * Client handlers can override this to provide other ways to obtain a machine from whatever is selected
	 * 
	 * This default implementation returns the machine, if any, which is the first selection in the structured selection
	 * associated with the given event. Otherwise returns null.
	 * 
	 * @param event
	 * @return
	 * @throws ExecutionException
	 */
	protected IMachineRoot getRoot(ExecutionEvent event) throws ExecutionException {
		// Get the selected Event-B machine
		ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		IMachineRoot mchRoot = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				if (obj instanceof IMachineRoot) {
					mchRoot = (IMachineRoot) obj;
				}
			}
		}
		return mchRoot;
	}

}