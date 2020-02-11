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

package ac.soton.eventb.probsupport.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IMachineRoot;

public class StartHandler extends AbstractHandler implements IHandler {

//	@Override
//	public void addHandlerListener(IHandlerListener handlerListener) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void dispose() {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
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
		
		// If a machine is selected, start the animations for it
		if (mchRoot != null) {
			AnimationManager.startAnimation(mchRoot);
		}

		return null;
	}

//	@Override
//	public boolean isEnabled() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean isHandled() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void removeHandlerListener(IHandlerListener handlerListener) {
//		// TODO Auto-generated method stub
//
//	}

}
