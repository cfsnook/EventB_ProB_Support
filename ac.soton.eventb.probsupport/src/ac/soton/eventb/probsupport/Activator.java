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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class Activator extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "ac.soton.eventb.probsupport";
	//private static BundleContext context;

	// The shared instance
	private static Activator plugin;
	
	//the potential animation participants
	private Map<IAnimationParticipant,String> participants= new HashMap<IAnimationParticipant, String>();
	
	/**
	 * The constructor
	 */
	public Activator() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		plugin = this;
		
		//populate participants from extensions
		for (final IExtension participantsExtension : Platform.getExtensionRegistry().getExtensionPoint(Identifiers.EXTPT_ANIMATIONPARTICIPANTS_EXTPTID).getExtensions()) {
			for (final IConfigurationElement participantsExtensionElement : participantsExtension.getConfigurationElements()) {
				String participantId = participantsExtensionElement.getAttribute(Identifiers.EXTPT_ANIMATIONPARTICIPANT_ID);				
				try {
					participants.put((IAnimationParticipant) participantsExtensionElement.createExecutableExtension(Identifiers.EXTPT_ANIMATIONPARTICIPANT_CLASS), participantId);
				} catch (Exception e) {
					logError("Failed to create executable class when loading animation participant extension"+participantId, e);
					e.printStackTrace();
				}
			}
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
		super.stop(bundleContext);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * return the collection of AnimationPArticipants as an immutable set
	 * 
	 */
	public static Set<IAnimationParticipant> getParticipants() {
		return Collections.unmodifiableSet(plugin.participants.keySet());
	}
	
	/**
	 * returns the id for a given participant
	 * @param participant
	 * @return
	 */
	public static String getParticipantID(IAnimationParticipant participant) {
		return plugin.participants.get(participant);
	}
	
	/**
	 * log error
	 * @param message
	 * @param e
	 */
	public static void logError(String message, Exception e) {
		 Activator.getDefault().getLog().log(new Status(
				 IStatus.ERROR,
				 Activator.PLUGIN_ID,
				 IStatus.ERROR,
				 message,
				 e));
	}
	
	public static void logError(String message) {
		logError(message,null);
	}


}
