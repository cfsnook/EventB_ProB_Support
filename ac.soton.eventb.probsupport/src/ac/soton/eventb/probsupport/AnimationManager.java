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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IMachineRoot;

import ac.soton.eventb.probsupport.data.History_;
import ac.soton.eventb.probsupport.data.Operation_;
import ac.soton.eventb.probsupport.data.State_;
import de.prob.core.Animator;
import de.prob.core.command.ExecuteOperationCommand;
import de.prob.core.command.LoadEventBModelCommand;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.HistoryItem;
import de.prob.core.domainobjects.MachineDescription;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.core.domainobjects.Variable;
import de.prob.exceptions.ProBException;

public class AnimationManager {

	public static int historyPosition;
	public static IMachineRoot mchRoot=null;

	/**
	 * Start Animations for the given machine root
	 * 
	 * This starts a new ProB animation on the given machine and then 
	 * calls the startAnimating method of all registered animation participants.
	 * 
	 * @param mchRoot
	 */
	public static void startAnimation(IMachineRoot mchRoot) {
		if (AnimationManager.mchRoot!=null) return;
		try {
			//refresh project in workspace (avoids ProB errors)
			IProject project = mchRoot.getRodinProject().getProject();
			project.refreshLocal(IResource.DEPTH_ONE, null);
			
			// start ProB animation
			System.out.println("Starting ProB for " + mchRoot.getHandleIdentifier());
			LoadEventBModelCommand.load(Animator.getAnimator(), mchRoot);
			
		} catch (CoreException e) {  	//refreshLocal
			e.printStackTrace();
			Activator.logError("Animation aborted: Failed to refresh project " + mchRoot.getRodinProject().getHandleIdentifier(), e);
			System.out.println("Animation aborted: Failed to refresh project " + mchRoot.getRodinProject().getHandleIdentifier());
		} catch (ProBException e) {		//load
			e.printStackTrace();
			Activator.logError("Animation aborted: Failed to start ProB for machine " + mchRoot.getHandleIdentifier(), e);
			System.out.println("Animation aborted: Failed to start ProB for machine " + mchRoot.getHandleIdentifier());
		}
		AnimationManager.mchRoot = mchRoot;
		AnimationManager.historyPosition = 0;
		//tell the participants to start
		for (IAnimationParticipant participant : Activator.getParticipants()) {
			System.out.println("Starting participant "+Activator.getParticipantID(participant) +" for " + mchRoot.getHandleIdentifier());
			participant.startAnimating(mchRoot);	
		}	
		
	}
	
	/**
	 * Stop Animations for the given machine root
	 * 
	 * Calls the stopAnimating method of all registered animation participants.
	 * 
	 * @param mchRoot
	 */
	public static void stopAnimation (IMachineRoot mchRoot) {
		if (AnimationManager.mchRoot==null) return;
		if (mchRoot.equals(AnimationManager.mchRoot)) {
			//tell participants they can stop
			for (IAnimationParticipant participant : Activator.getParticipants()) {
				participant.stopAnimating(mchRoot);
			}
			AnimationManager.mchRoot=null;
			AnimationManager.historyPosition = 0;
		}
	}
	
	public static void restartAnimation (IMachineRoot mchRoot) {
		if (mchRoot.equals(AnimationManager.mchRoot)) {
			stopAnimation(mchRoot);
			startAnimation(mchRoot);
		}
	}
	
	/**
	 * Checks whether the given machine root is being animated
	 * 
	 * @param mchRoot
	 * @return
	 */
	public static boolean isRunning(IMachineRoot mchRoot) {
		return  mchRoot!=null && mchRoot.equals(AnimationManager.mchRoot);
	}

	/**
	 * This is called by the Animation Listener when ProB has a state change.
	 * It tells the participants to update themselves. 
	 * The participants should call back to this AnimationManager to obtain the information they need to update
	 * 
	 */
	public static void stateChanged() {
		if (mchRoot==null) 
			return;
		History history = getAnimator(mchRoot).getHistory();
		MachineDescription mch = getAnimator(mchRoot).getMachineDescription();
//		if (AnimationManager.historyPosition ==0 || history.getCurrentPosition()>AnimationManager.historyPosition) {
			//tell the participants to update
			for (IAnimationParticipant participant : Activator.getParticipants()) {
				System.out.println("Updating participant "+Activator.getParticipantID(participant) +" for " + mchRoot.getHandleIdentifier());
				participant.updateAnimation(mchRoot);	
			}	
			historyPosition = history.getCurrentPosition();
//		}
	}
	
	
	/**
	 * Returns the current state of the given animation
	 * @param mchRoot
	 * @return
	 */
	public static State_ getCurrentState(IMachineRoot mchRoot) {
		Animator animator = getAnimator(mchRoot);
		return convert(animator.getCurrentState());
	}
	
