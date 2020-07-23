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

/**
 * A data structure representing an operation invocation of an animation
 * 
 * This is an operation name and a list of argument values (as strings)
 * 
 * @author cfsnook
 *
 */
public class Operation_ {

	private String name;
	private List<String> arguments;
	
	public Operation_(String name, List<String> arguments) {
		this.name = name;
		this.arguments = arguments;
		
	}
	
	/**
	 * returns the name of the operation
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * returns a list of the operation arguments
	 * @return
	 */
	public List<String> getArguments(){
		return arguments;
	}
	
	/**
	 * returns a string representing the operation.
	 * (i.e. name followed by the arguments)
	 * @return
	 */
	public String inStringFormat() {
		return name+" "+arguments;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) return true;
		if (obj instanceof Operation_ &&
				((Operation_)obj).getName().equals(this.getName()) &&
				((Operation_)obj).getArguments().equals(this.getArguments())
				) {
			return true;
		}
		return false;
	}
}

