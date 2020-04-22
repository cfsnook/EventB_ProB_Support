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

package ac.soton.eventb.probsupport.BMotionStudio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IMachineRoot;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import ac.soton.eventb.probsupport.Activator;
import ac.soton.eventb.probsupport.IAnimationParticipant;
import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.BMotionStudioEditor;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.prob.core.Animator;

/**
 * BMotionStudio Animation Participant
 * 
 * @author cfsnook
 *
 */
public class BMSAnimationParticipant implements IAnimationParticipant {

	private static final String BMOTION_STUDIO_EXT = "bmso";
	
	//remember the editors we run at start, so we can restart/stop them later
	private Map<BMotionStudioEditor,IFile> bmsEditors = new HashMap<BMotionStudioEditor,IFile>();
	
	/* (non-Javadoc)
	 * @see ac.soton.eventb.probsupport.IAnimationParticipant#startAnimating(org.eventb.core.IMachineRoot)
	 */
	@Override
	public void startAnimation(IMachineRoot mchRoot) {
		bmsEditors.clear();  //remember the editors so that we can stop them
		IProject project = mchRoot.getRodinProject().getProject();
		String mchName = mchRoot.getComponentName();
		// Find all the open BMS editors that are for the given machine root and run them
		for (IWorkbenchPage page : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()) {  
			for (IEditorReference editorRef : page.getEditorReferences()) {
				IEditorPart editor = editorRef.getEditor(true);
	    		//look for BMotionStudio editors on the same machine
	    		if (editor instanceof BMotionStudioEditor) {
	    			BMotionStudioEditor bmsEditor = (BMotionStudioEditor) editor;
	    			IFile bmspf = bmsEditor.getVisualization().getProjectFile();
	    			if (bmspf != null && BMOTION_STUDIO_EXT.equals(bmspf.getFileExtension())){
	    				String bmsMachineName = bmsEditor.getVisualization().getMachineName();
	    				IProject bmsproject = bmspf.getProject();
	    				if (bmsMachineName.startsWith(mchName) && project.equals(bmsproject)){
	    					if (runBMotionStudio(bmsEditor, bmspf)) {
	    						bmsEditors.put(bmsEditor,bmspf);
	    					}
	    				}
	    			}
		    	}
			}
		}
	}


	/* (non-Javadoc)
	 * @see ac.soton.eventb.probsupport.IAnimationParticipant#stopAnimating(org.eventb.core.IMachineRoot)
	 */
	@Override
	public void stopAnimation(IMachineRoot mchRoot) {
		for (Entry<BMotionStudioEditor, IFile> bmsEditorEntry : bmsEditors.entrySet()) {
			if (bmsEditorEntry.getKey().getVisualization().getMachineName().startsWith(mchRoot.getComponentName())) {
				bmsEditorEntry.getKey().reset();
			}
		}
		bmsEditors.clear();
	}
	
	/* (non-Javadoc)
	 * @see ac.soton.eventb.probsupport.IAnimationParticipant#updateAnimation(org.eventb.core.IMachineRoot)
	 */
	@Override
	public void updateAnimation(IMachineRoot mchRoot) {
		//do nothing as BMotion Studio does its own listening
	}
	
	/* (non-Javadoc)
	 * @see ac.soton.eventb.probsupport.IAnimationParticipant#restartAnimation(org.eventb.core.IMachineRoot)
	 */
	@Override
	public void restartAnimation(IMachineRoot mchRoot) {
		for (Entry<BMotionStudioEditor, IFile> bmsEditorEntry : bmsEditors.entrySet()) {
			runBMotionStudio(bmsEditorEntry.getKey(), bmsEditorEntry.getValue());
		}
	}
	
	
	/**
	 * Run the BMotion Studio editor
	 * 
	 * @param bmsFile
	 * @param animator
	 * @return 
	 */
	private boolean runBMotionStudio(BMotionStudioEditor bmsEditor, IFile bmsFile){
		try {
			Visualization visualization = createVisualizationRoot(bmsFile);
			Animation animation = new Animation(Animator.getAnimator(), visualization);
			bmsEditor.createRunPage(visualization, animation);
		} catch (CoreException e) {
			Activator.logError("Eclipse Core Exception while attempting to launch BMotion Studio", e);
			return false;
		} catch (IOException e) {
			Activator.logError("IO Exception while attempting to launch BMotion Studio", e);
			return false;
		} catch (ParserConfigurationException e) {
			Activator.logError("Parser Configuration Exception while attempting to launch BMotion Studio", e);
			return false;
		} catch (SAXException e) {
			Activator.logError("SAX Exception while attempting to launch BMotion Studio", e);
			return false;
		}
		return true;
	}
	
	/**
	 * Return a visualisation object for the given BMotion Studio .bms file
	 * @param bmsFile 
	 * 
	 * @return Visualization
	 * @throws CoreException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
    private static Visualization createVisualizationRoot(IFile bmsFile) throws CoreException, IOException, ParserConfigurationException, SAXException {
            XStream xstream = new XStream(new DomDriver()) {
                    @Override
                    protected MapperWrapper wrapMapper(MapperWrapper next) {
                            return new MapperWrapper(next) {                      
                            		@Override
                                    public boolean shouldSerializeMember(@SuppressWarnings("rawtypes") Class definedIn,String fieldName) {
                                            if (definedIn == Object.class) {
                                                    return false;
                                            }
                                            return super.shouldSerializeMember(definedIn, fieldName); 
                                    }
                            };
                    }
            };
            BMotionEditorPlugin.setAliases(xstream);
            Visualization visualization = (Visualization) xstream.fromXML(bmsFile.getContents());
            visualization.setProjectFile(bmsFile);
            visualization.setIsRunning(true);
            return visualization;
    }

    
}
