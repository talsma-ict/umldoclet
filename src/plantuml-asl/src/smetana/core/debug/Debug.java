/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2020, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
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
package smetana.core.debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Debug {

	private final Map<String, String> methodNames = new HashMap<String, String>();
	private final Collection<String> called = new LinkedHashSet<String>();

	public void entering(String signature, String methodName) {
		methodNames.put(signature, methodName);
		if (called.contains(signature) == false) {
			called.add(signature);
		}
	}

	public void leaving(String signature, String methodName) {
	}

	public void reset() {
		methodNames.clear();
		called.clear();
	}

	public void printMe() {
		System.err.println("methodNames=" + methodNames.size());
		System.err.println("called=" + called.size());
		final List<String> called2 = new ArrayList<String>(called);
		for (int i = 0; i < called.size(); i++) {
			System.err.println("n " + i + " " + methodNames.get(called2.get(i)) + " " + called2.get(i));
		}
		final Set<String> called3 = new HashSet<String>(called);
		for (String s : called3) {
			System.err.println("p " + s);
		}
	}

}
