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
import java.util.HashSet;
import java.util.Set;

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

public class BMSAnimationParticipant implements IAnimationParticipant {

	private static final String BMOTION_STUDIO_EXT = "bmso";
	
	//remember the editors we run at start, so we can stop them later
	private Set<BMotionStudioEditor> bmsEditors = new HashSet<BMotionStudioEditor>();
	
	@Override
	public void startAnimating(IMachineRoot mchRoot) {
		bmsEditors.clear();  //remember the editors so that we can stop them
		IProject project = mchRoot.getRodinProject().getProject();
		String mchName = mchRoot.getComponentName();
		// Find all the open BMS editors that are for the given machine root and run them
		Set<IFile> bmsFiles = new HashSet<IFile>();
		for (IWorkbenchPage page : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()) {   //   activeWorkbenchWindow.getPages()) {
			for (IEditorReference editorRef : page.getEditorReferences()) {
				IEditorPart editor = editorRef.getEditor(true);
	    		//look for BMotionStudio editors on the same machine
	    		if (editor instanceof BMotionStudioEditor) {
	    			BMotionStudioEditor bmsEditor = (BMotionStudioEditor) editor;
	    			Object bmspf = bmsEditor.getVisualization().getProjectFile();
	    			if (bmspf instanceof IFile && BMOTION_STUDIO_EXT.equals(((IFile)bmspf).getFileExtension())){
		    			String bmsMachineName = bmsEditor.getVisualization().getMachineName();
		    			IProject bmsproject = ((IFile)bmspf).getProject();
	    				if (bmsMachineName.startsWith(mchName) && project.equals(bmsproject)){
							if (!bmsFiles.contains(bmspf)) {
								bmsFiles.add(((IFile)bmspf));
								if (runBMotionStudio(bmsEditor, (IFile)bmspf)) {
									bmsEditors.add(bmsEditor);
								}
							}
	    				}
	    			}
		    	}
			}
		}
	}

	@Override
	public void stopAnimating(IMachineRoot mchRoot) {
		for (BMotionStudioEditor bmsEditor : bmsEditors) {
			if (bmsEditor.getVisualization().getMachineName().startsWith(mchRoot.getComponentName())) {
				bmsEditor.removeRunPage();
			}
		}
		bmsEditors.clear();
	}
	
	/**
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

	@Override
	public void updateAnimation(IMachineRoot mchRoot) {
		//do nothing as BMS does its own listening
	}
    
}
