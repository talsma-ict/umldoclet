/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  https://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * https://plantuml.com/patreon (only 1$ per month!)
 * https://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.tim;

import java.util.Collections;
import java.util.Set;

public class TFunctionSignature {

	private final String functionName;
	private final int nbArg;
	private final Set<String> namedArguments;

	public TFunctionSignature(String functionName, int nbArg) {
		this(functionName, nbArg, Collections.<String>emptySet());
	}

	public TFunctionSignature(String functionName, int nbArg, Set<String> namedArguments) {
		if (functionName == null) {
			throw new IllegalArgumentException();
		}
		this.functionName = functionName;
		this.nbArg = nbArg;
		this.namedArguments = namedArguments;
	}

	public boolean sameFunctionNameAs(TFunctionSignature other) {
		return getFunctionName().equals(other.getFunctionName());
	}

	@Override
	public String toString() {
		return functionName + "/" + nbArg + " " + namedArguments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + functionName.hashCode();
		result = prime * result + nbArg;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		final TFunctionSignature other = (TFunctionSignature) obj;
		return functionName.equals(other.functionName) && nbArg == other.nbArg;
	}

	public final String getFunctionName() {
		return functionName;
	}

	public final int getNbArg() {
		return nbArg;
	}

	public final Set<String> getNamedArguments() {
		return namedArguments;
	}
}
