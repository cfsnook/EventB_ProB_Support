package ac.soton.eventb.probsupport;

import org.eventb.core.IMachineRoot;

public interface IAnimationParticipant {

	void startAnimating(IMachineRoot mchRoot);
	
	void stopAnimating(IMachineRoot mchRoot);
	
}
