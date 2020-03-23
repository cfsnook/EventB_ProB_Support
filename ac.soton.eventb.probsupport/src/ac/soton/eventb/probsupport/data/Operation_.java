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

package ac.soton.eventb.probsupport.data;

import java.util.List;

public class Operation_ {

	private String name;
	private List<String> arguments;
	
	public Operation_(String name, List<String> arguments) {
		this.name = name;
		this.arguments = arguments;
		
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getArguments(){
		return arguments;
	}
	
	/**
	 * 
	 * @return
	 */
	public String inStringFormat() {
		//String s1 = toString();
		//String s1 = name+arguments;
		return name+" "+arguments; //toString().replaceFirst("\\(", " (");
	}
}