	/**
	 * Returns the history of the given animation as a Map
	 * @param mchRoot
	 * @return
	 */
	public static History_ getHistory(IMachineRoot mchRoot) {
		Animator animator = getAnimator(mchRoot);
		History proBHistory = animator.getHistory();
		return (convert(proBHistory));
	}

	public static List<Operation_> getEnabledOperations(IMachineRoot mchRoot) {
		Animator animator = getAnimator(mchRoot);
		State currentState = animator.getCurrentState();
		
		History hist  = animator.getHistory();
		
		int pos = animator.getHistory().getCurrentPosition();
		int size  = animator.getHistory().size();
		HistoryItem hi = hist.getHistoryItem(pos);
		HistoryItem[] all = hist.getAllItems();
		State histstate = all[pos].getState();
		
		List<Operation_> enabledOperations = new ArrayList<Operation_>();
		// for each enabled operation in the ProB model create an operation_
		for(Operation proBop: currentState.getEnabledOperations()){
			enabledOperations.add(convert(proBop));
			}
		return enabledOperations;
	}
	
	public static void executeOperation(IMachineRoot mchRoot, Operation_ operation, boolean silent)  {
		Animator animator = getAnimator(mchRoot);
		Operation proBOperation = getOperation(animator, operation);
		try {
			ExecuteOperationCommand.executeOperation(animator, proBOperation, silent);
		} catch (ProBException e) {
			e.printStackTrace();
		}
	}
	
	////// private helper methods
	
	private static Animator getAnimator(IMachineRoot mchRoot) {
		return Animator.getAnimator();
	}
	
	private static Operation getOperation(Animator animator, Operation_ operation) {
		List<Operation> enabledOps = animator.getCurrentState().getEnabledOperations();
		for (Operation enabled : enabledOps) {
			if (enabled.getName().equals(operation.getName()) &&
					enabled.getArguments().size()==operation.getArguments().size()) {
				boolean match = true;
				for (int i=0; i<enabled.getArguments().size(); i++ ) {
					if (!enabled.getArguments().get(i).equals(operation.getArguments().get(i))) {
						match=false;
						break;
					}
				}
				if (match == true) return enabled;
			}
		}
		return null;
	}

	private static Operation_ convert(Operation proBOperation) {
//		String opname = proBOperation.getName();
//		List<String> args = proBOperation.getArguments();
		return new Operation_(proBOperation.getName(), proBOperation.getArguments());
	}
	
	private static State_ convert(State proBState) {
		State_ state = new State_();
		for (Entry<String, Variable> entry : proBState.getValues().entrySet()) {
			Variable v = entry.getValue();
			String key = entry.getKey();
			String id = entry.getValue().getIdentifier();
			String value = entry.getValue().getValue();
			state.add(entry.getValue().getIdentifier(), entry.getValue().getValue());
		}
		return state;
	}
	
	/**
	 * 
	 * @param proBHistory
	 * @return
	 */
	private static History_ convert(History proBHistory) {
		History_ history = new History_();
//		HistoryItem hi = null;
		HistoryItem[] historyItems = proBHistory.getAllItems();
		int currentPos = proBHistory.getCurrentPosition();
//		for (HistoryItem next_hi : proBHistory.getAllItems()) {
		for (int i=0; i<currentPos; i++) {
			history.addItem(convert(historyItems[i].getOperation()), convert(historyItems[i+1].getState()));
//			HistoryItem next_hi = historyItems[i];
//			if (hi==null) {
//				history.addItem(null, convert(next_hi.getState()));
//			}else {
//				history.addItem(convert(hi.getOperation()), convert(next_hi.getState()));
//			}
//			hi = next_hi;
		}
		return history;
	}


}
