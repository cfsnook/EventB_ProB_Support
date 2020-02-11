package ac.soton.eventb.probsupport;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

public class AnimationParticipantFactory {
	
	//the shared instance
	private static AnimationParticipantFactory factory = null;
	//the participants
	private Collection<IAnimationParticipant> participants = new HashSet<IAnimationParticipant>();
	
	private AnimationParticipantFactory() {
		for (final IExtension participantsExtension : Platform.getExtensionRegistry().getExtensionPoint(Identifiers.EXTPT_ANIMATIONPARTICIPANTS_EXTPTID).getExtensions()) {
			for (final IConfigurationElement participantsExtensionElement : participantsExtension.getConfigurationElements()) {
				Object participantId = participantsExtensionElement.getAttribute(Identifiers.EXTPT_ANIMATIONPARTICIPANT_ID);				
				Object participantClass;
				try {
					participantClass = participantsExtensionElement.createExecutableExtension(Identifiers.EXTPT_ANIMATIONPARTICIPANT_CLASS);

					if (participantClass instanceof IAnimationParticipant) {
						participants.add((IAnimationParticipant) participantClass);
					}
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * return the TranslatorFactory shared instance
	 * (on the first call, this creates it by loading the translation extensions)
	 * @return
	 * @throws CoreException
	 */
	public static AnimationParticipantFactory getFactory() {
		if (factory == null){
			factory = new AnimationParticipantFactory();
		}
		return factory;
	}
	
	/**
	 * return the collection of AnimationPArticipants as an immutable set
	 * 
	 */
	public static Set<IAnimationParticipant> getParticipants() {
		factory = null;
		if (factory==null) getFactory();
		return Collections.unmodifiableSet((Set<IAnimationParticipant>) factory.participants);
	}
	
}
