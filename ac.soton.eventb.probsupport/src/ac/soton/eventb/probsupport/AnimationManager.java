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
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.core.domainobjects.Variable;
import de.prob.exceptions.ProBException;

/**
 * This manages the ProB animation and relays information to the animation participants
 * 
 * @author cfsnook
 *
 */
public class AnimationManager {

	// the currently animated machines 
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
		if (!isRunning(mchRoot)){ 
			try {
				//refresh project in workspace (avoids ProB errors)
				IProject project = mchRoot.getRodinProject().getProject();
				project.refreshLocal(IResource.DEPTH_ONE, null);
				
				// start ProB animation
				System.out.println("Starting ProB for " + mchRoot.getHandleIdentifier());
				LoadEventBModelCommand.load(Animator.getAnimator(), mchRoot);
				
			} catch (CoreException e) {  	//refreshLocal
				e.printStackTrace();
				Activator.logError("Animation manager: Failed to refresh project " + mchRoot.getRodinProject().getHandleIdentifier(), e);
				System.out.println("Animation manager: Failed to refresh project " + mchRoot.getRodinProject().getHandleIdentifier());
			} catch (ProBException e) {		//load
				e.printStackTrace();
				Activator.logError("Animation manager: Failed to start ProB for machine " + mchRoot.getHandleIdentifier(), e);
				System.out.println("Animation manager: Failed to start ProB for machine " + mchRoot.getHandleIdentifier());
			}
			AnimationManager.mchRoot = mchRoot;
			//tell the participants to start
			for (IAnimationParticipant participant : Activator.getParticipants()) {
				System.out.println("Starting participant "+Activator.getParticipantID(participant) +" for " + mchRoot.getHandleIdentifier());
				try {
					participant.startAnimating(mchRoot);
				} catch (Exception e) {
					e.printStackTrace();
					Activator.logError("Animation manager: Failed to start Animation Participant " + participant.toString(), e);
					System.out.println("Animation manager: Failed to start Animation Participant " + participant.toString());
				}
			}
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
		//if (isRunning(mchRoot)){ 
			//tell participants they can stop
			for (IAnimationParticipant participant : Activator.getParticipants()) {
				try {
					participant.stopAnimating(mchRoot);
				} catch (Exception e) {
					e.printStackTrace();
					Activator.logError("Animation manager: Failed to stop Animation Participant " + participant.toString(), e);
					System.out.println("Animation manager: Failed to stop Animation Participant " + participant.toString());
				}
			}
			AnimationManager.mchRoot=null;
		//}
	}
	
	public static void restartAnimation (IMachineRoot mchRoot) {
		if (isRunning(mchRoot)){ 
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
		if (isRunning(mchRoot)){ 
			//tell the participants to update
			for (IAnimationParticipant participant : Activator.getParticipants()) {
				System.out.println("Animation manager: Updating participant "+Activator.getParticipantID(participant) +" for " + mchRoot.getHandleIdentifier());
				participant.updateAnimation(mchRoot);	
			}
		}
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
	 * 
	 * @param mchRoot
	 * @return
	 */
	public static History_ getHistory(IMachineRoot mchRoot) {
		Animator animator = getAnimator(mchRoot);
		History proBHistory = animator.getHistory();
		return (convert(proBHistory));
	}

	/**
	 * Returns a list of the currently enabled operation invocations
	 * 
	 * @param mchRoot
	 * @return
	 */
	public static List<Operation_> getEnabledOperations(IMachineRoot mchRoot) {
		Animator animator = getAnimator(mchRoot);
		State currentState = animator.getCurrentState();		
		List<Operation_> enabledOperations = new ArrayList<Operation_>();
		// for each enabled operation in the ProB model create an operation_
		for(Operation proBop: currentState.getEnabledOperations()){
			enabledOperations.add(convert(proBop));
			}
		return enabledOperations;
	}
	
	/**
	 * 
	 * @param mchRoot
	 * @param operation
	 * @param silent
	 */
	public static void executeOperation(IMachineRoot mchRoot, Operation_ operation, boolean silent)  {
		Animator animator = getAnimator(mchRoot);
		Operation proBOperation = getOperation(animator, operation);
		try {
			ExecuteOperationCommand.executeOperation(animator, proBOperation, silent);
		} catch (ProBException e) {
			e.printStackTrace();
		}
	}
	
	/////////////////////////// private helper methods ////////////////////////////////////////
	
	/**
	 * @param mchRoot
	 * @return
	 */
	private static Animator getAnimator(IMachineRoot mchRoot) {
		return Animator.getAnimator();
	}
	
	/**
	 * @param animator
	 * @param operation
	 * @return
	 */
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

	/**
	 * @param proBOperation
	 * @return
	 */
	private static Operation_ convert(Operation proBOperation) {
		return new Operation_(proBOperation.getName(), proBOperation.getArguments());
	}
	
	/**
	 * @param proBState
	 * @return
	 */
	private static State_ convert(State proBState) {
		State_ state = new State_();
		for (Entry<String, Variable> entry : proBState.getValues().entrySet()) {
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
		HistoryItem[] historyItems = proBHistory.getAllItems();
		int currentPos = proBHistory.getCurrentPosition();
		for (int i=0; i<currentPos; i++) {
			history.addItem(convert(historyItems[i].getOperation()), convert(historyItems[i+1].getState()));
		}
		return history;
	}

}
