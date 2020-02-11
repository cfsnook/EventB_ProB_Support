package ac.soton.eventb.probsupport.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IMachineRoot;

import ac.soton.eventb.probsupport.Activator;
import ac.soton.eventb.probsupport.IAnimationParticipant;
import de.prob.core.Animator;
import de.prob.core.command.LoadEventBModelCommand;
import de.prob.exceptions.ProBException;

public class AnimationManager {

	/**
	 * Start Animations for the given machine root
	 * 
	 * This starts a new ProB animation on the given machine and then 
	 * calls the startAnimating method of all registered animation participants.
	 * 
	 * @param mchRoot
	 */
	public static void startAnimation(IMachineRoot mchRoot) {
		try {
			
			//refresh project in workspace (avoids ProB errors)
			IProject project = mchRoot.getRodinProject().getProject();
			project.refreshLocal(IResource.DEPTH_ONE, null);
			
			// start ProB animation
			System.out.println("Starting ProB for " + mchRoot.getHandleIdentifier());
			LoadEventBModelCommand.load(Animator.getAnimator(), mchRoot);
				
			//tell the participants to start
			for (IAnimationParticipant participant : Activator.getParticipants()) {
				System.out.println("Starting participant "+Activator.getParticipantID(participant) +" for " + mchRoot.getHandleIdentifier());
				participant.startAnimating(mchRoot);	
			}	
			
		} catch (CoreException e) {  	//refreshLocal
			e.printStackTrace();
			Activator.logError("Animation aborted: Failed to refresh project " + mchRoot.getRodinProject().getHandleIdentifier(), e);
			System.out.println("Animation aborted: Failed to refresh project " + mchRoot.getRodinProject().getHandleIdentifier());
		} catch (ProBException e) {		//load
			e.printStackTrace();
			Activator.logError("Animation aborted: Failed to start ProB for machine " + mchRoot.getHandleIdentifier(), e);
			System.out.println("Animation aborted: Failed to start ProB for machine " + mchRoot.getHandleIdentifier());
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
		//tell participants they can stop
		for (IAnimationParticipant participant : Activator.getParticipants()) {
			participant.stopAnimating(mchRoot);
		}
	}
	
}
